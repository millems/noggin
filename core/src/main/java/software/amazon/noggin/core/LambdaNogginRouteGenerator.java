package software.amazon.noggin.core;

import software.amazon.noggin.Application;
import software.amazon.noggin.util.Names;

public class LambdaNogginRouteGenerator extends HttpNogginRouteGenerator {
    protected String getClassName(Application application) {
        return Names.toJavaClassName(application.name()) + "NogginLambda";
    }
}
