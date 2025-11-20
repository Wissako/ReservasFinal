package com.luis.reservasnuevoprueba.repository;


import com.luis.reservasnuevoprueba.entities.Aula;
import com.luis.reservasnuevoprueba.entities.Horario;
import com.luis.reservasnuevoprueba.entities.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    // todas las reservas de un aula
    List<Reserva> findByAula(Aula aula);

    // Encontrar reservas en fecha específica
    List<Reserva> findByAulaAndFecha(Aula aula, LocalDate fecha);

    // Encontrar reservas que se solapan en fecha y aula
    @Query("SELECT r FROM Reserva r WHERE r.aula = :aula AND r.fecha = :fecha " +
            "AND EXISTS (SELECT 1 FROM r.horarios h WHERE h IN :horarios)")
    List<Reserva> findReservasQueSesolapan(@Param("aula") Aula aula,
                                           @Param("fecha") LocalDate fecha,
                                           @Param("horarios") List<Horario> horarios);

    // Encontrar reservas de un aula posteriores a una fecha
    @Query("SELECT r FROM Reserva r WHERE r.aula = :aula AND r.fecha >= :fecha")
    List<Reserva> findReservasFuturas(@Param("aula") Aula aula, @Param("fecha") LocalDate fecha);
}