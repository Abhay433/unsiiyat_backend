package com.unsiiyat.backend.modules.theme;

import org.springframework.data.jpa.domain.Specification;

import com.unsiiyat.backend.common.filters.SpecificationBuilder;

public class ThemeSpecification {

    private ThemeSpecification() {
    }

    public static Specification<ThemeEntity> filter(ThemeFilterRequest filterRequest) {
        return new SpecificationBuilder<ThemeEntity>()
                .with(nameEqual(filterRequest.getName()))
                .with(slugEqual(filterRequest.getSlug()))
                .build();
    }

    public static Specification<ThemeEntity> nameEqual(String name) {
        return (root, query, criteriaBuilder) -> {
            if (name == null) {
                return null;
            }
            return criteriaBuilder.equal(root.get("name"), name);
        };
    }

    public static Specification<ThemeEntity> slugEqual(String slug) {
        return (root, query, criteriaBuilder) -> {
            if (slug == null) {
                return null;
            }
            return criteriaBuilder.equal(root.get("slug"), slug);
        };
    }

}
