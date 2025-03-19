package es.santander.ascender.proyectoFinal2.dto;

public class DetalleCompraListDTO {
    private Long idArticulo;
    private String nombreArticulo;
    private String descripcionArticulo;
    private String codigoBarrasArticulo;
    private String familiaArticulo;
    private Integer cantidad;
    private Double precioUnitario;
    private Double subtotal;

    // Constructor vacío
    public DetalleCompraListDTO() {
    }

    // Constructor con parámetros
    public DetalleCompraListDTO(Long idArticulo, String nombreArticulo, String descripcionArticulo, String codigoBarrasArticulo, String familiaArticulo, Integer cantidad, Double precioUnitario, Double subtotal) {
        this.idArticulo = idArticulo;
        this.nombreArticulo = nombreArticulo;
        this.descripcionArticulo = descripcionArticulo;
        this.codigoBarrasArticulo = codigoBarrasArticulo;
        this.familiaArticulo = familiaArticulo;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = subtotal;
    }

    // Getters y setters
    public Long getIdArticulo() {
        return idArticulo;
    }

    public void setIdArticulo(Long idArticulo) {
        this.idArticulo = idArticulo;
    }

    public String getNombreArticulo() {
        return nombreArticulo;
    }

    public void setNombreArticulo(String nombreArticulo) {
        this.nombreArticulo = nombreArticulo;
    }

    public String getDescripcionArticulo() {
        return descripcionArticulo;
    }

    public void setDescripcionArticulo(String descripcionArticulo) {
        this.descripcionArticulo = descripcionArticulo;
    }

    public String getCodigoBarrasArticulo() {
        return codigoBarrasArticulo;
    }

    public void setCodigoBarrasArticulo(String codigoBarrasArticulo) {
        this.codigoBarrasArticulo = codigoBarrasArticulo;
    }

    public String getFamiliaArticulo() {
        return familiaArticulo;
    }

    public void setFamiliaArticulo(String familiaArticulo) {
        this.familiaArticulo = familiaArticulo;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(Double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }
}
