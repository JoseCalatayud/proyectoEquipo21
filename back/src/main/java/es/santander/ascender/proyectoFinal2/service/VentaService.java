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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        // 1. Buscar usuario.
        Optional<Usuario> usuarioOptional = usuarioService.buscarPorId(ventaRequestDTO.getIdUsuario());
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
}