package vn.edu.fpt.transitlink.storage.presentation;

import vn.edu.fpt.transitlink.shared.dto.StandardResponse;
import vn.edu.fpt.transitlink.storage.presentation.dto.FileResponse;
import vn.edu.fpt.transitlink.storage.presentation.dto.FileUrlResponse;
import vn.edu.fpt.transitlink.storage.application.StorageService;
import vn.edu.fpt.transitlink.storage.domain.model.FileInfo;
import vn.edu.fpt.transitlink.storage.domain.model.FileType;
import vn.edu.fpt.transitlink.storage.infrastructure.StorageProvider;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/storage")
public class StorageController {

    private final StorageService storageService;
    private final StorageProvider storageProvider;

    public StorageController(StorageService storageService, StorageProvider storageProvider) {
        this.storageService = storageService;
        this.storageProvider = storageProvider;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StandardResponse<FileResponse>> uploadFile(
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {

        String userId = authentication.getName();
        FileInfo fileInfo = storageService.uploadFile(file, userId);
        return ResponseEntity
                .status(201)
                .body(StandardResponse.success(FileResponse.from(fileInfo)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FileResponse> getFileInfo(@PathVariable UUID id) {
        return storageService.findById(id)
                .map(file -> ResponseEntity.ok(FileResponse.from(file)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/files/{id}")
    public void downloadFile(@PathVariable UUID id, HttpServletResponse response) {
        try {
            FileInfo fileInfo = storageService.findById(id)
                    .orElseThrow(() -> new RuntimeException("File not found"));

            try (InputStream inputStream = storageService.downloadFile(id)) {
                response.setContentType(fileInfo.getContentType());
                response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + URLEncoder.encode(fileInfo.getOriginalName(), StandardCharsets.UTF_8) + "\"");

                StreamUtils.copy(inputStream, response.getOutputStream());
                response.flushBuffer();
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @GetMapping("/files/{id}/preview")
    public void previewFile(@PathVariable UUID id, HttpServletResponse response) {
        try {
            FileInfo fileInfo = storageService.findById(id)
                    .orElseThrow(() -> new RuntimeException("File not found"));

            if (!fileInfo.getContentType().startsWith("image/") &&
                    !fileInfo.getContentType().equals("application/pdf")) {
                response.setStatus(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
                return;
            }

            try (InputStream inputStream = storageService.downloadFile(id)) {
                response.setContentType(fileInfo.getContentType());
                response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "inline");

                StreamUtils.copy(inputStream, response.getOutputStream());
                response.flushBuffer();
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFile(@PathVariable UUID id, Authentication authentication) {
        try {
            String userId = authentication.getName();
            storageService.deleteFile(id, userId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/my-files")
    public ResponseEntity<List<FileResponse>> getUserFiles(Authentication authentication) {
        String userId = authentication.getName();
        List<FileInfo> files = storageService.getUserFiles(userId);
        List<FileResponse> response = files.stream().map(FileResponse::from).toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/files")
    public ResponseEntity<List<FileResponse>> getFilesByType(@RequestParam FileType type) {
        List<FileInfo> files = storageService.getFilesByType(type);
        List<FileResponse> response = files.stream().map(FileResponse::from).toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/url")
    public ResponseEntity<FileUrlResponse> getFileUrl(@PathVariable UUID id) {
        return storageService.findById(id)
                .map(file -> {
                    String url = storageProvider.getPublicUrl(file);
                    return ResponseEntity.ok(new FileUrlResponse(url));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
