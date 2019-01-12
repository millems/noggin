package software.amazon.noggin.runtime.core.serialization;

import java.io.OutputStream;

public interface Marshaller<T> {
    OutputStream marshal(T input);
}
