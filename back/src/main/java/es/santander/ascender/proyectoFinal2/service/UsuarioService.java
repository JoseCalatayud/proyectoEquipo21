package es.santander.ascender.proyectoFinal2.service;

import es.santander.ascender.proyectoFinal2.model.RolUsuario;
import es.santander.ascender.proyectoFinal2.model.Usuario;
import es.santander.ascender.proyectoFinal2.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findByIdAndActivoTrue(id);
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorUsername(String username) {
        return usuarioRepository.findByUsernameAndActivoTrue(username);
    }

    @Transactional(readOnly = true)
    public Usuario obtenerUsuarioPorUsername(String username) {
        return usuarioRepository.findByUsernameAndActivoTrue(username).orElseThrow(() -> new IllegalArgumentException("No existe el usuario"));
    }

    @Transactional(readOnly = true)
    public List<Usuario> buscarPorRol(RolUsuario rol) {
        return usuarioRepository.findByRol(rol);
    }

    @Transactional
    public Usuario crear(Usuario usuario) {
        // Verificar si ya existe un usuario con el mismo username
        if (usuarioRepository.existsByUsername(usuario.getUsername())) {
            throw new IllegalArgumentException("Ya existe un usuario con el nombre: " + usuario.getUsername());
        }

        // Encriptar la contraseña
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario actualizar(Usuario usuario) {
        Optional<Usuario> usuarioExistente = usuarioRepository.findByIdAndActivoTrue(usuario.getId());

        if (usuarioExistente.isEmpty()) {
            throw new IllegalArgumentException("No existe el usuario con ID: " + usuario.getId());
        }

        // Si se está cambiando el username, verificar que no exista otro usuario con ese username
        if (!usuarioExistente.get().getUsername().equals(usuario.getUsername()) &&
                usuarioRepository.existsByUsername(usuario.getUsername())) {
            throw new IllegalArgumentException("Ya existe un usuario con el nombre: " + usuario.getUsername());
        }

        // Si la contraseña ha cambiado, encriptarla
        if (!usuario.getPassword().equals(usuarioExistente.get().getPassword())) {
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        }

        return usuarioRepository.save(usuario);
    }

    @Transactional
    public void eliminar(Long id) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(id);

        if (usuarioOptional.isEmpty()) {
            throw new IllegalArgumentException("No existe el usuario con ID: " + id);
        }

        Usuario usuario = usuarioOptional.get();
        usuario.setActivo(false); // Borrado lógico: se desactiva el usuario
        usuarioRepository.save(usuario);
    }
    
    @Transactional
    public void reactivar(Long id) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(id);

        if (usuarioOptional.isEmpty()) {
            throw new IllegalArgumentException("No existe el usuario con ID: " + id);
        }

        Usuario usuario = usuarioOptional.get();
        usuario.setActivo(true); // Reactivar el usuario
        usuarioRepository.save(usuario);
    }

    @Transactional(readOnly = true)
    public boolean esAdmin(Usuario usuario) {
        return usuario != null && "ADMIN".equals(usuario.getRol().name());
    }

    
    @Transactional(readOnly = true)
    public List<Usuario> listarActivos() {
        return usuarioRepository.findByActivoTrue();
    }
}
