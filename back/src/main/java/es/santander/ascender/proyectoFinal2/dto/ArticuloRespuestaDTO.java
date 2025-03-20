package es.santander.ascender.proyectoFinal2.dto;

public class ArticuloRespuestaDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private String codigoBarras;
    private String familia;
    private String fotografia;
    private Double precioVenta;

    // Constructor vacío
    public ArticuloRespuestaDTO() {
    }

    // Constructor con parámetros
    public ArticuloRespuestaDTO(Long id, String nombre, String descripcion, String codigoBarras, String familia, String fotografia, Double precioVenta) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.codigoBarras = codigoBarras;
        this.familia = familia;
        this.fotografia = fotografia;
        this.precioVenta = precioVenta;
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

    public Double getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(Double precioVenta) {
        this.precioVenta = precioVenta;
    }
}
