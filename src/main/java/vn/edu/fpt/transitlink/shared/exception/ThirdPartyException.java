package vn.edu.fpt.transitlink.shared.exception;

public class ThirdPartyException extends RuntimeException {
    private final String serviceName;
    private final int statusCode;
    private final String responseBody;

    public ThirdPartyException(String message, String serviceName, int statusCode, String responseBody) {
        super(message);
        this.statusCode = statusCode;
        this.responseBody = responseBody;
        this.serviceName = serviceName;
    }

    public ThirdPartyException(String message, String serviceName, int statusCode, String responseBody, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
        this.responseBody = responseBody;
        this.serviceName = serviceName;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public String getServiceName() {
        return serviceName;
    }
}
