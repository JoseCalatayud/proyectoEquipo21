package es.santander.ascender.proyectoFinal2.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.santander.ascender.proyectoFinal2.model.RolUsuario;
import es.santander.ascender.proyectoFinal2.model.Usuario;
import es.santander.ascender.proyectoFinal2.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class UsuarioControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Usuario admin;
    private Usuario user;
    private String adminPassword = "password";
    private String userPassword = "password";

    @BeforeEach
    public void setup() {
        // Limpiamos el repositorio
        usuarioRepository.deleteAll();

        // Creamos usuarios de prueba
        admin = new Usuario();
        admin.setUsername("admin_test");
        admin.setPassword(passwordEncoder.encode(adminPassword));
        admin.setRol(RolUsuario.ADMIN);
        usuarioRepository.save(admin);

        user = new Usuario();
        user.setUsername("user_test");
        user.setPassword(passwordEncoder.encode(userPassword));
        user.setRol(RolUsuario.USER);   
        usuarioRepository.save(user);
    }

    @Test
    @WithMockUser(username = "admin_test", roles = {"ADMIN"})
    public void listarUsuarios_conAdmin_deberiaRetornarTodosLosUsuarios() throws Exception {
        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[?(@.username == 'admin_test')]", hasSize(1)))
                .andExpect(jsonPath("$[?(@.username == 'user_test')]", hasSize(1)));
    }

    @Test
    @WithMockUser(username = "user_test", roles = {"USER"})
    public void listarUsuarios_conUser_deberiaRetornarForbidden() throws Exception {
        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin_test", roles = {"ADMIN"})
    public void buscarPorId_conIdExistente_deberiaRetornarUsuario() throws Exception {
        mockMvc.perform(get("/api/usuarios/" + user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("user_test")))
                .andExpect(jsonPath("$.rol", is("USER")));
    }

    @Test
    @WithMockUser(username = "admin_test", roles = {"ADMIN"})
    public void buscarPorId_conIdInexistente_deberiaRetornarNotFound() throws Exception {
        mockMvc.perform(get("/api/usuarios/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user_test", roles = {"USER"})
    public void buscarPorId_conUser_deberiaRetornarForbidden() throws Exception {
        mockMvc.perform(get("/api/usuarios/" + admin.getId()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin_test", roles = {"ADMIN"})
    public void buscarPorUsername_conUsernameExistente_deberiaRetornarUsuario() throws Exception {
        mockMvc.perform(get("/api/usuarios/username/user_test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("user_test")))
                .andExpect(jsonPath("$.rol", is("USER")));
    }

    @Test
    @WithMockUser(username = "admin_test", roles = {"ADMIN"})
    public void buscarPorUsername_conUsernameInexistente_deberiaRetornarNotFound() throws Exception {
        mockMvc.perform(get("/api/usuarios/username/noexiste"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin_test", roles = {"ADMIN"})
    public void buscarPorRol_conRolExistente_deberiaRetornarUsuarios() throws Exception {
        mockMvc.perform(get("/api/usuarios/rol/ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].username", is("admin_test")));

        mockMvc.perform(get("/api/usuarios/rol/USER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].username", is("user_test")));
    }

    @Test
    @WithMockUser(username = "admin_test", roles = {"ADMIN"})
    public void crearUsuario_conAdmin_deberiaCrearUsuario() throws Exception {
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setUsername("nuevo_usuario");
        nuevoUsuario.setPassword("password123");
        nuevoUsuario.setRol(RolUsuario.USER);

        mockMvc.perform(post("/api/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevoUsuario)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username", is("nuevo_usuario")))
                .andExpect(jsonPath("$.rol", is("USER")));

        // Verificar que el usuario fue creado
        List<Usuario> usuarios = usuarioRepository.findAll();
        assert(usuarios.size() == 3);
    }

    @Test
    @WithMockUser(username = "admin_test", roles = {"ADMIN"})
    public void crearUsuario_conUsernameExistente_deberiaRetornarBadRequest() throws Exception {
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setUsername("user_test"); // Username ya existente
        nuevoUsuario.setPassword("password123");
        nuevoUsuario.setRol(RolUsuario.USER);

        mockMvc.perform(post("/api/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevoUsuario)))
                .andExpect(status().isBadRequest());
                
    }

    @Test
    @WithMockUser(username = "admin_test", roles = {"ADMIN"})
    public void actualizarUsuario_conAdmin_deberiaActualizarUsuario() throws Exception {
        user.setUsername("PEPE");
        user.setRol(RolUsuario.ADMIN);
        user.setPassword(passwordEncoder.encode("new_password"));

        mockMvc.perform(put("/api/usuarios/" + user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("PEPE")))
                .andExpect(jsonPath("$.rol", is("ADMIN")));

        // Verificar que el rol fue actualizado
        Usuario usuarioActualizado = usuarioRepository.findById(user.getId()).orElse(null);
        assert(usuarioActualizado != null && RolUsuario.ADMIN.equals(usuarioActualizado.getRol()));
    }

    @Test
    @WithMockUser(username = "admin_test", roles = {"ADMIN"})
    public void actualizarUsuario_conIdInexistente_deberiaRetornarBadRequest() throws Exception {
        Usuario usuarioInexistente = new Usuario();
        usuarioInexistente.setId(999L);
        usuarioInexistente.setUsername("no_existe");
        usuarioInexistente.setPassword("password");
        usuarioInexistente.setRol(RolUsuario.USER);

        mockMvc.perform(put("/api/usuarios/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuarioInexistente)))
                .andExpect(jsonPath("$.mensaje", containsString("El usuario que no existe. Debe crear uno nuevo")));
    }

    

    

    @Test
    @WithMockUser(username = "user_test", roles = {"USER"})
    public void realizarCualquierOperacion_conUser_deberiaRetornarForbidden() throws Exception {
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setUsername("test");
        nuevoUsuario.setPassword("password123");
        nuevoUsuario.setRol(RolUsuario.USER);

        // GET operations
        mockMvc.perform(get("/api/usuarios")).andExpect(status().isForbidden());
        mockMvc.perform(get("/api/usuarios/1")).andExpect(status().isForbidden());
        mockMvc.perform(get("/api/usuarios/username/admin")).andExpect(status().isForbidden());
        mockMvc.perform(get("/api/usuarios/rol/ADMIN")).andExpect(status().isForbidden());
        
        // POST operation
        mockMvc.perform(post("/api/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevoUsuario)))
                .andExpect(status().isForbidden());
        
        // PUT operation
        mockMvc.perform(put("/api/usuarios/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevoUsuario)))
                .andExpect(status().isForbidden());
        
        
    }
}
