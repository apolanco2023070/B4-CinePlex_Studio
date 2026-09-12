package org.cineplex.system.repository;

import org.cineplex.system.config.DatabaseConnection;
import org.cineplex.system.model.Rol;
import org.cineplex.system.model.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioFactory {
    
    /**
     * Busca un usuario por su nombre de usuario
     * @param nombreUsuario el username a buscar
     * @return Usuario encontrado o null si no existe
     */
    public Usuario buscarPorNombreUsuario(String nombreUsuario) {
        String sql = "SELECT u.user_id, u.username, u.password, r.role_id, r.name " +
                     "FROM users u " +
                     "INNER JOIN role r ON u.role_id = r.role_id " +
                     "WHERE u.username = ?";

        try (Connection con = DatabaseConnection.getDatabaseInstance().getConnectionDB();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            
            pstmt.setString(1, nombreUsuario);
            
            try (ResultSet rs = pstmt.executeQuery()) {
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
    
    /**
     * Registra un nuevo usuario en la base de datos
     * @param fullName Nombre completo
     * @param username Nombre de usuario
     * @param password Contraseña
     * @param email Correo electrónico
     * @param roleId ID del rol (1=ADMINISTRATOR, 2=MANAGER)
     * @return true si se registró correctamente, false si hubo error
     */
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
}