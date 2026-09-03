package com.unsiiyat.backend.modules.contextText;

import java.time.LocalDateTime;

import com.unsiiyat.backend.modules.content.ContentEntity;
import com.unsiiyat.backend.modules.script.ScriptEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "content_texts", uniqueConstraints = {
        @UniqueConstraint(name = "uk_content_texts_content_script", columnNames = { "content_id", "script_id" })
})
public class ContentTextEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "content_id", nullable = false)
    private ContentEntity content;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "script_id", nullable = false)
    private ScriptEntity script;

    @Column(name = "title", length = 500)
    private String title;

    @Column(name = "body", nullable = false, columnDefinition = "TEXT")
    private String body;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public ContentTextEntity() {
    }

    public ContentTextEntity(ContentEntity content, ScriptEntity script, String title, String body) {
        this.content = content;
        this.script = script;
        this.title = title;
        this.body = body;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public ContentTextEntity(Long id, ContentEntity content, ScriptEntity script, String title, String body,
            LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.content = content;
        this.script = script;
        this.title = title;
        this.body = body;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ContentEntity getContent() {
        return content;
    }

    public void setContent(ContentEntity content) {
        this.content = content;
    }

    public ScriptEntity getScript() {
        return script;
    }

    public void setScript(ScriptEntity script) {
        this.script = script;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
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
        return "ContentTextEntity [id=" + id + ", contentId=" + content.getId() + ", scriptId=" + script.getId()
                + ", title=" + title + ", body=" + body + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + "]";
    }

}
