package vn.edu.fpt.transitlink.storage.domain.repository;

import vn.edu.fpt.transitlink.storage.domain.model.FileInfo;
import vn.edu.fpt.transitlink.storage.domain.model.FileType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FileInfoRepository {
    FileInfo save(FileInfo fileInfo);
    Optional<FileInfo> findById(UUID id);
    Optional<FileInfo> findByIdAndDeletedFalse(UUID id);
    List<FileInfo> findByUploadedByAndDeletedFalse(String uploadedBy);
    List<FileInfo> findByFileTypeAndDeletedFalse(FileType fileType);
    void delete(FileInfo fileInfo);
}

