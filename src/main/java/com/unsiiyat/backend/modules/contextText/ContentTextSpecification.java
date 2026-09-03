package com.unsiiyat.backend.modules.contextText;

import org.springframework.data.jpa.domain.Specification;

import com.unsiiyat.backend.common.filters.SpecificationBuilder;

public class ContentTextSpecification {

    private ContentTextSpecification() {
    }

    public static Specification<ContentTextEntity> filter(ContentTextFilterRequest filterRequest) {
        return new SpecificationBuilder<ContentTextEntity>()
                .with(contentIdEqual(filterRequest.getContentId()))
                .with(scriptIdEqual(filterRequest.getScriptId()))
                .with(titleEqual(filterRequest.getTitle()))
                .build();
    }

    public static Specification<ContentTextEntity> contentIdEqual(Long contentId) {
        return (root, query, criteriaBuilder) -> {
            if (contentId == null) {
                return null;
            }
            return criteriaBuilder.equal(root.get("content").get("id"), contentId);
        };
    }

    public static Specification<ContentTextEntity> scriptIdEqual(Long scriptId) {
        return (root, query, criteriaBuilder) -> {
            if (scriptId == null) {
                return null;
            }
            return criteriaBuilder.equal(root.get("script").get("id"), scriptId);
        };
    }

    public static Specification<ContentTextEntity> titleEqual(String title) {
        return (root, query, criteriaBuilder) -> {
            if (title == null) {
                return null;
            }
            return criteriaBuilder.equal(root.get("title"), title);
        };
    }
}
