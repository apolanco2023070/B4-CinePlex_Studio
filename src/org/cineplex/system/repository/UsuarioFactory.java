package org.cineplex.system.repository;

import org.cineplex.system.config.ConexionDB;
import org.cineplex.system.model.Role;
import org.cineplex.system.model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioFactory {
    
    public User buscarPorNombreUsuario(String nombreUsuario) {
        String sql = "SELECT u.user_id, u.username, u.password, r.role_id, r.name " +
                     "FROM users u " +
                     "INNER JOIN role r ON u.role_id = r.role_id " +
                     "WHERE u.username = ?";

        try (Connection con = ConexionDB.getInstanciaConexionDB().getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            
            pstmt.setString(1, nombreUsuario);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Role rol = new Role(rs.getInt("role_id"), rs.getString("name"));
                    return new User(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rol
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en UsuarioRepository: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    public boolean registrarUsuario(String fullName, String username, String password, String email, int roleId) {
        String sql = "INSERT INTO users (full_name, username, password, email, role_id) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = ConexionDB.getInstanciaConexionDB().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, fullName);
            pstmt.setString(2, username);
            pstmt.setString(3, password);
            pstmt.setString(4, email);
            pstmt.setInt(5, roleId);
            
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                System.err.println("Error: El usuario o email ya existe");
            } else {
                System.err.println("Error al registrar usuario: " + e.getMessage());
            }
            e.printStackTrace();
            return false;
        }
    }
}