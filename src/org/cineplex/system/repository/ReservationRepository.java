/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cineplex.system.repository;

import java.sql.Connection;
import org.cineplex.system.config.ConexionDB;
import java.sql.CallableStatement;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;
import java.sql.ResultSet;
import org.cineplex.system.model.SeatStatus;
/**
 *
 * @author informatica
 */
public class ReservationRepository {
   
    public List<SeatStatus> getSeatsForScreening(Integer screeningId) {
        List<SeatStatus> seats = new ArrayList<>();
        String sql = "{call sp_get_seats_for_screening(?)}";
        try (Connection conn = ConexionDB.getInstanciaConexionDB().getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            
            cstmt.setInt(1, screeningId);
            try (ResultSet rs = cstmt.executeQuery()) {
                while (rs.next()) {
                    seats.add(new SeatStatus(
                        rs.getInt("seat_id"),
                        rs.getInt("seat_number"),
                        rs.getString("status")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al cargar asientos: " + e.getMessage(), e);
        }
        return seats;
    }

    public void createReservation(Integer userId, Integer screeningId, Integer seatId, String status) {
        String sql = "{call sp_insert_reservation(?, ?, ?, ?)}";
        try (Connection conn = ConexionDB.getInstanciaConexionDB().getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            
            cstmt.setInt(1, userId); 
            cstmt.setInt(2, screeningId);
            cstmt.setInt(3, seatId);
            cstmt.setString(4, status);
            cstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al reser  var: " + e.getMessage(), e);
        }
    }
}

