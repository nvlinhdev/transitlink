package vn.edu.fpt.transitlink.storage.infrastructure;

import vn.edu.fpt.transitlink.storage.domain.model.FileInfo;
import vn.edu.fpt.transitlink.storage.domain.exception.StorageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.time.format.DateTimeFormatter;

@Component
@ConditionalOnProperty(name = "storage.provider", havingValue = "local", matchIfMissing = true)
public class LocalStorageProvider implements StorageProvider {

    private final Path rootPath;
    private final String baseUrl;

    public LocalStorageProvider(@Value("${storage.root-path:./storage}") String rootPath,
                                @Value("${storage.base-url:http://localhost:8888/api/storage/files}") String baseUrl) {
        this.rootPath = Paths.get(rootPath);
        this.baseUrl = baseUrl;
        initStorage();
    }

    private void initStorage() {
        try {
            Files.createDirectories(rootPath);
        } catch (IOException e) {
            throw new StorageException("Failed to create storage directory", e);
        }
    }

    @Override
    public void store(InputStream inputStream, FileInfo fileInfo) {
        try {
            Path filePath = getFilePath(fileInfo);
            Files.createDirectories(filePath.getParent());
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new StorageException("Failed to store file", e);
        }
    }

    @Override
    public InputStream retrieve(FileInfo fileInfo) {
        try {
            Path filePath = getFilePath(fileInfo);
            return Files.newInputStream(filePath);
        } catch (IOException e) {
            throw new StorageException("Failed to retrieve file", e);
        }
    }

    @Override
    public void delete(FileInfo fileInfo) {
        try {
            Path filePath = getFilePath(fileInfo);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new StorageException("Failed to delete file", e);
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

