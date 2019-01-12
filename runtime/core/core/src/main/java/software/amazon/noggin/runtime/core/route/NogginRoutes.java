package software.amazon.noggin.runtime.core.route;

public interface NogginRoutes<I, O> {
    O invoke(I request);
}
