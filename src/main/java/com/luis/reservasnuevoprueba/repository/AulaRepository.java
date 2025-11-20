package com.luis.reservasnuevoprueba.repository;


import com.luis.reservasnuevoprueba.entities.Aula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AulaRepository extends JpaRepository<Aula, Long> {

    // capacidad superior
    @Query("SELECT a FROM Aula a WHERE a.capacidad >= :capacidad")
    List<Aula> findByCapacidadMinima(@Param("capacidad") Integer capacidad);

    //aulas con pcs
    @Query("SELECT a FROM Aula a WHERE a.esAulaDeOrdenadores = true")
    List<Aula> findAulasConOrdenadores();

    // pc y capacidad minima
    @Query("SELECT a FROM Aula a WHERE a.esAulaDeOrdenadores = true AND a.capacidad >= :capacidad")
    List<Aula> findAulasConOrdenadoresYCapacidad(@Param("capacidad") Integer capacidad);
}