package es.santander.ascender.proyectoFinal2.dto.venta;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class DetalleVentaRequestDTO {

    @NotNull(message = "El ID del artículo es obligatorio")
    private Long idArticulo;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad;

    // Getters y setters
    public Long getIdArticulo() {
        return idArticulo;
    }

    public void setIdArticulo(Long idArticulo) {
        this.idArticulo = idArticulo;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
}
