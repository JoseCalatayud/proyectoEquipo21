package es.santander.ascender.proyectoFinal2.dto;

import java.time.LocalDateTime;
import java.util.List;

public class VentaResponseDTO {
    private Long id;
    private LocalDateTime fecha;
    private Double total;
    private UsuarioVentaDTO usuario;
    private List<DetalleVentaListDTO> detalles;

    // Constructor vacío
    public VentaResponseDTO() {
    }

    // Constructor con parámetros
    public VentaResponseDTO(Long id, LocalDateTime fecha, Double total, UsuarioVentaDTO usuario, List<DetalleVentaListDTO> detalles) {
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

    public UsuarioVentaDTO getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioVentaDTO usuario) {
        this.usuario = usuario;
    }

    public List<DetalleVentaListDTO> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleVentaListDTO> detalles) {
        this.detalles = detalles;
    }
}
