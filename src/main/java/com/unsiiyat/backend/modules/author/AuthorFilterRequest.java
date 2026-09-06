package com.unsiiyat.backend.modules.author;

import java.time.LocalDate;

import com.unsiiyat.backend.common.filters.BaseFilterRequest;

public class AuthorFilterRequest extends BaseFilterRequest {

    private LocalDate birthDate;
    private LocalDate deathDate;
    private String search;

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
                "birthDate=" + birthDate +
                ", deathDate=" + deathDate +
                ", search='" + search + '\'' +
                '}' + super.toString();
    }
}
