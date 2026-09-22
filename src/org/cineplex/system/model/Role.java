package org.cineplex.system.model;

public class Role {

    private int roleId;
    private String roleName;
    private RoleType roleType; // Referencia al enum para validaciones rápidas (antes TypeRol)

    public Role() {
    }

    public Role(int roleId, String roleName) {
        this.roleId = roleId;
        this.roleName = roleName;
        this.roleType = RoleType.fromString(roleName);
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
        this.roleType = RoleType.fromString(roleName);
    }

    public RoleType getRoleType() {
        return roleType;
    }

    public boolean is(RoleType expectedType) {
        return this.roleType == expectedType;
    }

    @Override
    public String toString() {
        return "Role{roleId=" + roleId + ", roleName='" + roleName + "'}";
    }
}
