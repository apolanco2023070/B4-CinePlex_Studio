package org.cineplex.system.repository;

import java.sql.Connection;
import java.sql.CallableStatement;
import org.cineplex.system.config.DatabaseConnection;
import org.cineplex.system.model.Movie;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository para manejo de películas
 */
public class MovieRepository {

    /**
     * Guarda una película en la base de datos
     */
    public void saveMovie(Movie movie) {
        String sql = "INSERT INTO movie (title, duration, director, genre_id, rating_id, poster_url) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getDatabaseInstance().getConnectionDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, movie.getTitle());
            pstmt.setInt(2, movie.getDuration());
            pstmt.setString(3, movie.getDirector());
            pstmt.setInt(4, movie.getGenreId());
            pstmt.setString(5, movie.getRating());
            pstmt.setString(6, movie.getPosterUrl());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error al guardar la película: " + e.getMessage());
            throw new RuntimeException("No se pudo registrar la película. Verifica los datos.", e);
        }
    }

    /**
     * Actualiza una película existente
     */
    public boolean updateMovie(Movie movie) {
        String sql = "UPDATE movie SET title = ?, duration = ?, director = ?, " +
                     "genre_id = ?, rating_id = ?, poster_url = ? WHERE movie_id = ?";
        
        try (Connection conn = DatabaseConnection.getDatabaseInstance().getConnectionDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, movie.getTitle());
            pstmt.setInt(2, movie.getDuration());
            pstmt.setString(3, movie.getDirector());
            pstmt.setInt(4, movie.getGenreId());
            pstmt.setString(5, movie.getRating());
            pstmt.setString(6, movie.getPosterUrl());
            pstmt.setInt(7, movie.getMovieId());
            
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar película: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Obtiene todas las películas
     */
    public List<Movie> getAllMovies() {
        List<Movie> moviesList = new ArrayList<>();
        String sql = "SELECT m.movie_id, m.title, m.duration, m.director, " +
                     "g.name AS genre_name, m.rating_id, m.poster_url " +
                     "FROM movie m " +
                     "INNER JOIN genre g ON m.genre_id = g.genre_id " +
                     "ORDER BY m.title ASC";

        try (Connection conn = DatabaseConnection.getDatabaseInstance().getConnectionDB();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Movie movie = new Movie();
                movie.setMovieId(rs.getInt("movie_id"));
                movie.setTitle(rs.getString("title"));
                movie.setDuration(rs.getInt("duration"));
                movie.setDirector(rs.getString("director"));
                movie.setGenreName(rs.getString("genre_name"));
                movie.setRating(rs.getString("rating_id"));
                movie.setPosterUrl(rs.getString("poster_url"));
                moviesList.add(movie);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener películas: " + e.getMessage());
            e.printStackTrace();
        }

        return moviesList;
    }
}