package com.luis.reservasnuevoprueba.DTO;

public record ErrorResponse(
        String mensaje,
        int status,
        String timestamp
) {}