package es.santander.ascender.proyectoFinal2.service;

import es.santander.ascender.proyectoFinal2.dto.usuario.UsuarioRequestDTO;
import es.santander.ascender.proyectoFinal2.dto.usuario.UsuarioResponseDTO;
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
        return convertToUsuarioResponseDTOList(listaUsuarios);
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarPorId(Long id) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        if (usuario.isEmpty()) {
            throw new NoSuchElementException("No existe el usuario con ID: " + id);
        }
        return new UsuarioResponseDTO(usuario.get().getUsername(), usuario.get().getRol(), usuario.get().isActivo()); // Se incluye el estado activo
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarPorUsername(String username) {
        Optional<Usuario> usuario = usuarioRepository.findByUsername(username);
        if (usuario.isEmpty()) {
            throw new NoSuchElementException("No existe el usuario con username: " + username);
        }
        return new UsuarioResponseDTO(usuario.get().getUsername(), usuario.get().getRol(), usuario.get().isActivo()); // Se incluye el estado activo
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> buscarPorRol(RolUsuario rol) {
        List<Usuario> listaUsuarios = usuarioRepository.findByRol(rol);
        return convertToUsuarioResponseDTOList(listaUsuarios);
    }

    
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

        return new UsuarioResponseDTO(usuario.getUsername(), usuario.getRol(), usuario.isActivo()); // Se establece el estado activo por defecto
    }

    
    public UsuarioResponseDTO actualizar(Long id,UsuarioRequestDTO usuarioRequest) {
        Optional<Usuario> usuarioExistente = usuarioRepository
                .findById(id);
        if (usuarioExistente.isEmpty()) {
            throw new NoSuchElementException("El usuario que no existe. Debe crear uno nuevo");
        }
        Usuario usuario = usuarioExistente.get();
        if(usuarioRequest.getUsername() == null)  {
            usuarioRequest.setUsername(usuario.getUsername());
        }
        if(usuarioRequest.getPassword() == null ) {
            usuarioRequest.setPassword(usuario.getPassword());
        }
        if(usuarioRequest.getRol() == null )  {
            usuarioRequest.setRol(usuario.getRol().toString());
        }
        //Verificar que el rol es válido
        try {
            RolUsuario.valueOf(usuarioRequest.getRol().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("El rol no es válido. Debe ser ADMIN o USER");
        }
        
        usuario.setUsername(usuarioRequest.getUsername());
        usuario.setRol(RolUsuario.valueOf(usuarioRequest.getRol().toUpperCase()));        
        usuario.setPassword(passwordEncoder.encode(usuarioRequest.getPassword()));
        usuario.setActivo(true); // No se encripta aquí, se hace en el repositorio


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
        return new UsuarioResponseDTO(usuario.getUsername(), usuario.getRol(), usuario.isActivo()); 
    }

    
    public UsuarioResponseDTO borradoLogicoDeUsuario(Long id) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(id);
        if (usuarioOptional.isEmpty()) {
            throw new NoSuchElementException("No existe el usuario con ID: " + id);
        }
        Usuario usuario = usuarioOptional.get();

        if (!usuario.isActivo()) {
            throw new IllegalStateException("El usuario ya está desactivado");
        }
        usuario.setActivo(false); // Borrado lógico: se desactiva el usuario
        usuarioRepository.save(usuario);
        return new UsuarioResponseDTO(usuario.getUsername(), usuario.getRol(), usuario.isActivo()); 
    }

    
    public UsuarioResponseDTO reactivar(Long id) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(id);
        if (usuarioOptional.isEmpty()) {
            throw new NoSuchElementException("No existe el usuario con ID: " + id);
        }
        Usuario usuario = usuarioOptional.get();

        if (usuario.isActivo()) {
            throw new IllegalStateException("El usuario ya está activo");
        }
        usuario.setActivo(true); // Reactivar el usuario
        usuarioRepository.save(usuario);
        return new UsuarioResponseDTO(usuario.getUsername(), usuario.getRol(), usuario.isActivo()); 
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
                .map(usuario -> new UsuarioResponseDTO(usuario.getUsername(), usuario.getRol(), usuario.isActivo())) // Se incluye el estado activo
                .toList();
        return listaUsuariosDTO;
    }
}
