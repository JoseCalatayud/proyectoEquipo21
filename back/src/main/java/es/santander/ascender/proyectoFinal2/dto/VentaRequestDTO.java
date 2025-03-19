package es.santander.ascender.proyectoFinal2.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class VentaRequestDTO {

    @Valid
    @NotEmpty(message = "Debe tener detalles de venta")
    private List<DetalleVentaDTO> detalles;

    // Getters y Setters
    public List<DetalleVentaDTO> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleVentaDTO> detalles) {
        this.detalles = detalles;
    }
}
