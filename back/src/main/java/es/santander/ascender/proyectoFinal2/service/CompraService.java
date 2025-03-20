package es.santander.ascender.proyectoFinal2.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.santander.ascender.proyectoFinal2.dto.CompraListDTO;
import es.santander.ascender.proyectoFinal2.dto.CompraRequestDTO;
import es.santander.ascender.proyectoFinal2.dto.DetalleCompraDTO;
import es.santander.ascender.proyectoFinal2.dto.DetalleCompraListDTO;
import es.santander.ascender.proyectoFinal2.model.Articulo;
import es.santander.ascender.proyectoFinal2.model.Compra;
import es.santander.ascender.proyectoFinal2.model.DetalleCompra;
import es.santander.ascender.proyectoFinal2.model.Usuario;
import es.santander.ascender.proyectoFinal2.repository.CompraRepository;

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
    public List<CompraListDTO> listarTodas() {
        List<Compra> compras = compraRepository.findAll();
        List<CompraListDTO> compraListDTOS = new ArrayList<>();

        for (Compra compra : compras) {
            List<DetalleCompraListDTO> detalleCompraListDTOS = new ArrayList<>();
            for (DetalleCompra detalleCompra : compra.getDetalles()) {
                Articulo articulo = detalleCompra.getArticulo();
                DetalleCompraListDTO detalleCompraListDTO = new DetalleCompraListDTO(
                        articulo.getId(),
                        articulo.getNombre(),
                        articulo.getDescripcion(),
                        articulo.getCodigoBarras(),
                        articulo.getFamilia(),
                        detalleCompra.getCantidad(),
                        detalleCompra.getPrecioUnitario(),
                        detalleCompra.getSubtotal());
                detalleCompraListDTOS.add(detalleCompraListDTO);
            }
            CompraListDTO compraListDTO = new CompraListDTO(
                    compra.getId(),
                    compra.getFecha(),
                    compra.getTotal(),
                    detalleCompraListDTOS);
            compraListDTOS.add(compraListDTO);
        }
        return compraListDTOS;
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

    @Transactional
    public Compra crearCompra(CompraRequestDTO compraRequestDTO) {
        // 1. Obtener el usuario autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalArgumentException("Usuario no autenticado");
        }
        Usuario usuario = usuarioService.obtenerUsuarioPorUsername(auth.getName());

        // 2. Validar que no hay artículos duplicados
        Set<Long> articulosIds = new HashSet<>();
        for (DetalleCompraDTO detalleDTO : compraRequestDTO.getDetalles()) {
            if (!articulosIds.add(detalleDTO.getIdArticulo())) {
                throw new IllegalArgumentException("No se permite duplicar artículos en la misma compra");
            }
        }
        // 3. Crear compra
        Compra compra = new Compra(usuario);

        // 4. Procesar cada detalle de compra
        for (DetalleCompraDTO detalleDTO : compraRequestDTO.getDetalles()) {
            // 3.1. Buscar articulo.
            Articulo articulo = articuloService.buscarPorId(detalleDTO.getIdArticulo())
                    .orElseThrow(() -> new IllegalArgumentException("No existe el articulo"));

            // 4.2. Verificar que el artículo no está borrado
            if (articulo.isBorrado()) {
                throw new IllegalStateException(
                        "No se puede comprar el artículo porque está descatalogado: " + articulo.getNombre());
            }

            // 3.4. Crear detalle compra
            // Usar el precio de compra del DTO
            DetalleCompra detalleCompra = new DetalleCompra(articulo, detalleDTO.getCantidad(),
                    detalleDTO.getPrecioUnitario());

            // 3.5. Agregar detalle a compra
            compra.agregarDetalle(detalleCompra);

            // 3.6. Actualizar stock
            
            articuloService.actualizarStock(detalleDTO.getIdArticulo(), detalleDTO.getCantidad());
            // Calculamos el nuevo precio promedio ponderado
            articuloService.actualizarPrecioPromedioPonderado(articulo, detalleDTO.getCantidad(),
                    detalleDTO.getPrecioUnitario());

        }

        // 4. Guardar la compra
        return compraRepository.save(compra);
    }

    @Transactional
    public void anularCompra(Long id) {
        Compra compra = compraRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe la compra con ID: " + id));

        // Restar el stock que se había añadido
        for (DetalleCompra detalle : compra.getDetalles()) {
            // Usamos synchronized para evitar condiciones de carrera
            synchronized (detalle.getArticulo()) {
                articuloService.actualizarStock(detalle.getArticulo().getId(), -detalle.getCantidad());
            }
        }

        // Eliminar la compra
        compraRepository.deleteById(id);
    }
}
