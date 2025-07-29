package vn.edu.fpt.transitlink.storage.domain.repository;

import vn.edu.fpt.transitlink.storage.domain.model.FileInfo;

import java.util.Optional;
import java.util.UUID;

public interface FileInfoRepository {
    FileInfo save(FileInfo fileInfo);
    Optional<FileInfo> findById(UUID id);
    void delete(FileInfo fileInfo);
}

