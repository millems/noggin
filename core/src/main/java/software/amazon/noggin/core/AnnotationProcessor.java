package software.amazon.noggin.core;

import com.google.auto.common.BasicAnnotationProcessor;
import com.google.common.collect.SetMultimap;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import software.amazon.noggin.Application;
import software.amazon.noggin.NogginRuntime;
import software.amazon.noggin.runtime.util.Validate;

public class AnnotationProcessor extends BasicAnnotationProcessor {
    @Override
    protected Iterable<? extends ProcessingStep> initSteps() {
        return Collections.singletonList(new GenerateRoutes());
    }

    private class GenerateRoutes implements ProcessingStep {
        @Override
        public Set<? extends Class<? extends Annotation>> annotations() {
            Set<Class<? extends Annotation>> result = new HashSet<>();
            result.add(Application.class);
            return result;
        }

        @Override
        public Set<? extends Element> process(SetMultimap<Class<? extends Annotation>, Element> elementsByAnnotation) {
            Set<Element> applications = elementsByAnnotation.get(Application.class);
            applications.forEach(this::processClass);
            return Collections.emptySet();
        }

        private void processClass(Element classElement) {
            Validate.isTrue(classElement.getKind() == ElementKind.CLASS,
                            "%s must a class to be annotated with @Application.", () -> classElement);

            Set<Modifier> modifiers = classElement.getModifiers();
            Validate.isTrue(modifiers.contains(Modifier.PUBLIC), "%s must be public.", () -> classElement);
            Validate.isTrue(!modifiers.contains(Modifier.ABSTRACT), "%s must not be abstract.", () -> classElement);

            NogginRuntime generatorType = classElement.getAnnotation(Application.class).runtime();
            NogginRouteGenerator generator = NogginRouteGenerator.forType(generatorType);

            try {
                generator.generateApplication(processingEnv.getFiler(), ApplicationElement.fromElement(classElement));
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }
}
