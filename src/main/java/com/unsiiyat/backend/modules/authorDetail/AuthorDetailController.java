package com.unsiiyat.backend.modules.authorDetail;

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
@RequestMapping("/api/author-details")
public class AuthorDetailController {

    @Autowired
    private AuthorDetailService authorDetailService;

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorDetailController.class);

    @PostMapping("/list")
    public ResponseEntity<PagedResponse<AuthorDetailDto>> filterAuthorDetail(@RequestBody AuthorDetailFilterRequest request) {
        LOGGER.debug("filterAuthorDetail endpoint called");
        PagedResponse<AuthorDetailDto> response = authorDetailService.filterAuthorDetail(request);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/addOrUpdate")
    public ResponseEntity<ApiResponse<Void>> addOrUpdateAuthorDetail(@RequestBody AuthorDetailDto request) {
        LOGGER.debug("addOrUpdateAuthorDetail endpoint called");
        authorDetailService.addOrUpdateAuthorDetail(request);
        return ResponseEntity.ok(ApiResponse.success("Author detail saved successfully", null));
    }

}
