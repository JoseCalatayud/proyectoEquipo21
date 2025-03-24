package es.santander.ascender.proyectoFinal2.dto.articulo;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ArticuloUpdateRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    private String descripcion;

    @NotBlank(message = "El código de barras es obligatorio")
    private String familia;

    private String fotografia;

    @NotNull(message = "El precio de venta es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio de venta debe ser mayor que cero")
    private Double precioVenta;

    // Constructor vacío
    public ArticuloUpdateRequestDTO() {
    }

    // Constructor con parámetros
    public ArticuloUpdateRequestDTO(String nombre, String descripcion, String familia, String fotografia, Double precioVenta) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.familia = familia;
        this.fotografia = fotografia;
        this.precioVenta = precioVenta;
    }

    // Getters y setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFamilia() {
        return familia;
    }

    public void setFamilia(String familia) {
        this.familia = familia;
    }

    public String getFotografia() {
        return fotografia;
    }

    public void setFotografia(String fotografia) {
        this.fotografia = fotografia;
    }

    public Double getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(Double precioVenta) {
        this.precioVenta = precioVenta;
    }
}
