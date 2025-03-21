package es.santander.ascender.proyectoFinal2.dto;

import es.santander.ascender.proyectoFinal2.model.RolUsuario;

public class UsuarioResponseDTO {

    private String username;
    private RolUsuario rol;

    public UsuarioResponseDTO() {
    }

    public UsuarioResponseDTO(String username, RolUsuario rol) {
        this.username = username;
        this.rol = rol;
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
}
