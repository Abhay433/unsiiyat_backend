package com.unsiiyat.backend.modules.author;

import java.time.LocalDate;

import com.unsiiyat.backend.common.filters.BaseFilterRequest;

public class AuthorFilterRequest extends BaseFilterRequest {

    private LocalDate birthDate;
    private LocalDate deathDate;

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

    @Override
    public String toString() {
        return "AuthorFilterRequest{" +
                "birthDate=" + birthDate +
                ", deathDate=" + deathDate +
                '}' + super.toString();
    }
}
