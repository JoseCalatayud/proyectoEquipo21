package es.santander.ascender.proyectoFinal2.repository;

import es.santander.ascender.proyectoFinal2.model.RolUsuario;
import es.santander.ascender.proyectoFinal2.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsername(String username);

    boolean existsByUsername(String username);

    List<Usuario> findByRol(RolUsuario rol);

    List<Usuario> findByActivoTrue(); // Nuevo método para buscar usuarios activos

    Optional<Usuario> findByIdAndActivoTrue(Long id); // Nuevo metodo para buscar por id y que este activo.

    Optional<Usuario> findByUsernameAndActivoTrue(String username); // Nuevo metodo para buscar por username y que este activo.
}
