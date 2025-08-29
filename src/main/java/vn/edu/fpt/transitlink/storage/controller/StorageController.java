package vn.edu.fpt.transitlink.storage.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import vn.edu.fpt.transitlink.shared.dto.StandardResponse;
import vn.edu.fpt.transitlink.shared.exception.BusinessException;
import vn.edu.fpt.transitlink.storage.exception.StorageErrorCode;
import vn.edu.fpt.transitlink.storage.dto.FileInfoDTO;
import vn.edu.fpt.transitlink.storage.service.StorageService;
import vn.edu.fpt.transitlink.storage.entity.FileInfo;

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
import java.util.UUID;

@RestController
@RequestMapping("/api/storage")
@Tag(name = "Storage API", description = "Manage file storage including upload, preview, download, and deletion")
public class StorageController {

    private final StorageService storageService;

    public StorageController(StorageService storageService) {
        this.storageService = storageService;
    }

    @Operation(summary = "Upload a file", description = "Upload a new file and associate it with the authenticated user.")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StandardResponse<FileInfoDTO>> uploadFile(
            @Parameter(description = "File to be uploaded", required = true)
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {

        UUID userId = UUID.fromString(authentication.getName());
        try {
            FileInfo fileInfo = storageService.uploadFile(file, userId);
            return ResponseEntity
                    .status(201)
                    .body(StandardResponse.created(FileInfoDTO.from(fileInfo)));
        } catch (Exception e) {
            throw new BusinessException(StorageErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    @Operation(summary = "Get file info", description = "Retrieve infomation of a file by its ID.")
    @GetMapping("/{id}")
    public ResponseEntity<StandardResponse<FileInfoDTO>> getFileInfo(
            @Parameter(description = "File ID", required = true)
            @PathVariable UUID id) {

        FileInfo file = storageService.findById(id)
                .orElseThrow(() -> new BusinessException(StorageErrorCode.FILE_NOT_FOUND));
        return ResponseEntity.ok(StandardResponse.success(FileInfoDTO.from(file)));
    }

    @Operation(summary = "Download a file", description = "Download the binary content of a file by its ID.")
    public void downloadFile(
            @Parameter(description = "File ID", required = true)
            @PathVariable UUID id,
            HttpServletResponse response) {

        FileInfo fileInfo = storageService.findById(id)
                .orElseThrow(() -> new BusinessException(StorageErrorCode.FILE_NOT_FOUND));
        try (InputStream inputStream = storageService.downloadFile(id)) {
            response.setContentType(fileInfo.getContentType());
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + URLEncoder.encode(fileInfo.getOriginalName(), StandardCharsets.UTF_8) + "\"");
            StreamUtils.copy(inputStream, response.getOutputStream());
            response.flushBuffer();
        } catch (Exception e) {
            throw new BusinessException(StorageErrorCode.FILE_DOWNLOAD_FAILED);
        }
    }

    @GetMapping("/{id}/preview")
    public void previewFile(
            @Parameter(description = "File ID", required = true)
            @PathVariable UUID id,
            HttpServletResponse response) {

        FileInfo fileInfo = storageService.findById(id)
                .orElseThrow(() -> new BusinessException(StorageErrorCode.FILE_NOT_FOUND));

        if (!fileInfo.getContentType().startsWith("image/") &&
                !fileInfo.getContentType().equals("application/pdf")) {
            throw new BusinessException(StorageErrorCode.FILE_NOT_SUPPORTED);
        }

        try (InputStream inputStream = storageService.downloadFile(id)) {
            response.setContentType(fileInfo.getContentType());
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "inline");
            StreamUtils.copy(inputStream, response.getOutputStream());
            response.flushBuffer();
        } catch (Exception e) {
            throw new BusinessException(StorageErrorCode.FILE_DOWNLOAD_FAILED);
        }
    }

    @Operation(summary = "Delete a file", description = "Delete a file by ID if the user is the owner.")
    @DeleteMapping("/{id}")
    public ResponseEntity<StandardResponse<Void>> deleteFile(
            @Parameter(description = "File ID", required = true)
            @PathVariable UUID id,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        storageService.deleteFile(id, userId);
        return ResponseEntity.noContent().build();
    }
}
