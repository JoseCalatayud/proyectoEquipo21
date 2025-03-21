package es.santander.ascender.proyectoFinal2.dto;

public class UsuarioRequestDTO {

    private String username;
    private String password;
    private String rol; // ADMIN o USER
    private boolean activo;

    public UsuarioRequestDTO() {
    }

    public UsuarioRequestDTO(String username, String password, String rol, boolean activo) {
        this.username = username;
        this.password = password;
        this.rol = rol;
        this.activo = activo;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}