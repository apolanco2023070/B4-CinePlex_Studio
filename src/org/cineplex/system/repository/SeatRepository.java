/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cineplex.system.repository;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.cineplex.system.config.DatabaseConnection;
import org.cineplex.system.model.Seat;

public class SeatRepository {

    public void saveSeat(Seat seat) {
        String sql = "{call sp_insert_seat(?, ?)}";
        
        try (Connection conn = DatabaseConnection.getDatabaseInstance().getConnectionDB();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            
            cstmt.setInt(1, seat.getSeatNumber());
            cstmt.setInt(2, seat.getAuditoriumId());
            cstmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("Error al guardar asiento: " + e.getMessage());
            throw new RuntimeException("No se pudo registrar el asiento.", e);
        }
    }

    public List<Seat> findByAuditoriumId(Integer auditoriumId) {
        List<Seat> seats = new ArrayList<>();
        String sql = "{call sp_get_seats_by_auditorium(?)}";
        
        try (Connection conn = DatabaseConnection.getDatabaseInstance().getConnectionDB();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            
            cstmt.setInt(1, auditoriumId);
            
            try (ResultSet rs = cstmt.executeQuery()) {
                while (rs.next()) {
                    Seat seat = new Seat();
                    seat.setSeatId(rs.getInt("seat_id"));
                    seat.setSeatNumber(rs.getInt("seat_number"));
                    seat.setAuditoriumId(rs.getInt("auditorium_id"));
                    seats.add(seat);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al consultar asientos: " + e.getMessage());
            throw new RuntimeException("No se pudo cargar los asientos.", e);
        }
        return seats;
    }

    public boolean existsByAuditoriumId(Integer auditoriumId) {
        String sql = "{call sp_check_seats_exist(?)}";
        
        try (Connection conn = DatabaseConnection.getDatabaseInstance().getConnectionDB();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            
            cstmt.setInt(1, auditoriumId);
            
            try (ResultSet rs = cstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("exists_flag") > 0;
                }
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al verificar asientos: " + e.getMessage());
            return false;
        }
    }

    public void deleteByAuditoriumId(Integer auditoriumId) {
        String sql = "{call sp_delete_seats_by_auditorium(?)}";
        
        try (Connection conn = DatabaseConnection.getDatabaseInstance().getConnectionDB();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            
            cstmt.setInt(1, auditoriumId);
            cstmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("Error al eliminar asientos: " + e.getMessage());
            throw new RuntimeException("No se pudo eliminar los asientos.", e);
        }
    }
}
