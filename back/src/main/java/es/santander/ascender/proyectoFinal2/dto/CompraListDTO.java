package es.santander.ascender.proyectoFinal2.dto;

import java.time.LocalDateTime;
import java.util.List;

public class CompraListDTO {
    private Long id;
    private LocalDateTime fecha;
    private Double total;
    private String usuario;
    private List<DetalleCompraListDTO> detalles;

    // Constructor vacío
    public CompraListDTO() {
    }

    // Constructor con parámetros
    public CompraListDTO(Long id, LocalDateTime fecha, Double total, String usuario, List<DetalleCompraListDTO> detalles) {
        this.id = id;
        this.fecha = fecha;
        this.total = total;
        this.usuario = usuario;
        this.detalles = detalles;
    }

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public String getUsuario() {
        return usuario;
    }
    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public List<DetalleCompraListDTO> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleCompraListDTO> detalles) {
        this.detalles = detalles;
    }
}
