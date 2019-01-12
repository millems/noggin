package software.amazon.noggin.runtime.core.route;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;
import org.junit.Test;
import software.amazon.noggin.runtime.core.http.HttpRequest;
import software.amazon.noggin.runtime.core.http.route.HttpResource;

public class HttpResourceTest {
//    @Test
//    public void singleRoutesBuildCorrectly() {
//        Object value = new Object();
//
//        assertThat(createResource(value, "a/b/c/d").child("a").child("b").child("c").child("d").value()).isEqualTo(value);
//        assertThat(createResource(value, "").value()).isEqualTo(value);
//        assertThat(createResource(value, "a").child("a").value()).isEqualTo(value);
//        assertThat(createResource(value, "/").value()).isEqualTo(value);
//        assertThat(createResource(value, "//").value()).isEqualTo(value);
//        assertThat(createResource(value, "///").child("").child("").value()).isEqualTo(value);
//        assertThat(createResource(value, "/a").child("a").value()).isEqualTo(value);
//        assertThat(createResource(value, "a/").child("a").value()).isEqualTo(value);
//        assertThat(createResource(value, "a//").child("a").child("").value()).isEqualTo(value);
//
//        assertThat(createResource(value, "a/b/c/d").child("a").child("b").value()).isNull();
//    }
//
//    @Test
//    public void nonOverlappingRoutesBuildCorrectly() {
//        Map<String, Object> routes = new TreeMap<>();
//        routes.put("a/b", new Object());
//        routes.put("b/c", new Object());
//
//        HttpResource<Object> resource = HttpResource.create(routes);
//
//        assertThat(resource.child("a").child("b").value()).isEqualTo(routes.get("a/b"));
//        assertThat(resource.child("b").child("c").value()).isEqualTo(routes.get("b/c"));
//    }
//
//    @Test
//    public void overlappingRoutesBuildCorrectly() {
//        Map<String, Object> routes = new TreeMap<>();
//        routes.put("/", new Object());
//        routes.put("/a/b", new Object());
//        routes.put("/a/c/d", new Object());
//        routes.put("/a", new Object());
//
//        HttpResource<Object> resource = HttpResource.create(routes);
//
//        assertThat(resource.value()).isEqualTo(routes.get("/"));
//        assertThat(resource.child("a").child("b").value()).isEqualTo(routes.get("/a/b"));
//        assertThat(resource.child("a").child("c").child("d").value()).isEqualTo(routes.get("/a/c/d"));
//        assertThat(resource.child("a").value()).isEqualTo(routes.get("/a"));
//    }
//
//    @Test
//    public void duplicateRoutesFail() {
//        assertThatThrownBy(() -> {
//            Map<String, Object> routes = new TreeMap<>();
//            routes.put("/", new Object());
//            routes.put("", new Object());
//            HttpResource.create(routes);
//        }).isInstanceOf(IllegalStateException.class);
//
//        assertThatThrownBy(() -> {
//            Map<String, Object> routes = new TreeMap<>();
//            routes.put("/a/b/", new Object());
//            routes.put("a/b", new Object());
//            HttpResource.create(routes);
//        }).isInstanceOf(IllegalStateException.class);
//    }
//
//    @Test
//    public void routesWithVariablesBuildCorrectly() {
//        Map<String, Object> routes = new TreeMap<>();
//        routes.put("{v1}", new Object());
//        routes.put("a/{v2}", new Object());
//        routes.put("b/c/{v3}/d", new Object());
//        routes.put("a", new Object());
//
//        HttpResource<Object> resource = HttpResource.create(routes);
//
//        assertThat(resource.variableResource().value()).isEqualTo(routes.get("{v1}"));
//        assertThat(resource.variableName()).isEqualTo("v1");
//
//        assertThat(resource.child("a").variableResource().value()).isEqualTo(routes.get("a/{v2}"));
//        assertThat(resource.child("a").variableName()).isEqualTo("v2");
//
//        assertThat(resource.child("b").child("c").variableResource().child("d").value()).isEqualTo(routes.get("b/c/{v3}/d"));
//        assertThat(resource.child("b").child("c").variableName()).isEqualTo("v3");
//    }
//
//    @Test
//    public void overlappingVariablesAreNotSupported() {
//        assertThatThrownBy(() -> {
//            Map<String, Object> routes = new TreeMap<>();
//            routes.put("{v1}", new Object());
//            routes.put("{v2}", new Object());
//            HttpResource.create(routes);
//        }).isInstanceOf(IllegalStateException.class);
//
//        assertThatThrownBy(() -> {
//            Map<String, Object> routes = new TreeMap<>();
//            routes.put("a/{v1}", new Object());
//            routes.put("a/{v2}", new Object());
//            HttpResource.create(routes);
//        }).isInstanceOf(IllegalStateException.class);
//
//        assertThatThrownBy(() -> {
//            Map<String, Object> routes = new TreeMap<>();
//            routes.put("a/{v1}/d", new Object());
//            routes.put("a/{v2}", new Object());
//            HttpResource.create(routes);
//        }).isInstanceOf(IllegalStateException.class);
//    }
//
//    @Test
//    public void resolveWorksCorrectly() {
//        Map<String, Object> routes = new TreeMap<>();
//        routes.put("/", new Object());
//        routes.put("/a/b", new Object());
//        routes.put("/a/c/d", new Object());
//        routes.put("/a", new Object());
//
//        HttpResource<Object> resource = HttpResource.create(routes);
//
//        assertThat(resource.resolve(httpRequest(""))).isEqualTo(routes.get("/"));
//        assertThat(resource.resolve(httpRequest("a/b"))).isEqualTo(routes.get("/a/b"));
//        assertThat(resource.resolve(httpRequest("/a/c/d"))).isEqualTo(routes.get("/a/c/d"));
//        assertThat(resource.resolve(httpRequest("/a/"))).isEqualTo(routes.get("/a"));
//    }
//
//    private HttpRequest httpRequest(String path) {
//        return HttpRequest.builder().httpMethod("GET").path(path).build();
//    }
//
//    private HttpResource<Object> createResource(Object value, String... paths) {
//        Map<String, Object> routes = new TreeMap<>();
//        Stream.of(paths).forEach(p -> routes.put(p, value));
//        return HttpResource.create(routes);
//    }
}