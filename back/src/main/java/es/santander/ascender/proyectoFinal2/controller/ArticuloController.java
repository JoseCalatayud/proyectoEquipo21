package es.santander.ascender.proyectoFinal2.controller;

import es.santander.ascender.proyectoFinal2.dto.articulo.ArticuloRequestDTO;
import es.santander.ascender.proyectoFinal2.dto.articulo.ArticuloResponseDTO;
import es.santander.ascender.proyectoFinal2.dto.articulo.ArticuloUpdateRequestDTO;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/articulos")
public class ArticuloController {

    @Autowired
    private ArticuloService articuloService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')") // Only ADMIN can list all articles
    public ResponseEntity<List<ArticuloResponseDTO>> listarArticulos() {
        return ResponseEntity.ok(articuloService.listarTodos());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')") // Both ADMIN and USER can get an article by ID
    public ResponseEntity<ArticuloResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(articuloService.buscarPorId(id));
        
    }

    @GetMapping("/codigo/{codigoBarras}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')") // Both ADMIN and USER can get an article by barcode
    public ResponseEntity<ArticuloResponseDTO> buscarPorCodigoBarras(@PathVariable String codigoBarras) {
        return ResponseEntity.ok(articuloService.buscarPorCodigoBarras(codigoBarras));
    }

    @GetMapping("/familia/{familia}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')") // Both ADMIN and USER can get articles by family
    public ResponseEntity<List<ArticuloResponseDTO>> buscarPorFamilia(@PathVariable String familia) {
        return ResponseEntity.ok(articuloService.buscarPorFamilia(familia));
    }

    @GetMapping("/buscar")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')") // Both ADMIN and USER can search articles by name
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
    public ResponseEntity<ArticuloResponseDTO> actualizarArticulo(@PathVariable Long id, @Valid @RequestBody ArticuloUpdateRequestDTO articuloActualizacionDTO) {
        ArticuloResponseDTO actualizadoArticulo = articuloService.actualizar(id, articuloActualizacionDTO);
        return ResponseEntity.ok(actualizadoArticulo);
    }

    @PostMapping("/{id}")    
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> borrarArticulo(@PathVariable Long id) {
        articuloService.borradoLogico(id);
        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Artículo eliminado correctamente");
        return ResponseEntity.ok(response);

    }

    
}
