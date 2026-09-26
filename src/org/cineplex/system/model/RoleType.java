/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cineplex.system.model;

/**
 * Enum que representa los tipos de rol disponibles en el sistema. Los valores
 * deben coincidir exactamente con los almacenados en la tabla 'role' de la BD.
 */

public enum RoleType {
    ADMINISTRATOR("ADMINISTRATOR"),
    MANAGER("MANAGER");

    private final String name;

    RoleType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static RoleType fromString(String roleName) {
        if (roleName == null) {
            return null;
        }
        for (RoleType type : RoleType.values()) {
            if (type.name.equalsIgnoreCase(roleName)) {
                return type;
            }
        }
        return null;
    }
}
