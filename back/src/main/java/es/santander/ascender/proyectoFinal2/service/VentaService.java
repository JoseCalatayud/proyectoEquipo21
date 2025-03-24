package es.santander.ascender.proyectoFinal2.service;

import es.santander.ascender.proyectoFinal2.dto.venta.DetalleVentaRequestDTO;
import es.santander.ascender.proyectoFinal2.dto.venta.VentaUsuarioDTO;
import es.santander.ascender.proyectoFinal2.dto.venta.DetalleVentaListResponseDTO;
import es.santander.ascender.proyectoFinal2.dto.venta.VentaRequestDTO;
import es.santander.ascender.proyectoFinal2.dto.venta.VentaResponseDTO;
import es.santander.ascender.proyectoFinal2.exception.StockInsuficienteException;
import es.santander.ascender.proyectoFinal2.model.Articulo;
import es.santander.ascender.proyectoFinal2.model.DetalleVenta;
import es.santander.ascender.proyectoFinal2.model.Usuario;
import es.santander.ascender.proyectoFinal2.model.Venta;
import es.santander.ascender.proyectoFinal2.repository.ArticuloRepository;
import es.santander.ascender.proyectoFinal2.repository.UsuarioRepository;
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
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class VentaService {

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private ArticuloRepository articuloRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ArticuloService articuloService;

  

    @Transactional(readOnly = true)
    public List<VentaResponseDTO> listarVentas() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        List<Venta> ventas = new ArrayList<>(); 
        if (usuarioLogueadoisAdmin(auth)) {
            ventas = ventaRepository.findAll();
            
        } else {
            ventas = ventaRepository.findByUsuario(usuarioRepository.findByUsername(auth.getName())
                    .orElseThrow(() -> new NoSuchElementException("No existe el usuario")));
        }
        List<VentaResponseDTO> ventasResponseDTO = ventas.stream()
                .map(v -> convertirVentaEnVentaResponseDTO(v))
                .collect(Collectors.toList());

        return ventasResponseDTO;
    }    

    public VentaResponseDTO crearVenta(VentaRequestDTO ventaRequestDTO) {
        // 1. Obtener el usuario autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new NoSuchElementException("Usuario no autenticado");
        }
        Optional<Usuario> usuarioOptional = usuarioRepository.findByUsername(auth.getName());
        if (usuarioOptional.isEmpty()) {
            throw new NoSuchElementException("Usuario no encontrado");
        }
        Set<Long> articulosIds = new HashSet<>();
        for (DetalleVentaRequestDTO detalleDTO : ventaRequestDTO.getDetalles()) {
            if (!articulosIds.add(detalleDTO.getIdArticulo())) {
                throw new IllegalArgumentException("No se permite duplicar artículos en la misma venta");
            }
        }
        // 2. Crear venta
        Venta venta = new Venta(usuarioOptional.get());
        // 3. Procesar cada detalle de venta
        for (DetalleVentaRequestDTO detalleDTO : ventaRequestDTO.getDetalles()) {
            // 3.1. Buscar articulo.
            Articulo articulo = articuloRepository.findById(detalleDTO.getIdArticulo())
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
        Optional<Venta> ventaOptional = ventaRepository.findById(id);
        if (ventaOptional.isEmpty()) {
            throw new NoSuchElementException("No existe la venta con ID: " + id);
        }
        Venta venta = ventaOptional.get();
        return Optional.of(convertirVentaEnVentaResponseDTO(venta));
    }

    @Transactional(readOnly = true)
    public List<VentaResponseDTO> buscarPorUsuario(Long idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new NoSuchElementException("No existe el usuario"));
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
        if (fechaFin.isBefore(fechaInicio)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin");
            
        }
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
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("No existe el usuario"));
        if(fechaFin.isBefore(fechaInicio)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }
        
        List<Venta> ventas = ventaRepository.findByUsuarioAndFechaBetween(usuario, fechaInicio, fechaFin);
        List<VentaResponseDTO> detalleVentaListDTOS = new ArrayList<>();
        for (Venta venta : ventas) {
            detalleVentaListDTOS.add(convertirVentaEnVentaResponseDTO(venta));
        }
        return detalleVentaListDTOS;

    }
    public void anularVenta(Long id) {    
        // 1. Obtener el usuario autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(!usuarioLogueadoisAdmin(auth)) {
            throw new IllegalAccessError("El usuario no tiene permisos para anular la venta");
        }
        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe la venta con ID: " + id));
        // Devolver el stock que se había restado
        for (DetalleVenta detalle : venta.getDetalles()) {
            articuloService.actualizarStock(detalle.getArticulo().getId(), detalle.getCantidad());
        }
        ventaRepository.deleteById(id);
    }

    private String validarUsuarioAuthyRol(String rol) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalAccessError("Usuario no autenticado");
        }
        // 1.1. Comprobar rol usuario
        if (!auth.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_" + rol))) {
            throw new IllegalArgumentException("El usuario no tiene permisos para ver este venta");
        }
        Usuario usuario = usuarioRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new NoSuchElementException("No existe el usuario"));
        if (!usuario.isActivo()) {
            throw new IllegalAccessError("El usuario no está activo");
        }
        return usuario.getUsername();
    }
    
    private VentaResponseDTO convertirVentaEnVentaResponseDTO(Venta venta) {
        VentaResponseDTO ventaResponseDTO = new VentaResponseDTO();

        // Convertir venta en VentaResponseDTO
        List<DetalleVentaListResponseDTO> detalleVentaListDTOS = new ArrayList<>();
        for (DetalleVenta detalleVenta : venta.getDetalles()) {
            DetalleVentaListResponseDTO detalleVentaListDTO = new DetalleVentaListResponseDTO(
                    detalleVenta.getArticulo().getId(),
                    detalleVenta.getArticulo().getNombre(),
                    detalleVenta.getArticulo().getDescripcion(),
                    detalleVenta.getArticulo().getCodigoBarras(),
                    detalleVenta.getArticulo().getFamilia(),
                    detalleVenta.getCantidad(),
                    detalleVenta.getSubtotal());
            detalleVentaListDTOS.add(detalleVentaListDTO);
        }

        VentaUsuarioDTO usuarioVentaDTO = new VentaUsuarioDTO(
                venta.getUsuario().getId(),
                venta.getUsuario().getUsername());

        ventaResponseDTO.setId(venta.getId());
        ventaResponseDTO.setFecha(venta.getFecha());
        ventaResponseDTO.setTotal(venta.getTotal());
        ventaResponseDTO.setUsuario(usuarioVentaDTO);
        ventaResponseDTO.setDetalles(detalleVentaListDTOS);

        return ventaResponseDTO;
    }
    private boolean usuarioLogueadoisAdmin(Authentication auth) {
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
        return isAdmin;
    }
    private Usuario obtenerUsuarioAutenticado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new NoSuchElementException("Usuario no autenticado");
        }
        return usuarioRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));
    }
}
