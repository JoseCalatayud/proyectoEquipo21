package es.santander.ascender.proyectoFinal2.service;

import es.santander.ascender.proyectoFinal2.dto.*;
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
import java.util.ArrayList;
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
    public List<VentaListDTO> listarVentas() {
        List<Venta> ventas = ventaRepository.findAll();
        List<VentaListDTO> ventaListDTOS = new ArrayList<>();

        for (Venta venta : ventas) {
            List<DetalleVentaListDTO> detalleVentaListDTOS = new ArrayList<>();
            for (DetalleVenta detalleVenta : venta.getDetalles()) {
                DetalleVentaListDTO detalleVentaListDTO = new DetalleVentaListDTO(
                        detalleVenta.getArticulo().getId(),
                        detalleVenta.getArticulo().getNombre(),
                        detalleVenta.getArticulo().getDescripcion(),
                        detalleVenta.getArticulo().getCodigoBarras(),
                        detalleVenta.getArticulo().getFamilia(),
                        detalleVenta.getCantidad(),
                        detalleVenta.getSubtotal()
                );
                detalleVentaListDTOS.add(detalleVentaListDTO);
            }
            UsuarioVentaDTO usuarioVentaDTO = new UsuarioVentaDTO(venta.getUsuario().getId(), venta.getUsuario().getUsername());
            VentaListDTO ventaListDTO = new VentaListDTO(
                    venta.getId(),
                    venta.getFecha(),
                    venta.getTotal(),
                    usuarioVentaDTO,
                    detalleVentaListDTOS
            );
            ventaListDTOS.add(ventaListDTO);
        }
        return ventaListDTOS;
    }

    
    public Venta crearVenta(VentaRequestDTO ventaRequestDTO) {
        // 1. Obtener el usuario autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new IllegalArgumentException("Usuario no autenticado");
        }
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
        // 4. Comprobar que la venta tiene detalles
        if (venta.getDetalles().isEmpty()) {
            throw new IllegalArgumentException("La venta debe tener al menos un detalle");
        }

        // 5. Guardar la venta
        return ventaRepository.save(venta);
    }

    @Transactional(readOnly = true)
    public Optional<VentaResponseDTO> buscarPorId(Long id) {
        Optional<Venta> ventaOptional = ventaRepository.findById(id);
        if (ventaOptional.isEmpty()) {
            return Optional.empty();
        }
        Venta venta = ventaOptional.get();

        List<DetalleVentaListDTO> detalleVentaListDTOS = new ArrayList<>();
        for (DetalleVenta detalleVenta : venta.getDetalles()) {
            DetalleVentaListDTO detalleVentaListDTO = new DetalleVentaListDTO(
                    detalleVenta.getArticulo().getId(),
                    detalleVenta.getArticulo().getNombre(),
                    detalleVenta.getArticulo().getDescripcion(),
                    detalleVenta.getArticulo().getCodigoBarras(),
                    detalleVenta.getArticulo().getFamilia(),
                    detalleVenta.getCantidad(),
                    detalleVenta.getSubtotal()
            );
            detalleVentaListDTOS.add(detalleVentaListDTO);
        }
        UsuarioVentaDTO usuarioVentaDTO = new UsuarioVentaDTO(venta.getUsuario().getId(), venta.getUsuario().getUsername());
        VentaResponseDTO ventaResponseDTO = new VentaResponseDTO(
                venta.getId(),
                venta.getFecha(),
                venta.getTotal(),
                usuarioVentaDTO,
                detalleVentaListDTOS
        );
        return Optional.of(ventaResponseDTO);
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
