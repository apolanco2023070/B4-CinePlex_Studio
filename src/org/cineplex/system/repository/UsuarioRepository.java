/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cineplex.system.repository;

import org.cineplex.system.model.Rol;
import org.cineplex.system.model.Usuario;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.cineplex.system.config.DatabaseConnection;

public class UsuarioRepository {
    
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
}
