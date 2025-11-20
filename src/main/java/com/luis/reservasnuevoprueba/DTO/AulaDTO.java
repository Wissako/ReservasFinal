package com.luis.reservasnuevoprueba.DTO;

public record AulaDTO(
        Long id,
        String nombre,
        Integer capacidad,
        Boolean esAulaDeOrdenadores,
        Integer numeroOrdenadores
) {}