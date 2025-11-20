package com.luis.reservasnuevoprueba.DTO;

public record AuthResponse(
        String token,
        String tipo,
        String email,
        String roles
) {
    public AuthResponse(String token, String email, String roles) {
        this(token, "Bearer", email, roles);
    }
}