package es.santander.ascender.proyectoFinal2.controller;

import es.santander.ascender.proyectoFinal2.dto.ArticuloUpdateDTO;
import es.santander.ascender.proyectoFinal2.dto.ArticuloRequestDTO;
import es.santander.ascender.proyectoFinal2.dto.ArticuloResponseDTO;
import es.santander.ascender.proyectoFinal2.service.ArticuloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/articulos")
public class ArticuloController {

    @Autowired
    private ArticuloService articuloService;

    @GetMapping
    public ResponseEntity<List<ArticuloResponseDTO>> listarArticulos() {
        return ResponseEntity.ok(articuloService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArticuloResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(articuloService.buscarPorId(id));
        
    }

    @GetMapping("/codigo/{codigoBarras}")
    public ResponseEntity<ArticuloResponseDTO> buscarPorCodigoBarras(@PathVariable String codigoBarras) {
        return ResponseEntity.ok(articuloService.buscarPorCodigoBarras(codigoBarras));
    }

    @GetMapping("/familia/{familia}")
    public ResponseEntity<List<ArticuloResponseDTO>> buscarPorFamilia(@PathVariable String familia) {
        return ResponseEntity.ok(articuloService.buscarPorFamilia(familia));
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<ArticuloResponseDTO>> buscarPorNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(articuloService.buscarPorNombre(nombre));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ArticuloResponseDTO> crearArticulo(@Valid @RequestBody ArticuloRequestDTO articuloDTO) {
        ArticuloResponseDTO articuloRespuestaDTO = articuloService.crear(articuloDTO);
        return new ResponseEntity<>(articuloRespuestaDTO, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ArticuloResponseDTO> actualizarArticulo(@PathVariable Long id, @Valid @RequestBody ArticuloUpdateDTO articuloActualizacionDTO) {
        ArticuloResponseDTO actualizadoArticulo = articuloService.actualizar(id, articuloActualizacionDTO);
        return ResponseEntity.ok(actualizadoArticulo);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> borrarArticulo(@PathVariable Long id) {
        articuloService.borradoLogico(id);
        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Artículo eliminado correctamente");
        return ResponseEntity.ok(response);

    }

    
}
