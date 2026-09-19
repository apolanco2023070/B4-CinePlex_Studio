package org.cineplex.system.repository;

import org.cineplex.system.model.Screening;
import java.sql.Connection;
import org.cineplex.system.config.ConexionDB;
import java.sql.CallableStatement;
import java.util.ArrayList;
import java.sql.Date;
import java.util.List;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Time;

public class ScreeningRepository {

    public void saveScreening(Screening screening) {
        String sql = "{call sp_insert_screening(?, ?, ?, ?)}";
        try (Connection conn = ConexionDB.getInstanciaConexionDB().getConnection(); CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.setInt(1, screening.getMovieId());
            cstmt.setInt(2, screening.getAuditoriumId());
            cstmt.setDate(3, Date.valueOf(screening.getShowDate()));
            cstmt.setTime(4, Time.valueOf(screening.getShowTime()));
            cstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error al registrar la función: " + e.getMessage(), e);
        }
    }

    public List<Screening> getAllScreening() {
        List<Screening> list = new ArrayList<>();
        String sql = "{call sp_get_all_screenings()}";

        try (Connection conn = ConexionDB.getInstanciaConexionDB().getConnection(); CallableStatement cstmt = conn.prepareCall(sql); ResultSet rs = cstmt.executeQuery()) {

            while (rs.next()) {
                Screening s = new Screening();
                s.setScreeningId(rs.getInt("screening_id"));

                s.setMovieId(rs.getInt("movie_id"));
                s.setAuditoriumId(rs.getInt("auditorium_id"));

                s.setMovieTitle(rs.getString("movie_title"));
                s.setAuditoriumName(rs.getString("auditorium_name"));
                s.setShowDate(rs.getDate("show_date").toLocalDate());
                s.setShowTime(rs.getTime("show_time").toLocalTime());

                list.add(s);
            }
        } catch (SQLException e) {
            System.err.println("Error al consultar funciones: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al consultar funciones: " + e.getMessage(), e);
        }
        return list;
    }
}
