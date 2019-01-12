package software.amazon.noggin.core;

import java.lang.annotation.Annotation;
import java.util.stream.Stream;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import software.amazon.noggin.Application;
import software.amazon.noggin.runtime.util.Validate;

public class ApplicationElement {
    private final Element element;

    public static ApplicationElement fromElement(Element element) {
        return new ApplicationElement(element);
    }

    private ApplicationElement(Element element) {
        Validate.isTrue(element.getAnnotation(Application.class) != null,
                        "%s must be an @Application.", () -> element);
        Validate.isTrue(element.getEnclosingElement().getKind() == ElementKind.PACKAGE,
                        "%s must be a top-level class.", () -> element);
        this.element = element;
    }

    public Application application() {
        return element.getAnnotation(Application.class);
    }

    public PackageElement elementPackage() {
        return (PackageElement) element.getEnclosingElement();
    }

    public Element element() {
        return element;
    }

    public Stream<ExecutableElement> constructors() {
        return findEnclosedElements(element, ElementKind.CONSTRUCTOR);
    }

    public Stream<ExecutableElement> methods() {
        return findEnclosedElements(element, ElementKind.METHOD);
    }

    public Stream<ExecutableElement> annotatedMethods(Class<? extends Annotation> annotationType) {
        return methods().filter(e -> e.getAnnotation(annotationType) != null);
    }

    private Stream<ExecutableElement> findEnclosedElements(Element element, ElementKind elementKind) {
        return element.getEnclosedElements().stream()
                      .filter(e -> e.getKind() == elementKind)
                      .filter(e -> e.getModifiers().contains(Modifier.PUBLIC))
                      .map(ExecutableElement.class::cast);
    }
}
