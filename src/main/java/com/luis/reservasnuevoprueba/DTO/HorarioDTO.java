package com.luis.reservasnuevoprueba.DTO;



import com.luis.reservasnuevoprueba.entities.Horario;

import java.time.LocalTime;

public record HorarioDTO(
        Long id,
        Horario.DiaSemana diaSemana,
        Integer sesionDia,
        LocalTime horaInicio,
        LocalTime horaFin
) {}