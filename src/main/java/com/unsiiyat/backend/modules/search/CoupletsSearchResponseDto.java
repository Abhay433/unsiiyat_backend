package com.unsiiyat.backend.modules.search;

import java.util.ArrayList;
import java.util.List;

public class CoupletsSearchResponseDto {

    private String text;
    private String detectedScript;
    private String detectedLanguage;
    private Long genreId;
    private String genreName;
    private String genreSlug;
    private long totalCount;
    private int page = 0;
    private int pageSize = 5;
    private int totalPages = 0;
    private boolean hasMore = false;
    private List<CoupletSearchResultDto> couplets = new ArrayList<>();

    public CoupletsSearchResponseDto() {
    }

    public CoupletsSearchResponseDto(String text, String detectedScript, String detectedLanguage) {
        this.text = text;
        this.detectedScript = detectedScript;
        this.detectedLanguage = detectedLanguage;
    }

    public CoupletsSearchResponseDto(String text, String detectedScript, String detectedLanguage, Long genreId,
            String genreName, String genreSlug, long totalCount, int page, int pageSize, int totalPages,
            boolean hasMore, List<CoupletSearchResultDto> couplets) {
        this.text = text;
        this.detectedScript = detectedScript;
        this.detectedLanguage = detectedLanguage;
        this.genreId = genreId;
        this.genreName = genreName;
        this.genreSlug = genreSlug;
        this.totalCount = totalCount;
        this.page = page;
        this.pageSize = pageSize;
        this.totalPages = totalPages;
        this.hasMore = hasMore;
        this.couplets = couplets != null ? couplets : new ArrayList<>();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDetectedScript() {
        return detectedScript;
    }

    public void setDetectedScript(String detectedScript) {
        this.detectedScript = detectedScript;
    }

    public String getDetectedLanguage() {
        return detectedLanguage;
    }

    public void setDetectedLanguage(String detectedLanguage) {
        this.detectedLanguage = detectedLanguage;
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

    public List<CoupletSearchResultDto> getCouplets() {
        return couplets;
    }

    public void setCouplets(List<CoupletSearchResultDto> couplets) {
        this.couplets = couplets;
    }
}
