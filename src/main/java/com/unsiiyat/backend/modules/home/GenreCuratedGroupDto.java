package com.unsiiyat.backend.modules.home;

import java.util.ArrayList;
import java.util.List;

import com.unsiiyat.backend.modules.content.ContentDto;

public class GenreCuratedGroupDto {

    private Long genreId;
    private String genreName;
    private String genreSlug;
    private long totalSelected;
    private List<ContentDto> contents = new ArrayList<>();

    public GenreCuratedGroupDto() {
    }

    public GenreCuratedGroupDto(Long genreId, String genreName, String genreSlug, long totalSelected, List<ContentDto> contents) {
        this.genreId = genreId;
        this.genreName = genreName;
        this.genreSlug = genreSlug;
        this.totalSelected = totalSelected;
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

    public long getTotalSelected() {
        return totalSelected;
    }

    public void setTotalSelected(long totalSelected) {
        this.totalSelected = totalSelected;
    }

    public List<ContentDto> getContents() {
        return contents;
    }

    public void setContents(List<ContentDto> contents) {
        this.contents = contents;
    }
}
