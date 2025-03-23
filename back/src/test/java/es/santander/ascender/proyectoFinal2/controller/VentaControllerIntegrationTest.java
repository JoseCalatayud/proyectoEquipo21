package es.santander.ascender.proyectoFinal2.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.santander.ascender.proyectoFinal2.dto.venta.DetalleVentaRequestDTO;
import es.santander.ascender.proyectoFinal2.dto.venta.VentaRequestDTO;
import es.santander.ascender.proyectoFinal2.model.Articulo;
import es.santander.ascender.proyectoFinal2.model.RolUsuario;
import es.santander.ascender.proyectoFinal2.model.Usuario;
import es.santander.ascender.proyectoFinal2.repository.ArticuloRepository;
import es.santander.ascender.proyectoFinal2.repository.UsuarioRepository;
import es.santander.ascender.proyectoFinal2.repository.VentaRepository;
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

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class VentaControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ArticuloRepository articuloRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private VentaRepository ventaRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    private Usuario admin;
    private Usuario user;
    private Articulo articulo1;
    private Articulo articulo2;
    private Long ventaId;

    @BeforeEach
    public void setup() {
        // Limpiamos los repositorios
        ventaRepository.deleteAll();
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
        articulo1.setFamilia("Test");
        articulo1.setPrecioVenta(10.0);
        articulo1.setStock(100);
        articulo1.setPrecioPromedioPonderado(8.0);
        articuloRepository.save(articulo1);

        articulo2 = new Articulo();
        articulo2.setNombre("Artículo Test 2");
        articulo2.setDescripcion("Descripción test 2");
        articulo2.setCodigoBarras("2234567890123");
        articulo2.setFamilia("Test");
        articulo2.setPrecioVenta(20.0);
        articulo2.setStock(50);
        articulo2.setPrecioPromedioPonderado(15.0);
        articuloRepository.save(articulo2);
    }

    @Test
    @WithMockUser(username = "admin_test", roles = {"ADMIN"})
    public void listarVentas_conAdmin_deberiaRetornarTodasLasVentas() throws Exception {
        // Primero creamos una venta para tener datos que listar
        crearVentaDePrueba();

        // Intentamos listar las ventas
        mockMvc.perform(get("/api/ventas/listar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].id", notNullValue()));
    }

    
    @Test
    @WithMockUser(username = "user_test", roles = {"USER"})
    public void crearVenta_conUser_deberiaCrearVenta() throws Exception {
        // Crear DTO para la venta
        VentaRequestDTO ventaRequestDTO = new VentaRequestDTO();
        List<DetalleVentaRequestDTO> detalles = new ArrayList<>();
        
        DetalleVentaRequestDTO detalle = new DetalleVentaRequestDTO();
        detalle.setIdArticulo(articulo1.getId());
        detalle.setCantidad(2);
        detalles.add(detalle);
        
        ventaRequestDTO.setDetalles(detalles);

        // Realizar la petición
        mockMvc.perform(post("/api/ventas/crear")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ventaRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.detalles", hasSize(1)));
    }

    @Test
    @WithMockUser(username = "user_test", roles = {"ADMIN"})
    public void buscarPorId_ventaPropia_deberiaRetornarVenta() throws Exception {
        // Crear venta
        crearVentaDePrueba();

        // Buscar por ID
        mockMvc.perform(get("/api/ventas/" + ventaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(ventaId.intValue())));
    }
    
    @Test
    @WithMockUser(username = "admin_test", roles = {"ADMIN"})
    public void buscarPorFechas_conAdmin_deberiaRetornarVentas() throws Exception {
        // Crear venta
        crearVentaDePrueba();

        // Buscar por fechas (un rango amplio para asegurar que encuentre la venta)
        mockMvc.perform(get("/api/ventas/fechas")
                .param("fechaInicio", "2020-01-01T00:00:00")
                .param("fechaFin", "2030-01-01T00:00:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @WithMockUser(username = "user_test", roles = {"ADMIN"})
    public void anularVenta_ventaPropia_deberiaAnularVenta() throws Exception {
        // Crear venta
        crearVentaDePrueba();

        // Anular venta
        mockMvc.perform(delete("/api/ventas/" + ventaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje", is("Venta anulada correctamente")));

        // Verificar que la venta ya no existe
        mockMvc.perform(get("/api/ventas/" + ventaId))
                .andExpect(status().isNotFound());
    }

    private void crearVentaDePrueba() throws Exception {
        // Crear DTO para la venta
        VentaRequestDTO ventaRequestDTO = new VentaRequestDTO();
        List<DetalleVentaRequestDTO> detalles = new ArrayList<>();
        
        DetalleVentaRequestDTO detalle = new DetalleVentaRequestDTO();
        detalle.setIdArticulo(articulo1.getId());
        detalle.setCantidad(1);
        detalles.add(detalle);
        
        ventaRequestDTO.setDetalles(detalles);

        // Realizar la petición
        String response = mockMvc.perform(post("/api/ventas/crear")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ventaRequestDTO)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        
        // Extraer el ID de la venta creada
        ventaId = objectMapper.readTree(response).get("id").asLong();
    }
}