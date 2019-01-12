package software.amazon.noggin.runtime.core.http.exception;

import software.amazon.noggin.runtime.core.http.exception.HttpException;

public class ResourceNotFoundException extends HttpException {
    public ResourceNotFoundException() {
        super(404, "Not Found");
    }
}
