package com.unsiiyat.backend.modules.theme;

import com.unsiiyat.backend.common.filters.BaseFilterRequest;

public class ThemeFilterRequest extends BaseFilterRequest {

    private String name;
    private String slug;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    @Override
    public String toString() {
        return "ThemeFilterRequest{" +
                "name='" + name + '\'' +
                ", slug='" + slug + '\'' +
                '}' + super.toString();
    }

}
