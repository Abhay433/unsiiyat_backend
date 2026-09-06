package com.unsiiyat.backend.modules.auth;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;

import com.unsiiyat.backend.common.response.ApiResponse;
import com.unsiiyat.backend.common.security.CustomUserDetails;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping(value = "/profile/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<String>> uploadProfilePhoto(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam("file") MultipartFile file) {
        String photoUrl = userService.updateProfilePicture(userDetails.getId(), file);
        return ResponseEntity.ok(ApiResponse.success("Profile photo updated successfully", photoUrl));
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PostMapping("/add-admin")
    public ResponseEntity<ApiResponse<UserDto>> addAdmin(@Valid @RequestBody CreateAdminRequestDto requestDto) {
        UserDto created = userService.createAdmin(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Admin user created successfully", created));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @GetMapping("/admins")
    public ResponseEntity<ApiResponse<List<UserDto>>> getAdmins() {
        List<UserDto> admins = userService.getAllAdmins();
        return ResponseEntity.ok(ApiResponse.success("Admins fetched successfully", admins));
    }

}