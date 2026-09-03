package com.unsiiyat.backend.modules.genre;

import com.unsiiyat.backend.common.filters.BaseFilterRequest;

public class GenreFilterRequest extends BaseFilterRequest {

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
        return "GenreFilterRequest{" +
                "name='" + name + '\'' +
                ", slug='" + slug + '\'' +
                '}' + super.toString();
    }
}
