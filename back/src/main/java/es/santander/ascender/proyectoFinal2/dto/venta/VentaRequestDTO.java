package es.santander.ascender.proyectoFinal2.dto.venta;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class VentaRequestDTO {

    @Valid
    @NotEmpty(message = "Debe tener detalles de venta")
    private List<DetalleVentaRequestDTO> detalles;

    // Getters y Setters
    public List<DetalleVentaRequestDTO> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleVentaRequestDTO> detalles) {
        this.detalles = detalles;
    }
}
