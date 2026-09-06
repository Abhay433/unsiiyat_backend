package com.unsiiyat.backend.modules.theme;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.unsiiyat.backend.common.response.ApiResponse;
import com.unsiiyat.backend.common.response.PagedResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/themes")
public class ThemeController {

    @Autowired
    private ThemeService themeService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ThemeController.class);

    @PostMapping("/list")
    public ResponseEntity<PagedResponse<ThemeDto>> filterTheme(@RequestBody ThemeFilterRequest request) {
        LOGGER.debug("filterStudentMarks endpoint called");
        PagedResponse<ThemeDto> response = themeService.filterTheme(request);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @PostMapping("/addOrUpdate")
    public ResponseEntity<ApiResponse<Void>> addOrUpdateTheme(@RequestBody ThemeDto request) {
        LOGGER.debug("addOrUpdateTheme endpoint called");
        themeService.addOrUpdateTheme(request);
        return ResponseEntity.ok(ApiResponse.success("Theme saved successfully", null));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @PostMapping("/delete")
    public ResponseEntity<ApiResponse<Void>> deleteTheme(@RequestBody ThemeDto request) {
        LOGGER.debug("deleteTheme endpoint called");
        themeService.deleteTheme(request);
        return ResponseEntity.ok(ApiResponse.success("Theme deleted successfully", null));
    }

}
