package software.amazon.noggin.runtime.core.http;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import software.amazon.noggin.runtime.util.Strings;
import software.amazon.noggin.runtime.util.Validate;

public class HttpRequest extends HttpMessage {
    private String path;
    private List<String> pathComponents;
    private String httpMethod;

    public static Builder builder() {
        return new Builder();
    }

    private HttpRequest(Builder builder) {
        super(builder);
        this.path = Validate.notNull(builder.path, "path");
        this.httpMethod = Validate.notNull(builder.httpMethod, "httpMethod");
        this.pathComponents = Strings.split(Strings.trim(builder.path, "/"), '/');
    }

    public String path() {
        return path;
    }

    public List<String> pathComponents() {
        return Collections.unmodifiableList(pathComponents);
    }

    public String httpMethod() {
        return httpMethod;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", HttpRequest.class.getSimpleName() + "[", "]")
                .add("httpMethod='" + httpMethod + "'")
                .add("path='" + path + "'")
                .add("headers=" + headers())
                .add("body=" + Arrays.toString(body().orElse(null)))
                .toString();
    }

    public static final class Builder extends HttpMessage.Builder {
        private String path;
        private String httpMethod;

        private Builder() {
        }

        @Override
        public Builder putHeader(String name, String value) {
            super.putHeader(name, value);
            return this;
        }

        @Override
        public Builder headers(Map<String, String> headers) {
            super.headers(headers);
            return this;
        }

        public Builder body(byte[] body) {
            super.body(body);
            return this;
        }

        public Builder path(String path) {
            this.path = path;
            return this;
        }

        public Builder httpMethod(String httpMethod) {
            this.httpMethod = httpMethod;
            return this;
        }

        public HttpRequest build() {
            return new HttpRequest(this);
        }
    }
}
