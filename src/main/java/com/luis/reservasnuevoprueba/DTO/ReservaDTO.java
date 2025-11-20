package com.luis.reservasnuevoprueba.DTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record ReservaDTO(
        Long id,
        LocalDateTime fecha,
        String motivo,
        Integer numAsistentes,
        LocalDate fechaCreacion,
        AulaDTO aula,
        List<HorarioDTO> horarios,
        String usuarioEmail
) {}