package com.unsiiyat.backend.modules.search;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.unsiiyat.backend.common.response.ApiResponse;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchController.class);

    @Autowired
    private SearchService searchService;

    @PostMapping
    public ResponseEntity<ApiResponse<SearchResponseDto>> search(@RequestBody(required = false) SearchRequestDto request) {
        LOGGER.debug("POST /api/search called with payload: {}", request);
        if (request == null) {
            request = new SearchRequestDto("");
        }
        SearchResponseDto result = searchService.search(request);
        return ResponseEntity.ok(ApiResponse.success("Search completed successfully", result));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<SearchResponseDto>> searchGet(
            @RequestParam(value = "text", required = false) String text,
            @RequestParam(value = "query", required = false) String query) {
        String searchText = text != null && !text.trim().isEmpty() ? text : query;
        LOGGER.debug("GET /api/search called with query: {}", searchText);
        SearchRequestDto request = new SearchRequestDto(searchText);
        SearchResponseDto result = searchService.search(request);
        return ResponseEntity.ok(ApiResponse.success("Search completed successfully", result));
    }
}
