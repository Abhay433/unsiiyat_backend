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

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/addOrUpdate")
    public ResponseEntity<ApiResponse<Void>> addOrUpdateContent(@RequestBody ContentDto request) {
        LOGGER.debug("addOrUpdateContent endpoint called");
        contentService.addOrUpdateContent(request);
        return ResponseEntity.ok(ApiResponse.success("Content saved successfully", null));
    }

}
