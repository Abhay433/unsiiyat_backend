package com.unsiiyat.backend.modules.content;

import org.springframework.data.jpa.domain.Specification;

import com.unsiiyat.backend.common.filters.SpecificationBuilder;

public class ContentSpecification {

    private ContentSpecification() {
    }

    public static Specification<ContentEntity> filter(ContentFilterRequest filterRequest) {
        return new SpecificationBuilder<ContentEntity>()
                .with(idEqual(filterRequest.getId()))
                .with(genreIdEqual(filterRequest.getGenreId()))
                .with(authorIdEqual(filterRequest.getAuthorId()))
                .with(titleLike(filterRequest.getTitle()))
                .with(authorNameLike(filterRequest.getAuthorName()))
                .with(themeIdEqual(filterRequest.getThemeId()))
                .with(searchLike(filterRequest.getSearch()))
                .with(scriptIdEqual(filterRequest.getScriptId()))
                .with(scriptCodeEqual(filterRequest.getScriptCode()))
                .with(isSelectedEqual(filterRequest.getIsSelected()))
                .build();
    }

    public static Specification<ContentEntity> idEqual(Long id) {
        return (root, query, criteriaBuilder) -> {
            if (id == null) {
                return null;
            }
            return criteriaBuilder.equal(root.get("id"), id);
        };
    }

    public static Specification<ContentEntity> scriptIdEqual(Long scriptId) {
        return (root, query, criteriaBuilder) -> {
            if (scriptId == null) {
                return null;
            }
            query.distinct(true);
            var textJoin = root.join("contentTexts", jakarta.persistence.criteria.JoinType.INNER);
            return criteriaBuilder.equal(textJoin.get("script").get("id"), scriptId);
        };
    }

    public static Specification<ContentEntity> scriptCodeEqual(String scriptCode) {
        return (root, query, criteriaBuilder) -> {
            if (scriptCode == null || scriptCode.trim().isEmpty()) {
                return null;
            }
            query.distinct(true);
            var textJoin = root.join("contentTexts", jakarta.persistence.criteria.JoinType.INNER);
            var scriptJoin = textJoin.join("script", jakarta.persistence.criteria.JoinType.INNER);
            return criteriaBuilder.equal(criteriaBuilder.lower(scriptJoin.get("code")), scriptCode.trim().toLowerCase());
        };
    }

    public static Specification<ContentEntity> genreIdEqual(Long genreId) {
        return (root, query, criteriaBuilder) -> {
            if (genreId == null) {
                return null;
            }
            return criteriaBuilder.equal(root.get("genre").get("id"), genreId);
        };
    }

    public static Specification<ContentEntity> authorIdEqual(Long authorId) {
        return (root, query, criteriaBuilder) -> {
            if (authorId == null) {
                return null;
            }
            return criteriaBuilder.equal(root.get("author").get("id"), authorId);
        };
    }

    public static Specification<ContentEntity> titleLike(String title) {
        return (root, query, criteriaBuilder) -> {
            if (title == null || title.trim().isEmpty()) {
                return null;
            }
            query.distinct(true);
            String cleanTitle = title.trim();
            String rawPattern = "%" + cleanTitle.toLowerCase() + "%";
            String normalizedTitle = com.unsiiyat.backend.common.util.LanguageDetectorUtil.normalizeText(cleanTitle);
            String normalizedPattern = "%" + normalizedTitle + "%";

            var textJoin = root.join("contentTexts", jakarta.persistence.criteria.JoinType.LEFT);
            var scriptJoin = textJoin.join("script", jakarta.persistence.criteria.JoinType.LEFT);

            String langCode = com.unsiiyat.backend.common.util.LanguageDetectorUtil.detectLanguageCode(cleanTitle);

            var unaccentTextTitle = criteriaBuilder.function("unaccent", String.class, criteriaBuilder.lower(textJoin.get("title")));

            jakarta.persistence.criteria.Predicate contentTextMatch = criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(textJoin.get("title")), rawPattern),
                    criteriaBuilder.like(unaccentTextTitle, normalizedPattern),
                    criteriaBuilder.like(unaccentTextTitle, rawPattern)
            );

            if (langCode != null && !"unknown".equalsIgnoreCase(langCode)) {
                contentTextMatch = criteriaBuilder.or(
                        criteriaBuilder.and(
                                criteriaBuilder.equal(criteriaBuilder.lower(scriptJoin.get("code")), langCode.toLowerCase()),
                                contentTextMatch
                        ),
                        contentTextMatch
                );
            }

            return contentTextMatch;
        };
    }

    public static Specification<ContentEntity> authorNameLike(String authorName) {
        return (root, query, criteriaBuilder) -> {
            if (authorName == null || authorName.trim().isEmpty()) {
                return null;
            }
            query.distinct(true);
            String clean = authorName.trim();
            String rawPattern = "%" + clean.toLowerCase() + "%";
            String normalized = com.unsiiyat.backend.common.util.LanguageDetectorUtil.normalizeText(clean);
            String normalizedPattern = "%" + normalized + "%";

            var authorJoin = root.join("author", jakarta.persistence.criteria.JoinType.LEFT);
            var authorDetailJoin = authorJoin.join("authorDetails", jakarta.persistence.criteria.JoinType.LEFT);
            var unaccentAuthorName = criteriaBuilder.function("unaccent", String.class, criteriaBuilder.lower(authorDetailJoin.get("name")));

            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(authorDetailJoin.get("name")), rawPattern),
                    criteriaBuilder.like(unaccentAuthorName, normalizedPattern),
                    criteriaBuilder.like(unaccentAuthorName, rawPattern)
            );
        };
    }

    public static Specification<ContentEntity> themeIdEqual(Long themeId) {
        return (root, query, criteriaBuilder) -> {
            if (themeId == null) {
                return null;
            }
            query.distinct(true);
            return criteriaBuilder.equal(root.join("themes").get("id"), themeId);
        };
    }

    public static Specification<ContentEntity> searchLike(String search) {
        return (root, query, criteriaBuilder) -> {
            if (search == null || search.trim().isEmpty()) {
                return null;
            }
            query.distinct(true);
            String clean = search.trim();
            String rawPattern = "%" + clean.toLowerCase() + "%";
            String normalized = com.unsiiyat.backend.common.util.LanguageDetectorUtil.normalizeText(clean);
            String normalizedPattern = "%" + normalized + "%";

            var authorJoin = root.join("author", jakarta.persistence.criteria.JoinType.LEFT);
            var authorDetailJoin = authorJoin.join("authorDetails", jakarta.persistence.criteria.JoinType.LEFT);
            var textJoin = root.join("contentTexts", jakarta.persistence.criteria.JoinType.LEFT);

            var unaccentTextTitle = criteriaBuilder.function("unaccent", String.class, criteriaBuilder.lower(textJoin.get("title")));
            var unaccentAuthorName = criteriaBuilder.function("unaccent", String.class, criteriaBuilder.lower(authorDetailJoin.get("name")));

            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(textJoin.get("title")), rawPattern),
                    criteriaBuilder.like(unaccentTextTitle, normalizedPattern),
                    criteriaBuilder.like(criteriaBuilder.lower(authorDetailJoin.get("name")), rawPattern),
                    criteriaBuilder.like(unaccentAuthorName, normalizedPattern)
            );
        };
    }

    public static Specification<ContentEntity> isSelectedEqual(Boolean isSelected) {
        return (root, query, criteriaBuilder) -> {
            if (isSelected == null) {
                return null;
            }
            return criteriaBuilder.equal(root.get("isSelected"), isSelected);
        };
    }
}
