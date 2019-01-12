package software.amazon.noggin.runtime.core.http;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.util.Arrays;
import java.util.Map;
import java.util.StringJoiner;
import software.amazon.noggin.runtime.util.Validate;

public class HttpResponse extends HttpMessage {
    private int statusCode;

    public static Builder builder() {
        return new Builder();
    }

    private HttpResponse(Builder builder) {
        super(builder);
        this.statusCode = Validate.notNull(builder.statusCode, "statusCode");
    }

    public int statusCode() {
        return statusCode;
    }

    public Builder toBuilder() {
        return new Builder(this);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", HttpRequest.class.getSimpleName() + "[", "]")
                .add("statusCode=" + statusCode)
                .add("headers=" + headers())
                .add("body=" + Arrays.toString(body().orElse(null)))
                .toString();
    }

    public static final class Builder extends HttpMessage.Builder {
        private Integer statusCode;

        private Builder() {}

        public Builder(HttpResponse httpResponse) {
            super(httpResponse);
            this.statusCode = httpResponse.statusCode;
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

        public Builder utf8Body(String body) {
            body(body.getBytes(UTF_8));
            return this;
        }

        public Builder body(byte[] body) {
            super.body(body);
            return this;
        }

        public Builder statusCode(Integer statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        public HttpResponse build() {
            return new HttpResponse(this);
        }
    }
}
