package com.unsiiyat.backend.modules.content;

import com.unsiiyat.backend.common.filters.BaseFilterRequest;

public class ContentFilterRequest extends BaseFilterRequest {

    private Long id;
    private Long genreId;
    private Long authorId;
    private String title;
    private Long themeId;
    private String search;
    private String authorName;
    private Long scriptId;
    private String scriptCode;

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

    public Long getScriptId() {
        return scriptId;
    }

    public void setScriptId(Long scriptId) {
        this.scriptId = scriptId;
    }

    public String getScriptCode() {
        return scriptCode;
    }

    public void setScriptCode(String scriptCode) {
        this.scriptCode = scriptCode;
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
