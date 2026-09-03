package com.unsiiyat.backend.modules.auth;

public class LoginResponseDto {

    private String token;
    private String tokenType = "Bearer";
    private UserRole role;
    private String email;
    private String name;

    public LoginResponseDto() {
    }

    public LoginResponseDto(UserRole role, String token) {
        this.role = role;
        this.token = token;
        this.tokenType = "Bearer";
    }

    public LoginResponseDto(String token, UserRole role, String email, String name) {
        this.token = token;
        this.role = role;
        this.email = email;
        this.name = name;
        this.tokenType = "Bearer";
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
