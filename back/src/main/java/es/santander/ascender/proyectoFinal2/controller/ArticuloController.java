package es.santander.ascender.proyectoFinal2.controller;

import es.santander.ascender.proyectoFinal2.dto.ArticuloActualizacionDTO;
import es.santander.ascender.proyectoFinal2.dto.ArticuloDTO;
import es.santander.ascender.proyectoFinal2.dto.ArticuloRespuestaDTO;
import es.santander.ascender.proyectoFinal2.model.Articulo;
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
import java.util.Optional;

@RestController
@RequestMapping("/api/articulos")
public class ArticuloController {

    @Autowired
    private ArticuloService articuloService;

    @GetMapping
    public ResponseEntity<List<Articulo>> listarArticulos() {
        return ResponseEntity.ok(articuloService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Articulo> buscarPorId(@PathVariable Long id) {
        Optional<Articulo> articulo = articuloService.buscarPorId(id);
        return articulo.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/codigo/{codigoBarras}")
    public ResponseEntity<Articulo> buscarPorCodigoBarras(@PathVariable String codigoBarras) {
        Optional<Articulo> articulo = articuloService.buscarPorCodigoBarras(codigoBarras);
        return articulo.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/familia/{familia}")
    public ResponseEntity<List<Articulo>> buscarPorFamilia(@PathVariable String familia) {
        return ResponseEntity.ok(articuloService.buscarPorFamilia(familia));
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<Articulo>> buscarPorNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(articuloService.buscarPorNombre(nombre));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ArticuloRespuestaDTO> crearArticulo(@Valid @RequestBody ArticuloDTO articuloDTO) {
        Articulo nuevoArticulo = articuloService.crear(articuloDTO);
        ArticuloRespuestaDTO articuloRespuestaDTO = new ArticuloRespuestaDTO(
                nuevoArticulo.getId(),
                nuevoArticulo.getNombre(),
                nuevoArticulo.getDescripcion(),
                nuevoArticulo.getCodigoBarras(),
                nuevoArticulo.getFamilia(),
                nuevoArticulo.getFotografia(),
                nuevoArticulo.getPrecioVenta()
        );
        return new ResponseEntity<>(articuloRespuestaDTO, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Articulo> actualizarArticulo(@PathVariable Long id, @Valid @RequestBody ArticuloActualizacionDTO articuloActualizacionDTO) {
        Articulo actualizadoArticulo = articuloService.actualizar(id, articuloActualizacionDTO);
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

    @GetMapping("/verificar/{codigoBarras}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> verificarExistencia(@PathVariable String codigoBarras) {
        boolean existe = articuloService.existeArticulo(codigoBarras);
        Map<String, Object> response = new HashMap<>();
        response.put("existe", existe);
        return ResponseEntity.ok(response);
    }
}
