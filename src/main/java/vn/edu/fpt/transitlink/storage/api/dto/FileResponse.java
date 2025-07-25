package vn.edu.fpt.transitlink.storage.api.dto;

import vn.edu.fpt.transitlink.storage.domain.model.FileInfo;
import vn.edu.fpt.transitlink.storage.domain.model.FileType;

import java.util.UUID;

public record FileResponse(
        UUID id,
        String originalName,
        String contentType,
        long size,
        FileType fileType,
        String uploadedBy
) {
    public static FileResponse from(FileInfo fileInfo) {
        return new FileResponse(
                fileInfo.getId(),
                fileInfo.getOriginalName(),
                fileInfo.getContentType(),
                fileInfo.getSize(),
                fileInfo.getFileType(),
                fileInfo.getUploadedBy()
        );
    }
}
