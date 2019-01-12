package software.amazon.noggin.runtime.core.http;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public abstract class HttpMessage {
    private Map<String, String> headers;
    private byte[] body;

    protected HttpMessage(Builder builder) {
        this.headers = builder.headers;
        this.body = builder.body;
    }

    public Map<String, String> headers() {
        return Collections.unmodifiableMap(headers);
    }

    public Optional<byte[]> body() {
        return Optional.ofNullable(body);
    }

    public abstract static class Builder {
        Map<String, String> headers = new LinkedHashMap<>();
        // TODO: make immutable
        private byte[] body;

        protected Builder() {}

        protected Builder(HttpMessage message) {
            this.headers.putAll(message.headers);
            this.body = message.body;
        }

        public Builder putHeader(String name, String value) {
            this.headers.put(name, value);
            return this;
        }

        public Builder headers(Map<String, String> headers) {
            this.headers.clear();
            headers.forEach((k, v) -> this.headers.put(k.toLowerCase(Locale.US), v));
            return this;
        }

        public Builder body(byte[] body) {
            this.body = body;
            return this;
        }
    }
}


