package vn.edu.fpt.transitlink.shared.dto;

public record ValidationError(
        String field,
        String message,
        Object rejectedValue
) {}