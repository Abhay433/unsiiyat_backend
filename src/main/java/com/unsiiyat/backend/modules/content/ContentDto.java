package com.unsiiyat.backend.modules.content;

import com.unsiiyat.backend.modules.author.AuthorDto;
import com.unsiiyat.backend.modules.contextText.ContentTextDto;
import com.unsiiyat.backend.modules.genre.GenreDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ContentDto {

    private Long id;
    private Long genreId;
    private Long authorId;
    private String title;
    private Set<Long> themeIds;
    private AuthorDto author;
    private GenreDto genre;
    private List<ContentTextDto> contentTexts = new ArrayList<>();
    private ContentTextDto primaryText;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isSelected = false;

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

    public AuthorDto getAuthor() {
        return author;
    }

    public void setAuthor(AuthorDto author) {
        this.author = author;
    }

    public GenreDto getGenre() {
        return genre;
    }

    public void setGenre(GenreDto genre) {
        this.genre = genre;
    }

    public List<ContentTextDto> getContentTexts() {
        return contentTexts;
    }

    public void setContentTexts(List<ContentTextDto> contentTexts) {
        this.contentTexts = contentTexts;
    }

    public ContentTextDto getPrimaryText() {
        return primaryText;
    }

    public void setPrimaryText(ContentTextDto primaryText) {
        this.primaryText = primaryText;
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

    public Boolean getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(Boolean isSelected) {
        this.isSelected = isSelected;
    }
}
