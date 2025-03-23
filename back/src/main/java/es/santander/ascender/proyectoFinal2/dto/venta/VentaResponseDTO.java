package es.santander.ascender.proyectoFinal2.dto.venta;

import java.time.LocalDateTime;
import java.util.List;

public class VentaResponseDTO {
    private Long id;
    private LocalDateTime fecha;
    private Double total;
    private VentaUsuarioDTO usuario;
    private List<DetalleVentaListResponseDTO> detalles;

    // Constructor vacío
    public VentaResponseDTO() {
    }

    // Constructor con parámetros
    public VentaResponseDTO(Long id, LocalDateTime fecha, Double total, VentaUsuarioDTO usuario, List<DetalleVentaListResponseDTO> detalles) {
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

    public VentaUsuarioDTO getUsuario() {
        return usuario;
    }

    public void setUsuario(VentaUsuarioDTO usuario) {
        this.usuario = usuario;
    }

    public List<DetalleVentaListResponseDTO> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleVentaListResponseDTO> detalles) {
        this.detalles = detalles;
    }
}
