package com.unsiiyat.backend.common.response;

import org.springframework.data.domain.Page;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;

public class PagedResponse<T> {

    private boolean success;
    private String message;
    @JsonProperty("data")
    private List<T> content;

    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean last;
    private LocalDateTime timestamp;

    public PagedResponse() {
    }

    public PagedResponse(boolean success, String message, List<T> content, int page, int size, long totalElements,
            int totalPages, boolean last, LocalDateTime timestamp) {
        this.success = success;
        this.message = message;
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.last = last;
        this.timestamp = timestamp;
    }

    public static <T> PagedResponse<T> fromPage(Page<T> pageData, String message) {

        return new PagedResponse<>(true, message, pageData.getContent(), pageData.getNumber(), pageData.getSize(),
                pageData.getTotalElements(), pageData.getTotalPages(), pageData.isLast(), LocalDateTime.now());
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public List<T> getContent() {
        return content;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public boolean isLast() {
        return last;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
