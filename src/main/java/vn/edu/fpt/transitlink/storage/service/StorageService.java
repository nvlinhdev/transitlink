package vn.edu.fpt.transitlink.storage.service;

import org.springframework.web.multipart.MultipartFile;
import vn.edu.fpt.transitlink.storage.entity.FileInfo;

import java.io.InputStream;
import java.util.Optional;
import java.util.UUID;

public interface StorageService {
    public FileInfo uploadFile(MultipartFile multipartFile, UUID uploadedBy);
    public Optional<FileInfo> findById(UUID id);
    public InputStream downloadFile(UUID id);
    public void deleteFile(UUID id, UUID deletedBy);
}