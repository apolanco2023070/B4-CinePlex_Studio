package org.cineplex.system.repository;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.cineplex.system.config.DatabaseConnection;
import org.cineplex.system.model.Movie;

public class MovieRepository {

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
            return name;
        }
    }

    public void saveMovie(Movie movie) throws Exception {
        boolean isUpdate = movie.getMovieId() > 0;
        String sql = isUpdate
                ? "{CALL sp_update_movie(?, ?, ?, ?, ?, ?, ?)}"
                : "{CALL sp_insert_movie(?, ?, ?, ?, ?, ?)}";

        try (Connection conn = DatabaseConnection.getDatabaseConnectionInstance().getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {

            int index = 1;
            if (isUpdate) {
                cstmt.setInt(index++, movie.getMovieId());
            }
            cstmt.setString(index++, movie.getTitle());
            cstmt.setInt(index++, movie.getDuration());
            cstmt.setString(index++, movie.getDirector());
            cstmt.setInt(index++, movie.getGenreId());
            cstmt.setString(index++, movie.getRating());
            cstmt.setString(index, movie.getPosterUrl());

            cstmt.executeUpdate();
        } catch (SQLException e) {
            throw new Exception("Error al guardar la película: " + e.getMessage(), e);
        }
    }

    public List<Movie> getAllMovies() throws Exception {
        List<Movie> movies = new ArrayList<>();
        String sql = "{CALL sp_get_all_movies()}";

        try (Connection conn = DatabaseConnection.getDatabaseConnectionInstance().getConnection(); 
             CallableStatement cstmt = conn.prepareCall(sql); 
             ResultSet rs = cstmt.executeQuery()) {

            while (rs.next()) {
                Movie movie = new Movie(
                        rs.getInt("movie_id"),
                        rs.getString("title"),
                        rs.getInt("duration"),
                        rs.getString("director"),
                        rs.getInt("genre_id"),
                        rs.getString("rating_id"),
                        rs.getString("poster_url")
                );
                movie.setGenreName(rs.getString("genre_name"));
                movies.add(movie);
            }
        } catch (SQLException e) {
            throw new Exception("Error al obtener películas: " + e.getMessage(), e);
        }
        return movies;
    }

    public List<Movie> getMoviesByGenreId(int genreId) throws Exception {
        List<Movie> movies = new ArrayList<>();
          String sql = "{CALL sp_get_movies_by_genre_id(?)}";

        try (Connection conn = DatabaseConnection.getDatabaseConnectionInstance().getConnection(); 
             CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.setInt(1, genreId);
            try (ResultSet rs = cstmt.executeQuery()) {
                while (rs.next()) {
                    Movie movie = new Movie(
                            rs.getInt("movie_id"),
                            rs.getString("title"),
                            rs.getInt("duration"),
                            rs.getString("director"),
                            rs.getInt("genre_id"),
                            rs.getString("rating_id"),
                            rs.getString("poster_url")
                    );
                    movie.setGenreName(rs.getString("genre_name"));
                    movies.add(movie);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al obtener películas por género: " + e.getMessage(), e);
        }
        return movies;
    }

    public List<GenreOption> getAllGenres() throws Exception {
        List<GenreOption> genres = new ArrayList<>();
        String sql = "{CALL sp_get_all_genres()}";

        try (Connection conn = DatabaseConnection.getDatabaseConnectionInstance().getConnection(); 
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
}