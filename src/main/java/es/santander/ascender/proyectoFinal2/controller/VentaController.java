package es.santander.ascender.proyectoFinal2.controller;

import es.santander.ascender.proyectoFinal2.model.Venta;
import es.santander.ascender.proyectoFinal2.service.VentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    @Autowired
    private VentaService ventaService;

    @GetMapping("/listar")
    public ResponseEntity<List<Venta>> listarVentas() {
        List<Venta> ventas = ventaService.listarVentas();
        return ResponseEntity.ok(ventas);
    }

    @PostMapping("/crear")
    public ResponseEntity<Venta> crearVenta(@RequestParam Long idUsuario,
                                            @RequestParam Long idArticulo,
                                            @RequestParam Integer cantidad) {
        try {
            Venta nuevaVenta = ventaService.crearVenta(idUsuario, idArticulo, cantidad);
            return ResponseEntity.ok(nuevaVenta);
        } catch (Exception e) {
            // Devolver el error.
            return ResponseEntity.badRequest().build();
        }
    }
}
