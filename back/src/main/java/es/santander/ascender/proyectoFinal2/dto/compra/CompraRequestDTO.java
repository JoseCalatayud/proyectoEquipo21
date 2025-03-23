package es.santander.ascender.proyectoFinal2.dto.compra;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class CompraRequestDTO {
    @NotEmpty(message = "La compra debe tener al menos un detalle")
    @Valid
    private List<DetalleCompraRequestDTO> detalles;

    // Constructor vacío
    public CompraRequestDTO() {
    }

    // Constructor con parámetros
    public CompraRequestDTO(List<DetalleCompraRequestDTO> detalles) {
        this.detalles = detalles;
    }

    // Getters y setters
    public List<DetalleCompraRequestDTO> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleCompraRequestDTO> detalles) {
        this.detalles = detalles;
    }
}
