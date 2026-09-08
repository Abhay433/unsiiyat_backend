package com.unsiiyat.backend.modules.home;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.unsiiyat.backend.common.response.ApiResponse;
import com.unsiiyat.backend.modules.content.ContentDto;

@RestController
@RequestMapping("/api/home")
public class HomeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    private HomeService homeService;

    /**
     * Endpoint to fetch the Ghazal of the Day.
     * Accessible via both /ghazal-of-the-day and /gazal-of-the-day.
     *
     * @param scriptId Optional script ID (Urdu=1, Hindi=2, English=3) to format
     *                 primary text
     * @param random   Optional flag; if true, returns a fresh random Ghazal instead
     *                 of the daily fixed one
     */
    @GetMapping(path = { "/ghazal-of-the-day", "/gazal-of-the-day" })
    public ResponseEntity<ApiResponse<ContentDto>> getGhazalOfTheDay(
            @RequestParam(value = "scriptId", required = false) Long scriptId,
            @RequestParam(value = "random", required = false, defaultValue = "false") boolean random) {
        LOGGER.debug("GET /api/home/ghazal-of-the-day called (scriptId={}, random={})", scriptId, random);
        ContentDto ghazal = homeService.getGhazalOfTheDay(scriptId, random);

        if (ghazal == null) {
            return ResponseEntity.ok(ApiResponse.success("No Ghazals currently available", null));
        }

        return ResponseEntity.ok(ApiResponse.success("Ghazal of the day fetched successfully", ghazal));
    }

    /**
     * Endpoint to fetch a purely random Ghazal on demand.
     */
    @GetMapping("/random-ghazal")
    public ResponseEntity<ApiResponse<ContentDto>> getRandomGhazal(
            @RequestParam(value = "scriptId", required = false) Long scriptId) {
        LOGGER.debug("GET /api/home/random-ghazal called (scriptId={})", scriptId);
        ContentDto ghazal = homeService.getGhazalOfTheDay(scriptId, true);

        if (ghazal == null) {
            return ResponseEntity.ok(ApiResponse.success("No Ghazals currently available", null));
        }

        return ResponseEntity.ok(ApiResponse.success("Random Ghazal fetched successfully", ghazal));
    }

    /**
     * Endpoint to fetch curated / selected contents grouped by genre.
     * Fixed limit of 8 contents per genre.
     *
     * @param scriptId Optional script ID to format primary text
     */
    @GetMapping("/curated-genres")
    public ResponseEntity<ApiResponse<List<GenreCuratedGroupDto>>> getCuratedGenres(
            @RequestParam(value = "scriptId", required = false) Long scriptId) {
        LOGGER.debug("GET /api/home/curated-genres called (scriptId={})", scriptId);
        List<GenreCuratedGroupDto> groups = homeService.getCuratedContentsByGenre(scriptId);
        return ResponseEntity.ok(ApiResponse.success("Curated genres fetched successfully", groups));
    }

    /**
     * Endpoint to fetch Selected / Curated Ghazals (fixed limit of 8 per genre).
     *
     * @param scriptId Optional script ID to format primary text
     */
    @GetMapping(path = { "/selected-ghazals", "/curated-ghazals" })
    public ResponseEntity<ApiResponse<List<ContentDto>>> getSelectedGhazals(
            @RequestParam(value = "scriptId", required = false) Long scriptId) {
        LOGGER.debug("GET /api/home/selected-ghazals called (scriptId={})", scriptId);
        List<ContentDto> ghazals = homeService.getSelectedGhazals(scriptId);
        return ResponseEntity.ok(ApiResponse.success("Selected Ghazals fetched successfully", ghazals));
    }

    @GetMapping(path = { "/selected-nazm", "/curated-nazm" })
    public ResponseEntity<ApiResponse<List<ContentDto>>> getSelectedNazms(
            @RequestParam(value = "scriptId", required = false) Long scriptId) {
        LOGGER.debug("GET /api/home/selected-nazm called (scriptId={})", scriptId);
        List<ContentDto> nazms = homeService.getSelectedNazms(scriptId);
        return ResponseEntity.ok(ApiResponse.success("Selected Nazms fetched successfully", nazms));
    }
}
