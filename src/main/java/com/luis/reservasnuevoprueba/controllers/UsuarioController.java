package com.luis.reservasnuevoprueba.controllers;



import com.luis.reservasnuevoprueba.DTO.CambiarPasswordRequest;
import com.luis.reservasnuevoprueba.DTO.UsuarioDTO;
import com.luis.reservasnuevoprueba.DTO.UsuarioUpdateRequest;
import com.luis.reservasnuevoprueba.entities.Usuario;
import com.luis.reservasnuevoprueba.repository.UsuarioRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/usuario")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Obtener perfil del usuario autenticado
     */
    @GetMapping("/perfil")
    public ResponseEntity<?> obtenerPerfil(Authentication authentication) {
        try {
            String email = authentication.getName();
            Usuario usuario = usuarioRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            UsuarioDTO usuarioDTO = new UsuarioDTO(
                    usuario.getId(),
                    usuario.getNombre(),
                    usuario.getEmail(),
                    usuario.getRoles()
            );

            return ResponseEntity.ok(usuarioDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al obtener perfil"));
        }
    }

    /**
     * Modificar datos del usuario autenticado
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<?> modificarUsuario(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioUpdateRequest request,
            Authentication authentication) {
        try {
            Usuario usuario = usuarioRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Solo admin puede modificar cualquier usuario
            // Los usuarios normales solo pueden modificarse a sí mismos
            String emailAutenticado = authentication.getName();
            boolean esAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

            if (!esAdmin && !usuario.getEmail().equals(emailAutenticado)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "No tienes permiso para modificar este usuario"));
            }

            // Actualizar datos
            if (request.nombre() != null && !request.nombre().isBlank()) {
                usuario.setNombre(request.nombre());
            }
            if (request.email() != null && !request.email().isBlank()) {
                // Verificar que el email no esté en uso
                if (!usuario.getEmail().equals(request.email()) &&
                        usuarioRepository.findByEmail(request.email()).isPresent()) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(Map.of("error", "El email ya está en uso"));
                }
                usuario.setEmail(request.email());
            }

            usuarioRepository.save(usuario);

            return ResponseEntity.ok(Map.of("mensaje", "Usuario actualizado correctamente"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al actualizar usuario"));
        }
    }

    /**
     * Cambiar contraseña del usuario autenticado
     */
    @PatchMapping("/cambiar-pass")
    public ResponseEntity<?> cambiarPassword(
            @Valid @RequestBody CambiarPasswordRequest request,
            Authentication authentication) {
        try {
            String email = authentication.getName();
            Usuario usuario = usuarioRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Verificar contraseña actual
            if (!passwordEncoder.matches(request.passwordActual(), usuario.getPassword())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "La contraseña actual es incorrecta"));
            }

            // Actualizar contraseña
            usuario.setPassword(passwordEncoder.encode(request.nuevaPassword()));
            usuarioRepository.save(usuario);

            return ResponseEntity.ok(Map.of("mensaje", "Contraseña cambiada correctamente"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al cambiar contraseña"));
        }
    }

    /**
     * Eliminar usuario (solo ADMIN)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Long id) {
        try {
            if (!usuarioRepository.existsById(id)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Usuario no encontrado"));
            }

            usuarioRepository.deleteById(id);
            return ResponseEntity.ok(Map.of("mensaje", "Usuario eliminado correctamente"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al eliminar usuario"));
        }
    }
}