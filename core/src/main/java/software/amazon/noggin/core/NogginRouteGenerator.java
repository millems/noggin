package software.amazon.noggin.core;

import java.io.IOException;
import javax.annotation.processing.Filer;
import software.amazon.noggin.NogginRuntime;
import software.amazon.noggin.runtime.util.Validate;

public interface NogginRouteGenerator {
    static NogginRouteGenerator forType(NogginRuntime type) {
        return forClassName(type.generatorClass());
    }

    static NogginRouteGenerator forClassName(String className) {
        try {
            Object instance = Class.forName(className).newInstance();
            return Validate.isInstanceOf(instance, NogginRouteGenerator.class,
                                         "%s must be a NogginRouteGenerator.", () -> className);
        } catch (InstantiationException | ClassNotFoundException | IllegalAccessException e) {
            throw new IllegalStateException("Unable to load " + className, e);
        }

    }

    // TODO: Harden up this interface.
    void generateApplication(Filer filer, ApplicationElement application) throws IOException;
}
