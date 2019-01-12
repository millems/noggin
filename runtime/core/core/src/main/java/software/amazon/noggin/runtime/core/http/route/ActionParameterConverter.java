package software.amazon.noggin.runtime.core.http.route;

import software.amazon.noggin.runtime.core.http.HttpRequest;

public class ActionParameterConverter {
    private ActionParameterConverter() {}

    public static HttpRequest identity(HttpRequest request) {
        return request;
    }

    public static String stringPathVariable(HttpRequest request, Integer pathIndex) {
        return request.pathComponents().get(pathIndex);
    }

}
