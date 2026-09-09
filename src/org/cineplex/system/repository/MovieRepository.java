/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cineplex.system.repository;

import java.sql.Connection;
import java.sql.CallableStatement;
import org.cineplex.system.config.DatabaseConnection;
import org.cineplex.system.model.Movie;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author informatica
 */
public class MovieRepository {

    /**
     * 
     * @param saveMovie 
     * 
     * Procedimiento que llama al SP en la database para guardar peliculas
     */
    public void saveMovie(Movie movie) {
        String sql = "{call sp_insert_movie(?,?,?,?,?,?)}";

        // Try-with-resources: Connection y CallableStatement se cierran solos al terminar
        try (Connection conn = DatabaseConnection.getDatabaseInstance().getConnectionDB();
     CallableStatement callSP = conn.prepareCall(sql)) {

            callSP.setString(1, movie.getTitle());
            callSP.setInt(2, movie.getDuration());
            callSP.setString(3, movie.getDirector());
            callSP.setInt(4, movie.getGenreId());
            callSP.setString(5, movie.getRating());
            callSP.setString(6, movie.getPosterUrl());

            callSP.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error al guardar la película: " + e.getMessage());
            throw new RuntimeException("No se pudo registrar la película. Verifica los datos.", e);
        }
    }

    /**
     * HU8: Obtiene todas las películas llamando al Stored Procedure.
     */
    public List<Movie> getAllMovies() {
        List<Movie> moviesList = new ArrayList<>();
        String sql = "{call sp_get_all_movies()}";

        // Try-with-resources: Connection, CallableStatement y ResultSet se cierran solos
        try (Connection conn = DatabaseConnection.getDatabaseInstance().getConnectionDB();
                CallableStatement callSP = conn.prepareCall(sql); ResultSet rs = callSP.executeQuery()) {

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
            
        }

        return moviesList;
    }
}
