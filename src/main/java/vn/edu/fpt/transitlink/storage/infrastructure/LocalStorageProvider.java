package vn.edu.fpt.transitlink.storage.infrastructure;

import lombok.extern.slf4j.Slf4j;
import vn.edu.fpt.transitlink.shared.config.AppProperties;
import vn.edu.fpt.transitlink.shared.exception.BusinessException;
import vn.edu.fpt.transitlink.storage.domain.exception.StorageErrorCode;
import vn.edu.fpt.transitlink.storage.domain.model.FileInfo;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Component
@ConditionalOnProperty(name = "storage.provider", havingValue = "local", matchIfMissing = true)
@Slf4j
public class LocalStorageProvider implements StorageProvider {

    private final AppProperties props;

    public LocalStorageProvider(AppProperties props) {
        this.props = props;
        log.info("Initializing LocalStorageProvider with root: {}", props.storage().rootPath().toAbsolutePath());
        initStorage();
    }

    private void initStorage() {
        try {
            Files.createDirectories(props.storage().rootPath());
            log.info("Storage root initialized at: {}", props.storage().rootPath().toAbsolutePath());
        } catch (IOException e) {
            log.error("Cannot create storage root at {}: {}", props.storage().rootPath(), e.getMessage());
            throw new BusinessException(StorageErrorCode.STORAGE_INITIALIZATION_FAILED, "Could not initialize storage directory", e);
        }
    }

    @Override
    public FileInfo store(InputStream inputStream, FileInfo fileInfo) {
        Path filePath = getAbsolutePath(fileInfo);
        try {
            Files.createDirectories(filePath.getParent());
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            log.info("Stored file at: {}", filePath);
            String publicUrl = getPublicUrl(fileInfo);
            return fileInfo.withUrl(publicUrl);
        } catch (IOException e) {
            log.error("Failed to store file at {}: {}", filePath, e.getMessage());
            throw new BusinessException(StorageErrorCode.FILE_STORE_FAILED, "Failed to store file", e);
        }
    }

    @Override
    public InputStream retrieve(FileInfo fileInfo) {
        Path filePath = getAbsolutePath(fileInfo);
        try {
            if (!Files.exists(filePath)) {
                log.warn("File not found: {}", filePath);
                throw new BusinessException(StorageErrorCode.FILE_NOT_FOUND, "File not found: " + filePath);
            }
            return Files.newInputStream(filePath, StandardOpenOption.READ);
        } catch (IOException e) {
            throw new BusinessException(StorageErrorCode.FILE_RETRIEVE_FAILED, "Failed to retrieve file: " + filePath, e);
        }
    }

    @Override
    public void delete(FileInfo fileInfo) {
        Path filePath = getAbsolutePath(fileInfo);
        try {
            Files.deleteIfExists(filePath);
            log.info("Deleted file: {}", filePath);
        } catch (IOException e) {
            log.error("Failed to delete file {}: {}", filePath, e.getMessage());
            throw new BusinessException(StorageErrorCode.FILE_DELETE_FAILED, "Failed to delete file: " + filePath, e);
        }
    }

    @Override
    public String getPublicUrl(FileInfo fileInfo) {
        String relativePath = getRelativePath(fileInfo).toString().replace("\\", "/");
        return props.storage().baseUrl() + "/" + relativePath;
    }

    private Path getRelativePath(FileInfo fileInfo) {
        LocalDate uploadDate = fileInfo.getUploadedAt()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        String dateFolder = DateTimeFormatter.ofPattern("yyyy/MM/dd").format(uploadDate);

        return Paths.get(dateFolder)
                .resolve(fileInfo.getFileType().name().toLowerCase())
                .resolve(fileInfo.getStoredName());
    }

    private Path getAbsolutePath(FileInfo fileInfo) {
        return props.storage().rootPath().resolve(getRelativePath(fileInfo));
    }
}
