package com.luis.reservasnuevoprueba.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AulaRequest(
        @NotBlank(message = "El nombre del aula es obligatorio")
        String nombre,

        @NotNull(message = "La capacidad es obligatoria")
        @Min(value = 1, message = "La capacidad debe ser al menos 1")
        Integer capacidad,

        @NotNull(message = "Debe indicar si es aula de ordenadores")
        Boolean esAulaDeOrdenadores,

        @Min(value = 0, message = "El número de ordenadores no puede ser negativo")
        Integer numeroOrdenadores
) {

    public AulaRequest {
        if (Boolean.TRUE.equals(esAulaDeOrdenadores)) {
            if (numeroOrdenadores == null || numeroOrdenadores <= 0) {
                throw new IllegalArgumentException("Las aulas de ordenadores deben tener al menos 1 ordenador");
            }
        } else {
            if (numeroOrdenadores != null && numeroOrdenadores > 0) {
                throw new IllegalArgumentException("Las aulas normales no pueden tener ordenadores");
            }
        }
    }
}