package com.unsiiyat.backend.modules.home;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unsiiyat.backend.modules.content.ContentDto;
import com.unsiiyat.backend.modules.content.ContentRepository;
import com.unsiiyat.backend.modules.content.ContentService;
import com.unsiiyat.backend.modules.genre.GenreEntity;
import com.unsiiyat.backend.modules.genre.GenreRepository;

@Service
public class HomeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HomeService.class);

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private ContentService contentService;

    @Autowired
    private GenreRepository genreRepository;

    /**
     * Returns the Ghazal of the Day.
     * The Ghazal is selected deterministically based on the current calendar date (Asia/Kolkata),
     * ensuring it remains consistent throughout the day for all users and changes automatically daily.
     *
     * @param preferredScriptId Optional preferred script ID (Urdu, Hindi, English)
     * @param forceRandom If true, picks a fresh random Ghazal instead of the daily fixed one
     * @return ContentDto representing the chosen Ghazal
     */
    @Transactional(readOnly = true)
    public ContentDto getGhazalOfTheDay(Long preferredScriptId, boolean forceRandom) {
        // 1. Resolve Ghazal Genre ID from the database
        Long ghazalGenreId = resolveGhazalGenreId();
        LOGGER.debug("Resolved Ghazal genre ID: {}", ghazalGenreId);

        // 2. Fetch all IDs belonging to the Ghazal genre
        List<Long> contentIds = (ghazalGenreId != null)
                ? contentRepository.findIdsByGenreId(ghazalGenreId)
                : List.of();

        // Fallback: if no contents found with Ghazal genre, pick from all contents
        if (contentIds == null || contentIds.isEmpty()) {
            LOGGER.warn("No contents found for Ghazal genre ID {}, falling back to all available content IDs", ghazalGenreId);
            contentIds = contentRepository.findAllIds();
        }

        if (contentIds == null || contentIds.isEmpty()) {
            LOGGER.warn("No contents found in the database");
            return null;
        }

        // 3. Choose index: either daily deterministic seed or fresh random
        int chosenIndex;
        if (forceRandom) {
            chosenIndex = ThreadLocalRandom.current().nextInt(contentIds.size());
        } else {
            // Seed with today's epoch day so the chosen ghazal is stable for the entire day and changes daily
            long epochDay = LocalDate.now(ZoneId.of("Asia/Kolkata")).toEpochDay();
            Random dailyRandom = new Random(epochDay);
            chosenIndex = dailyRandom.nextInt(contentIds.size());
        }

        Long chosenContentId = contentIds.get(chosenIndex);
        LOGGER.info("Ghazal of the Day selected content ID: {} (index {} of {} items, forceRandom={})",
                chosenContentId, chosenIndex, contentIds.size(), forceRandom);

        // 4. Fetch the full entity and map to ContentDto
        return contentRepository.findById(chosenContentId)
                .map(entity -> contentService.mapToContentDto(entity, preferredScriptId))
                .orElse(null);
    }

    /**
     * Resolves the Ghazal Genre ID dynamically by checking slug, name, and multilingual variations.
     */
    public Long resolveGhazalGenreId() {
        // Check by exact slug "ghazal"
        Optional<GenreEntity> bySlug = genreRepository.findBySlug("ghazal");
        if (bySlug.isPresent()) {
            return bySlug.get().getId();
        }

        // Check by name "Ghazal"
        Optional<GenreEntity> byName = genreRepository.findByName("Ghazal");
        if (byName.isPresent()) {
            return byName.get().getId();
        }

        // Check across all genres for variations
        List<GenreEntity> allGenres = genreRepository.findAll();
        for (GenreEntity g : allGenres) {
            String slug = g.getSlug() != null ? g.getSlug().toLowerCase() : "";
            String name = g.getName() != null ? g.getName().toLowerCase() : "";
            if ("ghazal".equals(slug) || name.contains("ghazal") || name.contains("غزل") || name.contains("ग़ज़ल")) {
                return g.getId();
            }
        }

        // Fallback to first available genre if present
        return allGenres.isEmpty() ? null : allGenres.get(0).getId();
    }
}
