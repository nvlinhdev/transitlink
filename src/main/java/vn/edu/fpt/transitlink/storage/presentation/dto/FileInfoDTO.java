package vn.edu.fpt.transitlink.storage.presentation.dto;

import vn.edu.fpt.transitlink.storage.domain.model.FileInfo;
import vn.edu.fpt.transitlink.storage.domain.model.FileType;

import java.util.UUID;


public record FileInfoDTO(
        UUID id,
        String originalName,
        String contentType,
        long size,
        FileType fileType,
        String url,
        UUID uploadedBy
) {
    public static FileInfoDTO from(FileInfo fileInfo) {
        return new FileInfoDTO(
                fileInfo.getId(),
                fileInfo.getOriginalName(),
                fileInfo.getContentType(),
                fileInfo.getSize(),
                fileInfo.getFileType(),
                fileInfo.getUrl(),
                fileInfo.getUploadedBy()
        );
    }
}
