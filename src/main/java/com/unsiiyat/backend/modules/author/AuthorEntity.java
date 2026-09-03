package com.unsiiyat.backend.modules.author;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.unsiiyat.backend.modules.authorDetail.AuthorDetailEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "authors")
public class AuthorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "death_date")
    private LocalDate deathDate;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<AuthorDetailEntity> authorDetails = new ArrayList<>();

    public AuthorEntity() {
    }

    public AuthorEntity(LocalDate birthDate, LocalDate deathDate) {
        this.birthDate = birthDate;
        this.deathDate = deathDate;
    }

    public AuthorEntity(Long id, LocalDate birthDate, LocalDate deathDate) {
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

    public List<AuthorDetailEntity> getAuthorDetails() {
        return authorDetails;
    }

    public void setAuthorDetails(List<AuthorDetailEntity> authorDetails) {
        this.authorDetails = authorDetails;
    }

    public void addAuthorDetail(AuthorDetailEntity detail) {
        authorDetails.add(detail);
        detail.setAuthor(this);
    }

    public void removeAuthorDetail(AuthorDetailEntity detail) {
        authorDetails.remove(detail);
        detail.setAuthor(null);
    }
}
