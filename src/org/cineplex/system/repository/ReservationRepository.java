package org.cineplex.system.repository;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.cineplex.system.config.DatabaseConnection;

public class ReservationRepository {

    public List<Map<String, Object>> getReservationsMapByUserName(String userName) throws Exception {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "{CALL sp_get_all_reservations()}";

        try (Connection conn = DatabaseConnection.getDatabaseConnectionInstance().getConnection();
             CallableStatement cstmt = conn.prepareCall(sql);
             ResultSet rs = cstmt.executeQuery()) {

            while (rs.next()) {
                String dbUserName = rs.getString("user_name");

                if (dbUserName != null && dbUserName.toLowerCase().contains(userName.trim().toLowerCase())) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("reservation_id", rs.getInt("reservation_id"));
                    map.put("user_name", dbUserName);
                    map.put("movie_title", rs.getString("movie_title"));
                    map.put("auditorium_name", rs.getString("auditorium_name"));
                    map.put("seat_number", rs.getString("seat_number"));
                    map.put("show_date", rs.getString("show_date"));
                    map.put("show_time", rs.getString("show_time"));
                    map.put("status", rs.getString("status"));

                    list.add(map);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al buscar reservas por usuario: " + e.getMessage(), e);
        }
        return list;
    }

    public void cancelReservation(int reservationId) throws Exception {
        String sql = "{CALL sp_cancel_reservation(?)}";

        try (Connection conn = DatabaseConnection.getDatabaseConnectionInstance().getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.setInt(1, reservationId);
            cstmt.executeUpdate();
        } catch (SQLException e) {
            throw new Exception("Error al cancelar la reserva: " + e.getMessage(), e);
        }
    }
}