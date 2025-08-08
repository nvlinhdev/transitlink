package vn.edu.fpt.transitlink.storage.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "file_info")
@NoArgsConstructor
@Getter
public class FileInfo {
    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;
    @Column(name = "original_name", nullable = false)
    private String originalName;
    @Column(name = "stored_name", nullable = false, unique = true)
    private String storedName;
    @Column(name = "content_type", nullable = false)
    private String contentType;
    @Column(name = "size", nullable = false)
    private long size;
    @Enumerated(EnumType.STRING)
    @Column(name = "file_type", nullable = false)
    private FileType fileType;
    @Column(name = "url", nullable = false)
    private String url;
    @Column(name = "uploaded_by", nullable = false)
    private UUID uploadedBy;
    @Column(name = "uploaded_at", nullable = false)
    private Instant uploadedAt;
    private FileInfo(UUID id, String originalName, String storedName, String contentType,
                     long size, FileType fileType, UUID uploadedBy, Instant uploadedAt) {
        this.id = id;
        this.originalName = originalName;
        this.storedName = storedName;
        this.contentType = contentType;
        this.size = size;
        this.fileType = fileType;
        this.uploadedBy = uploadedBy;
        this.uploadedAt = uploadedAt;
    }
    public static FileInfo create(String originalName, String contentType, long size, UUID uploadedBy) {
        UUID id = UUID.randomUUID();
        String extension = getFileExtension(originalName);
        String storedName = id + (extension.isEmpty() ? "" : "." + extension);
        FileType fileType = FileType.fromContentType(contentType);

        return new FileInfo(id, originalName, storedName, contentType, size,
                fileType, uploadedBy, Instant.now());
    }
    public FileInfo withUrl(String url) {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("URL must not be blank");
        }
        this.url = url;
        return this;
    }
    private static String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        return lastDotIndex > 0 ? filename.substring(lastDotIndex + 1) : "";
    }
}