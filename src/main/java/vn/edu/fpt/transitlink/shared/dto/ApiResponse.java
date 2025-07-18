package vn.edu.fpt.transitlink.shared.dto;

import java.time.LocalDateTime;

public record ApiResponse<T>(
        boolean success,
        T data,
        Meta meta
) {
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, Meta.now());
    }

    public record Meta(LocalDateTime timestamp) {
        public static Meta now() {
            return new Meta(LocalDateTime.now());
        }
    }
}