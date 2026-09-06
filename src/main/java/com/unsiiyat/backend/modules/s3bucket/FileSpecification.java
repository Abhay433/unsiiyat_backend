package com.unsiiyat.backend.modules.s3bucket;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.data.jpa.domain.Specification;

import com.unsiiyat.backend.common.filters.SpecificationBuilder;

public class FileSpecification {

    public static Specification<UploadedFileEntity> filter(FileFilterRequest request) {

        return new SpecificationBuilder<UploadedFileEntity>()
                .with(idEqual(request.getId()))
                .with(fileNameLike(request.getFileName()))
                .with(fileTypeEqual(request.getFileType()))
                .with(filePathLike(request.getFilePath()))
                .with(fileSizeEqual(request.getFileSize()))
                .with(createdAtEqual(request.getCreatedAt()))
                .build();
    }

    public static Specification<UploadedFileEntity> idEqual(UUID id) {
        return (root, query, cb) -> {
            if (id == null) {
                return null;
            }
            return cb.equal(root.get("id"), id);
        };
    }

    public static Specification<UploadedFileEntity> fileNameLike(String fileName) {
        return (root, query, cb) -> {
            if (fileName == null) {
                return null;
            }
            return cb.like(root.get("fileName"), "%" + fileName + "%");
        };
    }

    public static Specification<UploadedFileEntity> fileTypeEqual(String fileType) {
        return (root, query, cb) -> {
            if (fileType == null) {
                return null;
            }
            return cb.equal(root.get("fileType"), fileType);
        };
    }

    public static Specification<UploadedFileEntity> filePathLike(String filePath) {
        return (root, query, cb) -> {
            if (filePath == null) {
                return null;
            }
            return cb.like(root.get("filePath"), "%" + filePath + "%");
        };
    }

    public static Specification<UploadedFileEntity> fileSizeEqual(Long fileSize) {
        return (root, query, cb) -> {
            if (fileSize == null) {
                return null;
            }
            return cb.equal(root.get("fileSize"), fileSize);
        };
    }

    public static Specification<UploadedFileEntity> createdAtEqual(LocalDate createdAt) {
        return (root, query, cb) -> {
            if (createdAt == null) {
                return null;
            }
            return cb.equal(root.get("createdAt"), createdAt);
        };
    }

}