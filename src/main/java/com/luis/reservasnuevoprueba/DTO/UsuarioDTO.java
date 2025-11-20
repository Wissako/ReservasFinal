package com.luis.reservasnuevoprueba.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UsuarioDTO(
        Long id,
        String nombre,
        @Email String email,
        String roles
) {}