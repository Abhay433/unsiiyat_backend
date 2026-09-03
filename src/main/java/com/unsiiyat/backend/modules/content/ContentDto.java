package com.unsiiyat.backend.modules.content;

import java.time.LocalDateTime;
import java.util.Set;

public class ContentDto {

    private Long id;
    private Long genreId;
    private Long authorId;
    private String title;
    private Set<Long> themeIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ContentDto() {
    }

    public ContentDto(Long id, Long genreId, Long authorId, String title, Set<Long> themeIds) {
        this.id = id;
        this.genreId = genreId;
        this.authorId = authorId;
        this.title = title;
        this.themeIds = themeIds;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGenreId() {
        return genreId;
    }

    public void setGenreId(Long genreId) {
        this.genreId = genreId;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Set<Long> getThemeIds() {
        return themeIds;
    }

    public void setThemeIds(Set<Long> themeIds) {
        this.themeIds = themeIds;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
