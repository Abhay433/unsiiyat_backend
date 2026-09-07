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
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size,
            @RequestParam(value = "genreId", required = false) Long genreId,
            @RequestParam(value = "type", required = false) String type) {
        String searchText = text != null && !text.trim().isEmpty() ? text : query;
        LOGGER.debug("GET /api/search called with query: {}, page: {}, size: {}, genreId: {}, type: {}", searchText, page, size, genreId, type);
        SearchRequestDto request = new SearchRequestDto(searchText);
        request.setPage(page);
        request.setSize(size);
        request.setGenreId(genreId);
        request.setType(type);
        SearchResponseDto result = searchService.search(request);
        return ResponseEntity.ok(ApiResponse.success("Search completed successfully", result));
    }

    @GetMapping("/contents")
    public ResponseEntity<ApiResponse<GenreSearchResultDto>> searchContents(
            @RequestParam(value = "text", required = false) String text,
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "genreId") Long genreId,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size) {
        String searchText = text != null && !text.trim().isEmpty() ? text : query;
        LOGGER.debug("GET /api/search/contents called with query: {}, genreId: {}, page: {}, size: {}", searchText, genreId, page, size);
        SearchRequestDto request = new SearchRequestDto(searchText);
        request.setGenreId(genreId);
        request.setPage(page);
        request.setSize(size);
        GenreSearchResultDto result = searchService.searchGenreContents(request);
        return ResponseEntity.ok(ApiResponse.success("Genre contents fetched successfully", result));
    }

    @GetMapping("/authors")
    public ResponseEntity<ApiResponse<SearchResponseDto>> searchAuthors(
            @RequestParam(value = "text", required = false) String text,
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size) {
        String searchText = text != null && !text.trim().isEmpty() ? text : query;
        LOGGER.debug("GET /api/search/authors called with query: {}, page: {}, size: {}", searchText, page, size);
        SearchRequestDto request = new SearchRequestDto(searchText);
        request.setPage(page);
        request.setSize(size);
        request.setType("authors");
        SearchResponseDto result = searchService.searchAuthorsOnly(request);
        return ResponseEntity.ok(ApiResponse.success("Authors search completed successfully", result));
    }
}
