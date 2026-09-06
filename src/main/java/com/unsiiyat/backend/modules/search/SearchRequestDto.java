package com.unsiiyat.backend.modules.search;

public class SearchRequestDto {

    private String text;
    private String query;

    public SearchRequestDto() {
    }

    public SearchRequestDto(String text) {
        this.text = text;
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

    @Override
    public String toString() {
        return "SearchRequestDto{" +
                "text='" + getText() + '\'' +
                '}';
    }
}
