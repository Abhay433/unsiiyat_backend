package com.unsiiyat.backend.modules.auth;

import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.unsiiyat.backend.common.exceptions.ResourceNotFoundException;
import com.unsiiyat.backend.common.security.CustomUserDetails;
import com.unsiiyat.backend.modules.s3bucket.FileService;
import com.unsiiyat.backend.modules.s3bucket.UploadedFileDto;

import jakarta.transaction.Transactional;

@Service
public class UserService {

    @Autowired
    private FileService fileService;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public String updateProfilePicture(Long userId, MultipartFile file) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        String oldPhotoUrl = user.getProfilePictureUrl();
        // 1. Upload new photo
        UploadedFileDto uploaded = fileService.uploadAndSave(file);
        user.setProfilePictureUrl(uploaded.getFilePath());
        userRepository.save(user);
        // 2. Clean up previous photo from R2 and uploaded_files table
        if (oldPhotoUrl != null && !oldPhotoUrl.isBlank()) {
            fileService.deleteFileByPath(oldPhotoUrl);
        }
        return uploaded.getFilePath();
    }

}
