package org.cineplex.system.repository;

import org.cineplex.system.config.DatabaseConnection;
import org.cineplex.system.model.Rol;
import org.cineplex.system.model.Usuario;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioFactory {
    
    // Método existente - buscar por username
    public Usuario buscarPorNombreUsuario(String nombreUsuario) {
        String sql = "{CALL sp_obtener_usuario_por_username(?)}";

        try (Connection con = DatabaseConnection.getDatabaseInstance().getConnectionDB();
             CallableStatement cs = con.prepareCall(sql)) {
            cs.setString(1, nombreUsuario);
            try (ResultSet rs = cs.executeQuery()) {
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
    
    // ✅ NUEVO MÉTODO - Registrar usuario (Gerente)
    public boolean registrarUsuario(String fullName, String username, String password, String email, int roleId) {
        String sql = "INSERT INTO users (full_name, username, password, email, role_id) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getDatabaseInstance().getConnectionDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, fullName);
            pstmt.setString(2, username);
            pstmt.setString(3, password);
            pstmt.setString(4, email);
            pstmt.setInt(5, roleId);
            
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            // Manejar errores de duplicados (username o email)
            if (e.getMessage().contains("Duplicate entry")) {
                System.err.println("Error: El usuario o email ya existe");
            } else {
                System.err.println("Error al registrar usuario: " + e.getMessage());
            }
            e.printStackTrace();
            return false;
        }
    }
    
    // ✅ MÉTODO OPCIONAL - Consultar todos los usuarios
    public java.util.List<Usuario> obtenerTodosLosUsuarios() {
        java.util.List<Usuario> usuarios = new java.util.ArrayList<>();
        String sql = "SELECT u.user_id, u.full_name, u.username, u.email, u.password, r.role_id, r.name " +
                     "FROM users u INNER JOIN role r ON u.role_id = r.role_id ORDER BY u.username";
        
        try (Connection conn = DatabaseConnection.getDatabaseInstance().getConnectionDB();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Rol rol = new Rol(rs.getInt("role_id"), rs.getString("name"));
                Usuario usuario = new Usuario(
                    rs.getInt("user_id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rol
                );
                // Agregar atributos adicionales si es necesario
                usuarios.add(usuario);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener usuarios: " + e.getMessage());
            e.printStackTrace();
        }
        
        return usuarios;
    }
}