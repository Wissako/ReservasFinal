package com.luis.reservasnuevoprueba.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UsuarioUpdateRequest(
        String nombre,

        @Email(message = "El email debe ser válido")
        String email
) {}