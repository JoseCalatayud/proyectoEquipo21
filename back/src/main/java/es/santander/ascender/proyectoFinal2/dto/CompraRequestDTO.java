package es.santander.ascender.proyectoFinal2.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class CompraRequestDTO {
    @NotEmpty(message = "La compra debe tener al menos un detalle")
    @Valid
    private List<DetalleCompraDTO> detalles;

    // Constructor vacío
    public CompraRequestDTO() {
    }

    // Constructor con parámetros
    public CompraRequestDTO(List<DetalleCompraDTO> detalles) {
        this.detalles = detalles;
    }

    // Getters y setters
    public List<DetalleCompraDTO> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleCompraDTO> detalles) {
        this.detalles = detalles;
    }
}
