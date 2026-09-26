package org.cineplex.system.repository;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import org.cineplex.system.config.ConexionDB;
import org.cineplex.system.model.Screening;

public class ScreeningRepository {

    public boolean hasTimeConflict(int auditoriumId, LocalDate showDate, LocalTime showTime) throws Exception {
        String sql = "{CALL sp_check_screening_conflict(?, ?, ?)}";
        try (Connection conn = ConexionDB.getInstanciaConexionDB().getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.setInt(1, auditoriumId);
            cstmt.setDate(2, Date.valueOf(showDate));
            cstmt.setTime(3, Time.valueOf(showTime));
            try (ResultSet rs = cstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("conflict_count") > 0;
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al verificar conflicto de horario: " + e.getMessage(), e);
        }
        return false;
    }

    // Alias para compatibilidad con ScreeningListController
    public List<Screening> getAllScreening() throws Exception {
        return getAllScreenings();
    }

    public List<Screening> getAllScreenings() throws Exception {
        List<Screening> screenings = new ArrayList<>();
        String sql = "{CALL sp_get_all_screenings()}";
        try (Connection conn = ConexionDB.getInstanciaConexionDB().getConnection();
             CallableStatement cstmt = conn.prepareCall(sql);
             ResultSet rs = cstmt.executeQuery()) {
            while (rs.next()) {
                Screening screening = new Screening();
                screening.setScreeningId(rs.getInt("screening_id"));
                screening.setMovieId(rs.getInt("movie_id"));
                screening.setAuditoriumId(rs.getInt("auditorium_id"));
                screening.setShowDate(rs.getDate("show_date").toLocalDate());
                screening.setShowTime(rs.getTime("show_time").toLocalTime());
                screening.setMovieTitle(rs.getString("movie_title"));
                screening.setAuditoriumName(rs.getString("auditorium_name"));
                screenings.add(screening);
            }
        } catch (SQLException e) {
            throw new Exception("Error al consultar las funciones: " + e.getMessage(), e);
        }
        return screenings;
    }

    public void saveScreening(Screening screening) throws Exception {
        String sql = "{CALL sp_insert_screening(?, ?, ?, ?)}";
        try (Connection conn = ConexionDB.getInstanciaConexionDB().getConnection(); 
             CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.setInt(1, screening.getMovieId());
            cstmt.setInt(2, screening.getAuditoriumId());
            cstmt.setDate(3, Date.valueOf(screening.getShowDate()));
            cstmt.setTime(4, Time.valueOf(screening.getShowTime()));
            cstmt.executeUpdate();
        } catch (SQLException e) {
            throw new Exception("Error al registrar la función: " + e.getMessage(), e);
        }
    }

    public void deleteScreening(int screeningId) throws Exception {
        String sql = "{CALL sp_delete_screening(?)}";
        try (Connection conn = ConexionDB.getInstanciaConexionDB().getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.setInt(1, screeningId);
            cstmt.executeUpdate();
        } catch (SQLException e) {
            throw new Exception("No se pudo eliminar la función. Verifica que no tenga reservas asociadas: " + e.getMessage(), e);
        }
    }
}