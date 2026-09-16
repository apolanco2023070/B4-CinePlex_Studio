/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cineplex.system.dao;

import org.cineplex.system.model.Rol;
import org.cineplex.system.model.Usuario;
import org.cineplex.system.util.ConexionDB;

import java.sql.*;

public class UsuarioDAO {

    public Usuario buscarPorNombreUsuario(String nombreUsuario) {
        String sql = "SELECT u.id_usuario, u.nombre_usuario, u.password, " +
                     "r.id_rol, r.nombre_rol " +
                     "FROM USUARIO u " +
                     "JOIN ROL r ON u.id_rol = r.id_rol " +
                     "WHERE u.nombre_usuario = ?";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nombreUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Rol rol = new Rol(rs.getInt("id_rol"), rs.getString("nombre_rol"));
                    return new Usuario(
                        rs.getInt("id_usuario"),
                        rs.getString("nombre_usuario"),
                        rs.getString("password"),
                        rol
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}