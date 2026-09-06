package com.unsiiyat.backend.modules.theme;

import com.unsiiyat.backend.common.filters.BaseFilterRequest;

public class ThemeFilterRequest extends BaseFilterRequest {

    private String name;
    private String slug;
    private String search;

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

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    @Override
    public String toString() {
        return "ThemeFilterRequest{" +
                "name='" + name + '\'' +
                ", slug='" + slug + '\'' +
                ", search='" + search + '\'' +
                '}' + super.toString();
    }

}
