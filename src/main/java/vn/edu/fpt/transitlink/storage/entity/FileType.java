package vn.edu.fpt.transitlink.storage.entity;

import java.util.Set;

public enum FileType {
    IMAGE(Set.of("image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp")),
    DOCUMENT(Set.of("application/pdf", "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document")),
    SPREADSHEET(Set.of("text/csv", "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")),
    UNKNOWN(Set.of());

    private final Set<String> mimeTypes;

    FileType(Set<String> mimeTypes) { this.mimeTypes = mimeTypes; }

    public static FileType fromContentType(String contentType) {
        for (FileType type : values()) {
            if (type.mimeTypes.contains(contentType.toLowerCase())) {
                return type;
            }
        }
        return UNKNOWN;
    }

    public boolean isAllowed() { return this != UNKNOWN; }
}
