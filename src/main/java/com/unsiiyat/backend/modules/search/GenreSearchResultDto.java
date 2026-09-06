package com.unsiiyat.backend.modules.search;

import java.util.ArrayList;
import java.util.List;

import com.unsiiyat.backend.modules.content.ContentDto;

public class GenreSearchResultDto {

    private Long genreId;
    private String genreName;
    private String genreSlug;
    private long totalCount;
    private List<ContentDto> contents = new ArrayList<>();

    public GenreSearchResultDto() {
    }

    public GenreSearchResultDto(Long genreId, String genreName, String genreSlug, long totalCount, List<ContentDto> contents) {
        this.genreId = genreId;
        this.genreName = genreName;
        this.genreSlug = genreSlug;
        this.totalCount = totalCount;
        this.contents = contents;
    }

    public Long getGenreId() {
        return genreId;
    }

    public void setGenreId(Long genreId) {
        this.genreId = genreId;
    }

    public String getGenreName() {
        return genreName;
    }

    public void setGenreName(String genreName) {
        this.genreName = genreName;
    }

    public String getGenreSlug() {
        return genreSlug;
    }

    public void setGenreSlug(String genreSlug) {
        this.genreSlug = genreSlug;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public List<ContentDto> getContents() {
        return contents;
    }

    public void setContents(List<ContentDto> contents) {
        this.contents = contents;
    }
}
