package es.santander.ascender.proyectoFinal2.service;

import es.santander.ascender.proyectoFinal2.dto.CompraRequestDTO;
import es.santander.ascender.proyectoFinal2.dto.DetalleCompraDTO;
import es.santander.ascender.proyectoFinal2.exception.StockInsuficienteException;
import es.santander.ascender.proyectoFinal2.model.Articulo;
import es.santander.ascender.proyectoFinal2.model.Compra;
import es.santander.ascender.proyectoFinal2.model.DetalleCompra;
import es.santander.ascender.proyectoFinal2.model.Usuario;
import es.santander.ascender.proyectoFinal2.repository.CompraRepository;
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
public class CompraService {

    @Autowired
    private CompraRepository compraRepository;

    @Autowired
    private ArticuloService articuloService;

    @Autowired
    private UsuarioService usuarioService;

    @Transactional(readOnly = true)
    public List<Compra> listarTodas() {
        return compraRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Compra> buscarPorId(Long id) {
        return compraRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Compra> buscarPorUsuario(Usuario usuario) {
        return compraRepository.findByUsuario(usuario);
    }

    @Transactional(readOnly = true)
    public List<Compra> buscarPorFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return compraRepository.findByFechaBetween(fechaInicio, fechaFin);
    }

    @Transactional(readOnly = true)
    public List<Compra> buscarPorUsuarioYFechas(Usuario usuario, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return compraRepository.findByUsuarioAndFechaBetween(usuario, fechaInicio, fechaFin);
    }

    public Compra crearCompra(CompraRequestDTO compraRequestDTO) {
        // 1. Obtener el usuario autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalArgumentException("Usuario no autenticado");
        }
        Usuario usuario = usuarioService.obtenerUsuarioPorUsername(auth.getName());

        // 2. Crear compra
        Compra compra = new Compra(usuario);

        // 3. Procesar cada detalle de compra
        for (DetalleCompraDTO detalleDTO : compraRequestDTO.getDetalles()) {
            // 3.1. Buscar articulo.
            Articulo articulo = articuloService.buscarPorId(detalleDTO.getIdArticulo()).orElseThrow(() -> new IllegalArgumentException("No existe el articulo"));

            // 3.2. Verificar que el artículo no está borrado
            if (articulo.isBorrado()) {
                throw new IllegalStateException("No se puede comprar el artículo porque está descatalogado: " + articulo.getNombre());
            }

            // 3.3. Comprobar stock.
            if (!articuloService.hayStockSuficiente(detalleDTO.getIdArticulo(), detalleDTO.getCantidad())) {
                throw new StockInsuficienteException(articulo.getNombre(), articulo.getStock(), detalleDTO.getCantidad());
            }

            // 3.4. Crear detalle compra
            DetalleCompra detalleCompra = new DetalleCompra(articulo, detalleDTO.getCantidad(), articulo.getPrecioCompra());

            // 3.5. Agregar detalle a compra
            compra.agregarDetalle(detalleCompra);

            // 3.6. Actualizar stock
            articuloService.actualizarStock(detalleDTO.getIdArticulo(), detalleDTO.getCantidad());
        }

        // 4. Guardar la compra
        return compraRepository.save(compra);
    }

    public void anularCompra(Long id) {
        Compra compra = compraRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("No existe la compra con ID: " + id));

        // Restar el stock que se había añadido
        for (DetalleCompra detalle : compra.getDetalles()) {
            articuloService.actualizarStock(detalle.getArticulo().getId(), -detalle.getCantidad());
        }

        // Eliminar la compra
        compraRepository.deleteById(id);
    }
}
