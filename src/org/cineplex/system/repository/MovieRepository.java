package org.cineplex.system.repository;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.cineplex.system.config.ConexionDB;
import org.cineplex.system.model.Movie;

public class MovieRepository {

    /**
     * Guarda una nueva película llamando al Stored Procedure.
     */
    public void saveMovie(Movie movie) throws Exception {
        String sql = "{CALL sp_insert_movie(?, ?, ?, ?, ?, ?)}";

        try (Connection conn = ConexionDB.getInstanciaConexionDB().getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.setString(1, movie.getTitle());
            cstmt.setInt(2, movie.getDuration());
            cstmt.setString(3, movie.getDirector());
            cstmt.setInt(4, movie.getGenreId());
            cstmt.setString(5, movie.getRating());
            cstmt.setString(6, movie.getPosterUrl());

            cstmt.executeUpdate();
        } catch (SQLException e) {
            throw new Exception("Error de base de datos al guardar la película: " + e.getMessage(), e);
        }
    }

    /**
     * Obtiene todas las películas llamando al Stored Procedure.
     * (Este SP fue corregido anteriormente para incluir genre_id y rating_id)
     */
    public List<Movie> getAllMovies() throws Exception {
        List<Movie> movies = new ArrayList<>();
        String sql = "{CALL sp_get_all_movies()}";

        try (Connection conn = ConexionDB.getInstanciaConexionDB().getConnection();
             CallableStatement cstmt = conn.prepareCall(sql);
             ResultSet rs = cstmt.executeQuery()) {

            while (rs.next()) {
                Movie movie = new Movie(
                    rs.getInt("movie_id"),
                    rs.getString("title"),
                    rs.getInt("duration"),
                    rs.getString("director"),
                    rs.getInt("genre_id"),      // <-- Coincide con la corrección de la DB
                    rs.getString("rating_id"), 
                    rs.getString("poster_url")
                );
                movies.add(movie);
            }
        } catch (SQLException e) {
            throw new Exception("Error al obtener películas: " + e.getMessage(), e);
        }
        return movies;
    }

    /**
     * Obtiene una película específica por su ID.
     * NOTA: Se usa SQL directo para evitar errores si el SP 'sp_get_movie_by_id' no existe aún en tu DB.
     */
    public Movie getMovieById(int movieId) throws Exception {
        String sql = "SELECT movie_id, title, duration, director, genre_id, rating_id, poster_url " +
                     "FROM movie WHERE movie_id = ?";
        
        try (Connection conn = ConexionDB.getInstanciaConexionDB().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, movieId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Movie(
                        rs.getInt("movie_id"),
                        rs.getString("title"),
                        rs.getInt("duration"),
                        rs.getString("director"),
                        rs.getInt("genre_id"),
                        rs.getString("rating_id"), 
                        rs.getString("poster_url")
                    );
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al obtener la película por ID: " + e.getMessage(), e);
        }
        return null;
    }

    /**
     * Actualiza una película existente.
     * NOTA: Se usa SQL directo para evitar errores si el SP 'sp_update_movie' no existe aún en tu DB.
     */
    public boolean updateMovie(Movie movie) throws Exception {
        String sql = "UPDATE movie SET title = ?, duration = ?, director = ?, " +
                     "genre_id = ?, rating_id = ?, poster_url = ? WHERE movie_id = ?";
        
        try (Connection conn = ConexionDB.getInstanciaConexionDB().getConnection();
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
            throw new Exception("Error al actualizar película: " + e.getMessage(), e);
        }
    }

    /**
     * Obtiene las películas filtradas por su ID de género.
     */
    public List<Movie> getMoviesByGenreId(int genreId) throws Exception {
        List<Movie> movies = new ArrayList<>();
        // Si tienes el SP, usa "{CALL sp_get_movies_by_genre_id(?)}", si no, usa este SQL directo:
        String sql = "SELECT movie_id, title, duration, director, genre_id, rating_id, poster_url " +
                     "FROM movie WHERE genre_id = ?";

        try (Connection conn = ConexionDB.getInstanciaConexionDB().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, genreId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    movies.add(new Movie(
                        rs.getInt("movie_id"),
                        rs.getString("title"),
                        rs.getInt("duration"),
                        rs.getString("director"),
                        rs.getInt("genre_id"),
                        rs.getString("rating_id"),
                        rs.getString("poster_url")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al obtener películas por género: " + e.getMessage(), e);
        }
        return movies;
    }

    /**
     * Obtiene todos los géneros disponibles para llenar el ComboBox.
     */
    public List<GenreOption> getAllGenres() throws Exception {
        List<GenreOption> genres = new ArrayList<>();
        String sql = "{CALL sp_get_all_genres()}";

        try (Connection conn = ConexionDB.getInstanciaConexionDB().getConnection();
             CallableStatement cstmt = conn.prepareCall(sql);
             ResultSet rs = cstmt.executeQuery()) {

            while (rs.next()) {
                genres.add(new GenreOption(rs.getInt("genre_id"), rs.getString("name")));
            }
        } catch (SQLException e) {
            throw new Exception("Error al obtener géneros: " + e.getMessage(), e);
        }
        return genres;
    }

    /**
     * Clase auxiliar interna para manejar las opciones del ComboBox de géneros.
     */
    public static class GenreOption {
        private final int id;
        private final String name;

        public GenreOption(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return name; // Esto es lo que verá el usuario en el ComboBox
        }
    }
}