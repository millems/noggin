package software.amazon.noggin.core;

import static com.squareup.javapoet.CodeBlock.joining;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.stream.Stream;
import javax.annotation.processing.Filer;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import software.amazon.noggin.Application;
import software.amazon.noggin.http.Accepts;
import software.amazon.noggin.http.Method;
import software.amazon.noggin.http.Path;
import software.amazon.noggin.http.Produces;
import software.amazon.noggin.runtime.core.http.HttpRequest;
import software.amazon.noggin.runtime.core.http.HttpResponse;
import software.amazon.noggin.runtime.core.http.route.ActionResponseConverter;
import software.amazon.noggin.runtime.core.http.route.HttpNogginRoutes;
import software.amazon.noggin.runtime.core.http.route.HttpResource;
import software.amazon.noggin.runtime.core.http.route.HttpRoute;
import software.amazon.noggin.runtime.core.http.route.ActionParameterConverter;
import software.amazon.noggin.runtime.core.route.NogginRoutes;
import software.amazon.noggin.runtime.util.Validate;

public abstract class HttpNogginRouteGenerator implements NogginRouteGenerator {

    @Override
    public final void generateApplication(Filer filer, ApplicationElement element) throws IOException {

        String packageName = element.elementPackage().getQualifiedName().toString();
        String className = getClassName(element.application());

        TypeSpec generatedClass = generate(packageName, className, element);
        JavaFile.builder(packageName, generatedClass)
                .build()
                .writeTo(filer);

        FileObject resource = filer.createResource(StandardLocation.CLASS_OUTPUT,
                                                   "software.amazon.noggin", "application.properties");

        try (Writer writer = resource.openWriter()) {
            String fullyQualifiedApplicationClass = packageName + "." + className;
            writer.append("application.class = ").append(fullyQualifiedApplicationClass).append("\n")
                  .append("application.name = ").append(element.application().name()).append("\n")
                  .append("application.type = ").append(element.application().runtime().name()).append("\n");
        }
    }

    protected abstract String getClassName(Application application);

    private TypeSpec generate(String packageName, String className, ApplicationElement applicationElement) {
        return TypeSpec.classBuilder(ClassName.get(packageName, className))
                       .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                       .addSuperinterface(ParameterizedTypeName.get(NogginRoutes.class, HttpRequest.class, HttpResponse.class))
                       .addField(applicationField(applicationElement))
                       .addField(delegateField(applicationElement))
                       .addMethod(invokeMethod())
                       .build();
    }

    private FieldSpec applicationField(ApplicationElement applicationElement) {
        TypeName applicationClassName = TypeName.get(applicationElement.element().asType());
        return FieldSpec.builder(applicationClassName, "APPLICATION", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                        .initializer("new $T()", applicationClassName)
                        .build();
    }

    private FieldSpec delegateField(ApplicationElement applicationElement) {
        return FieldSpec.builder(HttpNogginRoutes.class, "DELEGATE", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                        .initializer(delegateInitializer(applicationElement))
                        .build();
    }

    private CodeBlock delegateInitializer(ApplicationElement applicationElements) {
        CodeBlock.Builder result = CodeBlock.builder()
                                            .add("$T.builder()", HttpNogginRoutes.class);

        applicationElements.annotatedMethods(Path.class)
                           .forEach(method -> {
                               Path route = method.getAnnotation(Path.class);
                               result.add("\n.addRoute($T.builder()", HttpRoute.class)
                                     .add("\n  .path($S)", route.value())
                                     .add(method(method.getAnnotation(Method.class)))
                                     .add(accepts(method.getAnnotation(Accepts.class)))
                                     .add(produces(method.getAnnotation(Produces.class)))
                                     .add("\n  .action(").add(actionCall(route, method)).add(")")
                                     .add("\n  .build())");
                           });

        return result.add("\n.build()")
                     .build();
    }

    private CodeBlock method(Method httpMethod) {
        return httpMethod == null ? CodeBlock.of("")
                                  : CodeBlock.builder()
                                             .add("\n  .methods(")
                                             .add(Stream.of(httpMethod.value())
                                                        .map(m -> CodeBlock.of("$S", m))
                                                        .collect(CodeBlock.joining(", ")))
                                             .add(")")
                                             .build();
    }

    private CodeBlock accepts(Accepts accepts) {
        return accepts == null ? CodeBlock.of("")
                               : CodeBlock.builder()
                                          .add("\n  .accepts(")
                                          .add(Stream.of(accepts.value())
                                                     .map(a -> CodeBlock.of("$S", a))
                                                     .collect(CodeBlock.joining(", ")))
                                          .add(")")
                                          .build();
    }

    private CodeBlock produces(Produces produces) {
        return produces == null ? CodeBlock.of("")
                                : CodeBlock.builder()
                                           .add("\n  .produces(")
                                           .add(Stream.of(produces.value())
                                                      .map(p -> CodeBlock.of("$S", p))
                                                      .collect(CodeBlock.joining(", ")))
                                           .add(")")
                                           .build();
    }

    private CodeBlock actionCall(Path route, ExecutableElement method) {
        Map<String, Integer> variableIndices = HttpResource.getVariableIndices(route.value());

        return CodeBlock.builder()
                        .add("r -> $T.", ActionResponseConverter.class)
                        .add(responseConverterMethod(method.getReturnType()))
                        .add("(APPLICATION.$L(", method.getSimpleName())
                        .add(method.getParameters().stream()
                                   .map(p -> CodeBlock.of("$T.$L",
                                                          ActionParameterConverter.class,
                                                          parameterConverterMethodCall(p, variableIndices)))
                                   .collect(joining(",")))
                        .add("))")
                        .build();

    }

    private String responseConverterMethod(TypeMirror returnType) {
        switch (returnType.toString()) {
            case "software.amazon.noggin.runtime.core.http.HttpResponse":
                return "identity";
            case "java.lang.String":
                return "stringToUtfBody";
            default:
                throw new IllegalStateException("Unsupported method return type: " + returnType);
        }
    }

    private String parameterConverterMethodCall(VariableElement variableElement, Map<String, Integer> variableIndices) {
        String variableType = variableElement.asType().toString();

        switch (variableType) {
            case "software.amazon.noggin.runtime.core.http.HttpRequest":
                return "identity(r)";
            case "java.lang.String":
                Name variableName = variableElement.getSimpleName();
                Integer variableIndex = variableIndices.get(variableName.toString());
                Validate.notNull(variableIndex, "String parameter %s could not be mapped to a path variable.", () -> variableName);
                return "stringPathVariable(r, " + variableIndex + ")";
            default:
                throw new IllegalStateException("Unsupported method parameter: " + variableElement);
        }
    }

    private MethodSpec invokeMethod() {
        return MethodSpec.methodBuilder("invoke")
                         .returns(HttpResponse.class)
                         .addParameter(HttpRequest.class, "request")
                         .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                         .addAnnotation(Override.class)
                         .addStatement("return DELEGATE.invoke(request)")
                         .build();
    }
}
