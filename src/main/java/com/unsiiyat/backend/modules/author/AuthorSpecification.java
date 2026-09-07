package com.unsiiyat.backend.modules.author;

import java.time.LocalDate;

import org.springframework.data.jpa.domain.Specification;

import com.unsiiyat.backend.common.filters.SpecificationBuilder;

public class AuthorSpecification {

    private AuthorSpecification() {
    }

    public static Specification<AuthorEntity> filter(AuthorFilterRequest filterRequest) {
        return new SpecificationBuilder<AuthorEntity>()
                .with(idEqual(filterRequest.getId()))
                .with(birthDateEqual(filterRequest.getBirthDate()))
                .with(deathDateEqual(filterRequest.getDeathDate()))
                .with(searchLike(filterRequest.getSearch()))
                .build();
    }

    public static Specification<AuthorEntity> idEqual(Long id) {
        return (root, query, criteriaBuilder) -> {
            if (id == null) {
                return null;
            }
            return criteriaBuilder.equal(root.get("id"), id);
        };
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

    public static Specification<AuthorEntity> searchLike(String search) {
        return (root, query, criteriaBuilder) -> {
            if (search == null || search.trim().isEmpty()) {
                return null;
            }
            String pattern = "%" + search.trim().toLowerCase() + "%";
            query.distinct(true);
            var authorDetailJoin = root.join("authorDetails", jakarta.persistence.criteria.JoinType.LEFT);
            return criteriaBuilder.or(
                criteriaBuilder.like(criteriaBuilder.lower(authorDetailJoin.get("name")), pattern)
            );
        };
    }
}
