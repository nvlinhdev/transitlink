package vn.edu.fpt.transitlink.storage.infrastructure;

import vn.edu.fpt.transitlink.storage.domain.model.FileInfo;
import java.io.InputStream;

public interface StorageProvider {
    void store(InputStream inputStream, FileInfo fileInfo);
    InputStream retrieve(FileInfo fileInfo);
    void delete(FileInfo fileInfo);
    String getPublicUrl(FileInfo fileInfo);
}
