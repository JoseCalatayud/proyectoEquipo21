package es.santander.ascender.proyectoFinal2.service;

import es.santander.ascender.proyectoFinal2.dto.articulo.ArticuloRequestDTO;
import es.santander.ascender.proyectoFinal2.dto.articulo.ArticuloResponseDTO;
import es.santander.ascender.proyectoFinal2.dto.articulo.ArticuloUpdateRequestDTO;
import es.santander.ascender.proyectoFinal2.exception.StockInsuficienteException;
import es.santander.ascender.proyectoFinal2.model.Articulo;
import es.santander.ascender.proyectoFinal2.repository.ArticuloRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ArticuloService {
    @Autowired
    private ArticuloRepository articuloRepository;

    @Transactional(readOnly = true)
    public List<ArticuloResponseDTO> listarTodos() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        List<Articulo> listaArticulos = new ArrayList<>();
        if (usuarioLogueadoisAdmin(auth)) {
            listaArticulos = articuloRepository.findAll();
        } else {
            listaArticulos = articuloRepository.findByBorradoFalse();
        }

        // COnvertir Articulo a ArticuloResponseDTO
        List<ArticuloResponseDTO> listaArticulosDTO = listaArticulos.stream()
                .map(this::convertArticuloToArticuloRespuestaDTO)
                .collect(Collectors.toList());
        return listaArticulosDTO;
        // Devolver todos los artículos
    }

    @Transactional(readOnly = true)
    public ArticuloResponseDTO buscarPorId(Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (usuarioLogueadoisAdmin(auth)) {
            Optional<Articulo> articulo = articuloRepository.findById(id);
            if (articulo.isEmpty()) {
                throw new NoSuchElementException("No existe el artículo con ID: " + id);
            }
            return convertArticuloToArticuloRespuestaDTO(articulo.get());
        } else {
            Optional<Articulo> articulo = articuloRepository.findByIdAndBorradoFalse(id);
            return convertArticuloToArticuloRespuestaDTO(articulo.get());
        }
    }

    @Transactional(readOnly = true)
    public ArticuloResponseDTO buscarPorCodigoBarras(String codigoBarras) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (usuarioLogueadoisAdmin(auth)) {
            Optional<Articulo> articulo = articuloRepository.findByCodigoBarras(codigoBarras);
            if (articulo.isEmpty()) {
                throw new NoSuchElementException("No existe el artículo con códifo de barras: " + codigoBarras);
            }
            return convertArticuloToArticuloRespuestaDTO(articulo.get());
        } else {
            Optional<Articulo> articulo = articuloRepository.findByCodigoBarrasAndBorradoFalse(codigoBarras);
            return convertArticuloToArticuloRespuestaDTO(articulo.get());
        }
    }

    @Transactional(readOnly = true)
    public List<ArticuloResponseDTO> buscarPorFamilia(String familia) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        List<Articulo> listaArticulos = new ArrayList<>();
        if (usuarioLogueadoisAdmin(auth)) {
            listaArticulos = articuloRepository.findByFamilia(familia);
        } else {
            listaArticulos = articuloRepository.findByFamiliaAndBorradoFalse(familia);
        }
        List<ArticuloResponseDTO> listaArticulosDTO = listaArticulos.stream()
                .map(this::convertArticuloToArticuloRespuestaDTO)
                .collect(Collectors.toList());
        return listaArticulosDTO;

    }

    @Transactional(readOnly = true)
    public List<ArticuloResponseDTO> buscarPorNombre(String nombre) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        List<Articulo> listaArticulos = new ArrayList<>();
        if (usuarioLogueadoisAdmin(auth)) {
            listaArticulos = articuloRepository.findByNombreContainingIgnoreCase(nombre);
        } else {
            listaArticulos = articuloRepository.findByNombreContainingIgnoreCaseAndBorradoFalse(nombre);
            
        }
        List<ArticuloResponseDTO> listaArticulosDTO = listaArticulos.stream()
                .map(this::convertArticuloToArticuloRespuestaDTO)
                .collect(Collectors.toList());
        return listaArticulosDTO;
    }

    public ArticuloResponseDTO crear(ArticuloRequestDTO articuloDTO) {
        validarCampos(articuloDTO);
        if (articuloRepository.existsByCodigoBarras(articuloDTO.getCodigoBarras())) {
            throw new IllegalArgumentException(
                    "Ya existe un artículo con el código de barras: " + articuloDTO.getCodigoBarras());
        }


        Articulo articulo = new Articulo(
                articuloDTO.getNombre(),
                articuloDTO.getDescripcion(),
                articuloDTO.getCodigoBarras(),
                articuloDTO.getFamilia(),
                articuloDTO.getFotografia(),
                articuloDTO.getPrecioVenta(),
                0 // Stock inicial en 0
        );
        articulo.setPrecioPromedioPonderado(articuloDTO.getPrecioVenta());

        Articulo articuloGuardado = articuloRepository.save(articulo);
        return convertArticuloToArticuloRespuestaDTO(articuloGuardado);
    }

    

    public ArticuloResponseDTO actualizar(Long id, ArticuloUpdateRequestDTO articuloActualizacionDTO) {
        Optional<Articulo> articuloExistenteOptional = articuloRepository.findById(id);

        if (articuloExistenteOptional.isEmpty()) {
            throw new NoSuchElementException("No existe el artículo con ID: " + id);
        }

        Articulo articuloExistente = articuloExistenteOptional.get();

        // Actualizar los campos permitidos
        articuloExistente.setNombre(articuloActualizacionDTO.getNombre());
        articuloExistente.setDescripcion(articuloActualizacionDTO.getDescripcion());
        articuloExistente.setFamilia(articuloActualizacionDTO.getFamilia());
        articuloExistente.setFotografia(articuloActualizacionDTO.getFotografia());
        articuloExistente.setPrecioVenta(articuloActualizacionDTO.getPrecioVenta());

        Articulo articuloGuardado = articuloRepository.save(articuloExistente);
        return convertArticuloToArticuloRespuestaDTO(articuloGuardado);
    }

    public void borradoLogico(Long id) {
        Optional<Articulo> articuloOptional = articuloRepository.findById(id);

        if (articuloOptional.isEmpty()) {
            throw new NoSuchElementException("No existe el artículo con ID: " + id);
        }
        Articulo articulo = articuloOptional.get();
        articulo.setBorrado(true);
        articuloRepository.save(articulo);
    }
    public void activarArticulo(Long id) {
        Optional<Articulo> articuloOptional = articuloRepository.findById(id);

        if (articuloOptional.isEmpty()) {
            throw new NoSuchElementException("No existe el artículo con ID: " + id);
        }
        Articulo articulo = articuloOptional.get();
        articulo.setBorrado(false);
        articuloRepository.save(articulo);
    }

    @Transactional(readOnly = true)
    public boolean existeArticulo(String codigoBarras) {
        return articuloRepository.existsByCodigoBarras(codigoBarras);
    }

    public void actualizarStock(Long id, int cantidad) {
        Optional<Articulo> articuloOptional = articuloRepository.findById(id);
        if (articuloOptional.isEmpty()) {
            throw new NoSuchElementException("No existe el artículo con ID: " + id);
        }
        Articulo articulo = articuloOptional.get();
        int nuevoStock = articulo.getStock() + cantidad;
        if (nuevoStock < 0) {
            throw new StockInsuficienteException(articulo.getNombre(), articulo.getStock(), cantidad);
        }
        articulo.setStock(nuevoStock);
        articuloRepository.save(articulo);
    }

    @Transactional(readOnly = true)
    public boolean hayStockSuficiente(Long id, int cantidad) {
        Optional<Articulo> articuloOptional = articuloRepository.findById(id);
        if (articuloOptional.isEmpty()) {
            throw new NoSuchElementException("No existe el artículo con ID: " + id);
        }
        return articuloOptional.get().getStock() >= cantidad;
    }

    public void actualizarPrecioPromedioPonderado(Articulo articulo, int cantidadComprada,
            double precioUnitarioCompra) {
        // Calculamos el precio promedio ponderado
        double valorTotalInventario = articulo.getStock() * articulo.getPrecioPromedioPonderado();
        double valorTotalCompra = cantidadComprada * precioUnitarioCompra;
        double nuevoStock = articulo.getStock() + cantidadComprada;
        double nuevoPrecioPromedioPonderado = (valorTotalInventario + valorTotalCompra) / nuevoStock;
        articulo.setPrecioPromedioPonderado(nuevoPrecioPromedioPonderado);
        articuloRepository.save(articulo);
    }

    private ArticuloResponseDTO convertArticuloToArticuloRespuestaDTO(Articulo articulo) {
        return new ArticuloResponseDTO(
                articulo.getId(),
                articulo.getNombre(),
                articulo.getDescripcion(),
                articulo.getCodigoBarras(),
                articulo.getFamilia(),
                articulo.getFotografia(),
                articulo.getPrecioVenta(),
                articulo.getStock(),
                articulo.getPrecioPromedioPonderado(),
                articulo.isBorrado());
    }

    private boolean usuarioLogueadoisAdmin(Authentication auth) {
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
        return isAdmin;
    }
    private void validarCampos(ArticuloRequestDTO articuloDTO) {
        if (articuloDTO.getNombre() == null || articuloDTO.getNombre().isEmpty()) {
            throw new IllegalArgumentException("El nombre del artículo no puede ser nulo o vacío");
        }
        if (articuloDTO.getCodigoBarras() == null || articuloDTO.getCodigoBarras().isEmpty()) {
            throw new IllegalArgumentException("El código de barras del artículo no puede ser nulo o vacío");
        }
        if (articuloDTO.getPrecioVenta() == null || articuloDTO.getPrecioVenta() <= 0) {
            throw new IllegalArgumentException("El precio de venta del artículo no puede ser nulo o menor o igual a 0");
        }
        if (articuloDTO.getFamilia() == null || articuloDTO.getFamilia().isEmpty()) {
            throw new IllegalArgumentException("La familia del artículo no puede ser nula o vacía");
        }
        if (articuloDTO.getDescripcion() == null || articuloDTO.getDescripcion().isEmpty()) {
            throw new IllegalArgumentException("La descripción del artículo no puede ser nula o vacía");
        }
        if (articuloDTO.getFotografia() == null || articuloDTO.getFotografia().isEmpty()) {
            throw new IllegalArgumentException("La fotografía del artículo no puede ser nula o vacía");
        }
    }
}
