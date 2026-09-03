package com.unsiiyat.backend.common.filters;

import org.springframework.data.jpa.domain.Specification;

public class GenericSpecification<T> {

    private GenericSpecification() {
    }

    public static <T> Specification<T> and(
            Specification<T> first,
            Specification<T> second) {

        if (first == null) {
            return second;
        }

        return first.and(second);
    }
}
