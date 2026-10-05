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

        try (Connection conn = DatabaseConnection.getDatabaseConnectionInstance().getConnection(); CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.setInt(1, seat.getSeatNumber());
            cstmt.setInt(2, seat.getAuditoriumId());
            cstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error saving seat: " + e.getMessage());
            throw new RuntimeException("No se pudo registrar el asiento.", e);
        }
    }

    public List<Seat> findByAuditoriumId(Integer auditoriumId) {
        List<Seat> seats = new ArrayList<>();
        String sql = "{call sp_get_seats_by_auditorium(?)}";

        try (Connection conn = DatabaseConnection.getDatabaseConnectionInstance().getConnection(); CallableStatement cstmt = conn.prepareCall(sql)) {

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
            System.err.println("Error querying seats: " + e.getMessage());
            throw new RuntimeException("No se pudo cargar los asientos.", e);
        }
        return seats;
    }

    public boolean existsByAuditoriumId(Integer auditoriumId) {
        String sql = "{call sp_check_seats_exist(?)}";

        try (Connection conn = DatabaseConnection.getDatabaseConnectionInstance().getConnection(); CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.setInt(1, auditoriumId);

            try (ResultSet rs = cstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("exists_flag") > 0;
                }
                return false;
            }

        } catch (SQLException e) {
            System.err.println("Error checking seats: " + e.getMessage());
            throw new RuntimeException("No se pudo verificar los asientos de la sala.", e);
        }
    }

    /**
     * Creates seats 1..capacity in a single transaction: either all of them
     * are created or none.
     */
    public void saveSeats(Integer auditoriumId, int capacity) {
        String sql = "{call sp_insert_seat(?, ?)}";

        try (Connection conn = DatabaseConnection.getDatabaseConnectionInstance().getConnection()) {
            conn.setAutoCommit(false);
            try (CallableStatement cstmt = conn.prepareCall(sql)) {
                for (int i = 1; i <= capacity; i++) {
                    cstmt.setInt(1, i);
                    cstmt.setInt(2, auditoriumId);
                    cstmt.executeUpdate();
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.err.println("Error saving seats: " + e.getMessage());
            throw new RuntimeException("No se pudieron generar los asientos.", e);
        }
    }

    public void deleteByAuditoriumId(Integer auditoriumId) {
        String sql = "{call sp_delete_seats_by_auditorium(?)}";

        try (Connection conn = DatabaseConnection.getDatabaseConnectionInstance().getConnection(); CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.setInt(1, auditoriumId);
            cstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error deleting seats: " + e.getMessage());
            throw new RuntimeException("No se pudo eliminar los asientos.", e);
        }
    }

    public List<Seat> getAvailableSeatsForScreening(Integer screeningId) {
        List<Seat> availableSeats = new ArrayList<>();
        String sql = "{call sp_get_available_seats_for_screening(?)}";

        try (Connection conn = DatabaseConnection.getDatabaseConnectionInstance().getConnection(); CallableStatement cstmt = conn.prepareCall(sql)) {

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
