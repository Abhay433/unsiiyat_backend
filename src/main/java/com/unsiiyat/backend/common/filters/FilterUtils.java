package com.unsiiyat.backend.common.filters;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class FilterUtils {

	private FilterUtils() {
	}

	public static Predicate likeIgnoreCase(CriteriaBuilder cb, Root<?> root, String field, String value) {

		if (value == null || value.isBlank()) {
			return null;
		}

		return cb.like(cb.lower(root.get(field)), "%" + value.toLowerCase() + "%");
	}

	public static Predicate equal(CriteriaBuilder cb, Root<?> root, String field, Object value) {

		if (value == null) {
			return null;
		}

		return cb.equal(root.get(field), value);
	}

	public static Predicate greaterThan(CriteriaBuilder cb, Root<?> root, String field, Comparable value) {

		if (value == null) {
			return null;
		}

		return cb.greaterThan(root.get(field), value);
	}

	public static Predicate lessThan(CriteriaBuilder cb, Root<?> root, String field, Comparable value) {

		if (value == null) {
			return null;
		}

		return cb.lessThan(root.get(field), value);
	}

	public static Predicate joinEqual(CriteriaBuilder cb, Root<?> root, String joinField, String targetField,
			Object value) {

		if (value == null) {
			return null;
		}

		return cb.equal(root.get(joinField).get(targetField), value);
	}

	public static Predicate startsWith(CriteriaBuilder cb, Root<?> root, String field, String value) {

		if (value == null || value.isBlank()) {
			return null;
		}

		return cb.like(cb.lower(root.get(field)), value.toLowerCase() + "%");
	}

	public static Predicate endsWith(CriteriaBuilder cb, Root<?> root, String field, String value) {

		if (value == null || value.isBlank()) {
			return null;
		}

		return cb.like(cb.lower(root.get(field)), "%" + value.toLowerCase());
	}
}
