package com.unsiiyat.backend.modules.script;

import com.unsiiyat.backend.common.filters.BaseFilterRequest;

public class ScriptFilterRequest extends BaseFilterRequest {

    private String code;
    private String name;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ScriptFilterRequest{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                '}' + super.toString();
    }
}
