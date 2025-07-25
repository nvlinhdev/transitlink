package vn.edu.fpt.transitlink.storage.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.transitlink.storage.domain.model.FileInfo;
import vn.edu.fpt.transitlink.storage.domain.model.FileType;
import vn.edu.fpt.transitlink.storage.domain.repository.FileInfoRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaFileInfoRepository extends JpaRepository<FileInfo, UUID>, FileInfoRepository {

    @Override
    Optional<FileInfo> findByIdAndDeletedFalse(UUID id);

    @Override
    List<FileInfo> findByUploadedByAndDeletedFalse(String uploadedBy);

    @Override
    List<FileInfo> findByFileTypeAndDeletedFalse(FileType fileType);
}

