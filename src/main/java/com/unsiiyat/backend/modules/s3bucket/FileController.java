package com.unsiiyat.backend.modules.s3bucket;

import com.unsiiyat.backend.common.response.ApiResponse;
import com.unsiiyat.backend.common.response.PagedResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileController.class);

    @Autowired
    private FileService fileService;

    @PostMapping("/list")
    public ResponseEntity<ApiResponse<PagedResponse<UploadedFileDto>>> listFiles(
            @RequestBody FileFilterRequest request) {
        LOGGER.debug("List files API called with filters: {}", request);
        PagedResponse<UploadedFileDto> responseDto = fileService.filterFiles(request);
        ApiResponse<PagedResponse<UploadedFileDto>> response = ApiResponse.success("Files fetched successfully",
                responseDto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<UploadedFileDto>> uploadFile(@RequestParam("file") MultipartFile file) {
        LOGGER.debug("Upload file API called for filename: {}", file.getOriginalFilename());
        UploadedFileDto responseDto = fileService.uploadAndSave(file);
        ApiResponse<UploadedFileDto> response = ApiResponse.success("File uploaded and saved successfully",
                responseDto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UploadedFileDto>> getFileMetadata(@PathVariable("id") UUID id) {
        LOGGER.debug("Get file metadata API called for ID: {}", id);
        UploadedFileDto responseDto = fileService.getFileMetadata(id);
        ApiResponse<UploadedFileDto> response = ApiResponse.success("File metadata retrieved successfully",
                responseDto);
        return ResponseEntity.ok(response);
    }

    // Streams the file back through the server (from R2) as an attachment,
    // tenant-scoped — so
    // downloads work without exposing/depending on a public bucket URL.
    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadFile(@PathVariable("id") UUID id) {
        LOGGER.debug("Download file API called for ID: {}", id);
        FileService.DownloadResult result = fileService.download(id);
        String encodedName = URLEncoder.encode(result.fileName() != null ? result.fileName() : "file",
                StandardCharsets.UTF_8).replace("+", "%20");
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + encodedName + "\"; filename*=UTF-8''" + encodedName)
                .contentType(MediaType.parseMediaType(result.contentType()))
                .body(result.bytes());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteFile(@PathVariable("id") UUID id) {
        LOGGER.debug("Delete file API called for ID: {}", id);
        fileService.deleteFile(id);
        ApiResponse<String> response = ApiResponse.success("File deleted successfully", null);
        return ResponseEntity.ok(response);
    }

}
