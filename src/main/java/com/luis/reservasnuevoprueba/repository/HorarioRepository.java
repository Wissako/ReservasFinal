package com.luis.reservasnuevoprueba.repository;


import com.luis.reservasnuevoprueba.entities.Horario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HorarioRepository extends JpaRepository<Horario, Long> {

    // día de la semana
    List<Horario> findByDiaSemana(Horario.DiaSemana diaSemana);

    //sesiones del día
    List<Horario> findBySesionDia(int sesionDia);

    // Encontrar horarios por día y sesión
    @Query("SELECT h FROM Horario h WHERE h.diaSemana = :diaSemana AND h.sesionDia = :sesionDia")
    List<Horario> findByDiaSemanaAndSesionDia(@Param("diaSemana") Horario.DiaSemana diaSemana,
                                              @Param("sesionDia") int sesionDia);
}