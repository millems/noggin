package software.amazon.noggin.runtime.core.http.route;

import java.util.ArrayList;
import java.util.List;
import software.amazon.noggin.runtime.core.http.HttpRequest;
import software.amazon.noggin.runtime.core.http.HttpResponse;
import software.amazon.noggin.runtime.core.http.exception.HttpException;
import software.amazon.noggin.runtime.core.route.NogginRoutes;

public class HttpNogginRoutes implements NogginRoutes<HttpRequest, HttpResponse> {
    private final HttpResource rootResource;

    public HttpNogginRoutes(Builder builder) {
        this.rootResource = HttpResource.create(builder.routes);
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public HttpResponse invoke(HttpRequest request) {
        try {
            HttpRoute route = rootResource.resolve(request);
            HttpResponse response = route.action().apply(request);
            response = addContentType(route, response);
            return response;
        } catch (HttpException e) {
            return HttpResponse.builder()
                               .statusCode(e.statusCode())
                               .utf8Body(String.format("{ \"httpError\": { \"statusCode\": \"%s\", \"message\": \"%s\" }}",
                                                       e.statusCode(), e.description()))
                               .build();
        }
    }

    private HttpResponse addContentType(HttpRoute route, HttpResponse response) {
        if (route.producesContentTypes().size() == 1 && !response.headers().containsKey("content-type")) {
            return response.toBuilder()
                           .putHeader("Content-Type", route.producesContentTypes().get(0))
                           .build();
        }
        return response;
    }

    public static final class Builder {
        private List<HttpRoute> routes = new ArrayList<>();

        private Builder() {}

        public Builder addRoute(HttpRoute route) {
            this.routes.add(route);
            return this;
        }

        public HttpNogginRoutes build() {
            return new HttpNogginRoutes(this);
        }
    }
}
