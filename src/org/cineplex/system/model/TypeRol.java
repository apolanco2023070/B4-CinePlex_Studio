/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cineplex.system.model;

/**
 * Enum que representa los tipos de rol disponibles en el sistema. Los valores
 * deben coincidir exactamente con los almacenados en la tabla 'role' de la BD.
 */
public enum TypeRol {
    ADMINISTRATOR("ADMINISTRATOR"),
    MANAGER("MANAGER");

    private final String nombre;

    TypeRol(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public static TypeRol fromString(String nombreRol) {
        if (nombreRol == null) {
            return null;
        }
        for (TypeRol tipo : TypeRol.values()) {
            if (tipo.nombre.equalsIgnoreCase(nombreRol)) {
                return tipo;
            }
        }
        return null;
    }
}
