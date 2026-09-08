package com.unsiiyat.backend.modules.content;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.unsiiyat.backend.common.response.ApiResponse;
import com.unsiiyat.backend.common.response.PagedResponse;

@RestController
@RequestMapping("/api/contents")
public class ContentController {

    @Autowired
    private ContentService contentService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ContentController.class);

    @PostMapping("/list")
    public ResponseEntity<PagedResponse<ContentDto>> filterContent(@RequestBody ContentFilterRequest request) {
        LOGGER.debug("filterContent endpoint called");
        PagedResponse<ContentDto> response = contentService.filterContent(request);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @PostMapping("/addOrUpdate")
    public ResponseEntity<ApiResponse<ContentDto>> addOrUpdateContent(@RequestBody ContentDto request) {
        LOGGER.debug("addOrUpdateContent endpoint called");
        ContentDto response = contentService.addOrUpdateContent(request);
        return ResponseEntity.ok(ApiResponse.success("Content saved successfully", response));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @PostMapping("/delete")
    public ResponseEntity<ApiResponse<String>> deleteContent(@RequestBody ContentDto request) {
        LOGGER.debug("deleteContent endpoint called");
        contentService.deleteContent(request);
        return ResponseEntity.ok(ApiResponse.success("Content deleted successfully", null));
    }

    @org.springframework.web.bind.annotation.GetMapping("/count-selected")
    public ResponseEntity<ApiResponse<Long>> countSelectedByGenre(
            @org.springframework.web.bind.annotation.RequestParam("genreId") Long genreId) {
        long count = contentService.countSelectedByGenre(genreId);
        return ResponseEntity.ok(ApiResponse.success("Selected count fetched successfully", count));
    }
}
