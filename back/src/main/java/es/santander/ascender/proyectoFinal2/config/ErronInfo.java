package es.santander.ascender.proyectoFinal2.config;

import java.time.LocalDateTime;

public class ErronInfo {
    private String mensaje;
    private LocalDateTime timestamp;
    private Object detalles;

    public ErronInfo(String mensaje) {
        this.mensaje = mensaje;
        this.timestamp = LocalDateTime.now();
        this.detalles = null;
    }

    public ErronInfo(String mensaje, Object detalles) {
        this.mensaje = mensaje;
        this.timestamp = LocalDateTime.now();
        this.detalles = detalles;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Object getDetalles() {
        return detalles;
    }

    public void setDetalles(Object detalles) {
        this.detalles = detalles;
    }
}