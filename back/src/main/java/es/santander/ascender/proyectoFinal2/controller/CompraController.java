package es.santander.ascender.proyectoFinal2.controller;

import es.santander.ascender.proyectoFinal2.dto.CompraRequestDTO;
import es.santander.ascender.proyectoFinal2.model.Compra;
import es.santander.ascender.proyectoFinal2.service.CompraService;
import es.santander.ascender.proyectoFinal2.service.UsuarioService;
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
@RequestMapping("/api/compras")
@PreAuthorize("hasRole('ADMIN')")
public class CompraController {

    @Autowired
    private CompraService compraService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<Compra>> listarCompras() {
        return ResponseEntity.ok(compraService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        Compra compra = compraService.buscarPorId(id).orElseThrow(() -> new IllegalArgumentException("No existe la compra con ID: " + id));
        return ResponseEntity.ok(compra);
    }

    @GetMapping("/fechas")
    public ResponseEntity<List<Compra>> buscarPorFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        return ResponseEntity.ok(compraService.buscarPorFechas(fechaInicio, fechaFin));
    }

    @PostMapping
    public ResponseEntity<?> realizarCompra(@Valid @RequestBody CompraRequestDTO compraRequestDTO) {
        try {
            Compra nuevaCompra = compraService.crearCompra(compraRequestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaCompra);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> anularCompra(@PathVariable Long id) {
        compraService.anularCompra(id);
        return ResponseEntity.ok(Map.of("mensaje", "Compra anulada correctamente"));
    }
}
