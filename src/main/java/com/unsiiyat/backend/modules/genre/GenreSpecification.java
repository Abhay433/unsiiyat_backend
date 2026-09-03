package com.unsiiyat.backend.modules.genre;

import org.springframework.data.jpa.domain.Specification;

import com.unsiiyat.backend.common.filters.SpecificationBuilder;

public class GenreSpecification {

    private GenreSpecification() {
    }

    public static Specification<GenreEntity> filter(GenreFilterRequest filterRequest) {
        return new SpecificationBuilder<GenreEntity>()
                .with(nameEqual(filterRequest.getName()))
                .with(slugEqual(filterRequest.getSlug()))
                .build();
    }

    public static Specification<GenreEntity> nameEqual(String name) {
        return (root, query, criteriaBuilder) -> {
            if (name == null) {
                return null;
            }
            return criteriaBuilder.equal(root.get("name"), name);
        };
    }

    public static Specification<GenreEntity> slugEqual(String slug) {
        return (root, query, criteriaBuilder) -> {
            if (slug == null) {
                return null;
            }
            return criteriaBuilder.equal(root.get("slug"), slug);
        };
    }
}
