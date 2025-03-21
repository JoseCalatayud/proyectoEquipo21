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
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
    public List<VentaResponseDTO> listarVentas() {
        List<Venta> ventas = ventaRepository.findAll();
        List<VentaResponseDTO> ventaResponseDTO = new ArrayList<>();

        for (Venta venta : ventas) {

            ventaResponseDTO.add(convertirVentaEnVentaResponseDTO(venta));
        }
        return ventaResponseDTO;
    }

    public VentaResponseDTO crearVenta(VentaRequestDTO ventaRequestDTO) {
        // 1. Obtener el usuario autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalArgumentException("Usuario no autenticado");
        }
        Usuario usuario = usuarioService.obtenerUsuarioPorUsername(auth.getName());
        Set<Long> articulosIds = new HashSet<>();
        for (DetalleVentaDTO detalleDTO : ventaRequestDTO.getDetalles()) {
            if (!articulosIds.add(detalleDTO.getIdArticulo())) {
                throw new IllegalArgumentException("No se permite duplicar artículos en la misma venta");
            }
        }
        // 2. Crear venta
        Venta venta = new Venta(usuario);
        // 3. Procesar cada detalle de venta
        for (DetalleVentaDTO detalleDTO : ventaRequestDTO.getDetalles()) {
            // 3.1. Buscar articulo.
            Articulo articulo = articuloService.buscarPorId(detalleDTO.getIdArticulo())
                    .orElseThrow(() -> new IllegalArgumentException("No existe el articulo"));

            // 3.2. Comprobar stock.
            if (!articuloService.hayStockSuficiente(detalleDTO.getIdArticulo(), detalleDTO.getCantidad())) {
                throw new StockInsuficienteException(articulo.getNombre(), articulo.getStock(),
                        detalleDTO.getCantidad());
            }

            // 3.3. Crear detalle venta
            DetalleVenta detalleVenta = new DetalleVenta(articulo, detalleDTO.getCantidad());

            // 3.4. Agregar detalle a venta
            venta.agregarDetalle(detalleVenta);

            // 3.5. Actualizar stock

            articuloService.actualizarStock(detalleDTO.getIdArticulo(), -detalleDTO.getCantidad());

        }
        // 4. Comprobar que la venta tiene detalles
        if (venta.getDetalles().isEmpty()) {
            throw new IllegalArgumentException("La venta debe tener al menos un detalle");
        }

        // 5. Guardar la venta
        ventaRepository.save(venta);

        return convertirVentaEnVentaResponseDTO(venta);
    }

    @Transactional(readOnly = true)
    public Optional<VentaResponseDTO> buscarPorId(Long id) {
        validarUsuarioAuthyRol("ADMIN");
        Optional<Venta> ventaOptional = ventaRepository.findById(id);
        if (ventaOptional.isEmpty()) {
            return Optional.empty();
        }
        Venta venta = ventaOptional.get();

        return Optional.of(convertirVentaEnVentaResponseDTO(venta));
    }

    @Transactional(readOnly = true)
    public List<VentaResponseDTO> buscarPorUsuario(Long idUsuario) {
        Usuario usuario = usuarioService.buscarPorId(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("No existe el usuario"));
        validarUsuarioAuthyRol("ADMIN");
        List<Venta> ventas = ventaRepository.findByUsuario(usuario);
        List<VentaResponseDTO> detalleVentaListDTOS = new ArrayList<>();
        for (Venta venta : ventas) {
            detalleVentaListDTOS.add(convertirVentaEnVentaResponseDTO(venta));
        }
        return detalleVentaListDTOS;
    }

    @Transactional(readOnly = true)
    public List<VentaResponseDTO> buscarPorFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        validarUsuarioAuthyRol("ADMIN");
        List<Venta> ventas = ventaRepository.findByFechaBetween(fechaInicio, fechaFin);
        List<VentaResponseDTO> detalleVentaListDTOS = new ArrayList<>();
        for (Venta venta : ventas) {
            detalleVentaListDTOS.add(convertirVentaEnVentaResponseDTO(venta));
        }
        return detalleVentaListDTOS;
    }

    @Transactional(readOnly = true)
    public List<VentaResponseDTO> buscarPorUsuarioYFechas(Long idUsuario, LocalDateTime fechaInicio,
            LocalDateTime fechaFin) {
        Usuario usuario = usuarioService.buscarPorId(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("No existe el usuario"));

        validarUsuarioAuthyRol("ADMIN");
        List<Venta> ventas = ventaRepository.findByUsuarioAndFechaBetween(usuario, fechaInicio, fechaFin);
        List<VentaResponseDTO> detalleVentaListDTOS = new ArrayList<>();
        for (Venta venta : ventas) {
            detalleVentaListDTOS.add(convertirVentaEnVentaResponseDTO(venta));
        }
        return detalleVentaListDTOS;

    }

    public void anularVenta(Long id) {
        
        Usuario usuario = usuarioService.obtenerUsuarioPorUsername(validarUsuarioAuthyRol("ADMIN"));
        
        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe la venta con ID: " + id));

         if (!usuarioService.esAdmin(usuario) && !venta.getUsuario().getId().equals(usuario.getId())) {
                throw new IllegalAccessError("No tienes permisos para anular esta venta");
            }
            
        // Devolver el stock que se había restado
        for (DetalleVenta detalle : venta.getDetalles()) {
            articuloService.actualizarStock(detalle.getArticulo().getId(), detalle.getCantidad());
        }
        ventaRepository.deleteById(id);
    }

    private String validarUsuarioAuthyRol(String rol) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalArgumentException("Usuario no autenticado");
        }
        // 1.1. Comprobar rol usuario
        if (!auth.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_" + rol))) {
            throw new IllegalArgumentException("El usuario no tiene permisos para ver este venta");
        }
        Usuario usuario = usuarioService.obtenerUsuarioPorUsername(auth.getName());
        return usuario.getUsername();
    }

    private VentaResponseDTO convertirVentaEnVentaResponseDTO(Venta venta) {
        VentaResponseDTO ventaResponseDTO = new VentaResponseDTO();

        // Convertir venta en VentaResponseDTO
        List<DetalleVentaListDTO> detalleVentaListDTOS = new ArrayList<>();
        for (DetalleVenta detalleVenta : venta.getDetalles()) {
            DetalleVentaListDTO detalleVentaListDTO = new DetalleVentaListDTO(
                    detalleVenta.getArticulo().getId(),
                    detalleVenta.getArticulo().getNombre(),
                    detalleVenta.getArticulo().getDescripcion(),
                    detalleVenta.getArticulo().getCodigoBarras(),
                    detalleVenta.getArticulo().getFamilia(),
                    detalleVenta.getCantidad(),
                    detalleVenta.getSubtotal());
            detalleVentaListDTOS.add(detalleVentaListDTO);
        }

        UsuarioVentaDTO usuarioVentaDTO = new UsuarioVentaDTO(
                venta.getUsuario().getId(),
                venta.getUsuario().getUsername());

        ventaResponseDTO.setId(venta.getId());
        ventaResponseDTO.setFecha(venta.getFecha());
        ventaResponseDTO.setTotal(venta.getTotal());
        ventaResponseDTO.setUsuario(usuarioVentaDTO);
        ventaResponseDTO.setDetalles(detalleVentaListDTOS);

        return ventaResponseDTO;
    }
}
