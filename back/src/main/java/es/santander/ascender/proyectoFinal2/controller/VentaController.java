package es.santander.ascender.proyectoFinal2.controller;

import es.santander.ascender.proyectoFinal2.dto.VentaRequestDTO;
import es.santander.ascender.proyectoFinal2.dto.VentaResponseDTO;
import es.santander.ascender.proyectoFinal2.service.VentaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    @Autowired
    private VentaService ventaService;

    @GetMapping("/listar")
    @PreAuthorize("hasRole('ADMIN')") // Only ADMIN can list all sales
    public ResponseEntity<List<VentaResponseDTO>> listarVentas() {
        return ResponseEntity.ok(ventaService.listarVentas());
    }

    @PostMapping("/crear")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')") // Both ADMIN and USER can create sales
    public ResponseEntity<VentaResponseDTO> crearVenta(@Valid @RequestBody VentaRequestDTO ventaRequestDTO) {
        return ResponseEntity.ok(ventaService.crearVenta(ventaRequestDTO));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')") // Both ADMIN and USER can get a sale by ID
    public ResponseEntity<VentaResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ventaService.buscarPorId(id).orElse(null));

          
    }

    @GetMapping("/usuario/{idUsuario}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')") // Both ADMIN and USER can get sales by user ID
    public ResponseEntity<List<VentaResponseDTO>> buscarPorUsuario(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(ventaService.buscarPorUsuario(idUsuario));
        
        
    }

    @GetMapping("/fechas")
    @PreAuthorize("hasRole('ADMIN')") // Only ADMIN can search sales by date range
    public ResponseEntity<List<VentaResponseDTO>> buscarPorFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        return ResponseEntity.ok(ventaService.buscarPorFechas(fechaInicio, fechaFin));
    }

    @GetMapping("/usuario/{idUsuario}/fechas")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')") // Both ADMIN and USER can search sales by user and date range
    public ResponseEntity<List<VentaResponseDTO>> buscarPorUsuarioYFechas(
            @PathVariable Long idUsuario,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        // Obtener el usuario autenticado
        
        return ResponseEntity.ok(ventaService.buscarPorUsuarioYFechas(idUsuario, fechaInicio, fechaFin));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')") // Both ADMIN and USER can cancel a sale
    public ResponseEntity<?> anularVenta(@PathVariable Long id) {
        ventaService.anularVenta(id);
        return ResponseEntity.ok(Map.of("mensaje", "Venta anulada correctamente"));
    }
}
