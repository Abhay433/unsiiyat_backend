package com.unsiiyat.backend.modules.content;

import org.springframework.data.jpa.domain.Specification;

import com.unsiiyat.backend.common.filters.SpecificationBuilder;

public class ContentSpecification {

    private ContentSpecification() {
    }

    public static Specification<ContentEntity> filter(ContentFilterRequest filterRequest) {
        return new SpecificationBuilder<ContentEntity>()
                .with(genreIdEqual(filterRequest.getGenreId()))
                .with(authorIdEqual(filterRequest.getAuthorId()))
                .with(titleEqual(filterRequest.getTitle()))
                .with(themeIdEqual(filterRequest.getThemeId()))
                .build();
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

    public static Specification<ContentEntity> titleEqual(String title) {
        return (root, query, criteriaBuilder) -> {
            if (title == null) {
                return null;
            }
            return criteriaBuilder.equal(root.get("title"), title);
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
}
