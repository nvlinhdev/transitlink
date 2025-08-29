package vn.edu.fpt.transitlink.shared.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;
import vn.edu.fpt.transitlink.shared.util.TimeUtil;

import java.util.List;

@Schema(name = "PaginatedResponse", description = "Paginated response with metadata")
public record PaginatedResponse<T>(

        @Schema(description = "Indicates if request was successful")
        boolean success,

        @Schema(description = "Message describing the result")
        String message,

        @Schema(description = "List of items returned for this page")
        List<T> data,

        @Schema(description = "Timestamp when the response was generated")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss 'UTC'")
        String timestamp,

        // Pagination metadata
        @Schema(description = "Current page number (0-based)")
        int page,

        @Schema(description = "Size of each page")
        int size,

        @Schema(description = "Total number of elements across all pages")
        long totalElements,

        @Schema(description = "Total number of pages")
        int totalPages,

        @Schema(description = "Whether there is a next page")
        boolean hasNext,

        @Schema(description = "Whether there is a previous page")
        boolean hasPrevious,

        @Schema(description = "Whether this is the first page")
        boolean isFirst,

        @Schema(description = "Whether this is the last page")
        boolean isLast
) {
    // Constructor với tính toán tự động - handle edge cases
    public PaginatedResponse(List<T> data, int page, int size, long totalElements) {
        this(
                true,
                "Success",
                data,
                TimeUtil.now(),
                page,
                size,
                totalElements,
                calculateTotalPages(totalElements, size),
                calculateHasNext(page, totalElements, size),
                page > 0,
                page == 0,
                calculateIsLast(page, totalElements, size)
        );
    }

    // Constructor với custom message
    public PaginatedResponse(String message, List<T> data, int page, int size, long totalElements) {
        this(
                true,
                message,
                data,
                TimeUtil.now(),
                page,
                size,
                totalElements,
                calculateTotalPages(totalElements, size),
                calculateHasNext(page, totalElements, size),
                page > 0,
                page == 0,
                calculateIsLast(page, totalElements, size)
        );
    }

    // Helper methods for calculations
    private static int calculateTotalPages(long totalElements, int size) {
        return size > 0 ? (int) Math.ceil((double) totalElements / size) : 0;
    }

    private static boolean calculateHasNext(int page, long totalElements, int size) {
        if (size <= 0) return false;
        int totalPages = calculateTotalPages(totalElements, size);
        return page < totalPages - 1;
    }

    private static boolean calculateIsLast(int page, long totalElements, int size) {
        if (totalElements == 0) return true;
        if (size <= 0) return true;
        int totalPages = calculateTotalPages(totalElements, size);
        return page >= totalPages - 1;
    }

    // Static factory method cho Spring Data Page - recommended
    public static <T> PaginatedResponse<T> from(Page<T> page) {
        return new PaginatedResponse<>(
                true,
                "Success",
                page.getContent(),
                TimeUtil.now(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.hasNext(),
                page.hasPrevious(),
                page.isFirst(),
                page.isLast()
        );
    }

    // Static factory method với custom message
    public static <T> PaginatedResponse<T> from(String message, Page<T> page) {
        return new PaginatedResponse<>(
                true,
                message,
                page.getContent(),
                TimeUtil.now(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.hasNext(),
                page.hasPrevious(),
                page.isFirst(),
                page.isLast()
        );
    }

    // Static factory method cho empty result
    public static <T> PaginatedResponse<T> empty(int page, int size) {
        return new PaginatedResponse<>(
                List.of(),
                page,
                size,
                0L
        );
    }

    // Static factory method cho empty result với custom message
    public static <T> PaginatedResponse<T> empty(String message, int page, int size) {
        return new PaginatedResponse<>(
                message,
                List.of(),
                page,
                size,
                0L
        );
    }
}