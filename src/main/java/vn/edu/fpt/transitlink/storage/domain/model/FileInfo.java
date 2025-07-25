package vn.edu.fpt.transitlink.storage.domain.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "file_info")
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

    @Column(name = "uploaded_by", nullable = false)
    private String uploadedBy;

    @Column(name = "uploaded_at", nullable = false)
    private LocalDateTime uploadedAt;

    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // Constructors
    protected FileInfo() {}

    private FileInfo(UUID id, String originalName, String storedName, String contentType,
                     long size, FileType fileType, String uploadedBy, LocalDateTime uploadedAt) {
        this.id = id;
        this.originalName = originalName;
        this.storedName = storedName;
        this.contentType = contentType;
        this.size = size;
        this.fileType = fileType;
        this.uploadedBy = uploadedBy;
        this.uploadedAt = uploadedAt;
        this.deleted = false;
    }

    public static FileInfo create(String originalName, String contentType, long size, String uploadedBy) {
        UUID id = UUID.randomUUID();
        String extension = getFileExtension(originalName);
        String storedName = id.toString() + (extension.isEmpty() ? "" : "." + extension);
        FileType fileType = FileType.fromContentType(contentType);

        return new FileInfo(id, originalName, storedName, contentType, size,
                fileType, uploadedBy, LocalDateTime.now());
    }

    private static String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        return lastDotIndex > 0 ? filename.substring(lastDotIndex + 1) : "";
    }

    public void markAsDeleted() {
        this.deleted = true;
        this.deletedAt = LocalDateTime.now();
    }

    // Getters
    public UUID getId() { return id; }
    public String getOriginalName() { return originalName; }
    public String getStoredName() { return storedName; }
    public String getContentType() { return contentType; }
    public long getSize() { return size; }
    public FileType getFileType() { return fileType; }
    public String getUploadedBy() { return uploadedBy; }
    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public boolean isDeleted() { return deleted; }
    public LocalDateTime getDeletedAt() { return deletedAt; }

    // Business methods
    public boolean isImage() { return fileType == FileType.IMAGE; }
    public boolean isDocument() { return fileType == FileType.DOCUMENT; }
    public boolean isSpreadsheet() { return fileType == FileType.SPREADSHEET; }
}