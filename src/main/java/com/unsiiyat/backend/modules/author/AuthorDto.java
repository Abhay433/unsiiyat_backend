package com.unsiiyat.backend.modules.author;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AuthorDto {

    private Long id;
    private LocalDate birthDate;
    private LocalDate deathDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public AuthorDto() {
    }

    public AuthorDto(Long id, LocalDate birthDate, LocalDate deathDate) {
        this.id = id;
        this.birthDate = birthDate;
        this.deathDate = deathDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
