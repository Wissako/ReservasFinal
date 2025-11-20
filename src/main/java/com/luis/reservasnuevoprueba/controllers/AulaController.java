package com.luis.reservasnuevoprueba.controllers;


import com.luis.reservasnuevoprueba.DTO.AulaRequest;
import com.luis.reservasnuevoprueba.entities.Aula;
import com.luis.reservasnuevoprueba.entities.Reserva;
import com.luis.reservasnuevoprueba.service.AulaService;
import com.luis.reservasnuevoprueba.service.ReservaService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/aulas")
@CrossOrigin(origins = "*")
@AllArgsConstructor
public class AulaController {
    private final AulaService aulaService;
    private final ReservaService reservaService;

    /**
     * Listar todas las aulas
     * ADMIN y PROFESOR pueden ver
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESOR')")
    @GetMapping
    public ResponseEntity<List<Aula>> listarAulas(
            @RequestParam(required = false) Integer capacidad,
            @RequestParam(required = false) Boolean ordenadores) {

        // TODO: Pasarlo a servicio
        List<Aula> aulas;

        if (capacidad != null && ordenadores != null && ordenadores) {
            aulas = aulaService.obtenerAulasConOrdenadoresYCapacidad(capacidad);
        } else if (capacidad != null) {
            aulas = aulaService.obtenerAulasPorCapacidad(capacidad);
        } else if (ordenadores != null && ordenadores) {
            aulas = aulaService.obtenerAulasConOrdenadores();
        } else {
            aulas = aulaService.obtenerTodasLasAulas();
        }

        return ResponseEntity.ok(aulas);
    }

    /**
     * Obtener datos de un aula
     * ADMIN y PROFESOR pueden ver
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESOR')")
    @GetMapping("/{id}")
    public ResponseEntity<Aula> obtenerAula(@PathVariable Long id) {
        return aulaService.obtenerAulaPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Crear nueva aula
     * Solo ADMIN puede crear
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Aula> crearAula(@Valid @RequestBody AulaRequest aulaRequest) {
        // TODO: Pasarlo a servicio
        Aula aula = new Aula();
        aula.setNombre(aulaRequest.nombre());
        aula.setCapacidad(aulaRequest.capacidad());
        aula.setEsAulaDeOrdenadores(aulaRequest.esAulaDeOrdenadores());
        aula.setNumeroOrdenadores(aulaRequest.numeroOrdenadores());

        Aula aulaCreada = aulaService.crearAula(aula);
        return ResponseEntity.status(HttpStatus.CREATED).body(aulaCreada);
    }

    /**
     * Modificar aula
     * Solo ADMIN puede modificar
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Aula> actualizarAula(
            @PathVariable Long id,
            @Valid @RequestBody AulaRequest aulaRequest) {
        // Que pasa si se pasa un campo nulo? Intenta una actualizacion parcial
        Aula aulaActualizada = new Aula();
        aulaActualizada.setNombre(aulaRequest.nombre());
        aulaActualizada.setCapacidad(aulaRequest.capacidad());
        aulaActualizada.setEsAulaDeOrdenadores(aulaRequest.esAulaDeOrdenadores());
        aulaActualizada.setNumeroOrdenadores(aulaRequest.numeroOrdenadores());

        Aula aula = aulaService.actualizarAula(id, aulaActualizada);
        return ResponseEntity.ok(aula);
    }

    /**
     * Eliminar un aula
     * Solo ADMIN puede eliminar
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarAula(@PathVariable Long id) {
        aulaService.eliminarAula(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Ver reservas de un aula
     * ADMIN y PROFESOR pueden ver
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESOR')")
    @GetMapping("/{id}/reservas")
    public ResponseEntity<List<Reserva>> obtenerReservasAula(@PathVariable Long id) {
        List<Reserva> reservas = reservaService.obtenerReservasPorAula(id);
        return ResponseEntity.ok(reservas);
    }

    /**
     * Ver reservas futuras de un aula
     * ADMIN y PROFESOR pueden ver
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESOR')")
    @GetMapping("/{id}/reservas-futuras")
    public ResponseEntity<List<Reserva>> obtenerReservasFuturasAula(@PathVariable Long id) {
        List<Reserva> reservas = reservaService.obtenerReservasFuturas(id);
        return ResponseEntity.ok(reservas);
    }

}