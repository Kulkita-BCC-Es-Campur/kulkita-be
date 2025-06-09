package com.example.kulkita.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String token;
    private String type = "Bearer";
    private String email;
    private String username;
    private String role;

    public AuthResponse(String token, String email, String username, String role) {
        this.token = token;
        this.email = email;
        this.username = username;
        this.role = role;
    }
}