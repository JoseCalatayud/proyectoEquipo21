package es.santander.ascender.proyectoFinal2.service;

import es.santander.ascender.proyectoFinal2.exception.StockInsuficienteException;
import es.santander.ascender.proyectoFinal2.model.Articulo;
import es.santander.ascender.proyectoFinal2.model.DetalleVenta;
import es.santander.ascender.proyectoFinal2.model.Usuario;
import es.santander.ascender.proyectoFinal2.model.Venta;
import es.santander.ascender.proyectoFinal2.repository.VentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
        return ventaRepository.findAll();//Remove the other code.
    }

    public Venta crearVenta(Long idUsuario, Long idArticulo, Integer cantidad) {
         //1. Buscar usuario.
        Optional<Usuario> usuarioOptional = usuarioService.buscarPorId(idUsuario);
        if (usuarioOptional.isEmpty()){
            throw new IllegalArgumentException("No existe el usuario");
        }
        Usuario usuario = usuarioOptional.get();
        //2. Buscar articulo.
        Optional<Articulo> articuloOptional = articuloService.buscarPorId(idArticulo);
        if (articuloOptional.isEmpty()) {
            throw new IllegalArgumentException("No existe el articulo");
        }
        Articulo articulo = articuloOptional.get();

        //3. Comprobar stock.
        if (!articuloService.hayStockSuficiente(idArticulo, cantidad)) {
            throw new StockInsuficienteException(articulo.getNombre(), articulo.getStock(), cantidad);
        }

        // 4. Crear venta
        Venta venta = new Venta(usuario);
        
        // 5. Crear detalle venta
        DetalleVenta detalleVenta = new DetalleVenta(articulo, cantidad);
        
        // 6. Agregar detalle a venta
        venta.agregarDetalle(detalleVenta);

        // 7. Guardar la venta
        ventaRepository.save(venta);
        
        //8. Actualizar stock
        articuloService.actualizarStock(idArticulo, -cantidad);
        return venta;
    }
}
