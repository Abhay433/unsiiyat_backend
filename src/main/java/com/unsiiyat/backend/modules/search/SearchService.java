package com.unsiiyat.backend.modules.search;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unsiiyat.backend.common.util.LanguageDetectorUtil;
import com.unsiiyat.backend.modules.author.AuthorDto;
import com.unsiiyat.backend.modules.author.AuthorEntity;
import com.unsiiyat.backend.modules.author.AuthorRepository;
import com.unsiiyat.backend.modules.author.AuthorService;
import com.unsiiyat.backend.modules.content.ContentEntity;
import com.unsiiyat.backend.modules.content.ContentRepository;
import com.unsiiyat.backend.modules.content.ContentService;
import com.unsiiyat.backend.modules.genre.GenreEntity;
import com.unsiiyat.backend.modules.genre.GenreRepository;

import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

@Service
public class SearchService {

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private AuthorService authorService;

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private ContentService contentService;

    @Autowired
    private GenreRepository genreRepository;

    @Transactional(readOnly = true)
    public SearchResponseDto search(SearchRequestDto request) {
        String text = request != null ? request.getText() : "";
        if (text == null || text.trim().isEmpty()) {
            return new SearchResponseDto("", "unknown", "Unknown");
        }

        String cleanText = text.trim();

        // 1. Detect language / script
        LanguageDetectorUtil.LanguageType langType = LanguageDetectorUtil.detectLanguage(cleanText);
        String scriptCode = langType.getCode();
        String scriptName = langType.getDisplayName();
        String normalizedText = LanguageDetectorUtil.normalizeText(cleanText);

        SearchResponseDto response = new SearchResponseDto(cleanText, scriptCode, scriptName);

        // 2. Search Authors (Max 5 data)
        List<AuthorDto> authors = searchAuthors(cleanText, scriptCode, normalizedText, 5);
        response.setAuthors(authors);

        // 3. Search Contents (Max 10 data per genre)
        List<GenreSearchResultDto> resultsByGenre = searchContentsByGenre(cleanText, scriptCode, normalizedText, 10);
        response.setResultsByGenre(resultsByGenre);

        return response;
    }

    private List<AuthorDto> searchAuthors(String text, String scriptCode, String normalizedText, int limit) {
        Specification<AuthorEntity> authorSpec = (root, query, criteriaBuilder) -> {
            query.distinct(true);
            String rawPattern = "%" + text.toLowerCase() + "%";
            String normPattern = "%" + normalizedText + "%";

            var detailJoin = root.join("authorDetails", JoinType.LEFT);
            var scriptJoin = detailJoin.join("script", JoinType.LEFT);
            var unaccentName = criteriaBuilder.function("unaccent", String.class, criteriaBuilder.lower(detailJoin.get("name")));

            Predicate nameMatch;
            if ("ur".equalsIgnoreCase(scriptCode)) {
                // Optimized for Urdu script
                nameMatch = criteriaBuilder.and(
                        criteriaBuilder.equal(criteriaBuilder.lower(scriptJoin.get("code")), "ur"),
                        criteriaBuilder.like(criteriaBuilder.lower(detailJoin.get("name")), rawPattern)
                );
            } else if ("hi".equalsIgnoreCase(scriptCode)) {
                // Optimized for Hindi script
                nameMatch = criteriaBuilder.and(
                        criteriaBuilder.equal(criteriaBuilder.lower(scriptJoin.get("code")), "hi"),
                        criteriaBuilder.like(criteriaBuilder.lower(detailJoin.get("name")), rawPattern)
                );
            } else if ("en".equalsIgnoreCase(scriptCode)) {
                // Optimized for English / Latin with unaccent
                nameMatch = criteriaBuilder.or(
                        criteriaBuilder.and(
                                criteriaBuilder.equal(criteriaBuilder.lower(scriptJoin.get("code")), "en"),
                                criteriaBuilder.or(
                                        criteriaBuilder.like(unaccentName, normPattern),
                                        criteriaBuilder.like(criteriaBuilder.lower(detailJoin.get("name")), rawPattern)
                                )
                        ),
                        criteriaBuilder.like(unaccentName, normPattern),
                        criteriaBuilder.like(criteriaBuilder.lower(detailJoin.get("name")), rawPattern)
                );
            } else {
                nameMatch = criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(detailJoin.get("name")), rawPattern),
                        criteriaBuilder.like(unaccentName, normPattern)
                );
            }

