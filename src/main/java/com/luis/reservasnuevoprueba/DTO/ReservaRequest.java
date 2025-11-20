package com.luis.reservasnuevoprueba.DTO;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;



public record ReservaRequest(
        @NotNull(message = "La fecha es obligatoria")
        @Future(message = "La fecha debe ser futura")
        LocalDateTime fecha,

        @NotBlank(message = "El motivo es obligatorio")
        String motivo,

        @NotNull(message = "El número de asistentes es obligatorio")
        @Min(value = 1, message = "Debe haber al menos 1 asistente")
        Integer numAsistentes,

        @NotNull(message = "El aula es obligatoria")
        Long aulaId,

        List<Long> horariosIds
) {}