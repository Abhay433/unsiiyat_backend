package com.unsiiyat.backend.modules.authorDetail;

import com.unsiiyat.backend.common.filters.BaseFilterRequest;

public class AuthorDetailFilterRequest extends BaseFilterRequest {

    private Long authorId;
    private Long scriptId;
    private String name;

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

    @Override
    public String toString() {
        return "AuthorDetailFilterRequest{" +
                "authorId=" + authorId +
                ", scriptId=" + scriptId +
                ", name='" + name + '\'' +
                '}' + super.toString();
    }
}
