package vn.edu.fpt.transitlink.storage.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import vn.edu.fpt.transitlink.shared.dto.StandardResponse;
import vn.edu.fpt.transitlink.shared.dto.ErrorResponse;
import vn.edu.fpt.transitlink.shared.exception.BusinessException;
import vn.edu.fpt.transitlink.storage.domain.exception.StorageErrorCode;
import vn.edu.fpt.transitlink.storage.presentation.dto.FileInfoDTO;
import vn.edu.fpt.transitlink.storage.application.StorageService;
import vn.edu.fpt.transitlink.storage.domain.model.FileInfo;

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
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "File uploaded successfully",
                    content = @Content(
                            schema = @Schema(implementation = StandardResponse.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "FileUploadSuccess",
                                    summary = "File upload successful",
                                    value = """
                                            {
                                              "success": true,
                                              "message": "File uploaded successfully",
                                              "data": {
                                                "id": "123e4567-e89b-12d3-a456-426614174000",
                                                "originalName": "example.jpg",
                                                "contentType": "image/jpeg",
                                                "size": 204800,
                                                "fileType": "IMAGE",
                                                "url": "https://example.com/files/123e4567-e89b-12d3-a456-426614174000.png",
                                                "uploadedBy": "123e4567-e89b-12d3-a456-426614174000"
                                              },
                                              "timestamp": "2025-07-28 22:00:00 UTC",
                                              "statusCode": 201
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized access",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "File upload failed",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "FileUploadFailed",
                                    summary = "File upload failed",
                                    value = """
                                            {
                                              "success": false,
                                              "message": "File upload failed",
                                              "error": "FILE_UPLOAD_FAILED",
                                              "path": "/api/storage",
                                              "statusCode": 500,
                                              "timestamp": "2025-07-28 22:00:00 UTC"
                                            }
                                            """
                            )
                    )
            )

    })
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
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "File metadata retrieved successfully",
                    content = @Content(
                            schema = @Schema(implementation = StandardResponse.class),
                            examples = @ExampleObject(
                                    name = "FileInfoSuccess",
                                    summary = "File metadata retrieved successfully",
                                    value = """
                                            {
                                              "success": true,
                                              "message": "File metadata retrieved successfully",
                                              "data": {
                                                "id": "123e4567-e89b-12d3-a456-426614174000",
                                                "originalName": "example.jpg",
                                                "contentType": "image/jpeg",
                                                "size": 204800,
                                                "fileType": "IMAGE",
                                                "url": "https://example.com/files/123e4567-e89b-12d3-a456-426614174000",
                                                "uploadedBy": "123e4567-e89b-12d3-a456-426614174000"
                                              },
                                              "timestamp": "2025-07-28 22:00:00 UTC",
                                              "statusCode": 200
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized access",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "File not found",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "FileNotFound",
                                    summary = "File not found",
                                    value = """
                                            {
                                              "success": false,
                                              "message": "File not found",
                                              "error": "FILE_NOT_FOUND",
                                              "path": "/api/storage/{id}",
                                              "statusCode": 404,
                                              "timestamp": "2025-07-28 22:00:00 UTC"
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<StandardResponse<FileInfoDTO>> getFileInfo(
            @Parameter(description = "File ID", required = true)
            @PathVariable UUID id) {

        FileInfo file = storageService.findById(id)
                .orElseThrow(() -> new BusinessException(StorageErrorCode.FILE_NOT_FOUND));
        return ResponseEntity.ok(StandardResponse.success(FileInfoDTO.from(file)));
    }

    @Operation(summary = "Download a file", description = "Download the binary content of a file by its ID.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "File downloaded successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE,
                            schema = @Schema(type = "string", format = "binary")

                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "File not found",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "FileNotFound",
                                    summary = "File not found",
                                    value = """
                                            {
                                              "success": false,
                                              "message": "File not found",
                                              "error": "FILE_NOT_FOUND",
                                              "path": "/api/storage/download/{id}",
                                              "statusCode": 404,
                                              "timestamp": "2025-07-28 22:00:00 UTC"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized access",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "File download failed",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "FileDownloadFailed",
                                    summary = "File download failed",
                                    value = """
                                            {
                                              "success": false,
                                              "message": "File download failed",
                                              "error": "FILE_DOWNLOAD_FAILED",
                                              "path": "/api/storage/download/{id}",
                                              "statusCode": 500,
                                              "timestamp": "2025-07-28 22:00:00 UTC"
                                            }
                                            """
                            )
                    ))
    })
    @GetMapping("/{id}/download")
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

    @Operation(summary = "Preview a file", description = "Preview file content inline. Only supports images and PDFs.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File previewed successfully"),
            @ApiResponse(responseCode = "400", description = "Unsupported file type for preview",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "File not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "File preview failed",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "File deleted successfully"),
            @ApiResponse(responseCode = "500", description = "File deletion failed",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
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
