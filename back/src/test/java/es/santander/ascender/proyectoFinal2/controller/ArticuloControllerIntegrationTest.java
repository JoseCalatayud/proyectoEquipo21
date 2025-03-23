package es.santander.ascender.proyectoFinal2.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.santander.ascender.proyectoFinal2.model.Articulo;
import es.santander.ascender.proyectoFinal2.model.RolUsuario;
import es.santander.ascender.proyectoFinal2.model.Usuario;
import es.santander.ascender.proyectoFinal2.repository.ArticuloRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class ArticuloControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ArticuloRepository articuloRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Usuario admin;
    private Usuario user;
    private Articulo articulo1;
    private Articulo articulo2;

    @BeforeEach
    public void setup() {
        // Limpiamos los repositorios
        articuloRepository.deleteAll();
        usuarioRepository.deleteAll();

        // Creamos usuarios de prueba
        admin = new Usuario();
        admin.setUsername("admin_test");
        admin.setPassword(passwordEncoder.encode("password"));
        admin.setRol(RolUsuario.ADMIN);
        usuarioRepository.save(admin);

        user = new Usuario();
        user.setUsername("user_test");
        user.setPassword(passwordEncoder.encode("password"));
        user.setRol(RolUsuario.USER);
        usuarioRepository.save(user);

        // Creamos artículos de prueba
        articulo1 = new Articulo();
        articulo1.setNombre("Artículo Test 1");
        articulo1.setDescripcion("Descripción test 1");
        articulo1.setCodigoBarras("1234567890123");
        articulo1.setFamilia("Electrónica");
        articulo1.setPrecioVenta(10.0);
        articulo1.setStock(100);
        articulo1.setPrecioPromedioPonderado(8.0);
        articuloRepository.save(articulo1);

        articulo2 = new Articulo();
        articulo2.setNombre("Artículo Test 2");
        articulo2.setDescripcion("Descripción test 2");
        articulo2.setCodigoBarras("2234567890123");
        articulo2.setFamilia("Hogar");
        articulo2.setPrecioVenta(20.0);
        articulo2.setStock(50);
        articulo2.setPrecioPromedioPonderado(15.0);
        articuloRepository.save(articulo2);
    }

    @Test
    @WithMockUser(username = "admin_test", roles = {"ADMIN"})
    public void listarArticulos_deberiaRetornarTodosLosArticulos() throws Exception {
        mockMvc.perform(get("/api/articulos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].nombre", is("Artículo Test 1")))
                .andExpect(jsonPath("$[1].nombre", is("Artículo Test 2")));
    }

    @Test
    @WithMockUser(username = "admin_test", roles = {"ADMIN"})
    public void buscarPorId_conIdExistente_deberiaRetornarArticulo() throws Exception {
        mockMvc.perform(get("/api/articulos/" + articulo1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(articulo1.getId().intValue())))
                .andExpect(jsonPath("$.nombre", is("Artículo Test 1")));
    }

    @Test
    @WithMockUser(username = "admin_test", roles = {"ADMIN"})
    public void buscarPorId_conIdInexistente_deberiaRetornarNotFound() throws Exception {
        mockMvc.perform(get("/api/articulos/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin_test", roles = {"ADMIN"})
    public void buscarPorCodigoBarras_conCodigoExistente_deberiaRetornarArticulo() throws Exception {
        mockMvc.perform(get("/api/articulos/codigo/1234567890123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigoBarras", is("1234567890123")))
                .andExpect(jsonPath("$.nombre", is("Artículo Test 1")));
    }

    @Test
    @WithMockUser(username = "admin_test", roles = {"ADMIN"})
    public void buscarPorCodigoBarras_conCodigoInexistente_deberiaRetornarNotFound() throws Exception {
        mockMvc.perform(get("/api/articulos/codigo/9999999999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin_test", roles = {"ADMIN"})
    public void buscarPorFamilia_conFamiliaExistente_deberiaRetornarArticulos() throws Exception {
        mockMvc.perform(get("/api/articulos/familia/Electrónica"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].familia", is("Electrónica")));
    }

    @Test
    @WithMockUser(username = "admin_test", roles = {"ADMIN"})
    public void buscarPorNombre_conNombreExistente_deberiaRetornarArticulos() throws Exception {
        mockMvc.perform(get("/api/articulos/buscar?nombre=Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        mockMvc.perform(get("/api/articulos/buscar?nombre=Test 1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nombre", is("Artículo Test 1")));
    }

    @Test
    @WithMockUser(username = "admin_test", roles = {"ADMIN"})
    public void crearArticulo_conAdmin_deberiaCrearArticulo() throws Exception {
        Articulo nuevoArticulo = new Articulo();
        nuevoArticulo.setNombre("Artículo Test 3");
        nuevoArticulo.setDescripcion("Descripción test 3");
        nuevoArticulo.setCodigoBarras("3234567890123");
        nuevoArticulo.setFamilia("Deportes");
        nuevoArticulo.setPrecioVenta(30.0);
        nuevoArticulo.setStock(30);
        nuevoArticulo.setPrecioPromedioPonderado(25.0);

        mockMvc.perform(post("/api/articulos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevoArticulo)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.nombre", is("Artículo Test 3")));

        // Verificar que el artículo fue creado
        List<Articulo> articulos = articuloRepository.findAll();
        assert(articulos.size() == 3);
    }

    @Test
    @WithMockUser(username = "user_test", roles = {"USER"})
    public void crearArticulo_conUser_deberiaRetornarForbidden() throws Exception {
        Articulo nuevoArticulo = new Articulo();
        nuevoArticulo.setNombre("Artículo Test 3");
        nuevoArticulo.setDescripcion("Descripción test 3");
        nuevoArticulo.setCodigoBarras("3234567890123");
        nuevoArticulo.setFamilia("Deportes");
        nuevoArticulo.setPrecioVenta(30.0);
        nuevoArticulo.setStock(30);
        nuevoArticulo.setPrecioPromedioPonderado(25.0);

        mockMvc.perform(post("/api/articulos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevoArticulo)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin_test", roles = {"ADMIN"})
    public void actualizarArticulo_conAdmin_deberiaActualizarArticulo() throws Exception {
        articulo1.setNombre("Artículo Test 1 Actualizado");
        articulo1.setDescripcion("Descripción actualizada");
        articulo1.setPrecioVenta(15.0);

        mockMvc.perform(put("/api/articulos/" + articulo1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(articulo1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", is("Artículo Test 1 Actualizado")))
                .andExpect(jsonPath("$.descripcion", is("Descripción actualizada")))
                .andExpect(jsonPath("$.precioVenta", is(15.0)));
    }

    @Test
    @WithMockUser(username = "admin_test", roles = {"ADMIN"})
    public void borrarArticulo_conAdmin_deberiaBorrarLogicamente() throws Exception {
        mockMvc.perform(post("/api/articulos/" + articulo1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje", is("Artículo eliminado correctamente")));

        // Verificar que el artículo fue marcado como borrado
        Articulo articuloBorrado = articuloRepository.findById(articulo1.getId()).orElse(null);
        assert(articuloBorrado != null && articuloBorrado.isBorrado());

        
        
    }
    

    

    

    
}
