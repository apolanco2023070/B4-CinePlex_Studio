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
import org.cineplex.system.model.TicketInfo;

/**
 * HU38: cruza reserva + película + función + sala + asiento.
 * HU37: emite el boleto (registra la fila en la tabla ticket).
 *
 * @author informatica
 */
public class TicketRepository {

    public List<TicketInfo> getActiveReservations() throws Exception {
        List<TicketInfo> reservations = new ArrayList<>();
        String sql = "{call sp_get_active_reservations()}";

        try (Connection conn = ConexionDB.getInstanciaConexionDB().getConnection();
             CallableStatement cstmt = conn.prepareCall(sql);
             ResultSet rs = cstmt.executeQuery()) {

            while (rs.next()) {
                reservations.add(mapBasicRow(rs));
            }
        } catch (SQLException e) {
            throw new Exception("Error al consultar las reservas activas: " + e.getMessage(), e);
        }
        return reservations;
    }

    public TicketInfo getReservationDetails(int reservationId) throws Exception {
        String sql = "{call sp_get_reservation_details(?)}";

        try (Connection conn = ConexionDB.getInstanciaConexionDB().getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.setInt(1, reservationId);

            try (ResultSet rs = cstmt.executeQuery()) {
                if (rs.next()) {
                    TicketInfo info = new TicketInfo();
                    info.setReservationId(rs.getInt("reservation_id"));
                    info.setReservationStatus(rs.getString("reservation_status"));
                    if (rs.getTimestamp("reservation_date") != null) {
                        info.setReservationDate(rs.getTimestamp("reservation_date").toLocalDateTime());
                    }
                    info.setCustomerName(rs.getString("customer_name"));
                    info.setMovieTitle(rs.getString("movie_title"));
                    info.setDuration(rs.getInt("duration"));
                    info.setAuditoriumName(rs.getString("auditorium_name"));
                    info.setShowDate(rs.getDate("show_date").toLocalDate());
                    info.setShowTime(rs.getTime("show_time").toLocalTime());
                    info.setSeatNumber(rs.getInt("seat_number"));
                    return info;
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al consultar el detalle de la reserva: " + e.getMessage(), e);
        }
        throw new Exception("No se encontró la reserva indicada.");
    }

    public void issueTicket(int reservationId) throws Exception {
        String sql = "{call sp_issue_ticket(?)}";

        try (Connection conn = ConexionDB.getInstanciaConexionDB().getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.setInt(1, reservationId);
            cstmt.executeUpdate();

        } catch (SQLException e) {
            if (e.getMessage() != null && e.getMessage().toLowerCase().contains("duplicate")) {
                throw new Exception("Ya se emitió un boleto para esta reserva.", e);
            }
            throw new Exception("Error al emitir el boleto: " + e.getMessage(), e);
        }
    }

    private TicketInfo mapBasicRow(ResultSet rs) throws SQLException {
        TicketInfo info = new TicketInfo();
        info.setReservationId(rs.getInt("reservation_id"));
        info.setCustomerName(rs.getString("customer_name"));
        info.setMovieTitle(rs.getString("movie_title"));
        info.setAuditoriumName(rs.getString("auditorium_name"));
        info.setShowDate(rs.getDate("show_date").toLocalDate());
        info.setShowTime(rs.getTime("show_time").toLocalTime());
        info.setSeatNumber(rs.getInt("seat_number"));
        info.setHasTicket(rs.getBoolean("has_ticket"));
        return info;
    }
}
