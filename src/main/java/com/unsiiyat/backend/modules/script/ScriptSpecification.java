package com.unsiiyat.backend.modules.script;

import org.springframework.data.jpa.domain.Specification;

import com.unsiiyat.backend.common.filters.SpecificationBuilder;

public class ScriptSpecification {

    private ScriptSpecification() {
    }

    public static Specification<ScriptEntity> filter(ScriptFilterRequest filterRequest) {
        return new SpecificationBuilder<ScriptEntity>()
                .with(codeEqual(filterRequest.getCode()))
                .with(nameEqual(filterRequest.getName()))
                .build();
    }

    public static Specification<ScriptEntity> codeEqual(String code) {
        return (root, query, criteriaBuilder) -> {
            if (code == null) {
                return null;
            }
            return criteriaBuilder.equal(root.get("code"), code);
        };
    }

    public static Specification<ScriptEntity> nameEqual(String name) {
        return (root, query, criteriaBuilder) -> {
            if (name == null) {
                return null;
            }
            return criteriaBuilder.equal(root.get("name"), name);
        };
    }
}
