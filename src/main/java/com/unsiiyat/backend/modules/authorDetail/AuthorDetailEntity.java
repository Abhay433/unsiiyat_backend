package com.unsiiyat.backend.modules.authorDetail;

import java.time.LocalDateTime;

import com.unsiiyat.backend.modules.author.AuthorEntity;
import com.unsiiyat.backend.modules.script.ScriptEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "author_detail", uniqueConstraints = {
        @UniqueConstraint(name = "uk_author_detail_author_script", columnNames = { "author_id", "script_id" })
})
public class AuthorDetailEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    private AuthorEntity author;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "script_id", nullable = false)
    private ScriptEntity script;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "biography", columnDefinition = "TEXT")
    private String biography;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public AuthorDetailEntity() {
    }

    public AuthorDetailEntity(AuthorEntity author, ScriptEntity script, String name, String biography) {
        this.author = author;
        this.script = script;
        this.name = name;
        this.biography = biography;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public AuthorDetailEntity(Long id, AuthorEntity author, ScriptEntity script, String name, String biography,
            LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.author = author;
        this.script = script;
        this.name = name;
        this.biography = biography;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AuthorEntity getAuthor() {
        return author;
    }

    public void setAuthor(AuthorEntity author) {
        this.author = author;
    }

    public ScriptEntity getScript() {
        return script;
    }

    public void setScript(ScriptEntity script) {
        this.script = script;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
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

    @Override
    public String toString() {
        return "AuthorDetailEntity [id=" + id + ", authorId=" + author.getId() + ", scriptId=" + script.getId()
                + ", name=" + name + ", biography=" + biography + ", createdAt=" + createdAt + ", updatedAt="
                + updatedAt + "]";
    }

}
