/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cineplex.system.model;

public class User {

    private int idUser;
    private String userName;
    private String password;
    private Role role;

    public User() {
    }

    public User(int idUser, String userName, String password, Role role) {
        this.idUser = idUser;
        this.userName = userName;
        this.password = password;
        this.role = role;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean hasRole(Role expectedRole) {
        return this.role != null && this.role.equals(expectedRole);
    }

    @Override
    public String toString() {
        return "Usuario{idUsuario=" + idUser
                + ", nombreUsuario='" + userName
                + "', rol=" + (role != null ? role.getRoleName(): "null") + "}";
    }
}
