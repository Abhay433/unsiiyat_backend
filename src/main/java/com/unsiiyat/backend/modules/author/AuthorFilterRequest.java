package com.unsiiyat.backend.modules.author;

import java.time.LocalDate;

import com.unsiiyat.backend.common.filters.BaseFilterRequest;

public class AuthorFilterRequest extends BaseFilterRequest {

    private Long id;
    private Long scriptId;
    private String scriptCode;
    private LocalDate birthDate;
    private LocalDate deathDate;
    private String search;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public LocalDate getDeathDate() {
        return deathDate;
    }

    public void setDeathDate(LocalDate deathDate) {
        this.deathDate = deathDate;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    @Override
    public String toString() {
        return "AuthorFilterRequest{" +
                "id=" + id +
                ", scriptId=" + scriptId +
                ", scriptCode='" + scriptCode + "'" +
                ", birthDate=" + birthDate +
                ", deathDate=" + deathDate +
                ", search='" + search + "'" +
                '}' + super.toString();
    }
}
