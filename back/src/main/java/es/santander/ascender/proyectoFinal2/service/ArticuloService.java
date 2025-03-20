package es.santander.ascender.proyectoFinal2.service;

import es.santander.ascender.proyectoFinal2.dto.ArticuloDTO;
import es.santander.ascender.proyectoFinal2.model.Articulo;
import es.santander.ascender.proyectoFinal2.repository.ArticuloRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ArticuloService {
    @Autowired
    private ArticuloRepository articuloRepository;

    @Transactional(readOnly = true)
    public List<Articulo> listarTodos() {
        return articuloRepository.findAll(); // Devolver todos los artículos
    }

    @Transactional(readOnly = true)
    public Optional<Articulo> buscarPorId(Long id) {
        return articuloRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Articulo> buscarPorCodigoBarras(String codigoBarras) {
        return articuloRepository.findByCodigoBarras(codigoBarras);
    }

    @Transactional(readOnly = true)
    public List<Articulo> buscarPorFamilia(String familia) {
        return articuloRepository.findByFamiliaAndBorradoFalse(familia);
    }

    @Transactional(readOnly = true)
    public List<Articulo> buscarPorNombre(String nombre) {
        return articuloRepository.findByNombreContainingIgnoreCaseAndBorradoFalse(nombre);
    }

   @Transactional
    public Articulo crear(ArticuloDTO articuloDTO) {
        if (articuloRepository.existsByCodigoBarras(articuloDTO.getCodigoBarras())) {
            throw new IllegalArgumentException("Ya existe un artículo con ese código de barras");
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

        return articuloRepository.save(articulo);
    }

    @Transactional
    public Articulo actualizar(Articulo articulo) {
        Optional<Articulo> articuloExistente = articuloRepository.findById(articulo.getId());

        if (articuloExistente.isEmpty()) {
            throw new IllegalArgumentException("No existe el artículo con ID: " + articulo.getId());
        }

        // No permitir cambiar el código de barras
        if (!articuloExistente.get().getCodigoBarras().equals(articulo.getCodigoBarras())) {
            throw new IllegalArgumentException("No se puede modificar el código de barras de un artículo existente");
        }

        return articuloRepository.save(articulo);
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
            throw new IllegalArgumentException("No existe el artículo con ID: " + id);
        }

        Articulo articulo = articuloOptional.get();
        int nuevoStock = articulo.getStock() + cantidad;

        if (nuevoStock < 0) {
            throw new IllegalArgumentException("No hay stock suficiente del artículo: " + articulo.getNombre());
        }

        articulo.setStock(nuevoStock);
        articuloRepository.save(articulo);
    }

    @Transactional(readOnly = true)
    public boolean hayStockSuficiente(Long id, int cantidad) {
        Optional<Articulo> articuloOptional = articuloRepository.findById(id);
        return articuloOptional.isPresent() && articuloOptional.get().getStock() >= cantidad;
    }

    @Transactional
    public void actualizarPrecioPromedioPonderado(Articulo articulo, int cantidadComprada, double precioUnitarioCompra) {
        //Calculamos el precio promedio ponderado
        double valorTotalInventario = articulo.getStock() * articulo.getPrecioPromedioPonderado();
        double valorTotalCompra = cantidadComprada * precioUnitarioCompra;
        double nuevoStock = articulo.getStock() + cantidadComprada;
        double nuevoPrecioPromedioPonderado = (valorTotalInventario + valorTotalCompra) / nuevoStock;
        articulo.setPrecioPromedioPonderado(nuevoPrecioPromedioPonderado);
        articuloRepository.save(articulo);
    }
}
