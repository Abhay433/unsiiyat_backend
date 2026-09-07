package com.unsiiyat.backend.modules.search;

public class SearchRequestDto {

    private String text;
    private String query;
    private Integer page = 0;
    private Integer size;
    private Long genreId;
    private String type;

    public SearchRequestDto() {
    }

    public SearchRequestDto(String text) {
        this.text = text;
    }

    public SearchRequestDto(String text, Integer page, Integer size) {
        this.text = text;
        this.page = page;
        this.size = size;
    }

    public String getText() {
        if (text != null && !text.trim().isEmpty()) {
            return text.trim();
        }
        return query != null ? query.trim() : "";
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Long getGenreId() {
        return genreId;
    }

    public void setGenreId(Long genreId) {
        this.genreId = genreId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "SearchRequestDto{" +
                "text='" + getText() + '\'' +
                ", page=" + page +
                ", size=" + size +
                ", genreId=" + genreId +
                ", type='" + type + '\'' +
                '}';
    }
}
