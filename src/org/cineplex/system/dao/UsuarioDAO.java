package org.cineplex.system.dao;

import org.cineplex.system.model.Rol;
import org.cineplex.system.model.Usuario;
import org.cineplex.system.config.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO {

    public Usuario buscarPorNombreUsuario(String nombreUsuario) {
        // CORREGIDO: Nombres de tablas y columnas en inglés según tu DDL
        String sql = "SELECT u.user_id, u.username, u.password, " +
                     "r.role_id, r.name " +
                     "FROM users u " +
                     "JOIN role r ON u.role_id = r.role_id " +
                     "WHERE u.username = ?";

        // CORREGIDO: Llamada correcta a la clase ConexionDB
        try (Connection con = ConexionDB.getInstanciaConexionDB().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nombreUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Rol rol = new Rol(rs.getInt("role_id"), rs.getString("name"));
                    return new Usuario(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rol
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en UsuarioDAO: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}