package com.unsiiyat.backend.modules.search;

import java.util.ArrayList;
import java.util.List;

import com.unsiiyat.backend.modules.content.ContentDto;

public class GenreSearchResultDto {

    private Long genreId;
    private String genreName;
    private String genreSlug;
    private long totalCount;
    private int page = 0;
    private int pageSize = 5;
    private int totalPages = 0;
    private boolean hasMore = false;
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

    public GenreSearchResultDto(Long genreId, String genreName, String genreSlug, long totalCount, int page, int pageSize, int totalPages, boolean hasMore, List<ContentDto> contents) {
        this.genreId = genreId;
        this.genreName = genreName;
        this.genreSlug = genreSlug;
        this.totalCount = totalCount;
        this.page = page;
        this.pageSize = pageSize;
        this.totalPages = totalPages;
        this.hasMore = hasMore;
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

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public boolean isHasMore() {
        return hasMore;
    }

    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }

    public List<ContentDto> getContents() {
        return contents;
    }

    public void setContents(List<ContentDto> contents) {
        this.contents = contents;
    }
}
