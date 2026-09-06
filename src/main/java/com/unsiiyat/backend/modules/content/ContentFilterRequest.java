package com.unsiiyat.backend.modules.content;

import com.unsiiyat.backend.common.filters.BaseFilterRequest;

public class ContentFilterRequest extends BaseFilterRequest {

    private Long genreId;
    private Long authorId;
    private String title;
    private Long themeId;
    private String search;
    private String authorName;

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

    public Long getThemeId() {
        return themeId;
    }

    public void setThemeId(Long themeId) {
        this.themeId = themeId;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    @Override
    public String toString() {
        return "ContentFilterRequest{" +
                "genreId=" + genreId +
                ", authorId=" + authorId +
                ", title='" + title + '\'' +
                ", themeId=" + themeId +
                ", search='" + search + '\'' +
                ", authorName='" + authorName + '\'' +
                '}' + super.toString();
    }
}
