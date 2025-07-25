package vn.edu.fpt.transitlink.shared.response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Slf4j
public class ErrorResponseBuilder {

    public static ResponseEntity<ErrorResponse> build(HttpStatus status, String message, String path, Exception ex) {
        if (status.is5xxServerError()) {
            log.error("[{}] {} - {}", status.value(), message, path, ex);
        } else {
            log.warn("[{}] {} - {}", status.value(), message, path);
        }

        ErrorResponse error = ErrorResponse.of(status.value(), status.getReasonPhrase(), message, path);
        return ResponseEntity.status(status).body(error);
    }

    // Optionally overload for use without exception
    public static ResponseEntity<ErrorResponse> build(HttpStatus status, String message, String path) {
        return build(status, message, path, null);
    }
}

