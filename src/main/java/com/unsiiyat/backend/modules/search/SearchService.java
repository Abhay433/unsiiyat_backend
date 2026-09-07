package com.unsiiyat.backend.modules.search;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unsiiyat.backend.common.exceptions.ResourceNotFoundException;
import com.unsiiyat.backend.common.filters.OffsetLimitPageRequest;
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
        int page = (request != null && request.getPage() != null && request.getPage() >= 0) ? request.getPage() : 0;
        Integer requestedSize = (request != null && request.getSize() != null && request.getSize() > 0) ? request.getSize() : null;

        // 1. Detect language / script
        LanguageDetectorUtil.LanguageType langType = LanguageDetectorUtil.detectLanguage(cleanText);
        String scriptCode = langType.getCode();
        String scriptName = langType.getDisplayName();
        String normalizedText = LanguageDetectorUtil.normalizeText(cleanText);

        SearchResponseDto response = new SearchResponseDto(cleanText, scriptCode, scriptName);
        response.setPage(page);

        // Content Pagination rule:
        // First time (page 0): default size = 5
        // Next time (page 1 onwards): default size = 10 (10-10 kr ke)
        long contentOffset;
        int contentLimit;
        if (page == 0) {
            contentLimit = (requestedSize != null) ? requestedSize : 5;
            contentOffset = 0;
        } else {
            contentLimit = (requestedSize != null) ? requestedSize : 10;
            contentOffset = 5L + (long) (page - 1) * contentLimit;
        }

        // Author Pagination rule:
        // First time (page 0): default size = 3
        // Next time (page 1 onwards): default size = 10 (10-10 kr ke)
        long authorOffset;
        int authorLimit;
        if (page == 0) {
            authorLimit = (requestedSize != null) ? requestedSize : 3;
            authorOffset = 0;
        } else {
            authorLimit = (requestedSize != null) ? requestedSize : 10;
            authorOffset = 3L + (long) (page - 1) * authorLimit;
        }
        String type = request != null ? request.getType() : null;
        Long targetGenreId = request != null ? request.getGenreId() : null;

        if ("authors".equalsIgnoreCase(type)) {
            response.setPageSize(authorLimit);
        } else {
            response.setPageSize(contentLimit);
        }

        // 2. Search Authors (Max 3 on initial search)
        if (type == null || "all".equalsIgnoreCase(type) || "authors".equalsIgnoreCase(type)) {
            Page<AuthorEntity> authorPage = searchAuthorsPage(cleanText, scriptCode, normalizedText, authorOffset, authorLimit);
            List<AuthorDto> authors = authorPage.getContent().stream()
                    .map(authorService::mapToAuthorDto)
                    .collect(Collectors.toList());
            response.setAuthors(authors);
            long totalAuthors = authorPage.getTotalElements();
            response.setTotalAuthors(totalAuthors);
            response.setTotalAuthorPages(calculateAuthorTotalPages(totalAuthors));
            response.setAuthorsHasMore((authorOffset + authors.size()) < totalAuthors);
        }

        // 3. Search Contents (by genre)
        if (type == null || "all".equalsIgnoreCase(type) || "contents".equalsIgnoreCase(type)) {
            List<GenreSearchResultDto> resultsByGenre = searchContentsByGenre(cleanText, scriptCode, normalizedText, targetGenreId, page, contentOffset, contentLimit);
            response.setResultsByGenre(resultsByGenre);
        }

        return response;
    }

    @Transactional(readOnly = true)
    public GenreSearchResultDto searchGenreContents(SearchRequestDto request) {
        String text = request != null ? request.getText() : "";
        Long genreId = request != null ? request.getGenreId() : null;
        if (genreId == null) {
            throw new ResourceNotFoundException("Genre ID is required for genre contents search");
        }

        int page = (request != null && request.getPage() != null && request.getPage() >= 0) ? request.getPage() : 0;
        Integer requestedSize = (request != null && request.getSize() != null && request.getSize() > 0) ? request.getSize() : null;

        long offset;
        int limit;
        if (page == 0) {
            limit = (requestedSize != null) ? requestedSize : 5;
            offset = 0;
        } else {
            limit = (requestedSize != null) ? requestedSize : 10;
            offset = 5L + (long) (page - 1) * limit;
        }

        String cleanText = (text != null) ? text.trim() : "";
        LanguageDetectorUtil.LanguageType langType = LanguageDetectorUtil.detectLanguage(cleanText);
        String scriptCode = langType.getCode();
        String normalizedText = LanguageDetectorUtil.normalizeText(cleanText);

        List<GenreSearchResultDto> results = searchContentsByGenre(cleanText, scriptCode, normalizedText, genreId, page, offset, limit);
        if (!results.isEmpty()) {
            return results.get(0);
        }

        GenreEntity genre = genreRepository.findById(genreId)
                .orElseThrow(() -> new ResourceNotFoundException("Genre not found with id: " + genreId));
        return new GenreSearchResultDto(genre.getId(), genre.getName(), genre.getSlug(), 0, page, limit, 0, false, List.of());
    }

    @Transactional(readOnly = true)
    public SearchResponseDto searchAuthorsOnly(SearchRequestDto request) {
        if (request == null) {
            request = new SearchRequestDto("");
        }
        request.setType("authors");
        return search(request);
    }

    public static int calculateTotalPages(long totalCount) {
        if (totalCount <= 0) {
            return 0;
        }
        if (totalCount <= 5) {
            return 1;
        }
        return 1 + (int) Math.ceil((double) (totalCount - 5) / 10.0);
    }

    public static int calculateAuthorTotalPages(long totalCount) {
        if (totalCount <= 0) {
            return 0;
        }
        if (totalCount <= 3) {
            return 1;
        }
        return 1 + (int) Math.ceil((double) (totalCount - 3) / 10.0);
    }

    private Page<AuthorEntity> searchAuthorsPage(String text, String scriptCode, String normalizedText, long offset, int limit) {
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

        Pageable pageable = new OffsetLimitPageRequest(offset, limit, Sort.by(Sort.Direction.DESC, "id"));
        Page<AuthorEntity> authorPage = authorRepository.findAll(authorSpec, pageable);

        // Fallback: If no script-specific authors found, try broad search
        if (!authorPage.hasContent() && offset == 0 && !"unknown".equalsIgnoreCase(scriptCode)) {
            Specification<AuthorEntity> fallbackSpec = (root, query, criteriaBuilder) -> {
                query.distinct(true);
                String normPattern = "%" + normalizedText + "%";
                var detailJoin = root.join("authorDetails", JoinType.LEFT);
                var unaccentName = criteriaBuilder.function("unaccent", String.class, criteriaBuilder.lower(detailJoin.get("name")));
                return criteriaBuilder.like(unaccentName, normPattern);
            };
            authorPage = authorRepository.findAll(fallbackSpec, pageable);
        }

        return authorPage;
    }

    private List<GenreSearchResultDto> searchContentsByGenre(
            String text, String scriptCode, String normalizedText,
            Long targetGenreId, int page, long offset, int limit) {

        List<GenreSearchResultDto> results = new ArrayList<>();
        List<GenreEntity> genres;

        if (targetGenreId != null) {
            genres = genreRepository.findById(targetGenreId).map(List::of).orElse(List.of());
        } else {
            genres = genreRepository.findAll();
        }

        Pageable pageable = new OffsetLimitPageRequest(offset, limit, Sort.by(Sort.Direction.DESC, "id"));

        for (GenreEntity genre : genres) {
            Specification<ContentEntity> spec = buildContentGenreSpec(genre.getId(), text, scriptCode, normalizedText);
            Page<ContentEntity> contentPage = contentRepository.findAll(spec, pageable);

            if (contentPage.hasContent() || targetGenreId != null) {
                GenreSearchResultDto group = new GenreSearchResultDto();
                group.setGenreId(genre.getId());
                group.setGenreName(genre.getName());
                group.setGenreSlug(genre.getSlug());
                group.setTotalCount(contentPage.getTotalElements());
                group.setPage(page);
                group.setPageSize(limit);
                group.setTotalPages(calculateTotalPages(contentPage.getTotalElements()));
                group.setHasMore((offset + contentPage.getContent().size()) < contentPage.getTotalElements());
                group.setContents(contentPage.getContent().stream()
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

                titleMatch = criteriaBuilder.or(
                        criteriaBuilder.and(
                                criteriaBuilder.equal(criteriaBuilder.lower(scriptJoin.get("code")), "en"),
                                criteriaBuilder.or(
                                        criteriaBuilder.like(unaccentTextTitle, normPattern),
                                        criteriaBuilder.like(criteriaBuilder.lower(textJoin.get("title")), rawPattern)
                                )
                        ),
                        criteriaBuilder.like(unaccentTextTitle, normPattern),
                        criteriaBuilder.like(criteriaBuilder.lower(textJoin.get("title")), rawPattern)
                );
            } else {
                var unaccentTextTitle = criteriaBuilder.function("unaccent", String.class, criteriaBuilder.lower(textJoin.get("title")));

                titleMatch = criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(textJoin.get("title")), rawPattern),
                        criteriaBuilder.like(unaccentTextTitle, normPattern)
                );
            }

            return criteriaBuilder.and(genrePredicate, titleMatch);
        };
    }
}
