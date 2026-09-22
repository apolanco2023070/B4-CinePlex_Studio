package org.cineplex.system.repository;

import org.cineplex.system.model.Role;
import org.cineplex.system.model.User;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.cineplex.system.config.ConexionDB;

public class UserRepository {

    public User findByUsername(String username) {
        String sql = "{CALL sp_obtener_usuario_por_username(?)}";

        try (Connection conn = ConexionDB.getInstanciaConexionDB().getConnection(); 
                CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.setString(1, username);

            try (ResultSet rs = cstmt.executeQuery()) {
                if (rs.next()) {
                    Role role = new Role(rs.getInt("role_id"), rs.getString("role_name"));
                    return new User(
                            rs.getInt("user_id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            role
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error in UserRepository: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
