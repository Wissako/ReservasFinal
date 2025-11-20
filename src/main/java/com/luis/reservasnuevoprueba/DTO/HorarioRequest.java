package com.luis.reservasnuevoprueba.DTO;


import com.luis.reservasnuevoprueba.entities.Horario;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

public record HorarioRequest(
        @NotNull(message = "El día de la semana es obligatorio")
        Horario.DiaSemana diaSemana,

        @NotNull(message = "La sesión del día es obligatoria")
        @Min(value = 1, message = "La sesión debe ser al menos 1")
        Integer sesionDia,

        @NotNull(message = "La hora de inicio es obligatoria")
        LocalTime horaInicio,

        @NotNull(message = "La hora de fin es obligatoria")
        LocalTime horaFin
) {}