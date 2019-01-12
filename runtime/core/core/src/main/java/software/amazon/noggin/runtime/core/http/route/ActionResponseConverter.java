package software.amazon.noggin.runtime.core.http.route;

import software.amazon.noggin.runtime.core.http.HttpResponse;

public class ActionResponseConverter {
    private ActionResponseConverter() {}

    public static HttpResponse identity(HttpResponse response) {
        return response;
    }

    public static HttpResponse stringToUtfBody(String response) {
        return HttpResponse.builder()
                           .statusCode(200)
                           .utf8Body(response)
                           .build();
    }
}
