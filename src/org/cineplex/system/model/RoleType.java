/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cineplex.system.model;

/**
 * Enum representing the role types available in the system. The values
 * must match exactly those stored in the 'role' table of the DB.
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
