package com.unsiiyat.backend.modules.authorDetail;

import org.springframework.data.jpa.domain.Specification;

import com.unsiiyat.backend.common.filters.SpecificationBuilder;

public class AuthorDetailSpecification {

    private AuthorDetailSpecification() {
    }

    public static Specification<AuthorDetailEntity> filter(AuthorDetailFilterRequest filterRequest) {
        return new SpecificationBuilder<AuthorDetailEntity>()
                .with(authorIdEqual(filterRequest.getAuthorId()))
                .with(scriptIdEqual(filterRequest.getScriptId()))
                .with(nameEqual(filterRequest.getName()))
                .build();
    }

    public static Specification<AuthorDetailEntity> authorIdEqual(Long authorId) {
        return (root, query, criteriaBuilder) -> {
            if (authorId == null) {
                return null;
            }
            return criteriaBuilder.equal(root.get("author").get("id"), authorId);
        };
    }

    public static Specification<AuthorDetailEntity> scriptIdEqual(Long scriptId) {
        return (root, query, criteriaBuilder) -> {
            if (scriptId == null) {
                return null;
            }
            return criteriaBuilder.equal(root.get("script").get("id"), scriptId);
        };
    }

    public static Specification<AuthorDetailEntity> nameEqual(String name) {
        return (root, query, criteriaBuilder) -> {
            if (name == null) {
                return null;
            }
            return criteriaBuilder.equal(root.get("name"), name);
        };
    }
}
