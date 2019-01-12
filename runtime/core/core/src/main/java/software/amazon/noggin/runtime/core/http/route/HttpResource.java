package software.amazon.noggin.runtime.core.http.route;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import software.amazon.noggin.runtime.core.http.HttpRequest;
import software.amazon.noggin.runtime.core.http.exception.ResourceNotFoundException;
import software.amazon.noggin.runtime.util.Strings;
import software.amazon.noggin.runtime.util.Validate;

public class HttpResource {
    private Map<String, HttpResource> childResources = new TreeMap<>();
    private Action value = new Action();

    private HttpResource variableResource = null;
    private String variableName = null;

    private HttpResource() {}

    public static HttpResource create(List<HttpRoute> routes) {
        HttpResource result = new HttpResource();

        for (HttpRoute route : routes) {
            String routePath = Strings.trim(route.path(), "/");

            if (routePath.length() == 0) {
                result.value.addRoute(route);
            } else {
                List<String> routeComponents = Strings.split(routePath, '/');
                HttpResource parentResource = result;
                for (int i = 0; i < routeComponents.size(); i++) {
                    String routeComponent = routeComponents.get(i);
                    HttpResource routeComponentResource;

                    if (isVariable(routeComponent)) {
                        String variable = variable(routeComponent);
                        routeComponentResource = new HttpResource();
                        routeComponentResource.value.addRoute(route);

                        Validate.isTrue(parentResource.variableName == null, "Duplicate variable route: %s", () -> route);
                        Validate.isTrue(parentResource.variableResource == null, "Duplicate variable route: %s", () -> route);

                        parentResource.variableName = variable;
                        parentResource.variableResource = routeComponentResource;
                    } else {
                        routeComponentResource = parentResource.childResources.get(routeComponent);

                        if (routeComponentResource == null) {
                            routeComponentResource = new HttpResource();
                            parentResource.childResources.put(routeComponent, routeComponentResource);
                        }

                        if (i == routeComponents.size() - 1) {
                            routeComponentResource.value.addRoute(route);
                        }
                    }

                    parentResource = routeComponentResource;
                }
            }
        }

        return result;
    }

    public HttpRoute resolve(HttpRequest request) {
        String path = Strings.trim(request.path(), "/");

        if (path.length() == 0) {
            return value.resolve(request);
        }

        List<String> pathComponents = request.pathComponents();

        HttpResource resultResource = this;
        for (String pathComponent : pathComponents) {
            HttpResource newResultResource = resultResource.childResources.get(pathComponent);

            if (newResultResource == null) {
                newResultResource = resultResource.variableResource;
            }

            if (newResultResource == null) {
                throw new ResourceNotFoundException();
            }

            resultResource = newResultResource;
        }

        return resultResource.value.resolve(request);
    }

    public static Map<String, Integer> getVariableIndices(String path) {
        path = Strings.trim(path, "/");
        List<String> pathComponents = Strings.split(path, '/');

        Map<String, Integer> result = new TreeMap<>();
        for (int i = 0; i < pathComponents.size(); i++) {
            String pathComponent = pathComponents.get(i);
            if (isVariable(pathComponent)) {
                result.put(variable(pathComponent), i);
            }
        }
        return Collections.unmodifiableMap(result);
    }

    private static boolean isVariable(String pathComponent) {
        return pathComponent.startsWith("{") && pathComponent.endsWith("}");
    }

    private static String variable(String pathComponent) {
        return pathComponent.substring(1, pathComponent.length() - 1);
    }

    private static final class Action {
        private static final String ANY = "ANY";
        private static final List<String> ANY_LIST = Collections.singletonList(ANY);

        private final Map<String, HttpRoute> routes = new TreeMap<>();

        private Action addRoute(HttpRoute route) {
            addMethod(route);
            return this;
        }

        private void addMethod(HttpRoute route) {
            List<String> methods = route.methods().isEmpty() ? ANY_LIST : route.methods();
            methods.forEach(method -> {
                Validate.isTrue(!routes.containsKey(method), "Route already exists for path %s and method %s.",
                                route::path, () -> method);
                routes.put(method, route);
            });
        }

        private HttpRoute resolve(HttpRequest request) {
            return resolve(routes, request.httpMethod());
        }

        private <T> T resolve(Map<String, T> map, String key) {
            T result = null;

            if (key != null) {
                result = map.get(key);
            }

            if (result == null) {
                result = map.get(ANY);
            }

            if (result == null) {
                throw new ResourceNotFoundException();
            }

            return result;
        }
    }
}

