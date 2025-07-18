package vn.edu.fpt.transitlink.shared.dto;

import java.util.List;

public record PaginationResponse<T>(
        List<T> data,
        Pagination pagination
) {
    public record Pagination(
            int page,
            int size,
            long totalElements,
            int totalPages,
            boolean hasNext,
            boolean hasPrevious
    ) {}
}