package com.unsiiyat.backend.common.config;

import jakarta.annotation.PostConstruct;
import net.coobird.thumbnailator.Thumbnails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.UUID;

@Service
public class CloudflareR2Service {

    private static final Logger LOGGER = LoggerFactory.getLogger(CloudflareR2Service.class);

    @Value("${cloudflare.r2.access-key-id}")
    private String accessKeyId;

    @Value("${cloudflare.r2.secret-access-key}")
    private String secretAccessKey;

    @Value("${cloudflare.r2.endpoint}")
    private String endpoint;

    @Value("${cloudflare.r2.bucket-name}")
    private String bucketName;

    @Value("${cloudflare.r2.public-url}")
    private String publicUrl;

    private S3Client s3Client;

    @PostConstruct
    public void init() {
        this.s3Client = S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKeyId, secretAccessKey)))
                .region(Region.of("auto"))
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .build())
                .build();

        LOGGER.debug("Cloudflare R2 S3Client initialized successfully");
    }

    public String uploadFile(MultipartFile file, String uniqueKey) throws Exception {
        LOGGER.debug("Uploading file to Cloudflare R2: {}", file.getOriginalFilename());

        validateFileSize(file);

        byte[] fileBytes;
        String contentType = file.getContentType();

        // Compress only if it is an image to prevent parsing issues on other documents
        if (contentType != null && contentType.startsWith("image/")) {
            try {
                fileBytes = compressImage(file);
            } catch (Exception e) {
                LOGGER.warn("Image compression failed, uploading raw file content: {}", e.getMessage());
                fileBytes = file.getBytes();
            }
        } else {
            fileBytes = file.getBytes();
        }

        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(uniqueKey)
                .contentType(contentType)
                .build();

        try {
            s3Client.putObject(putRequest, RequestBody.fromBytes(fileBytes));
            LOGGER.debug("Upload SUCCESS to R2: {}", uniqueKey);
        } catch (Exception e) {
            LOGGER.error("R2 upload FAILED", e);
            throw new RuntimeException("R2 upload failed", e);
        }

        String imageUrl = publicUrl.trim() + "/" + uniqueKey;
        LOGGER.debug("FINAL_URL=[{}]", imageUrl);

        return imageUrl;
    }

    // Fetch an object's bytes from R2 using the server's own credentials, so downloads work
    // without the bucket needing to be public. Returned to the browser as an attachment.
    public DownloadedFile downloadFile(String uniqueKey) {
        LOGGER.debug("Downloading file from Cloudflare R2: {}", uniqueKey);
        try {
            ResponseBytes<GetObjectResponse> object = s3Client.getObjectAsBytes(
                    GetObjectRequest.builder().bucket(bucketName).key(uniqueKey).build());
            return new DownloadedFile(object.asByteArray(), object.response().contentType());
        } catch (Exception e) {
            LOGGER.error("R2 download FAILED for key: {}", uniqueKey, e);
            throw new RuntimeException("R2 download failed: " + e.getMessage(), e);
        }
    }

    public record DownloadedFile(byte[] bytes, String contentType) {
    }

    public void deleteFile(String uniqueKey) {
        LOGGER.debug("Deleting file from Cloudflare R2: {}", uniqueKey);
        try {
            s3Client.deleteObject(software.amazon.awssdk.services.s3.model.DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(uniqueKey)
                    .build());
            LOGGER.debug("Delete SUCCESS from R2: {}", uniqueKey);
        } catch (Exception e) {
            LOGGER.error("R2 delete FAILED for key: {}", uniqueKey, e);
            throw new RuntimeException("R2 delete failed: " + e.getMessage(), e);
        }
    }

    private void validateFileSize(MultipartFile file) {
        long fileSize = file.getSize();
        if (fileSize > 32 * 1024 * 1024) { // 32MB
            throw new RuntimeException("File size exceeds 32MB limit");
        }
    }

    private byte[] compressImage(MultipartFile file) throws Exception {
        BufferedImage originalImage = ImageIO.read(file.getInputStream());
        if (originalImage == null) {
            throw new IOException("Failed to read image file content. It may be corrupt or an unsupported format.");
        }

        String extension = getFileExtension(file.getOriginalFilename());
        if (extension == null) {
            extension = "jpg";
        }

        BufferedImage resizedImage = Thumbnails.of(originalImage)
                .size(1024, 1024)
                .outputFormat(extension)
                .asBufferedImage();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(resizedImage, extension, baos);

        return baos.toByteArray();
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return null;
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }
}
