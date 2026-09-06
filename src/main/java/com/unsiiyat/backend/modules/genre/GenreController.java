package com.unsiiyat.backend.modules.genre;

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
@RequestMapping("/api/genres")
public class GenreController {

    @Autowired
    private GenreService genreService;

    private static final Logger LOGGER = LoggerFactory.getLogger(GenreController.class);

    @PostMapping("/list")
    public ResponseEntity<PagedResponse<GenreDto>> filterGenre(@RequestBody GenreFilterRequest request) {
        LOGGER.debug("filterGenre endpoint called");
        PagedResponse<GenreDto> response = genreService.filterGenre(request);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @PostMapping("/addOrUpdate")
    public ResponseEntity<ApiResponse<Void>> addOrUpdateGenre(@RequestBody GenreDto request) {
        LOGGER.debug("addOrUpdateGenre endpoint called");
        genreService.addOrUpdateGenre(request);
        return ResponseEntity.ok(ApiResponse.success("Genre saved successfully", null));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @PostMapping("/delete")
    public ResponseEntity<ApiResponse<Void>> deleteGenre(@RequestBody GenreDto request) {
        LOGGER.debug("deleteGenre endpoint called");
        genreService.deleteGenre(request);
        return ResponseEntity.ok(ApiResponse.success("Genre deleted successfully", null));
    }

}
