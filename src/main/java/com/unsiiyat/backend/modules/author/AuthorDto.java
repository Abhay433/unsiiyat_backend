package com.unsiiyat.backend.modules.author;

import com.unsiiyat.backend.modules.authorDetail.AuthorDetailDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AuthorDto {

    private Long id;
    private String avatarUrl;
    private String primaryName;
    private String primaryBio;
    private String name;
    private String urName;
    private String urBio;
    private String hiName;
    private String hiBio;
    private String enName;
    private String enBio;
    private LocalDate birthDate;
    private LocalDate deathDate;
    private List<AuthorDetailDto> details = new ArrayList<>();
    private List<AuthorDetailDto> authorDetails = new ArrayList<>();
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

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getPrimaryName() {
        return primaryName;
    }

    public void setPrimaryName(String primaryName) {
        this.primaryName = primaryName;
    }

    public String getPrimaryBio() {
        return primaryBio;
    }

    public void setPrimaryBio(String primaryBio) {
        this.primaryBio = primaryBio;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrName() {
        return urName;
    }

    public void setUrName(String urName) {
        this.urName = urName;
    }

    public String getUrBio() {
        return urBio;
    }

    public void setUrBio(String urBio) {
        this.urBio = urBio;
    }

    public String getHiName() {
        return hiName;
    }

    public void setHiName(String hiName) {
        this.hiName = hiName;
    }

    public String getHiBio() {
        return hiBio;
    }

    public void setHiBio(String hiBio) {
        this.hiBio = hiBio;
    }

    public String getEnName() {
        return enName;
    }

    public void setEnName(String enName) {
        this.enName = enName;
    }

    public String getEnBio() {
        return enBio;
    }

    public void setEnBio(String enBio) {
        this.enBio = enBio;
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

    public List<AuthorDetailDto> getDetails() {
        return details;
    }

    public void setDetails(List<AuthorDetailDto> details) {
        this.details = details;
    }

    public List<AuthorDetailDto> getAuthorDetails() {
        return authorDetails;
    }

    public void setAuthorDetails(List<AuthorDetailDto> authorDetails) {
        this.authorDetails = authorDetails;
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
