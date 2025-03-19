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

    public List<Venta> listarVentas() {
        return ventaRepository.findAll();
    }

    public Venta crearVenta(VentaRequestDTO ventaRequestDTO) {
        // 1. Obtener el usuario autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<Usuario> usuarioOptional = usuarioService.buscarPorUsername(auth.getName());
        if (usuarioOptional.isEmpty()) {
            throw new IllegalArgumentException("No existe el usuario");
        }
        Usuario usuario = usuarioOptional.get();

        // 2. Crear venta
        Venta venta = new Venta(usuario);

        // 3. Procesar cada detalle de venta
        for (DetalleVentaDTO detalleDTO : ventaRequestDTO.getDetalles()) {
            // 3.1. Buscar articulo.
            Optional<Articulo> articuloOptional = articuloService.buscarPorId(detalleDTO.getIdArticulo());
            if (articuloOptional.isEmpty()) {
                throw new IllegalArgumentException("No existe el articulo");
            }
            Articulo articulo = articuloOptional.get();

            // 3.2. Comprobar stock.
            if (!articuloService.hayStockSuficiente(detalleDTO.getIdArticulo(), detalleDTO.getCantidad())) {
                throw new StockInsuficienteException(articulo.getNombre(), articulo.getStock(), detalleDTO.getCantidad());
            }

            // 3.3. Crear detalle venta
            DetalleVenta detalleVenta = new DetalleVenta(articulo, detalleDTO.getCantidad());

            // 3.4. Agregar detalle a venta
            venta.agregarDetalle(detalleVenta);

            // 3.5. Actualizar stock
            articuloService.actualizarStock(detalleDTO.getIdArticulo(), -detalleDTO.getCantidad());
        }

        // 4. Guardar la venta
        return ventaRepository.save(venta);
    }

    public Optional<Venta> buscarPorId(Long id) {
        return ventaRepository.findById(id);
    }

    public List<Venta> buscarPorUsuario(Long idUsuario) {
        Optional<Usuario> usuarioOptional = usuarioService.buscarPorId(idUsuario);
        if (usuarioOptional.isEmpty()) {
            throw new IllegalArgumentException("No existe el usuario");
        }
        Usuario usuario = usuarioOptional.get();
        return ventaRepository.findByUsuario(usuario);
    }
    
    public List<Venta> buscarPorFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return ventaRepository.findByFechaBetween(fechaInicio, fechaFin);
    }

    public List<Venta> buscarPorUsuarioYFechas(Long idUsuario, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        Optional<Usuario> usuarioOptional = usuarioService.buscarPorId(idUsuario);
        if (usuarioOptional.isEmpty()){
            throw new IllegalArgumentException("No existe el usuario");
        }
        Usuario usuario = usuarioOptional.get();
        return ventaRepository.findByUsuarioAndFechaBetween(usuario, fechaInicio, fechaFin);
    }

    public void anularVenta(Long id) {
        Optional<Venta> ventaOptional = ventaRepository.findById(id);
        if (ventaOptional.isEmpty()) {
            throw new IllegalArgumentException("No existe la venta con ID: " + id);
        }

        Venta venta = ventaOptional.get();
        // Devolver el stock que se había restado
        for (DetalleVenta detalle : venta.getDetalles()) {
            articuloService.actualizarStock(detalle.getArticulo().getId(), detalle.getCantidad());
        }

        // Eliminar la venta
        ventaRepository.deleteById(id);
    }
}