            return nameMatch;
        };

        Page<AuthorEntity> authorPage = authorRepository.findAll(authorSpec, PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "id")));

        // Fallback: If no script-specific authors found, try broad search
        if (!authorPage.hasContent() && !"unknown".equalsIgnoreCase(scriptCode)) {
            Specification<AuthorEntity> fallbackSpec = (root, query, criteriaBuilder) -> {
                query.distinct(true);
                String normPattern = "%" + normalizedText + "%";
                var detailJoin = root.join("authorDetails", JoinType.LEFT);
                var unaccentName = criteriaBuilder.function("unaccent", String.class, criteriaBuilder.lower(detailJoin.get("name")));
                return criteriaBuilder.like(unaccentName, normPattern);
            };
            authorPage = authorRepository.findAll(fallbackSpec, PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "id")));
        }

        return authorPage.getContent().stream()
                .map(authorService::mapToAuthorDto)
                .collect(Collectors.toList());
    }

    private List<GenreSearchResultDto> searchContentsByGenre(String text, String scriptCode, String normalizedText, int limitPerGenre) {
        List<GenreSearchResultDto> results = new ArrayList<>();
        List<GenreEntity> genres = genreRepository.findAll();

        for (GenreEntity genre : genres) {
            Specification<ContentEntity> spec = buildContentGenreSpec(genre.getId(), text, scriptCode, normalizedText);
            Page<ContentEntity> page = contentRepository.findAll(spec, PageRequest.of(0, limitPerGenre, Sort.by(Sort.Direction.DESC, "id")));

            if (page.hasContent()) {
                GenreSearchResultDto group = new GenreSearchResultDto();
                group.setGenreId(genre.getId());
                group.setGenreName(genre.getName());
                group.setGenreSlug(genre.getSlug());
                group.setTotalCount(page.getTotalElements());
                group.setContents(page.getContent().stream()
                        .map(contentService::mapToContentDto)
                        .collect(Collectors.toList()));
                results.add(group);
            }
        }

        return results;
    }

    private Specification<ContentEntity> buildContentGenreSpec(Long genreId, String text, String scriptCode, String normalizedText) {
        return (root, query, criteriaBuilder) -> {
            query.distinct(true);
            String rawPattern = "%" + text.toLowerCase() + "%";
            String normPattern = "%" + normalizedText + "%";

            Predicate genrePredicate = criteriaBuilder.equal(root.get("genre").get("id"), genreId);

            var textJoin = root.join("contentTexts", JoinType.LEFT);
            var scriptJoin = textJoin.join("script", JoinType.LEFT);

            Predicate titleMatch;
            if ("ur".equalsIgnoreCase(scriptCode)) {
                // Optimized: search only in Urdu script content_texts
                titleMatch = criteriaBuilder.and(
                        criteriaBuilder.equal(criteriaBuilder.lower(scriptJoin.get("code")), "ur"),
                        criteriaBuilder.like(criteriaBuilder.lower(textJoin.get("title")), rawPattern)
                );
            } else if ("hi".equalsIgnoreCase(scriptCode)) {
                // Optimized: search only in Hindi script content_texts
                titleMatch = criteriaBuilder.and(
                        criteriaBuilder.equal(criteriaBuilder.lower(scriptJoin.get("code")), "hi"),
                        criteriaBuilder.like(criteriaBuilder.lower(textJoin.get("title")), rawPattern)
                );
            } else if ("en".equalsIgnoreCase(scriptCode)) {
                // Optimized: search in English script content_texts or unaccented title
                var unaccentTextTitle = criteriaBuilder.function("unaccent", String.class, criteriaBuilder.lower(textJoin.get("title")));
                var unaccentContentTitle = criteriaBuilder.function("unaccent", String.class, criteriaBuilder.lower(root.get("title")));

                titleMatch = criteriaBuilder.or(
                        criteriaBuilder.and(
                                criteriaBuilder.equal(criteriaBuilder.lower(scriptJoin.get("code")), "en"),
                                criteriaBuilder.or(
                                        criteriaBuilder.like(unaccentTextTitle, normPattern),
                                        criteriaBuilder.like(criteriaBuilder.lower(textJoin.get("title")), rawPattern)
                                )
                        ),
                        criteriaBuilder.like(unaccentTextTitle, normPattern),
                        criteriaBuilder.like(unaccentContentTitle, normPattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), rawPattern)
                );
            } else {
                var unaccentTextTitle = criteriaBuilder.function("unaccent", String.class, criteriaBuilder.lower(textJoin.get("title")));
                var unaccentContentTitle = criteriaBuilder.function("unaccent", String.class, criteriaBuilder.lower(root.get("title")));

                titleMatch = criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(textJoin.get("title")), rawPattern),
                        criteriaBuilder.like(unaccentTextTitle, normPattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), rawPattern),
                        criteriaBuilder.like(unaccentContentTitle, normPattern)
                );
            }

            return criteriaBuilder.and(genrePredicate, titleMatch);
        };
    }
}
