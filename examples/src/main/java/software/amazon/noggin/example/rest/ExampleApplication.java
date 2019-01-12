package software.amazon.noggin.example.rest;

import software.amazon.noggin.Application;
import software.amazon.noggin.http.Path;
import software.amazon.noggin.http.Produces;
import software.amazon.noggin.runtime.core.http.HttpResponse;

@Application(name = "example-application")
public class ExampleApplication {
    @Path("/howdy/{user}")
    @Produces("text/plain")
    public String howdy(String user) {
        return String.format("Howdy, %s!", user);
    }

    @Path("/")
    public HttpResponse howdy() {
        return HttpResponse.builder()
                           .statusCode(200)
                           .utf8Body("Howdy, World!")
                           .putHeader("content-type", "text/plain")
                           .build();
    }
}
