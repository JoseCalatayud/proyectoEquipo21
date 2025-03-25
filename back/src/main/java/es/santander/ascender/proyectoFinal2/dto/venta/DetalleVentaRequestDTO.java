package es.santander.ascender.proyectoFinal2.dto.venta;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class DetalleVentaRequestDTO {

    

    @NotNull(message = "El código de barras de barras del artículo es obligatorio")
    @Min(value = 1, message = "El código de barras del artículo debe ser al menos 1")
    private String codigoBarras;
    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad;


    // Getters y setters
    public String getCodigoBarras() {
        return codigoBarras;
    }
    public void setCodigoBarras(String codigoBarras) {
        this.codigoBarras = codigoBarras;
    }

    

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
}
