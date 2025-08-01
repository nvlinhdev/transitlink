package vn.edu.fpt.transitlink.storage.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vn.edu.fpt.transitlink.shared.exception.BusinessException;
import vn.edu.fpt.transitlink.storage.entity.FileInfo;
import vn.edu.fpt.transitlink.storage.entity.FileType;
import vn.edu.fpt.transitlink.storage.exception.StorageErrorCode;
import vn.edu.fpt.transitlink.storage.processor.FileProcessor;
import vn.edu.fpt.transitlink.storage.provider.StorageProvider;
import vn.edu.fpt.transitlink.storage.repository.FileInfoRepository;
import vn.edu.fpt.transitlink.storage.service.StorageService;

import java.io.InputStream;
import java.util.Optional;
import java.util.UUID;

@Service
public class StorageServiceImpl implements StorageService {
    private static final Logger logger = LoggerFactory.getLogger(StorageService.class);
    private final FileInfoRepository fileInfoRepository;
    private final StorageProvider storageProvider;
    private final FileProcessor fileProcessor;

    public StorageServiceImpl(FileInfoRepository fileInfoRepository,
                          StorageProvider storageProvider,
                          FileProcessor fileProcessor) {
        this.fileInfoRepository = fileInfoRepository;
        this.storageProvider = storageProvider;
        this.fileProcessor = fileProcessor;
    }

    @Override
    public FileInfo uploadFile(MultipartFile multipartFile, UUID uploadedBy) {
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
            if (fileInfo.getFileType() == FileType.IMAGE) {
                processedStream = fileProcessor.processImage(processedStream, fileInfo.getContentType());
            }

            // Store file physically
            fileInfo = storageProvider.store(processedStream, fileInfo);

            // Save metadata to database
            FileInfo savedFileInfo = fileInfoRepository.save(fileInfo);

            logger.info("File uploaded: {} by {}", fileInfo.getOriginalName(), uploadedBy);
            return savedFileInfo;

        } catch (Exception e) {
            logger.error("Upload failed: {}", multipartFile.getOriginalFilename(), e);
            throw new BusinessException(StorageErrorCode.FILE_UPLOAD_FAILED,
                    "Failed to upload file: " + multipartFile.getOriginalFilename(), e);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<FileInfo> findById(UUID id) {
        return fileInfoRepository.findById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public InputStream downloadFile(UUID id) {
        FileInfo fileInfo = fileInfoRepository.findById(id)
                .orElseThrow(() -> new BusinessException(StorageErrorCode.FILE_NOT_FOUND, "File not found: " + id));

        try {
            return storageProvider.retrieve(fileInfo);
        } catch (Exception e) {
            throw new BusinessException(StorageErrorCode.FILE_DOWNLOAD_FAILED,
                    "Failed to download file: " + fileInfo.getOriginalName(), e);
        }
    }

    @Override
    public void deleteFile(UUID id, UUID deletedBy) {
        FileInfo fileInfo = fileInfoRepository.findById(id)
                .orElseThrow(() -> new BusinessException(StorageErrorCode.FILE_NOT_FOUND, "File not found: " + id));

        if (!fileInfo.getUploadedBy().equals(deletedBy)) {
            throw new BusinessException(StorageErrorCode.NOT_PERMITTED);
        }

        try {
            // delete file metadata
            fileInfoRepository.delete(fileInfo);

            // Delete physical file
            storageProvider.delete(fileInfo);

            logger.info("File deleted: {} by {}", fileInfo.getOriginalName(), deletedBy);
        } catch (Exception e) {
            logger.error("Failed to delete file {} by {}: {}", fileInfo.getOriginalName(), deletedBy, e);
            throw new BusinessException(StorageErrorCode.FILE_DELETE_FAILED,
                    "Failed to delete file: " + fileInfo.getOriginalName(), e);
        }
    }
    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException(StorageErrorCode.FILE_EMPTY);
        }

        FileType type = FileType.fromContentType(file.getContentType());
        if (!type.isAllowed()) {
            throw new BusinessException(StorageErrorCode.FILE_NOT_SUPPORTED,
                    String.format("File type %s is not allowed", file.getContentType()));
        }

        long maxSize = getMaxSizeForType(type);
        if (file.getSize() > maxSize) {
            throw new BusinessException(StorageErrorCode.EXCEEDED_MAX_FILE_SIZE,
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
