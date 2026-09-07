package com.unsiiyat.backend.modules.content;

import com.unsiiyat.backend.modules.author.AuthorEntity;
import com.unsiiyat.backend.modules.contextText.ContentTextEntity;
import com.unsiiyat.backend.modules.genre.GenreEntity;
import com.unsiiyat.backend.modules.theme.ThemeEntity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "contents")
public class ContentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "genre_id", nullable = false)
    private GenreEntity genre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private AuthorEntity author;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "content", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ContentTextEntity> contentTexts = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "content_themes", joinColumns = @JoinColumn(name = "content_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "theme_id", referencedColumnName = "id"))
    private Set<ThemeEntity> themes = new HashSet<>();

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public ContentEntity() {
    }

    public ContentEntity(GenreEntity genre, AuthorEntity author) {
        this.genre = genre;
        this.author = author;

    }

    public ContentEntity(Long id, GenreEntity genre, AuthorEntity author) {
        this.id = id;
        this.genre = genre;
        this.author = author;

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public GenreEntity getGenre() {
        return genre;
    }

    public void setGenre(GenreEntity genre) {
        this.genre = genre;
    }

    public AuthorEntity getAuthor() {
        return author;
    }

    public void setAuthor(AuthorEntity author) {
        this.author = author;
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

    public List<ContentTextEntity> getContentTexts() {
        return contentTexts;
    }

    public void setContentTexts(List<ContentTextEntity> contentTexts) {
        this.contentTexts = contentTexts;
    }

    public Set<ThemeEntity> getThemes() {
        return themes;
    }

    public void setThemes(Set<ThemeEntity> themes) {
        this.themes = themes;
    }

    public void addContentText(ContentTextEntity contentText) {
        this.contentTexts.add(contentText);
        contentText.setContent(this);
    }

    public void removeContentText(ContentTextEntity contentText) {
        this.contentTexts.remove(contentText);
        contentText.setContent(null);
    }

    public void addTheme(ThemeEntity theme) {
        this.themes.add(theme);
    }

    public void removeTheme(ThemeEntity theme) {
        this.themes.remove(theme);
    }
}
