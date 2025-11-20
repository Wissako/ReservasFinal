package com.luis.reservasnuevoprueba.controllers;


import com.luis.reservasnuevoprueba.DTO.HorarioRequest;
import com.luis.reservasnuevoprueba.entities.Horario;
import com.luis.reservasnuevoprueba.repository.HorarioRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/horarios")
@CrossOrigin(origins = "*")
public class HorarioController {

    @Autowired
    private HorarioRepository horarioRepository;

    /**
     * Lista de todos los horarios
     * ADMIN y PROFESOR pueden ver
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESOR')")
    @GetMapping
    public ResponseEntity<List<Horario>> listarHorarios() {
        List<Horario> horarios = horarioRepository.findAll();
        return ResponseEntity.ok(horarios);
    }

    /**
     * Obtener un horario por ID
     * ADMIN y PROFESOR pueden ver
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESOR')")
    @GetMapping("/{id}")
    public ResponseEntity<Horario> obtenerHorario(@PathVariable Long id) {
        Optional<Horario> horario = horarioRepository.findById(id);
        return horario.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Crear nuevo horario
     * Solo ADMIN puede crear
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Horario> crearHorario(@Valid @RequestBody HorarioRequest horarioRequest) {
        Horario horario = new Horario();
        horario.setDiaSemana(horarioRequest.diaSemana());
        horario.setSesionDia(horarioRequest.sesionDia());
        horario.setHoraInicio(horarioRequest.horaInicio());
        horario.setHoraFin(horarioRequest.horaFin());

        Horario horarioCreado = horarioRepository.save(horario);
        return ResponseEntity.status(HttpStatus.CREATED).body(horarioCreado);
    }

    /**
     * Actualizar un horario
     * Solo ADMIN puede actualizar
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Horario> actualizarHorario(
            @PathVariable Long id,
            @Valid @RequestBody HorarioRequest horarioRequest) {

        Optional<Horario> horarioExistente = horarioRepository.findById(id);

        if (horarioExistente.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Horario horario = horarioExistente.get();
        horario.setDiaSemana(horarioRequest.diaSemana());
        horario.setSesionDia(horarioRequest.sesionDia());
        horario.setHoraInicio(horarioRequest.horaInicio());
        horario.setHoraFin(horarioRequest.horaFin());

        Horario horarioGuardado = horarioRepository.save(horario);
        return ResponseEntity.ok(horarioGuardado);
    }

    /**
     * Eliminar un horario
     * Solo ADMIN puede eliminar
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarHorario(@PathVariable Long id) {
        if (!horarioRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        horarioRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Horarios por día de la semana
     * ADMIN y PROFESOR pueden ver
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESOR')")
    @GetMapping("/dia/{diaSemana}")
    public ResponseEntity<List<Horario>> obtenerHorariosPorDia(
            @PathVariable Horario.DiaSemana diaSemana) {
        List<Horario> horarios = horarioRepository.findByDiaSemana(diaSemana);
        return ResponseEntity.ok(horarios);
    }

    /**
     * Obtener horarios por sesión del día
     * ADMIN y PROFESOR pueden ver
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESOR')")
    @GetMapping("/sesion/{sesionDia}")
    public ResponseEntity<List<Horario>> obtenerHorariosPorSesion(
            @PathVariable int sesionDia) {
        List<Horario> horarios = horarioRepository.findBySesionDia(sesionDia);
        return ResponseEntity.ok(horarios);
    }
}