package vn.edu.fpt.transitlink.storage.provider;

import vn.edu.fpt.transitlink.storage.entity.FileInfo;
import java.io.InputStream;

public interface StorageProvider {
    FileInfo store(InputStream inputStream, FileInfo fileInfo);
    InputStream retrieve(FileInfo fileInfo);
    void delete(FileInfo fileInfo);
    String getPublicUrl(FileInfo fileInfo);
}
