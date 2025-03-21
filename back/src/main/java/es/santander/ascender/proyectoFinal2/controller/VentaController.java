package es.santander.ascender.proyectoFinal2.controller;

import es.santander.ascender.proyectoFinal2.dto.VentaListDTO;
import es.santander.ascender.proyectoFinal2.dto.VentaRequestDTO;
import es.santander.ascender.proyectoFinal2.dto.VentaResponseDTO;
import es.santander.ascender.proyectoFinal2.model.Usuario;
import es.santander.ascender.proyectoFinal2.model.Venta;
import es.santander.ascender.proyectoFinal2.service.UsuarioService;
import es.santander.ascender.proyectoFinal2.service.VentaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    @Autowired
    private VentaService ventaService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/listar")
    @PreAuthorize("hasRole('ADMIN')") // Only ADMIN can list all sales
    public ResponseEntity<List<VentaListDTO>> listarVentas() {
        List<VentaListDTO> ventas = ventaService.listarVentas();
        return ResponseEntity.ok(ventas);
    }

    @PostMapping("/crear")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')") // Both ADMIN and USER can create sales
    public ResponseEntity<VentaResponseDTO> crearVenta(@Valid @RequestBody VentaRequestDTO ventaRequestDTO) {
        return ResponseEntity.ok(ventaService.crearVenta(ventaRequestDTO));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')") // Both ADMIN and USER can get a sale by ID
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        // Obtener el usuario autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<Usuario> usuario = usuarioService.buscarPorUsername(auth.getName());

        if (usuario.isEmpty()) {
            return ResponseEntity.status(401).body(null);
        }

        Optional<VentaResponseDTO> ventaOptional = ventaService.buscarPorId(id);

        if (ventaOptional.isPresent()) {
            // Si el usuario no es admin, solo puede ver sus propias ventas
            if (!usuarioService.esAdmin(usuario.get()) && !ventaOptional.get().getUsuario().getId().equals(usuario.get().getId())) {
                return ResponseEntity.status(403).body(Map.of("mensaje", "No tiene permisos para ver esta venta"));
            }
            return ResponseEntity.ok(ventaOptional.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/usuario/{idUsuario}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')") // Both ADMIN and USER can get sales by user ID
    public ResponseEntity<?> buscarPorUsuario(@PathVariable Long idUsuario) {
        // Obtener el usuario autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<Usuario> usuario = usuarioService.buscarPorUsername(auth.getName());

        if (usuario.isEmpty()) {
            return ResponseEntity.status(401).body(null);
        }

        try {
            List<Venta> ventas;
            // Si el usuario no es admin, solo puede ver sus propias ventas
            if (!usuarioService.esAdmin(usuario.get()) && !usuario.get().getId().equals(idUsuario)) {
                return ResponseEntity.status(403).body(Map.of("mensaje", "No tiene permisos para ver las ventas de este usuario"));
            }
            ventas = ventaService.buscarPorUsuario(idUsuario);
            return ResponseEntity.ok(ventas);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/fechas")
    @PreAuthorize("hasRole('ADMIN')") // Only ADMIN can search sales by date range
    public ResponseEntity<List<Venta>> buscarPorFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        return ResponseEntity.ok(ventaService.buscarPorFechas(fechaInicio, fechaFin));
    }

    @GetMapping("/usuario/{idUsuario}/fechas")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')") // Both ADMIN and USER can search sales by user and date range
    public ResponseEntity<?> buscarPorUsuarioYFechas(
            @PathVariable Long idUsuario,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        // Obtener el usuario autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<Usuario> usuario = usuarioService.buscarPorUsername(auth.getName());

        if (usuario.isEmpty()) {
            return ResponseEntity.status(401).body(null);
        }

        // Si el usuario no es admin, solo puede ver sus propias ventas
        if (!usuarioService.esAdmin(usuario.get()) && !usuario.get().getId().equals(idUsuario)) {
            return ResponseEntity.status(403).body(Map.of("mensaje", "No tiene permisos para ver las ventas de este usuario"));
        }

        return ResponseEntity.ok(ventaService.buscarPorUsuarioYFechas(idUsuario, fechaInicio, fechaFin));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')") // Both ADMIN and USER can cancel a sale
    public ResponseEntity<?> anularVenta(@PathVariable Long id) {
        // Obtener el usuario autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<Usuario> usuario = usuarioService.buscarPorUsername(auth.getName());

        if (usuario.isEmpty()) {
            return ResponseEntity.status(401).body(null);
        }

        Optional<VentaResponseDTO> ventaOptional = ventaService.buscarPorId(id);

        if (ventaOptional.isPresent()) {
            // Si el usuario no es admin, solo puede anular sus propias ventas
            if (!usuarioService.esAdmin(usuario.get()) && !ventaOptional.get().getUsuario().getId().equals(usuario.get().getId())) {
                return ResponseEntity.status(403).body(Map.of("mensaje", "No tiene permisos para anular esta venta"));
            }
            ventaService.anularVenta(id);
            return ResponseEntity.ok(Map.of("mensaje", "Venta anulada correctamente"));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
