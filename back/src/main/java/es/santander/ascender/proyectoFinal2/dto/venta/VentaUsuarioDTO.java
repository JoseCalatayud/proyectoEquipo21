package es.santander.ascender.proyectoFinal2.dto.venta;

public class VentaUsuarioDTO {
    private Long id;
    private String username;

    // Constructor vacío
    public VentaUsuarioDTO() {
    }

    // Constructor con parámetros
    public VentaUsuarioDTO(Long id, String username) {
        this.id = id;
        this.username = username;
    }

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
