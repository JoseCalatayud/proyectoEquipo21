package es.santander.ascender.proyectoFinal2.controller;

import es.santander.ascender.proyectoFinal2.dto.VentaRequestDTO;
import es.santander.ascender.proyectoFinal2.model.Venta;
import es.santander.ascender.proyectoFinal2.service.VentaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    @Autowired
    private VentaService ventaService;

    @GetMapping("/listar")
    public ResponseEntity<List<Venta>> listarVentas() {
        List<Venta> ventas = ventaService.listarVentas();
        return ResponseEntity.ok(ventas);
    }

    @PostMapping("/crear")
    public ResponseEntity<Venta> crearVenta(@Valid @RequestBody VentaRequestDTO ventaRequestDTO) {
        try {
            Venta nuevaVenta = ventaService.crearVenta(ventaRequestDTO);
            return ResponseEntity.ok(nuevaVenta);
        } catch (Exception e) {
            // Devolver el error.
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        Optional<Venta> venta = ventaService.buscarPorId(id);
        return venta.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<?> buscarPorUsuario(@PathVariable Long idUsuario) {
        try {
            List<Venta> ventas = ventaService.buscarPorUsuario(idUsuario);
            return ResponseEntity.ok(ventas);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
