package es.santander.ascender.proyectoFinal2.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.santander.ascender.proyectoFinal2.dto.compra.CompraListResponseDTO;
import es.santander.ascender.proyectoFinal2.dto.compra.CompraRequestDTO;
import es.santander.ascender.proyectoFinal2.service.CompraService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/compras")
@PreAuthorize("hasRole('ADMIN')")
public class CompraController {

    @Autowired
    private CompraService compraService;

    
    @GetMapping
    public ResponseEntity<List<CompraListResponseDTO>> listarCompras() {
        return ResponseEntity.ok(compraService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompraListResponseDTO> buscarPorId(@PathVariable Long id) {
        compraService.buscarPorId(id);
        return ResponseEntity.ok(compraService.buscarPorId(id));
        
    }

    @GetMapping("/fechas")
    public ResponseEntity<List<CompraListResponseDTO>> buscarPorFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        return ResponseEntity.ok(compraService.buscarPorFechas(fechaInicio, fechaFin));
    }

    @PostMapping
    public ResponseEntity<CompraListResponseDTO> realizarCompra(@Valid @RequestBody CompraRequestDTO compraRequestDTO) {
        CompraListResponseDTO compraCreada = compraService.crearCompra(compraRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(compraCreada);
                
                
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> anularCompra(@PathVariable Long id) {
        compraService.anularCompra(id);
        return ResponseEntity.ok(Map.of("mensaje", "Compra anulada correctamente"));
    }
}
