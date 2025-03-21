package es.santander.ascender.proyectoFinal2.model;

public class View {
     // Vista para usuarios comunes
     public interface UserView {}

     // Vista extendida para administradores (incluye la vista de usuario además)
     public interface AdminView extends UserView {}
 
}
