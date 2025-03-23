package es.santander.ascender.proyectoFinal2.dto.compra;

import java.time.LocalDateTime;
import java.util.List;

public class CompraListResponseDTO {
    private Long id;
    private LocalDateTime fecha;
    private Double total;
    private String usuario;
    private List<DetalleCompraListResponseDTO> detalles;

    // Constructor vacío
    public CompraListResponseDTO() {
    }

    // Constructor con parámetros
    public CompraListResponseDTO(Long id, LocalDateTime fecha, Double total, String usuario, List<DetalleCompraListResponseDTO> detalles) {
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

    public List<DetalleCompraListResponseDTO> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleCompraListResponseDTO> detalles) {
        this.detalles = detalles;
    }
}
