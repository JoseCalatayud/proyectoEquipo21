package es.santander.ascender.proyectoFinal2.service;

import es.santander.ascender.proyectoFinal2.dto.DetalleVentaDTO;
import es.santander.ascender.proyectoFinal2.dto.VentaRequestDTO;
import es.santander.ascender.proyectoFinal2.exception.StockInsuficienteException;
import es.santander.ascender.proyectoFinal2.model.Articulo;
import es.santander.ascender.proyectoFinal2.model.DetalleVenta;
import es.santander.ascender.proyectoFinal2.model.Usuario;
import es.santander.ascender.proyectoFinal2.model.Venta;
import es.santander.ascender.proyectoFinal2.repository.VentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class VentaService {

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private ArticuloService articuloService;

    @Autowired
    private UsuarioService usuarioService;

    @Transactional(readOnly = true)
    public List<Venta> listarVentas() {
        return ventaRepository.findAll();
    }

    @Transactional
    public Venta crearVenta(VentaRequestDTO ventaRequestDTO) {
        // 1. Obtener el usuario autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuario = usuarioService.obtenerUsuarioPorUsername(auth.getName());

        // 2. Crear venta
        Venta venta = new Venta(usuario);

        // 3. Procesar cada detalle de venta
        for (DetalleVentaDTO detalleDTO : ventaRequestDTO.getDetalles()) {
            // 3.1. Buscar articulo.
            Articulo articulo = articuloService.buscarPorId(detalleDTO.getIdArticulo())
                    .orElseThrow(() -> new IllegalArgumentException("No existe el articulo"));

            // 3.2. Comprobar stock.
            if (!articuloService.hayStockSuficiente(detalleDTO.getIdArticulo(), detalleDTO.getCantidad())) {
                throw new StockInsuficienteException(articulo.getNombre(), articulo.getStock(), detalleDTO.getCantidad());
            }

            // 3.3. Crear detalle venta
            DetalleVenta detalleVenta = new DetalleVenta(articulo, detalleDTO.getCantidad());

            // 3.4. Agregar detalle a venta
            venta.agregarDetalle(detalleVenta);

            // 3.5. Actualizar stock
            //Usamos synchronized para evitar condiciones de carrera
            synchronized (articulo) {
                articuloService.actualizarStock(detalleDTO.getIdArticulo(), -detalleDTO.getCantidad());
            }
        }

        // 4. Guardar la venta
        return ventaRepository.save(venta);
    }

    @Transactional(readOnly = true)
    public Optional<Venta> buscarPorId(Long id) {
        return ventaRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Venta> buscarPorUsuario(Long idUsuario) {
        Usuario usuario = usuarioService.buscarPorId(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("No existe el usuario"));
        return ventaRepository.findByUsuario(usuario);
    }

    @Transactional(readOnly = true)
    public List<Venta> buscarPorFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return ventaRepository.findByFechaBetween(fechaInicio, fechaFin);
    }

    @Transactional(readOnly = true)
    public List<Venta> buscarPorUsuarioYFechas(Long idUsuario, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        Usuario usuario = usuarioService.buscarPorId(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("No existe el usuario"));
        return ventaRepository.findByUsuarioAndFechaBetween(usuario, fechaInicio, fechaFin);
    }

    @Transactional
    public void anularVenta(Long id) {
        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe la venta con ID: " + id));

        // Devolver el stock que se había restado
        for (DetalleVenta detalle : venta.getDetalles()) {
            //Usamos synchronized para evitar condiciones de carrera
            synchronized (detalle.getArticulo()) {
                articuloService.actualizarStock(detalle.getArticulo().getId(), detalle.getCantidad());
            }
        }

        // Eliminar la venta
        ventaRepository.deleteById(id);
    }
}
