package com.unsiiyat.backend.modules.contextText;

public class ContentTextDto {

    private Long id;
    private Long contentId;
    private Long scriptId;
    private String title;
    private String body;

    public ContentTextDto() {
    }

    public ContentTextDto(Long id, Long contentId, Long scriptId, String title, String body) {
        this.id = id;
        this.contentId = contentId;
        this.scriptId = scriptId;
        this.title = title;
        this.body = body;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getContentId() {
        return contentId;
    }

    public void setContentId(Long contentId) {
        this.contentId = contentId;
    }

    public Long getScriptId() {
        return scriptId;
    }

    public void setScriptId(Long scriptId) {
        this.scriptId = scriptId;
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
}
