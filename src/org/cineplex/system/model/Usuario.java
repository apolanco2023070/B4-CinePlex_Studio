/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cineplex.system.model;

/**
 * Clase que representa un Usuario en el sistema.
 */
public class Usuario {
    private int idUsuario;
    private String nombreUsuario;
    private String password;
    private Rol rol;
    
    public Usuario() {}
    
    public Usuario(int idUsuario, String nombreUsuario, String password, Rol rol) {
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.password = password;
        this.rol = rol;
    }
    
    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
    
    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }
    

    public boolean tieneRol(TypeRol tipoRol) {
        return this.rol != null && this.rol.es(tipoRol);
    }
    
    @Override
    public String toString() {
        return "Usuario{idUsuario=" + idUsuario + 
               ", nombreUsuario='" + nombreUsuario + 
               "', rol=" + (rol != null ? rol.getNombreRol() : "null") + "}";
    }
}
