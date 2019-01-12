package software.amazon.noggin.runtime.core.route;

import software.amazon.noggin.runtime.util.Validate;

public class NogginRouteLoader {
    private static final String NOGGIN_ROUTES_CLASS_ENV = "NOGGIN_ROUTES_CLASS";

    public static <T extends NogginRoutes<?, ?>> NogginRoutes<?, ?> loadRoutesFromEnvironmentVariables() {
        String nogginRoutesClass = Validate.notNull(System.getenv(NOGGIN_ROUTES_CLASS_ENV),
                                                    "'%s' environment variable must be set.", () -> NOGGIN_ROUTES_CLASS_ENV);

        return NogginRouteLoader.loadRoutesFromNogginClassName(nogginRoutesClass, NogginRoutes.class);
    }

    public static <T extends NogginRoutes<?, ?>> NogginRoutes<?, ?> loadRoutesFromNogginClassName(String routesClassName,
                                                                                                  Class<T> routesType) {
        try {
            Class<?> nogginRoutesClass = Class.forName(routesClassName);
            Object implementation = nogginRoutesClass.newInstance();
            return Validate.isInstanceOf(implementation, routesType,
                                         "%s must implement %s.", () -> routesClassName,
                                         routesType::getName);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException("Unable to load noggin class '" + routesClassName + "'.", e);
        }
    }

    private NogginRouteLoader() {}
}
