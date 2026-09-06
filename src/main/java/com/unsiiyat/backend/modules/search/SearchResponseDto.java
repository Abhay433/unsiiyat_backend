package com.unsiiyat.backend.modules.search;

import java.util.ArrayList;
import java.util.List;

import com.unsiiyat.backend.modules.author.AuthorDto;

public class SearchResponseDto {

    private String text;
    private String detectedScript;
    private String detectedLanguage;
    private List<AuthorDto> authors = new ArrayList<>();
    private List<GenreSearchResultDto> resultsByGenre = new ArrayList<>();

    public SearchResponseDto() {
    }

    public SearchResponseDto(String text, String detectedScript, String detectedLanguage) {
        this.text = text;
        this.detectedScript = detectedScript;
        this.detectedLanguage = detectedLanguage;
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

    public List<AuthorDto> getAuthors() {
        return authors;
    }

    public void setAuthors(List<AuthorDto> authors) {
        this.authors = authors;
    }

    public List<GenreSearchResultDto> getResultsByGenre() {
        return resultsByGenre;
    }

    public void setResultsByGenre(List<GenreSearchResultDto> resultsByGenre) {
        this.resultsByGenre = resultsByGenre;
    }
}
