package com.unsiiyat.backend.modules.authorDetail;

public class AuthorDetailDto {

    private Long id;
    private Long authorId;
    private Long scriptId;
    private String name;
    private String biography;

    public AuthorDetailDto() {
    }

    public AuthorDetailDto(Long id, Long authorId, Long scriptId, String name, String biography) {
        this.id = id;
        this.authorId = authorId;
        this.scriptId = scriptId;
        this.name = name;
        this.biography = biography;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public Long getScriptId() {
        return scriptId;
    }

    public void setScriptId(Long scriptId) {
        this.scriptId = scriptId;
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
}
