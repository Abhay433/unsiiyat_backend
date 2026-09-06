package com.unsiiyat.backend.modules.contextText;

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
@RequestMapping("/api/content-texts")
public class ContentTextController {

    @Autowired
    private ContentTextService contentTextService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ContentTextController.class);

    @PostMapping("/list")
    public ResponseEntity<PagedResponse<ContentTextDto>> filterContentText(
            @RequestBody ContentTextFilterRequest request) {
        LOGGER.debug("filterContentText endpoint called");
        PagedResponse<ContentTextDto> response = contentTextService.filterContentText(request);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @PostMapping("/addOrUpdate")
    public ResponseEntity<ApiResponse<ContentTextDto>> addOrUpdateContentText(@RequestBody ContentTextDto request) {
        LOGGER.debug("addOrUpdateContentText endpoint called");
        ContentTextDto response = contentTextService.addOrUpdateContentText(request);
        return ResponseEntity.ok(ApiResponse.success("Content text saved successfully", response));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @PostMapping("/delete")
    public ResponseEntity<ApiResponse<String>> deleteContentText(@RequestBody ContentTextDto request) {
        LOGGER.debug("deleteContentText endpoint called");
        contentTextService.deleteContentText(request);
        return ResponseEntity.ok(ApiResponse.success("Content text deleted successfully", null));
    }

}
