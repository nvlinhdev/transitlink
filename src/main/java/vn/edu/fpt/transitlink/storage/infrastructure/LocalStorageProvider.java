package vn.edu.fpt.transitlink.storage.infrastructure;

import lombok.extern.slf4j.Slf4j;
import vn.edu.fpt.transitlink.shared.exception.BusinessException;
import vn.edu.fpt.transitlink.shared.exception.ErrorCode;
import vn.edu.fpt.transitlink.storage.domain.model.FileInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.time.format.DateTimeFormatter;

@Component
@ConditionalOnProperty(name = "storage.provider", havingValue = "local", matchIfMissing = true)
@Slf4j
public class LocalStorageProvider implements StorageProvider {

    private final Path rootPath;
    private final String baseUrl;

    public LocalStorageProvider(@Value("${storage.root-path:./storage}") String rootPath,
                                @Value("${storage.base-url:http://localhost:8888/api/storage/files}") String baseUrl) {
        this.rootPath = Paths.get(rootPath);
        this.baseUrl = baseUrl;
        log.info("Initializing LocalStorageProvider with path: {}", this.rootPath.toAbsolutePath());

        initStorage();
    }

    private void initStorage() {
        try {
            Files.createDirectories(rootPath);
            log.info("Storage directory initialized at: {}", rootPath.toAbsolutePath());
        } catch (IOException e) {
            log.error("Failed to create storage directory at {}: {}", rootPath.toAbsolutePath(), e.getMessage());
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Failed to create storage directory", e);
        }
    }

    @Override
    public void store(InputStream inputStream, FileInfo fileInfo) {
        try {
            Path filePath = getFilePath(fileInfo);
            Files.createDirectories(filePath.getParent());
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Failed to store file", e);
        }
    }

    @Override
    public InputStream retrieve(FileInfo fileInfo) {
        try {
            Path filePath = getFilePath(fileInfo);
            return Files.newInputStream(filePath);
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Failed to retrieve file", e);
        }
    }

    @Override
    public void delete(FileInfo fileInfo) {
        try {
            Path filePath = getFilePath(fileInfo);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Failed to delete file", e);
        }
    }

    @Override
    public String getPublicUrl(FileInfo fileInfo) {
        return baseUrl + "/" + fileInfo.getId();
    }

    private Path getFilePath(FileInfo fileInfo) {
        String dateFolder = fileInfo.getUploadedAt().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        return rootPath.resolve(dateFolder).resolve(fileInfo.getFileType().name().toLowerCase())
                .resolve(fileInfo.getStoredName());
    }
}

