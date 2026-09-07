package com.unsiiyat.backend.common.filters;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.Serializable;
import java.util.Objects;

public class OffsetLimitPageRequest implements Pageable, Serializable {

    private static final long serialVersionUID = 1L;

    private final long offset;
    private final int limit;
    private final Sort sort;

    public OffsetLimitPageRequest(long offset, int limit, Sort sort) {
        if (offset < 0) {
            throw new IllegalArgumentException("Offset must not be negative");
        }
        if (limit < 1) {
            throw new IllegalArgumentException("Limit must not be less than one");
        }
        this.offset = offset;
        this.limit = limit;
        this.sort = sort != null ? sort : Sort.unsorted();
    }

    public OffsetLimitPageRequest(long offset, int limit) {
        this(offset, limit, Sort.unsorted());
    }

    @Override
    public int getPageNumber() {
        return (int) (offset / limit);
    }

    @Override
    public int getPageSize() {
        return limit;
    }

    @Override
    public long getOffset() {
        return offset;
    }

    @Override
    public Sort getSort() {
        return sort;
    }

    @Override
    public Pageable next() {
        return new OffsetLimitPageRequest(getOffset() + getPageSize(), getPageSize(), getSort());
    }

    public Pageable previous() {
        return hasPrevious() ? new OffsetLimitPageRequest(getOffset() - getPageSize(), getPageSize(), getSort()) : this;
    }

    @Override
    public Pageable previousOrFirst() {
        return hasPrevious() ? previous() : first();
    }

    @Override
    public Pageable first() {
        return new OffsetLimitPageRequest(0, getPageSize(), getSort());
    }

    @Override
    public Pageable withPage(int pageNumber) {
        return new OffsetLimitPageRequest((long) pageNumber * getPageSize(), getPageSize(), getSort());
    }

    @Override
    public boolean hasPrevious() {
        return offset > 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OffsetLimitPageRequest that)) return false;
        return offset == that.offset && limit == that.limit && Objects.equals(sort, that.sort);
    }

    @Override
    public int hashCode() {
        return Objects.hash(offset, limit, sort);
    }

    @Override
    public String toString() {
        return "OffsetLimitPageRequest{" +
                "offset=" + offset +
                ", limit=" + limit +
                ", sort=" + sort +
                '}';
    }
}
