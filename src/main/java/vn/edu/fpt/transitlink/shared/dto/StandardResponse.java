package vn.edu.fpt.transitlink.shared.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.http.HttpStatus;
import vn.edu.fpt.transitlink.shared.util.TimeUtil;

public record StandardResponse<T>(
        boolean success,
        String message,
        T data,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss 'UTC'")
        String timestamp,
        int statusCode
) {
    // Constructor với timestamp tự động
    public StandardResponse(boolean success, String message, T data, int statusCode) {
        this(success, message, data, TimeUtil.now(), statusCode);
    }

    // Success factory methods
    public static <T> StandardResponse<T> success(T data) {
        return new StandardResponse<>(true, "Success", data, HttpStatus.OK.value());
    }

    public static <T> StandardResponse<T> success(String message, T data) {
        return new StandardResponse<>(true, message, data, HttpStatus.OK.value());
    }

    public static <T> StandardResponse<T> created(T data) {
        return new StandardResponse<>(true, "Created successfully", data, HttpStatus.CREATED.value());
    }

    public static <T> StandardResponse<T> created(String message, T data) {
        return new StandardResponse<>(true, message, data, HttpStatus.CREATED.value());
    }

    public static <T> StandardResponse<T> noContent() {
        return new StandardResponse<>(true, "No content", null, HttpStatus.NO_CONTENT.value());
    }

    public static <T> StandardResponse<T> noContent(String message) {
        return new StandardResponse<>(true, message, null, HttpStatus.NO_CONTENT.value());
    }
}
