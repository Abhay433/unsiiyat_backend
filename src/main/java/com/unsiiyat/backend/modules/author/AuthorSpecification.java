package com.unsiiyat.backend.modules.author;

import java.time.LocalDate;

import org.springframework.data.jpa.domain.Specification;

import com.unsiiyat.backend.common.filters.SpecificationBuilder;

public class AuthorSpecification {

    private AuthorSpecification() {
    }

    public static Specification<AuthorEntity> filter(AuthorFilterRequest filterRequest) {
        return new SpecificationBuilder<AuthorEntity>()
                .with(birthDateEqual(filterRequest.getBirthDate()))
                .with(deathDateEqual(filterRequest.getDeathDate()))
                .build();
    }

    public static Specification<AuthorEntity> birthDateEqual(LocalDate birthDate) {
        return (root, query, criteriaBuilder) -> {
            if (birthDate == null) {
                return null;
            }
            return criteriaBuilder.equal(root.get("birthDate"), birthDate);
        };
    }

    public static Specification<AuthorEntity> deathDateEqual(LocalDate deathDate) {
        return (root, query, criteriaBuilder) -> {
            if (deathDate == null) {
                return null;
            }
            return criteriaBuilder.equal(root.get("deathDate"), deathDate);
        };
    }
}
