package es.santander.ascender.proyectoFinal2.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity

public class Articulo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Version
    private Long version;
    

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    private String descripcion;

    @NotBlank(message = "El código de barras es obligatorio")
    @Column(nullable = false, unique = true)
    private String codigoBarras;

    @NotBlank(message = "La familia/categoría es obligatoria")
    private String familia;

    private String fotografia;

    @NotNull(message = "El precio de venta es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false)
    private Double precioVenta;

    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock debe ser mayor o igual a cero")
    private Integer stock;

    // Flag para borrado lógico
    private boolean borrado = false;

    @NotNull(message = "El precio promedio ponderado es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false)
    private Double precioPromedioPonderado;

    // Constructor vacío
    public Articulo() {
    }

    // Constructor con parámetros
    public Articulo(String nombre, String descripcion, String codigoBarras, String familia,
                    String fotografia, Double precioVenta, Integer stock) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.codigoBarras = codigoBarras;
        this.familia = familia;
        this.fotografia = fotografia;
        this.precioVenta = precioVenta;
        this.stock = stock;
        this.precioPromedioPonderado = 0.0; // Valor por defecto
    }

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(String codigoBarras) {
        this.codigoBarras = codigoBarras;
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
 

    public double getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(Double precioVenta) {
        this.precioVenta = precioVenta;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public boolean isBorrado() {
        return borrado;
    }

    public void setBorrado(boolean borrado) {
        this.borrado = borrado;
    }

    public Double getPrecioPromedioPonderado() {
        return precioPromedioPonderado;
    }

    public void setPrecioPromedioPonderado(Double precioPromedioPonderado) {
        this.precioPromedioPonderado = precioPromedioPonderado;
    }
    public Long getVersion() {
        return version;
    }
    public void setVersion(Long version) {
        this.version = version;
    }
}
