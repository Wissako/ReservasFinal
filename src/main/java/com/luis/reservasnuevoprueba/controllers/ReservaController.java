package com.luis.reservasnuevoprueba.controllers;

import com.luis.reservasnuevoprueba.DTO.ReservaDTO;
import com.luis.reservasnuevoprueba.DTO.ReservaRequest;
import com.luis.reservasnuevoprueba.DTO.AulaDTO;
import com.luis.reservasnuevoprueba.DTO.HorarioDTO;
import com.luis.reservasnuevoprueba.entities.Aula;
import com.luis.reservasnuevoprueba.entities.Horario;
import com.luis.reservasnuevoprueba.entities.Reserva;
import com.luis.reservasnuevoprueba.entities.Usuario;
import com.luis.reservasnuevoprueba.repository.AulaRepository;
import com.luis.reservasnuevoprueba.repository.HorarioRepository;
import com.luis.reservasnuevoprueba.repository.UsuarioRepository;
import com.luis.reservasnuevoprueba.service.ReservaService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/reservas")
@CrossOrigin(origins = "*")
@AllArgsConstructor
public class ReservaController {

    private final ReservaService reservaService;
    private final AulaRepository aulaRepository;
    private final HorarioRepository horarioRepository;
    private final UsuarioRepository usuarioRepository;

    /**
     * Listar todas las reservas
     * ADMIN y PROFESOR pueden ver
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESOR')")
    @GetMapping
    public ResponseEntity<List<ReservaDTO>> listarReservas() {
        List<Reserva> reservas = reservaService.obtenerTodasLasReservas();
        List<ReservaDTO> reservasDTO = reservas.stream()
                .map(this::convertirAReservaDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(reservasDTO);
    }

    /**
     * Obtener una reserva por ID
     * ADMIN y PROFESOR pueden ver
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESOR')")
    @GetMapping("/{id}")
    public ResponseEntity<ReservaDTO> obtenerReserva(@PathVariable Long id) {
        return reservaService.obtenerReservaPorId(id)
                .map(reserva -> ResponseEntity.ok(convertirAReservaDTO(reserva)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Crear nueva reserva
     * ADMIN y PROFESOR pueden crear
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESOR')")
    @PostMapping
    public ResponseEntity<ReservaDTO> crearReserva(
            @Valid @RequestBody ReservaRequest reservaRequest,
            Authentication authentication) {

        // Buscar el aula
        Aula aula = aulaRepository.findById(reservaRequest.aulaId())
                .orElseThrow(() -> new RuntimeException("Aula no encontrada"));

        // Buscar los horarios
        List<Horario> horarios = horarioRepository.findAllById(reservaRequest.horariosIds());

        // Buscar el usuario autenticado
        String email = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Crear la reserva
        Reserva reserva = new Reserva();
        reserva.setFecha(reservaRequest.fecha());
        reserva.setMotivo(reservaRequest.motivo());
        reserva.setNumAsistentes(reservaRequest.numAsistentes());
        reserva.setAula(aula);
        reserva.setHorarios(horarios);
        reserva.setUsuario(usuario);

        Reserva reservaCreada = reservaService.crearReserva(reserva);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertirAReservaDTO(reservaCreada));
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESOR')")
    @PutMapping("/{id}")
    public ResponseEntity<ReservaDTO> actualizarReserva(
            @PathVariable Long id,
            @Valid @RequestBody ReservaRequest reservaRequest,
            Authentication authentication) {

        // Verificar que la reserva existe
        Reserva reservaExistente = reservaService.obtenerReservaPorId(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        // Verificar permisos: Admin puede editar todas, Profesor solo las suyas
        String email = authentication.getName();
        boolean esAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!esAdmin && !reservaExistente.getUsuario().getEmail().equals(email)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Buscar el aula
        Aula aula = aulaRepository.findById(reservaRequest.aulaId())
                .orElseThrow(() -> new RuntimeException("Aula no encontrada"));

        // Buscar los horarios
        List<Horario> horarios = horarioRepository.findAllById(reservaRequest.horariosIds());

        // Actualizar la reserva
        reservaExistente.setFecha(reservaRequest.fecha());
        reservaExistente.setMotivo(reservaRequest.motivo());
        reservaExistente.setNumAsistentes(reservaRequest.numAsistentes());
        reservaExistente.setAula(aula);
        reservaExistente.setHorarios(horarios);

        Reserva reservaActualizada = reservaService.actualizarReserva(id, reservaExistente);
        return ResponseEntity.ok(convertirAReservaDTO(reservaActualizada));
    }

    /**
     * Eliminar una reserva
     * ADMIN puede eliminar cualquiera, PROFESOR solo las suyas
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarReserva(
            @PathVariable Long id,
            Authentication authentication) {

        // Verificar que la reserva existe
        Reserva reserva = reservaService.obtenerReservaPorId(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        // Verificar permisos
        String email = authentication.getName();
        boolean esAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!esAdmin && !reserva.getUsuario().getEmail().equals(email)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        reservaService.eliminarReserva(id, email);
        return ResponseEntity.noContent().build();
    }

    /**
     * Obtener reservas del usuario autenticado
     * Cada usuario puede ver sus propias reservas
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESOR')")
    @GetMapping("/mis-reservas")
    public ResponseEntity<List<ReservaDTO>> obtenerMisReservas(Authentication authentication) {
        String email = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<Reserva> reservas = reservaService.obtenerTodasLasReservas().stream()
                .filter(r -> r.getUsuario().getEmail().equals(email))
                .collect(Collectors.toList());

        List<ReservaDTO> reservasDTO = reservas.stream()
                .map(this::convertirAReservaDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(reservasDTO);
    }



    /**
     * Convertir una Reserva a ReservaDTO
     */
    private ReservaDTO convertirAReservaDTO(Reserva reserva) {
        AulaDTO aulaDTO = new AulaDTO(
                reserva.getAula().getId(),
                reserva.getAula().getNombre(),
                reserva.getAula().getCapacidad(),
                reserva.getAula().getEsAulaDeOrdenadores(),
                reserva.getAula().getNumeroOrdenadores()
        );

        List<HorarioDTO> horariosDTO = reserva.getHorarios().stream()
                .map(h -> new HorarioDTO(
                        h.getId(),
                        h.getDiaSemana(),
                        h.getSesionDia(),
                        h.getHoraInicio(),
                        h.getHoraFin()
                ))
                .collect(Collectors.toList());

        return new ReservaDTO(
                reserva.getId(),
                reserva.getFecha(),
                reserva.getMotivo(),
                reserva.getNumAsistentes(),
                reserva.getFechaCreacion(),
                aulaDTO,
                horariosDTO,
                reserva.getUsuario().getEmail()
        );
    }
}