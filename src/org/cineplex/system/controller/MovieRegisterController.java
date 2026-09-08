/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cineplex.system.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import org.cineplex.system.model.Movie;
import org.cineplex.system.repository.MovieRepository;

/**
 *
 * @author informatica
 */
public class MovieRegisterController {

    @FXML
    private Button btnRegisterMovie;

    @FXML
    private Label lblDirector;

    @FXML
    private Label lblGenre;

    @FXML
    private Label lblLength;

    @FXML
    private Label lblPoster;

    @FXML
    private Label lblRating;

    @FXML
    private Label lblTitle;

    @FXML
    private ListView<?> list;

    @FXML
    private TextField txtDirector;

    @FXML
    private TextField txtGenre;

    @FXML
    private TextField txtLength;

    @FXML
    private TextField txtPoster;

    @FXML
    private TextField txtRating;

    @FXML
    private TextField txtTitle;

    private final MovieRepository movieRepository;

    public MovieRegisterController() {
        this.movieRepository = new MovieRepository();
    }

    @FXML
    private void registerMovies() {

        String title = txtTitle.getText().trim();
        int duration = Integer.parseInt(txtLength.getText().trim());
        String director = txtDirector.getText().trim();
        String rating = txtRating.getText().trim().toUpperCase();
        String posterUrl = txtPoster.getText().trim();

        int genreId = obtenerGenreId(txtGenre.getText().trim());

        Movie movie = new Movie(title, duration, director, genreId, rating, posterUrl);

        // 4. Guardar en la base de datos
        try {
            movieRepository.saveMovie(movie);

            limpiarFormulario();

        } catch (Exception e) {
            System.err.println("Error al registrar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private int obtenerGenreId(String genreName) {
        switch (genreName.toLowerCase()) {
            case "action":
            case "acción":
                return 1;
            case "drama":
                return 2;
            case "comedy":
            case "comedia":
                return 3;
            default:
                return 1;
        }
    }

    private void limpiarFormulario() {
        txtTitle.clear();
        txtGenre.clear();
        txtLength.clear();
        txtRating.clear();
        txtDirector.clear();
        txtPoster.clear();
        txtTitle.requestFocus();
    }
}
