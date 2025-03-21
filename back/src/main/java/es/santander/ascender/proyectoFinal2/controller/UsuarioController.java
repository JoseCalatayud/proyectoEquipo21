package es.santander.ascender.proyectoFinal2.controller;

import es.santander.ascender.proyectoFinal2.dto.UsuarioRequestDTO;
import es.santander.ascender.proyectoFinal2.dto.UsuarioResponseDTO;
import es.santander.ascender.proyectoFinal2.model.RolUsuario;
import es.santander.ascender.proyectoFinal2.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@PreAuthorize("hasRole('ADMIN')")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listarUsuarios() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    @GetMapping("/activos")
    public ResponseEntity<List<UsuarioResponseDTO>> listarUsuariosActivos() {
        return ResponseEntity.ok(usuarioService.listarActivos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<UsuarioResponseDTO> buscarPorUsername(@PathVariable String username) {
        return ResponseEntity.ok(usuarioService.buscarPorUsername(username));
    }

    @GetMapping("/rol/{rol}")
    public ResponseEntity<List<UsuarioResponseDTO>> buscarPorRol(@PathVariable RolUsuario rol) {
        return ResponseEntity.ok(usuarioService.buscarPorRol(rol));
    }

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> crearUsuario(@Valid @RequestBody UsuarioRequestDTO usuarioRequest) {
        return ResponseEntity.status(201).body(usuarioService.crear(usuarioRequest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> actualizarUsuario(@PathVariable Long id, @Valid @RequestBody UsuarioRequestDTO usuarioRequestDTO) {
        return ResponseEntity.ok(usuarioService.actualizar(id, usuarioRequestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> eliminarUsuario(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.borradoLogicoDeUsuario(id));
    }
    
    @PutMapping("/activar/{id}")
    public ResponseEntity<UsuarioResponseDTO> reactivarUsuario(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.reactivar(id));
    }
}
