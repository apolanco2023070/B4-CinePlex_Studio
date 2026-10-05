package org.cineplex.system.repository;

import org.cineplex.system.config.DatabaseConnection;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

public class UserFactory {

    /**
     * Registers a user through sp_insert_user.
     *
     * @throws IllegalArgumentException if the username or email already exists.
     * @throws IllegalStateException for any other database failure.
     */
    public void registerUser(String fullName, String username, String password, String email, int roleId) {
        String sql = "{CALL sp_insert_user(?, ?, ?, ?, ?)}";

        try (Connection conn = DatabaseConnection.getDatabaseConnectionInstance().getConnection();
                CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.setString(1, fullName);
            cstmt.setString(2, username);
            cstmt.setString(3, password);
            cstmt.setString(4, email);
            cstmt.setInt(5, roleId);

            cstmt.executeUpdate();

        } catch (SQLException e) {
            String message = e.getMessage() == null ? "" : e.getMessage();
            if (e.getErrorCode() == 1062 || message.contains("Duplicate entry")) {
                throw new IllegalArgumentException("El usuario o el correo ya está registrado.", e);
            }
            System.err.println("Error registering user via SP: " + message);
            throw new IllegalStateException("No se pudo registrar el usuario: " + message, e);
        }
    }
}
