package com.unsiiyat.backend.modules.home;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unsiiyat.backend.modules.content.ContentDto;
import com.unsiiyat.backend.modules.content.ContentEntity;
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

    public static final int PER_GENRE_LIMIT = 8;

    /**
     * Returns curated / selected contents grouped by genre using default limit of 8.
     *
     * @param preferredScriptId Optional script ID to format primary text
     * @return List of GenreCuratedGroupDto
     */
    @Transactional(readOnly = true)
    public List<GenreCuratedGroupDto> getCuratedContentsByGenre(Long preferredScriptId) {
        return getCuratedContentsByGenre(PER_GENRE_LIMIT, preferredScriptId);
    }

    /**
     * Returns curated / selected contents grouped by genre.
     * For each genre, up to 'limit' (default 8) contents are returned,
     * prioritizing is_selected = true items.
     *
     * @param limit Maximum number of contents per genre (default: 8)
     * @param preferredScriptId Optional script ID to format primary text
     * @return List of GenreCuratedGroupDto
     */
    @Transactional(readOnly = true)
    public List<GenreCuratedGroupDto> getCuratedContentsByGenre(Integer limit, Long preferredScriptId) {
        int contentLimit = (limit != null && limit > 0) ? limit : 8;
        List<GenreEntity> genres = genreRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
        List<GenreCuratedGroupDto> result = new ArrayList<>();

        Pageable pageable = PageRequest.of(0, contentLimit, Sort.by(Sort.Direction.DESC, "id"));

        for (GenreEntity genre : genres) {
            // 1. Fetch curated (is_selected = true) contents for this genre
            List<ContentEntity> entities = contentRepository.findByGenreIdAndIsSelectedTrue(genre.getId(), pageable);
            long totalSelectedCount = contentRepository.countByGenreIdAndIsSelectedTrue(genre.getId());

            // 2. Fallback to latest contents if no is_selected items exist yet
            if (entities == null || entities.isEmpty()) {
                entities = contentRepository.findByGenreId(genre.getId(), pageable);
            }

            if (entities != null && !entities.isEmpty()) {
                List<ContentDto> contentDtos = entities.stream()
                        .map(entity -> contentService.mapToContentDto(entity, preferredScriptId))
                        .collect(Collectors.toList());

                GenreCuratedGroupDto group = new GenreCuratedGroupDto();
                group.setGenreId(genre.getId());
                group.setGenreName(genre.getName());
                group.setGenreSlug(genre.getSlug());
                group.setTotalSelected(totalSelectedCount);
                group.setContents(contentDtos);
                result.add(group);
            }
        }

        return result;
    }

    /**
     * Returns selected / curated Ghazals using default limit of 8 per genre.
     *
     * @param preferredScriptId Optional script ID to format primary text
     * @return List of ContentDto
     */
    @Transactional(readOnly = true)
    public List<ContentDto> getSelectedGhazals(Long preferredScriptId) {
        return getSelectedGhazals(PER_GENRE_LIMIT, preferredScriptId);
    }

    /**
     * Returns selected / curated Ghazals up to 'limit' (default 8).
     *
     * @param limit Maximum number of Ghazals to return (default: 8)
     * @param preferredScriptId Optional script ID to format primary text
     * @return List of ContentDto
     */
    @Transactional(readOnly = true)
    public List<ContentDto> getSelectedGhazals(Integer limit, Long preferredScriptId) {
        int contentLimit = (limit != null && limit > 0) ? limit : 8;
        Long ghazalGenreId = resolveGhazalGenreId();
        if (ghazalGenreId == null) {
            return List.of();
        }

        Pageable pageable = PageRequest.of(0, contentLimit, Sort.by(Sort.Direction.DESC, "id"));

        // 1. Fetch curated (is_selected = true) Ghazals
        List<ContentEntity> entities = contentRepository.findByGenreIdAndIsSelectedTrue(ghazalGenreId, pageable);

        // 2. Fallback to latest Ghazals if no is_selected items exist yet
        if (entities == null || entities.isEmpty()) {
            entities = contentRepository.findByGenreId(ghazalGenreId, pageable);
        }

        if (entities == null || entities.isEmpty()) {
            return List.of();
        }

        return entities.stream()
                .map(entity -> contentService.mapToContentDto(entity, preferredScriptId))
                .collect(Collectors.toList());
    }
}
