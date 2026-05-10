package com.veritrabajo.backend.identityaccess.application.dto;

import java.util.List;

public class AuthResponse {

    private String userId;
    private String email;
    private List<String> roles;
    private String token;

    public static AuthResponse from(
            AuthUserView user,
            String token
    ) {
        AuthResponse response = new AuthResponse();
        response.userId = user.userId();
        response.email = user.email();
        response.roles = user.roles();
        response.token = token;
        return response;
    }

    public String getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public List<String> getRoles() {
        return roles;
    }

    public String getToken() {
        return token;
    }
}
