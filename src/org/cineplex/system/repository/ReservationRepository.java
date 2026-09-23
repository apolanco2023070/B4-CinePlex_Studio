/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cineplex.system.repository;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import org.cineplex.system.config.ConexionDB;
import org.cineplex.system.model.Reservation;

/**
 * Encargado de crear la relación Reserva-Asiento-Función en la base de datos.
 *
 * @author informatica
 */
public class ReservationRepository {

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
}
