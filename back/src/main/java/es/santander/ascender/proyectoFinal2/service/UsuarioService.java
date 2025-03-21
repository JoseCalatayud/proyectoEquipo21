package es.santander.ascender.proyectoFinal2.service;

import es.santander.ascender.proyectoFinal2.dto.UsuarioRequestDTO;
import es.santander.ascender.proyectoFinal2.dto.UsuarioResponseDTO;
import es.santander.ascender.proyectoFinal2.exception.MyBadDataException;
import es.santander.ascender.proyectoFinal2.model.RolUsuario;
import es.santander.ascender.proyectoFinal2.model.Usuario;
import es.santander.ascender.proyectoFinal2.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarTodos() {
        List<Usuario> listaUsuarios = usuarioRepository.findAll();
        if (listaUsuarios.isEmpty()) {
            throw new NoSuchElementException("No existen usuarios en la base de datos");
        }
        return convertToUsuarioResponseDTOList(listaUsuarios);
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarPorId(Long id) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        if (usuario.isEmpty()) {
            throw new NoSuchElementException("No existe el usuario con ID: " + id);
        }
        return new UsuarioResponseDTO(usuario.get().getUsername(), usuario.get().getRol());
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarPorUsername(String username) {
        Optional<Usuario> usuario = usuarioRepository.findByUsername(username);
        if (usuario.isEmpty()) {
            throw new NoSuchElementException("No existe el usuario con username: " + username);
        }
        return new UsuarioResponseDTO(usuario.get().getUsername(), usuario.get().getRol());
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> buscarPorRol(RolUsuario rol) {
        List<Usuario> listaUsuarios = usuarioRepository.findByRol(rol);
        if (listaUsuarios.isEmpty()) {
            throw new NoSuchElementException("No existen usuarios con rol: " + rol);
        }
        return convertToUsuarioResponseDTOList(listaUsuarios);
    }

    @Transactional
    public UsuarioResponseDTO crear(UsuarioRequestDTO usuarioRequestDTO) {
        if (usuarioRepository.existsByUsername(usuarioRequestDTO.getUsername())) {
            throw new IllegalArgumentException(
                    "Ya existe un usuario con el nombre: " + usuarioRequestDTO.getUsername());
        }
        Usuario usuario = new Usuario();
        usuario.setUsername(usuarioRequestDTO.getUsername());
        usuario.setPassword(passwordEncoder.encode(usuarioRequestDTO.getPassword()));
        usuario.setRol(RolUsuario.valueOf(usuarioRequestDTO.getRol().toUpperCase()));

        usuarioRepository.save(usuario);

        return new UsuarioResponseDTO(usuario.getUsername(), usuario.getRol());
    }

    @Transactional
    public UsuarioResponseDTO actualizar(UsuarioRequestDTO usuarioRequest) {
        Optional<Usuario> usuarioExistente = usuarioRepository
                .findByUsernameAndActivoTrue(usuarioRequest.getUsername());
        if (usuarioExistente.isEmpty()) {
            throw new NoSuchElementException("No existe el usuario con username: " + usuarioRequest.getUsername());
        }
        Usuario usuario = usuarioExistente.get();
        // Si se está cambiando el username, verificar que no exista otro usuario con
        // ese username
        if (!usuarioExistente.get().getUsername().equals(usuario.getUsername()) &&
                usuarioRepository.existsByUsername(usuario.getUsername())) {
            throw new IllegalArgumentException("Ya existe un usuario con el nombre: " + usuario.getUsername());
        }
        // Si la contraseña ha cambiado, encriptarla
        if (!usuario.getPassword().equals(usuarioExistente.get().getPassword())) {
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        }
        usuarioRepository.save(usuario);
        return new UsuarioResponseDTO(usuario.getUsername(), usuario.getRol());
    }

    @Transactional
    public UsuarioResponseDTO borradoLogicoDeUsuario(Long id) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(id);
        if (usuarioOptional.isEmpty()) {
            throw new NoSuchElementException("No existe el usuario con ID: " + id);
        }
        if (!usuarioOptional.get().isActivo()) {
            throw new MyBadDataException("El usuario ya está desactivado", id);
        }
        Usuario usuario = usuarioOptional.get();
        usuario.setActivo(false); // Borrado lógico: se desactiva el usuario
        usuarioRepository.save(usuario);
        return new UsuarioResponseDTO(usuario.getUsername(), usuario.getRol());
    }

    @Transactional
    public UsuarioResponseDTO reactivar(Long id) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(id);
        if (usuarioOptional.isEmpty()) {
            throw new NoSuchElementException("No existe el usuario con ID: " + id);
        }
        if (usuarioOptional.get().isActivo()) {
            throw new MyBadDataException("El usuario ya está activo", id);
        }
        Usuario usuario = usuarioOptional.get();
        usuario.setActivo(true); // Reactivar el usuario
        usuarioRepository.save(usuario);
        return new UsuarioResponseDTO(usuario.getUsername(), usuario.getRol());
    }

    @Transactional(readOnly = true)
    public boolean esAdmin(String username) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findByUsername(username);
        if (usuarioOptional.isEmpty()) {
            throw new NoSuchElementException("No existe el usuario con username: " + username);
        }
        Usuario usuario = usuarioOptional.get();
        return usuario.getRol().equals(RolUsuario.ADMIN);
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarActivos() {
        List<Usuario> listaUsuarios = usuarioRepository.findByActivoTrue();
        if (listaUsuarios.isEmpty()) {
            throw new NoSuchElementException("No existen usuarios activos en la base de datos");
        }

        return convertToUsuarioResponseDTOList(listaUsuarios);
    }

    private List<UsuarioResponseDTO> convertToUsuarioResponseDTOList(List<Usuario> listaUsuarios) {
        List<UsuarioResponseDTO> listaUsuariosDTO = listaUsuarios.stream()
                .map(usuario -> new UsuarioResponseDTO(usuario.getUsername(), usuario.getRol()))
                .toList();
        return listaUsuariosDTO;
    }
}
