package software.amazon.noggin.runtime.lambda;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Base64;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import software.amazon.noggin.runtime.core.http.HttpRequest;
import software.amazon.noggin.runtime.core.http.HttpResponse;
import software.amazon.noggin.runtime.core.route.NogginRouteLoader;
import software.amazon.noggin.runtime.core.route.NogginRoutes;

public class LambdaApplication {
    private static final NogginRoutes<HttpRequest, HttpResponse> ROUTES;
    private static final RuntimeException INITIALIZATION_EXCEPTION;

    static {
        NogginRoutes<HttpRequest, HttpResponse> routes = null;
        RuntimeException initializationException = null;

        try {
            //noinspection unchecked TODO: fix
            routes = (NogginRoutes<HttpRequest, HttpResponse>) NogginRouteLoader.loadRoutesFromEnvironmentVariables();
        } catch (RuntimeException e) {
            initializationException = e;
        }

        ROUTES = routes;
        INITIALIZATION_EXCEPTION = initializationException;
    }

    public void handle(InputStream requestStream, OutputStream responseStream) throws Exception {
        if (INITIALIZATION_EXCEPTION != null) {
            // TODO: Should the customer be able to hide this exception?
            throw INITIALIZATION_EXCEPTION;
        }

        HttpRequest request = unmarshalHttpRequest(requestStream);

        System.out.println(request);

        HttpResponse response = ROUTES.invoke(request);

        System.out.println(response);

        marshalHttpResponse(response, responseStream);
    }

    private HttpRequest unmarshalHttpRequest(InputStream requestStream) throws IOException {
        try (InputStreamReader reader = new InputStreamReader(requestStream)) {
            JsonElement jsonTree = new JsonParser().parse(reader);
            JsonObject json = jsonTree.getAsJsonObject();

            HttpRequest.Builder requestBuilder =
                    HttpRequest.builder()
                               .path(json.get("path").getAsString())
                               .httpMethod(json.get("httpMethod").getAsString())
                               .headers(unmarshalHeaders(json.get("headers")));

            JsonElement body = json.get("body");
            if (body != null && !body.isJsonNull()) {
                if (json.get("isBase64Encoded").getAsBoolean()) {
                    requestBuilder.body(Base64.getDecoder().decode(body.getAsString()));
                } else {
                    throw new IllegalStateException("Non-base 64 encoded JSON body: " + json);
                }
            }

            return requestBuilder.build();
        }
    }

    private void marshalHttpResponse(HttpResponse response, OutputStream responseStream) throws IOException {
        JsonObject output = new JsonObject();
        output.addProperty("statusCode", response.statusCode());

        if (!response.headers().isEmpty()) {
            JsonObject headers = new JsonObject();
            response.headers().forEach(headers::addProperty);
            output.add("headers", headers);
        }

        response.body().ifPresent(b -> {
            // TODO: Binary responses
            output.addProperty("body", new String(b, UTF_8));
        });

        try (JsonWriter jsonWriter = new JsonWriter(new OutputStreamWriter(responseStream))) {
            Streams.write(output, jsonWriter);
        }
    }

    private Map<String, String> unmarshalHeaders(JsonElement headersElement) {
        if (headersElement != null && !headersElement.isJsonNull()) {
            return headersElement.getAsJsonObject()
                                 .entrySet().stream()
                                 .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().getAsString()));
        }
        return Collections.emptyMap();
    }
}
