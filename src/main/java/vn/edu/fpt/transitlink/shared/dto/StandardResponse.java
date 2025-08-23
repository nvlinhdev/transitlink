package vn.edu.fpt.transitlink.shared.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;
import vn.edu.fpt.transitlink.shared.util.TimeUtil;

@Schema(name = "StandardResponse", description = "Generic standard response wrapper")
public record StandardResponse<T>(

        @Schema(description = "Indicates if request was successful")
        boolean success,

        @Schema(description = "Message describing the result")
        String message,

        @Schema(description = "Returned data, can be any type")
        T data,

        @Schema(description = "Timestamp when the response was generated")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss 'UTC'")
        String timestamp
) {
    // Constructor với timestamp tự động
    public StandardResponse(boolean success, String message, T data) {
        this(success, message, data, TimeUtil.now());
    }

    // Success factory methods
    public static <T> StandardResponse<T> success(T data) {
        return new StandardResponse<>(true, "Success", data);
    }

    public static <T> StandardResponse<T> success(String message, T data) {
        return new StandardResponse<>(true, message, data);
    }

    public static <T> StandardResponse<T> created(T data) {
        return new StandardResponse<>(true, "Created successfully", data);
    }

    public static <T> StandardResponse<T> created(String message, T data) {
        return new StandardResponse<>(true, message, data);
    }
    public static <T> StandardResponse<T> noContent(String message) {
        return new StandardResponse<>(true, message, null);
    }
}