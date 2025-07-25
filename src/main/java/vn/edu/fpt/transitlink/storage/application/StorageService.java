package vn.edu.fpt.transitlink.storage.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.transitlink.storage.domain.exception.*;
import vn.edu.fpt.transitlink.storage.domain.model.FileInfo;
import vn.edu.fpt.transitlink.storage.domain.model.FileType;
import vn.edu.fpt.transitlink.storage.domain.repository.FileInfoRepository;
import vn.edu.fpt.transitlink.storage.infrastructure.FileProcessor;
import vn.edu.fpt.transitlink.storage.infrastructure.StorageProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class StorageService {

    private static final Logger logger = LoggerFactory.getLogger(StorageService.class);

    private final FileInfoRepository fileInfoRepository;
    private final StorageProvider storageProvider;
    private final FileProcessor fileProcessor;

    public StorageService(FileInfoRepository fileInfoRepository,
                          StorageProvider storageProvider,
                          FileProcessor fileProcessor) {
        this.fileInfoRepository = fileInfoRepository;
        this.storageProvider = storageProvider;
        this.fileProcessor = fileProcessor;
    }

    public FileInfo uploadFile(MultipartFile multipartFile, String uploadedBy) {
        validateFile(multipartFile);

        try {
            // Create file info entity
            FileInfo fileInfo = FileInfo.create(
                    multipartFile.getOriginalFilename(),
                    multipartFile.getContentType(),
                    multipartFile.getSize(),
                    uploadedBy
            );

            // Process file if needed (resize, compress)
            InputStream processedStream = multipartFile.getInputStream();
            if (fileInfo.isImage()) {
                processedStream = fileProcessor.processImage(processedStream, fileInfo.getContentType());
            }

            // Store file physically
            storageProvider.store(processedStream, fileInfo);

            // Save metadata to database
            FileInfo savedFileInfo = fileInfoRepository.save(fileInfo);

            logger.info("File uploaded: {} by {}", fileInfo.getOriginalName(), uploadedBy);
            return savedFileInfo;

        } catch (Exception e) {
            logger.error("Upload failed: {}", multipartFile.getOriginalFilename(), e);
            throw new StorageException("Upload failed: " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public Optional<FileInfo> findById(UUID id) {
        return fileInfoRepository.findByIdAndDeletedFalse(id);
    }

    @Transactional(readOnly = true)
    public InputStream downloadFile(UUID id) {
        FileInfo fileInfo = fileInfoRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new FileNotFoundException("File not found: " + id));

        try {
            return storageProvider.retrieve(fileInfo);
        } catch (Exception e) {
            throw new StorageException("Download failed: " + e.getMessage(), e);
        }
    }

    public void deleteFile(UUID id, String deletedBy) {
        FileInfo fileInfo = fileInfoRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new FileNotFoundException("File not found: " + id));

        try {
            // Soft delete in database
            fileInfo.markAsDeleted();
            fileInfoRepository.save(fileInfo);

            // Delete physical file
            storageProvider.delete(fileInfo);

            logger.info("File deleted: {} by {}", fileInfo.getOriginalName(), deletedBy);
        } catch (Exception e) {
            throw new StorageException("Delete failed: " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public List<FileInfo> getUserFiles(String userId) {
        return fileInfoRepository.findByUploadedByAndDeletedFalse(userId);
    }

    @Transactional(readOnly = true)
    public List<FileInfo> getFilesByType(FileType type) {
        return fileInfoRepository.findByFileTypeAndDeletedFalse(type);
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new InvalidFileException("File is empty");
        }

        FileType type = FileType.fromContentType(file.getContentType());
        if (!type.isAllowed()) {
            throw new InvalidFileException("File type not allowed: " + file.getContentType());
        }

        long maxSize = getMaxSizeForType(type);
        if (file.getSize() > maxSize) {
            throw new FileSizeExceededException(
                    String.format("File size %d exceeds limit %d", file.getSize(), maxSize));
        }
    }

    private long getMaxSizeForType(FileType type) {
        return switch (type) {
            case IMAGE -> 10 * 1024 * 1024; // 10MB
            case DOCUMENT -> 50 * 1024 * 1024; // 50MB
            case SPREADSHEET -> 20 * 1024 * 1024; // 20MB
            case UNKNOWN -> 5 * 1024 * 1024; // 5MB
        };
    }
}