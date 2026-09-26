package org.cineplex.system.repository;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.cineplex.system.config.ConexionDB;
import org.cineplex.system.model.Role;
import org.cineplex.system.model.User;

public class UsuarioFactory {

    public User buscarPorNombreUsuario(String nombreUsuario) {
        String sql = "{CALL sp_obtener_usuario_por_username(?)}";
        try (Connection con = ConexionDB.getInstanciaConexionDB().getConnection(); 
             CallableStatement cstmt = con.prepareCall(sql)) {
            cstmt.setString(1, nombreUsuario);
            try (ResultSet rs = cstmt.executeQuery()) {
                if (rs.next()) {
                    Role rol = new Role(rs.getInt("role_id"), rs.getString("role_name"));
                    return new User(
                            rs.getInt("user_id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rol
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en UsuarioFactory (buscar): " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public boolean registrarUsuario(String fullName, String username, String password, String email, int roleId) {
        String sql = "{CALL sp_insert_user(?, ?, ?, ?, ?)}";
        try (Connection conn = ConexionDB.getInstanciaConexionDB().getConnection(); 
             CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.setString(1, fullName);
            cstmt.setString(2, username);
            cstmt.setString(3, password);
            cstmt.setString(4, email);
            cstmt.setInt(5, roleId);
            cstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry") || e.getMessage().contains("1062")) {
                System.err.println("Error: El nombre de usuario o el email ya están registrados.");
            } else {
                System.err.println("Error al registrar usuario vía SP: " + e.getMessage());
            }
            e.printStackTrace();
            return false;
        }
    }
}