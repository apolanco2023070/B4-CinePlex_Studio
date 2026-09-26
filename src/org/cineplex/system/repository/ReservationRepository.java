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
import org.cineplex.system.config.ConexionDB;
import org.cineplex.system.model.Reservation;
import org.cineplex.system.model.SeatStatus;

/**
 * Encargado de crear la relación Reserva-Asiento-Función en la base de datos
 * y consultar estados de asientos.
 *
 * @author informatica
 */
public class ReservationRepository {

    /**
     * Versión 1: Usada por SeatAvailabilityController.
     * Crea una reserva recibiendo un objeto Reservation completo.
     */
    public void createReservation(Reservation reservation) throws Exception {
        String sql = "{call sp_create_reservation(?, ?, ?)}";

        try (Connection conn = ConexionDB.getInstanciaConexionDB().getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.setInt(1, reservation.getUserId());
            cstmt.setInt(2, reservation.getScreeningId());
            cstmt.setInt(3, reservation.getSeatId());
            cstmt.executeUpdate();

        } catch (SQLException e) {
            // El constraint uq_reservation_screening_seat impide reservar dos veces el mismo asiento.
            if (e.getMessage() != null && e.getMessage().toLowerCase().contains("duplicate")) {
                throw new Exception("Ese asiento ya fue reservado para esta función.", e);
            }
            throw new Exception("Error al crear la reserva: " + e.getMessage(), e);
        }
    }

    /**
     * Versión 2: Usada por SeatReservationController.
     * Crea una reserva recibiendo los parámetros por separado.
     */
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
            throw new RuntimeException("Error al reservar: " + e.getMessage(), e);
        }
    }

    /**
     * Obtiene el ID de la última reserva creada para un screening y asiento específicos.
     * Necesario para la generación inmediata del ticket en SeatReservationController.
     */
    public Integer getLastReservationId(Integer screeningId, Integer seatId) {
        String sql = "{call sp_get_last_reservation_id(?, ?)}";

        try (Connection conn = ConexionDB.getInstanciaConexionDB().getConnection(); 
             CallableStatement cstm = conn.prepareCall(sql)) {

            cstm.setInt(1, screeningId);
            cstm.setInt(2, seatId);

            try (ResultSet rs = cstm.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("reservation_id");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener la última reserva: " + e.getMessage(), e);
        }

        return null;
    }

    /**
     * Obtiene el estado de todos los asientos para una función específica.
     */
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
}