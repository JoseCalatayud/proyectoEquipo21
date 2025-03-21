package es.santander.ascender.proyectoFinal2.dto;

import es.santander.ascender.proyectoFinal2.model.RolUsuario;

public class UsuarioResponseDTO {

    private String username;
    private RolUsuario rol;
    private boolean activo; // Nuevo campo para el estado del usuario

    public UsuarioResponseDTO() {
    }

    public UsuarioResponseDTO(String username, RolUsuario rol, boolean activo) {
        this.username = username;
        this.rol = rol;
        this.activo = activo; 
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public RolUsuario getRol() {
        return rol;
    }
    public void setRol(RolUsuario rol) {
        this.rol = rol;
    }
    public boolean isActivo() {
        return activo;
    }
    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
