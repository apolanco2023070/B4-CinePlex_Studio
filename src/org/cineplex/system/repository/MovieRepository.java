package org.cineplex.system.repository;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.cineplex.system.model.Movie;
import org.cineplex.system.config.DatabaseConnection;

public class MovieRepository {

    public void saveMovie(Movie movie) throws Exception {
        String sql = "CALL sp_insert_movie(?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getDatabaseInstance().getConnectionDB();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            
            cstmt.setString(1, movie.getTitle());
            cstmt.setInt(2, movie.getDuration());
            cstmt.setString(3, movie.getDirector());
            cstmt.setInt(4, movie.getGenreId());
            cstmt.setString(5, movie.getRating()); // CHAR(1) se envía como String
            cstmt.setString(6, movie.getPosterUrl());
            
            cstmt.execute();
        } catch (SQLException e) {
            throw new Exception("Error de base de datos al guardar: " + e.getMessage(), e);
        }
    }

    public List<Movie> getAllMovies() throws Exception {
        List<Movie> movies = new ArrayList<>();
        String sql = "CALL sp_get_all_movies()";
        
        try (Connection conn = DatabaseConnection.getDatabaseInstance().getConnectionDB();
             CallableStatement cstmt = conn.prepareCall(sql);
             ResultSet rs = cstmt.executeQuery()) {
            
            while (rs.next()) {
                Movie movie = new Movie(
                    rs.getInt("movie_id"),
                    rs.getString("title"),  
                    rs.getInt("duration"),
                    rs.getString("director"),
                    rs.getInt("genre_id"),
                    rs.getString("rating_name"), // Obtenemos el nombre desde el JOIN
                    rs.getString("poster_url")
                );
                movies.add(movie);
            }
        } catch (SQLException e) {
            throw new Exception("Error al obtener películas: " + e.getMessage(), e);
        }
        return movies;
    }
}