/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cineplex.system.model;


public class Rol {
    private int idRol;
    private String nombreRol;
    private TypeRol tipoRol; // Referencia al enum para validaciones rápidas
    
    public Rol() {}
    
    public Rol(int idRol, String nombreRol) {
        this.idRol = idRol;
        this.nombreRol = nombreRol;
        this.tipoRol = TypeRol.fromString(nombreRol);
    }
    
    public int getIdRol() { return idRol; }
    public void setIdRol(int idRol) { this.idRol = idRol; }
    
    public String getNombreRol() { return nombreRol; }
    public void setNombreRol(String nombreRol) {
        this.nombreRol = nombreRol;
        this.tipoRol = TypeRol.fromString(nombreRol);
    }
    
    public TypeRol getTypeRol() { return tipoRol; }
    
 
    public boolean es(TypeRol tipoEsperado) {
        return this.tipoRol == tipoEsperado;
    }
    
    @Override
    public String toString() {
        return "Rol{idRol=" + idRol + ", nombreRol='" + nombreRol + "'}";
    }
}