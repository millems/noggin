package software.amazon.noggin.runtime.core.serialization;

import java.io.InputStream;

public interface Unmarshaller<T> {
    T unmarshal(InputStream input);
}
