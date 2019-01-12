package software.amazon.noggin.runtime.core.http.route;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import software.amazon.noggin.runtime.core.http.HttpRequest;
import software.amazon.noggin.runtime.core.http.HttpResponse;

public class HttpRoute {
    private final String path;
    private final List<String> methods = new ArrayList<>();
    private final List<String> acceptContentTypes = new ArrayList<>();
    private final List<String> producesContentTypes = new ArrayList<>();
    private final Function<HttpRequest, HttpResponse> action;

    public HttpRoute(Builder builder) {
        this.path = builder.path;
        this.methods.addAll(builder.methods);
        this.acceptContentTypes.addAll(builder.acceptContentTypes);
        this.producesContentTypes.addAll(builder.producesContentTypes);
        this.action = builder.action;
    }

    public String path() {
        return path;
    }

    public List<String> methods() {
        return Collections.unmodifiableList(methods);
    }

    public List<String> acceptContentTypes() {
        return Collections.unmodifiableList(acceptContentTypes);
    }

    public List<String> producesContentTypes() {
        return Collections.unmodifiableList(producesContentTypes);
    }

    public Function<HttpRequest, HttpResponse> action() {
        return action;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String path;
        private List<String> methods = Collections.emptyList();
        private List<String> acceptContentTypes = Collections.emptyList();
        private List<String> producesContentTypes = Collections.emptyList();
        private Function<HttpRequest, HttpResponse> action;

        private Builder() {}

        public Builder path(String path) {
            this.path = path;
            return this;
        }

        public Builder methods(String... methods) {
            this.methods = Arrays.asList(methods);
            return this;
        }

        public Builder accepts(String... acceptContentTypes) {
            this.acceptContentTypes = Arrays.asList(acceptContentTypes);
            return this;
        }

        public Builder produces(String... producesContentTypes) {
            this.producesContentTypes = Arrays.asList(producesContentTypes);
            return this;
        }

        public Builder action(Function<HttpRequest, HttpResponse> action) {
            this.action = action;
            return this;
        }

        public HttpRoute build() {
            return new HttpRoute(this);
        }
    }
}
