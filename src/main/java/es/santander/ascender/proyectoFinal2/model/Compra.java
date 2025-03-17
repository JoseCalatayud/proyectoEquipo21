package es.santander.ascender.proyectoFinal2.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Compra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false)
    private BigDecimal total;

    @OneToMany(mappedBy = "compra", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleCompra> detalles = new ArrayList<>();

    // Constructor vacío
    public Compra() {
        this.fecha = LocalDateTime.now();
        this.total = BigDecimal.ZERO;
    }

    // Constructor con parámetros
    public Compra(Usuario usuario) {
        this.fecha = LocalDateTime.now();
        this.usuario = usuario;
        this.total = BigDecimal.ZERO;
    }

    // Método para agregar un detalle
    public void agregarDetalle(DetalleCompra detalle) {
        detalles.add(detalle);
        detalle.setCompra(this);
        // Actualizar el total
        this.total = this.total.add(detalle.getSubtotal());
    }

    // Método para eliminar un detalle
    public void eliminarDetalle(DetalleCompra detalle) {
        detalles.remove(detalle);
        detalle.setCompra(null);
        // Actualizar el total
        this.total = this.total.subtract(detalle.getSubtotal());
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

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public List<DetalleCompra> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleCompra> detalles) {
        this.detalles = detalles;
    }
}