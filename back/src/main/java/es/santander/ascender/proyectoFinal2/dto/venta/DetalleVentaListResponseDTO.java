package es.santander.ascender.proyectoFinal2.dto.venta;

public class DetalleVentaListResponseDTO {
    private Long idArticulo;
    private String nombreArticulo;
    private String descripcionArticulo;
    private String codigoBarrasArticulo;
    private String familiaArticulo;
    private Integer cantidad;
    private Double subtotal;

    // Constructor vacío
    public DetalleVentaListResponseDTO() {
    }

    // Constructor con parámetros
    public DetalleVentaListResponseDTO(Long idArticulo, String nombreArticulo, String descripcionArticulo, String codigoBarrasArticulo, String familiaArticulo, Integer cantidad, Double subtotal) {
        this.idArticulo = idArticulo;
        this.nombreArticulo = nombreArticulo;
        this.descripcionArticulo = descripcionArticulo;
        this.codigoBarrasArticulo = codigoBarrasArticulo;
        this.familiaArticulo = familiaArticulo;
        this.cantidad = cantidad;
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

    public Double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }
}
