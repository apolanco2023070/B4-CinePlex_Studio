package org.cineplex.system.repository;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.cineplex.system.config.ConexionDB;
import org.cineplex.system.model.Seat;

public class SeatRepository {

    public void saveSeat(Seat seat) {
        String sql = "{CALL sp_insert_seat(?, ?)}";
        try (Connection conn = ConexionDB.getInstanciaConexionDB().getConnection(); 
             CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.setInt(1, seat.getSeatNumber());
            cstmt.setInt(2, seat.getAuditoriumId());
            cstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("No se pudo registrar el asiento: " + e.getMessage(), e);
        }
    }

    public List<Seat> findByAuditoriumId(Integer auditoriumId) {
        List<Seat> seats = new ArrayList<>();
        String sql = "{CALL sp_get_seats_by_auditorium(?)}";
        try (Connection conn = ConexionDB.getInstanciaConexionDB().getConnection(); 
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
            throw new RuntimeException("No se pudo cargar los asientos: " + e.getMessage(), e);
        }
        return seats;
    }

    public List<Seat> findAvailabilityByScreening(Integer screeningId) {
        List<Seat> seats = new ArrayList<>();
        String sql = "{CALL sp_get_seat_availability_by_screening(?)}";
        try (Connection conn = ConexionDB.getInstanciaConexionDB().getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.setInt(1, screeningId);
            try (ResultSet rs = cstmt.executeQuery()) {
                while (rs.next()) {
                    Seat seat = new Seat();
                    seat.setSeatId(rs.getInt("seat_id"));
                    seat.setSeatNumber(rs.getInt("seat_number"));
                    seat.setAuditoriumId(rs.getInt("auditorium_id"));
                    seat.setStatus(rs.getString("status"));
                    seats.add(seat);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("No se pudo consultar la disponibilidad: " + e.getMessage(), e);
        }
        return seats;
    }

    public boolean existsByAuditoriumId(Integer auditoriumId) {
        String sql = "{CALL sp_check_seats_exist(?)}";
        try (Connection conn = ConexionDB.getInstanciaConexionDB().getConnection(); 
             CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.setInt(1, auditoriumId);
            try (ResultSet rs = cstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("exists_flag") > 0;
                }
                return false;
            }
        } catch (SQLException e) {
            return false;
        }
    }

    public void deleteByAuditoriumId(Integer auditoriumId) {
        String sql = "{CALL sp_delete_seats_by_auditorium(?)}";
        try (Connection conn = ConexionDB.getInstanciaConexionDB().getConnection(); 
             CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.setInt(1, auditoriumId);
            cstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("No se pudo eliminar los asientos: " + e.getMessage(), e);
        }
    }

    public List<Seat> getAvailableSeatsForScreening(Integer screeningId) {
        List<Seat> availableSeats = new ArrayList<>();
        String sql = "{CALL sp_get_available_seats_for_screening(?)}";
        try (Connection conn = ConexionDB.getInstanciaConexionDB().getConnection(); 
             CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.setInt(1, screeningId);
            try (ResultSet rs = cstmt.executeQuery()) {
                while (rs.next()) {
                    Seat seat = new Seat();
                    seat.setSeatId(rs.getInt("seat_id"));
                    seat.setSeatNumber(rs.getInt("seat_number"));
                    seat.setAuditoriumId(rs.getInt("auditorium_id"));
                    availableSeats.add(seat);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener asientos disponibles: " + e.getMessage(), e);
        }
        return availableSeats;
    }
}