package software.amazon.noggin.runtime.core.http.exception;

public class HttpException extends RuntimeException {
    private final int statusCode;
    private final String description;

    public HttpException(int statusCode, String description) {

        this.statusCode = statusCode;
        this.description = description;
    }

    public int statusCode() {
        return statusCode;
    }

    public String description() {
        return description;
    }
}
