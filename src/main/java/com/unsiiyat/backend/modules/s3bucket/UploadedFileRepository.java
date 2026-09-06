package com.unsiiyat.backend.modules.s3bucket;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UploadedFileRepository
        extends JpaRepository<UploadedFileEntity, UUID>, JpaSpecificationExecutor<UploadedFileEntity> {

    Optional<UploadedFileEntity> findByFilePath(String filePath);

}