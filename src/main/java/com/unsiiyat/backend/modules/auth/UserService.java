package com.unsiiyat.backend.modules.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.unsiiyat.backend.common.exceptions.BadRequestException;
import com.unsiiyat.backend.common.exceptions.ResourceNotFoundException;
import com.unsiiyat.backend.modules.s3bucket.FileService;
import com.unsiiyat.backend.modules.s3bucket.UploadedFileDto;

import jakarta.transaction.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private FileService fileService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

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

    @Transactional
    public UserDto createAdmin(CreateAdminRequestDto request) {
        String email = request.getEmail();
        if (userRepository.existsByEmail(email)) {
            throw new BadRequestException("Email is already registered: " + email);
        }

        UserEntity user = new UserEntity();
        user.setName(request.getName());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole.ADMIN); // Strictly ADMIN by default
        user.setIsActive(true);

        UserEntity savedUser = userRepository.save(user);
        return UserDto.fromEntity(savedUser);
    }

    public List<UserDto> getAllAdmins() {
        return userRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(UserDto::fromEntity)
                .collect(Collectors.toList());
    }

}
