package es.santander.ascender.proyectoFinal2.service;

import es.santander.ascender.proyectoFinal2.dto.ArticuloUpdateDTO;
import es.santander.ascender.proyectoFinal2.exception.MyBadDataException;
import es.santander.ascender.proyectoFinal2.exception.StockInsuficienteException;
import es.santander.ascender.proyectoFinal2.dto.ArticuloRequestDTO;
import es.santander.ascender.proyectoFinal2.dto.ArticuloResponseDTO;
import es.santander.ascender.proyectoFinal2.model.Articulo;
import es.santander.ascender.proyectoFinal2.repository.ArticuloRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ArticuloService {
    @Autowired
    private ArticuloRepository articuloRepository;

    @Transactional(readOnly = true)
    public List<ArticuloResponseDTO> listarTodos() {
        List<Articulo> listaArticulos = articuloRepository.findAll();
        if (listaArticulos.isEmpty()) {
            throw new NoSuchElementException("No existen artículos en la base de datos");
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
        Optional<Articulo> articulo = articuloRepository.findById(id);
        if (articulo.isEmpty()) {
            throw new NoSuchElementException("No existe el artículo con ID: " + id);
        }
        return convertArticuloToArticuloRespuestaDTO(articulo.get());

    }

    @Transactional(readOnly = true)
    public ArticuloResponseDTO buscarPorCodigoBarras(String codigoBarras) {
        Optional<Articulo> articulo = articuloRepository.findByCodigoBarras(codigoBarras);
        if (articulo.isEmpty()) {
            throw new NoSuchElementException("No existe el artículo con código de barras: " + codigoBarras);
        }
        return convertArticuloToArticuloRespuestaDTO(articulo.get());
    }

    @Transactional(readOnly = true)
    public List<ArticuloResponseDTO> buscarPorFamilia(String familia) {
        List<Articulo> listaArticulos = articuloRepository.findByFamilia(familia);
        if(listaArticulos.isEmpty()) {
            throw new NoSuchElementException("No existen artículos en la familia: " + familia);
        }
        List<ArticuloResponseDTO> listaArticulosDTO = listaArticulos.stream()
                .map(this::convertArticuloToArticuloRespuestaDTO)
                .collect(Collectors.toList());
        return listaArticulosDTO;
        
    }

    @Transactional(readOnly = true)
    public List<ArticuloResponseDTO> buscarPorNombre(String nombre) {
        List<Articulo> listaArticulos = articuloRepository.findByNombreContainingIgnoreCaseAndBorradoFalse(nombre);
        if(listaArticulos.isEmpty()) {
            throw new NoSuchElementException("No existen artículos con el nombre: " + nombre);
        }
        List<ArticuloResponseDTO> listaArticulosDTO = listaArticulos.stream()
                .map(this::convertArticuloToArticuloRespuestaDTO)
                .collect(Collectors.toList());
        return listaArticulosDTO;
    }

    @Transactional
    public ArticuloResponseDTO crear(ArticuloRequestDTO articuloDTO) {
        if (articuloRepository.existsByCodigoBarras(articuloDTO.getCodigoBarras())) {
            throw new MyBadDataException("Ya existe un artículo con el código de barras: " + articuloDTO.getCodigoBarras(), 1);
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

    @Transactional
    public ArticuloResponseDTO actualizar(Long id, ArticuloUpdateDTO articuloActualizacionDTO) {
        Optional<Articulo> articuloExistenteOptional = articuloRepository.findById(id);

        if (articuloExistenteOptional.isEmpty()) {
            throw new MyBadDataException("No existe el artículo con ID: " + id, id);
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

    @Transactional
    public void borradoLogico(Long id) {
        Optional<Articulo> articuloOptional = articuloRepository.findById(id);

        if (articuloOptional.isEmpty()) {
            throw new IllegalArgumentException("No existe el artículo con ID: " + id);
        }
        Articulo articulo = articuloOptional.get();
        articulo.setBorrado(true);
        articuloRepository.save(articulo);
    }

    @Transactional(readOnly = true)
    public boolean existeArticulo(String codigoBarras) {
        return articuloRepository.existsByCodigoBarras(codigoBarras);
    }

    @Transactional
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

    @Transactional
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
                articulo.getPrecioPromedioPonderado());
    }
}
