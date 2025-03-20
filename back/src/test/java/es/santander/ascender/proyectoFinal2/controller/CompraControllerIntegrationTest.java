package es.santander.ascender.proyectoFinal2.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.santander.ascender.proyectoFinal2.dto.CompraRequestDTO;
import es.santander.ascender.proyectoFinal2.dto.DetalleCompraDTO;
import es.santander.ascender.proyectoFinal2.model.Articulo;
import es.santander.ascender.proyectoFinal2.model.Compra;
import es.santander.ascender.proyectoFinal2.model.DetalleCompra;
import es.santander.ascender.proyectoFinal2.model.RolUsuario;
import es.santander.ascender.proyectoFinal2.model.Usuario;
import es.santander.ascender.proyectoFinal2.repository.ArticuloRepository;
import es.santander.ascender.proyectoFinal2.repository.CompraRepository;
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

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class CompraControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ArticuloRepository articuloRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CompraRepository compraRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Usuario admin;
    private Usuario user;
    private Articulo articulo1;
    private Articulo articulo2;
    private Long compraId;

    @BeforeEach
    public void setup() {
        // Limpiamos los repositorios
        compraRepository.deleteAll();
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
    @WithMockUser(username = "admin_test", roles = { "ADMIN" })
    public void listarCompras_conAdmin_deberiaRetornarTodasLasCompras() throws Exception {
        // Primero creamos una compra para tener datos que listar
        crearCompraDePrueba();

        // Intentamos listar las compras
        mockMvc.perform(get("/api/compras"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].id", notNullValue()));
    }

    @Test
    @WithMockUser(username = "user_test", roles = { "USER" })
    public void listarCompras_conUser_deberiaRetornarForbidden() throws Exception {
        mockMvc.perform(get("/api/compras"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin_test", roles = { "ADMIN" })
    public void buscarPorId_conIdExistente_deberiaRetornarCompra() throws Exception {
        // Crear compra
        crearCompraDePrueba();

        // Buscar por ID
        mockMvc.perform(get("/api/compras/" + compraId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(compraId.intValue())));
    }

    @Test
    @WithMockUser(username = "admin_test", roles = { "ADMIN" })
    public void buscarPorId_conIdInexistente_deberiaRetornarBadRequest() throws Exception {
        mockMvc.perform(get("/api/compras/999"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user_test", roles = { "USER" })
    public void buscarPorId_conUser_deberiaRetornarForbidden() throws Exception {
        mockMvc.perform(get("/api/compras/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin_test", roles = { "ADMIN" })
    public void buscarPorFechas_conAdmin_deberiaRetornarCompras() throws Exception {
        // Crear compra
        crearCompraDePrueba();

        // Buscar por fechas (un rango amplio para asegurar que encuentre la compra)
        mockMvc.perform(get("/api/compras/fechas")
                .param("fechaInicio", "2020-01-01T00:00:00")
                .param("fechaFin", "2030-01-01T00:00:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @WithMockUser(username = "user_test", roles = { "USER" })
    public void buscarPorFechas_conUser_deberiaRetornarForbidden() throws Exception {
        mockMvc.perform(get("/api/compras/fechas")
                .param("fechaInicio", "2020-01-01T00:00:00")
                .param("fechaFin", "2030-01-01T00:00:00"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin_test", roles = { "ADMIN" })
    public void realizarCompra_conAdmin_deberiaCrearCompra() throws Exception {
        // Crear DTO para la compra
        CompraRequestDTO compraRequestDTO = new CompraRequestDTO();
        List<DetalleCompraDTO> detalles = new ArrayList<>();

        DetalleCompraDTO detalle = new DetalleCompraDTO();
        detalle.setIdArticulo(articulo1.getId());
        detalle.setCantidad(5);
        detalle.setPrecioUnitario(12.0);
        detalles.add(detalle);

        compraRequestDTO.setDetalles(detalles);

        // Realizar la petición
        mockMvc.perform(post("/api/compras")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(compraRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.detalles", hasSize(1)));

        // Verificar que el stock fue actualizado
        Articulo articuloActualizado = articuloRepository.findById(articulo1.getId()).orElse(null);
        assert (articuloActualizado != null && articuloActualizado.getStock() == 105); // 100 inicial + 5 comprados
    }

    @Test
    @WithMockUser(username = "user_test", roles = { "USER" })
    public void realizarCompra_conUser_deberiaRetornarForbidden() throws Exception {
        CompraRequestDTO compraRequestDTO = new CompraRequestDTO();
        List<DetalleCompraDTO> detalles = new ArrayList<>();

        DetalleCompraDTO detalle = new DetalleCompraDTO();
        detalle.setIdArticulo(articulo1.getId());
        detalle.setCantidad(5);
        detalle.setPrecioUnitario(12.0);
        detalles.add(detalle);

        compraRequestDTO.setDetalles(detalles);

        mockMvc.perform(post("/api/compras")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(compraRequestDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin_test", roles = { "ADMIN" })
    public void anularCompra_conAdmin_deberiaAnularCompra() throws Exception {
        // Crear compra
        crearCompraDePrueba();

        // Obtener stock antes de anular
        int stockAntes = articuloRepository.findById(articulo1.getId()).map(Articulo::getStock).orElse(0);

        // Anular compra
        mockMvc.perform(delete("/api/compras/" + compraId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje", is("Compra anulada correctamente")));

        // Verificar que la compra ya no existe
        mockMvc.perform(get("/api/compras/" + compraId))
                .andExpect(status().isBadRequest());

        // Verificar que el stock fue restaurado
        int stockDespues = articuloRepository.findById(articulo1.getId()).map(Articulo::getStock).orElse(0);
        assert (stockDespues == stockAntes - 3); // Se habían comprado 3 unidades
    }

    @Test
    @WithMockUser(username = "user_test", roles = { "USER" })
    public void anularCompra_conUser_deberiaRetornarForbidden() throws Exception {
        // Crear compra
        Compra compra = new Compra(admin);
        DetalleCompra detalle = new DetalleCompra(articulo1, 3, 9.0);
        compra.agregarDetalle(detalle);
        compra = compraRepository.save(compra);

        // Intentar anular como user
        mockMvc.perform(delete("/api/compras/" + compra.getId()))
                .andExpect(status().isForbidden());
    }

    private void crearCompraDePrueba() throws Exception {
        // Crear DTO para la compra
        CompraRequestDTO compraRequestDTO = new CompraRequestDTO();
        List<DetalleCompraDTO> detalles = new ArrayList<>();

        DetalleCompraDTO detalle = new DetalleCompraDTO();
        detalle.setIdArticulo(articulo1.getId());
        detalle.setCantidad(3);
        detalle.setPrecioUnitario(9.0);
        detalles.add(detalle);

        compraRequestDTO.setDetalles(detalles);

        // Realizar la petición
        String response = mockMvc.perform(post("/api/compras")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(compraRequestDTO)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        // Extraer el ID de la compra creada
        compraId = objectMapper.readTree(response).get("id").asLong();
    }
}
