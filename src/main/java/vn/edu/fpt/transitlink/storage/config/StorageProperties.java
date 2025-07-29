package vn.edu.fpt.transitlink.storage.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;

@Data
@Configuration
@ConfigurationProperties("storage")
public class StorageProperties {
    private String provider;
    private Path rootPath;
    private String baseUrl;
}

