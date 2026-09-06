package com.unsiiyat.backend.modules.author;

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
@RequestMapping("/api/authors")
public class AuthorController {

    @Autowired
    private AuthorService authorService;

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorController.class);

    @PostMapping("/list")
    public ResponseEntity<PagedResponse<AuthorDto>> filterAuthor(@RequestBody AuthorFilterRequest request) {
        LOGGER.debug("filterAuthor endpoint called");
        PagedResponse<AuthorDto> response = authorService.filterAuthor(request);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/addOrUpdate")
    public ResponseEntity<ApiResponse<AuthorDto>> addOrUpdateAuthor(@RequestBody AuthorDto request) {
        LOGGER.debug("addOrUpdateAuthor endpoint called");
        AuthorDto response = authorService.addOrUpdateAuthor(request);
        return ResponseEntity.ok(ApiResponse.success("Author saved successfully", response));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/delete")
    public ResponseEntity<ApiResponse<String>> deleteAuthor(@RequestBody AuthorDto request) {
        LOGGER.debug("deleteAuthor endpoint called");
        authorService.deleteAuthor(request);
        return ResponseEntity.ok(ApiResponse.success("Author deleted successfully", null));
    }

}
