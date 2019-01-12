package software.amazon.noggin.runtime.beanstalk;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.apache.commons.io.IOUtils;
import software.amazon.noggin.runtime.core.http.HttpRequest;
import software.amazon.noggin.runtime.core.http.HttpResponse;
import software.amazon.noggin.runtime.core.route.NogginRouteLoader;
import software.amazon.noggin.runtime.core.route.NogginRoutes;

public class BeanstalkApplication extends HttpServlet {
    private static final CountDownLatch SHUTDOWN_LATCH = new CountDownLatch(1);
    private final NogginRoutes<HttpRequest, HttpResponse> routes;

    public BeanstalkApplication() {
        //noinspection unchecked TODO: fix
        this.routes = (NogginRoutes<HttpRequest, HttpResponse>) NogginRouteLoader.loadRoutesFromEnvironmentVariables();
    }

    public static void main(String[] args) throws Exception {
        Logger.getLogger("").setLevel(Level.ALL);

        Tomcat tomcat = new Tomcat();
        tomcat.setPort(5000);
        Context context = tomcat.addContext("", null);
        tomcat.addServlet("", "beanstalk-application", new BeanstalkApplication());
        context.addServletMappingDecoded("/", "beanstalk-application");
        tomcat.start();

        Runtime.getRuntime().addShutdownHook(new Thread(SHUTDOWN_LATCH::countDown));
        SHUTDOWN_LATCH.await();

        tomcat.destroy();
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        super.service(req, res);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        dispatch(req, resp);
    }

    @Override
    protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        dispatch(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        dispatch(req, resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        dispatch(req, resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        dispatch(req, resp);
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        dispatch(req, resp);
    }

    @Override
    protected void doTrace(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        dispatch(req, resp);
    }

    private void dispatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpRequest request = adaptRequest(req);
        HttpResponse response = routes.invoke(request);
        returnResponse(response, resp);
    }

    private HttpRequest adaptRequest(HttpServletRequest servletRequest) throws IOException {
        return HttpRequest.builder()
                          .path(servletRequest.getRequestURI())
                          .httpMethod(servletRequest.getMethod())
                          .headers(adaptHeaders(servletRequest))
                          .body(adaptBody(servletRequest))
                          .build();
    }

    private Map<String, String> adaptHeaders(HttpServletRequest servletRequest) {
        return toStream(servletRequest.getHeaderNames())
                         .collect(toMap(h -> h, h -> toStream(servletRequest.getHeaders(h)).collect(joining(","))));
    }

    private <T> Stream<T> toStream(Enumeration<T> enumeration) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(new Iterator<T>() {
            @Override
            public boolean hasNext() {
                return enumeration.hasMoreElements();
            }

            @Override
            public T next() {
                return enumeration.nextElement();
            }
        }, Spliterator.ORDERED), false);
    }

    private byte[] adaptBody(HttpServletRequest servletRequest) throws IOException {
        ByteArrayOutputStream requestBody = new ByteArrayOutputStream();
        try (ServletInputStream requestStream = servletRequest.getInputStream()) {
            IOUtils.copy(requestStream, requestBody);
        }
        return requestBody.toByteArray();
    }

    private void returnResponse(HttpResponse response, HttpServletResponse servletResponse) {
        servletResponse.setStatus(response.statusCode());
        response.headers().forEach(servletResponse::setHeader);
        response.body().ifPresent(b -> {
            try (OutputStream stream = servletResponse.getOutputStream()){
                stream.write(b);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
    }
}
