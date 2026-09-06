package com.unsiiyat.backend.modules.s3bucket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.unsiiyat.backend.common.config.CloudflareR2Service;
import com.unsiiyat.backend.common.exceptions.ResourceNotFoundException;
import com.unsiiyat.backend.common.response.PagedResponse;

import java.util.UUID;

@Service
public class FileService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileService.class);

    @Value("${cloudflare.r2.public-url}")
    private String publicUrl;

    @Autowired
    private CloudflareR2Service cloudflareR2Service;

    @Autowired
    private UploadedFileRepository uploadedFileRepository;

    public PagedResponse<UploadedFileDto> filterFiles(FileFilterRequest request) {

        Sort sort = Sort.by(Sort.Direction.fromString(request.getSortDirection()), request.getSortBy());

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

        Page<UploadedFileEntity> filePage = uploadedFileRepository.findAll(FileSpecification.filter(request), pageable);

        Page<UploadedFileDto> dtoPage = filePage.map(this::mapToDto);

        return PagedResponse.fromPage(dtoPage, "Files fetched successfully");
    }

    public UploadedFileDto uploadAndSave(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        if (extension == null) {
            extension = "bin";
        }

        // Generate a unique path/key in R2
        String uniqueKey = "documents/" + UUID.randomUUID() + "." + extension;

        try {
            // Upload to Cloudflare R2
            String r2Url = cloudflareR2Service.uploadFile(file, uniqueKey);

            // Save metadata to database
            UploadedFileEntity entity = new UploadedFileEntity();
            entity.setFileName(originalFilename);
            entity.setFileType(extension);
            entity.setFilePath(r2Url);
            entity.setFileSize(file.getSize());

            UploadedFileEntity saved = uploadedFileRepository.save(entity);
            LOGGER.debug("File saved to DB with ID: {}", saved.getId());

            return mapToDto(saved);

        } catch (Exception e) {
            LOGGER.error("Failed to upload and save file metadata", e);
            throw new RuntimeException("File upload failed: " + e.getMessage(), e);
        }
    }

    public UploadedFileDto getFileMetadata(UUID id) {
        UploadedFileEntity entity = uploadedFileRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("File not found with ID: " + id));

        return mapToDto(entity);
    }

    public DownloadResult download(UUID id) {
        UploadedFileEntity entity = uploadedFileRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("File not found with ID: " + id));

        CloudflareR2Service.DownloadedFile df = cloudflareR2Service.downloadFile(extractKey(entity.getFilePath()));
        String contentType = df.contentType() != null ? df.contentType() : "application/octet-stream";
        return new DownloadResult(df.bytes(), entity.getFileName(), contentType);
    }

    private String extractKey(String filePath) {
        String key = filePath;
        String trimmed = publicUrl != null ? publicUrl.trim() : "";
        if (!trimmed.isEmpty() && filePath != null && filePath.startsWith(trimmed)) {
            key = filePath.substring(trimmed.length());
            if (key.startsWith("/")) {
                key = key.substring(1);
            }
        }
        return key;
    }

    public record DownloadResult(byte[] bytes, String fileName, String contentType) {
    }

    public void deleteFile(UUID id) {
        LOGGER.debug("Deleting file from DB and R2, ID: {}", id);
        UploadedFileEntity entity = uploadedFileRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("File not found with ID: " + id));

        String filePath = entity.getFilePath();
        String uniqueKey = extractKey(filePath);

        try {
            cloudflareR2Service.deleteFile(uniqueKey);
        } catch (Exception e) {
            LOGGER.error("Failed to delete file from Cloudflare R2: {}", uniqueKey, e);
            throw new RuntimeException("Failed to delete file from storage: " + e.getMessage(), e);
        }

        uploadedFileRepository.delete(entity);
        LOGGER.debug("File metadata deleted from DB, ID: {}", id);
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return null;
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }

    public void deleteFileByPath(String filePath) {
        if (filePath == null || filePath.isBlank())
            return;

        try {
            String uniqueKey = extractKey(filePath);
            cloudflareR2Service.deleteFile(uniqueKey);
        } catch (Exception e) {
            LOGGER.warn("Failed to delete old file from Cloudflare R2: {}", filePath, e);
        }

        try {
            uploadedFileRepository.findByFilePath(filePath).ifPresent(uploadedFileRepository::delete);
        } catch (Exception e) {
            LOGGER.warn("Failed to delete old file metadata from DB: {}", filePath, e);
        }
    }

    public UploadedFileDto mapToDto(UploadedFileEntity entity) {
        return new UploadedFileDto(
                entity.getId(),
                entity.getFileName(),
                entity.getFileType(),
                entity.getFilePath(),
                entity.getFileSize(),
                entity.getCreatedAt());
    }
}
