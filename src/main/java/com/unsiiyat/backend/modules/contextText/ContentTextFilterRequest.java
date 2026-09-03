package com.unsiiyat.backend.modules.contextText;

import com.unsiiyat.backend.common.filters.BaseFilterRequest;

public class ContentTextFilterRequest extends BaseFilterRequest {

    private Long contentId;
    private Long scriptId;
    private String title;

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

    @Override
    public String toString() {
        return "ContentTextFilterRequest{" +
                "contentId=" + contentId +
                ", scriptId=" + scriptId +
                ", title='" + title + '\'' +
                '}' + super.toString();
    }
}
