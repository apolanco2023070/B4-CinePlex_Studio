package org.cineplex.system.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import org.cineplex.system.model.Movie;
import org.cineplex.system.repository.MovieRepository;
import org.cineplex.system.utils.AlertInformation;
import org.cineplex.system.utils.Validations;
import java.util.List;

/**
 * Controlador para registro y edición de películas
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
    private ListView<Movie> list;

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
    private final AlertInformation alertInfo;
    private final Validations validaciones;
    
    // Variable para almacenar la película seleccionada
    private Movie movieSeleccionada;

    public MovieRegisterController() {
        this.movieRepository = new MovieRepository();
        this.alertInfo = new AlertInformation();
        this.validaciones = new Validations();
    }

    /**
     * Inicializa el controlador y carga las películas
     */
    @FXML
    public void initialize() {
        cargarPeliculas();
    }

    /**
     * Carga todas las películas en el ListView
     */
    private void cargarPeliculas() {
        List<Movie> peliculas = movieRepository.getAllMovies();
        list.getItems().clear();
        list.getItems().addAll(peliculas);
        
        // Configurar cómo se muestra cada película
        list.setCellFactory(param -> new javafx.scene.control.ListCell<Movie>() {
            @Override
            protected void updateItem(Movie movie, boolean empty) {
                super.updateItem(movie, empty);
                if (empty || movie == null) {
                    setText(null);
                } else {
                    setText(movie.getTitle() + " (" + movie.getDuration() + " min)");
                }
            }
        });
    }

    /**
     * Registra o actualiza una película
     */
    @FXML
    private void registerMovies() {
        try {
            String title = txtTitle.getText().trim();
            String lengthText = txtLength.getText().trim();
            String director = txtDirector.getText().trim();
            String rating = txtRating.getText().trim().toUpperCase();
            String posterUrl = txtPoster.getText().trim();
            String genreText = txtGenre.getText().trim();

            // Validaciones
            if (validaciones.emptyText(title) || validaciones.emptyText(lengthText) || 
                validaciones.emptyText(director) || validaciones.emptyText(rating) || 
                validaciones.emptyText(genreText)) {
                alertInfo.viewAlert("ERROR", "Campos vacíos", "Error de validación", 
                    "Todos los campos son obligatorios.");
                return;
            }

            if (!validaciones.isPositiveNumber(lengthText)) {
                alertInfo.viewAlert("ERROR", "Duración inválida", "Error de validación", 
                    "La duración debe ser un número positivo.");
                return;
            }

            if (!validaciones.isValidRating(rating)) {
                alertInfo.viewAlert("ERROR", "Clasificación inválida", "Error de validación", 
                    "La clasificación debe ser A, B o C.");
                return;
            }

            if (!validaciones.isValidGenre(genreText)) {
                alertInfo.viewAlert("ERROR", "Género inválido", "Error de validación", 
                    "El género debe ser Acción, Drama o Comedia.");
                return;
            }

            int duration = Integer.parseInt(lengthText);
            int genreId = obtenerGenreId(genreText);

            Movie movie = new Movie(title, duration, director, genreId, rating, posterUrl);

            boolean exito;
            
            if (movieSeleccionada != null) {
                // MODO EDICIÓN: Actualizar película existente
                movie.setMovieId(movieSeleccionada.getMovieId());
                exito = movieRepository.updateMovie(movie);
                
                if (exito) {
                    alertInfo.viewAlert("INFORMATION", "Éxito", "Actualización completada", 
                        "La película se actualizó correctamente.");
                }
            } else {
                // MODO CREACIÓN: Registrar nueva película
                movieRepository.saveMovie(movie);
                exito = true;
                
                if (exito) {
                    alertInfo.viewAlert("INFORMATION", "Éxito", "Registro completado", 
                        "La película se guardó correctamente.");
                }
            }

            limpiarFormulario();
            cargarPeliculas();

        } catch (Exception e) {
            alertInfo.viewAlert("ERROR", "Error", "Error de sistema",
                    "No se pudo procesar la película. Detalle: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Selecciona una película del listado para editar
     */
    @FXML
    public void seleccionarPelicula(MouseEvent event) {
        if (event.getClickCount() == 2) { // Doble click
            Movie movieSeleccionada = list.getSelectionModel().getSelectedItem();
            if (movieSeleccionada != null) {
                cargarDatosPelicula(movieSeleccionada);
            }
        }
    }

    /**
     * Carga los datos de una película en el formulario
     */
    @FXML
    public void cargarDatosPelicula(Movie movie) {
        this.movieSeleccionada = movie;
        
        txtTitle.setText(movie.getTitle());
        txtLength.setText(String.valueOf(movie.getDuration()));
        txtDirector.setText(movie.getDirector());
        txtGenre.setText(movie.getGenreName());
        txtRating.setText(movie.getRating());
        txtPoster.setText(movie.getPosterUrl());
        
        btnRegisterMovie.setText("Actualizar");
    }

    /**
     * Obtiene el ID del género según el nombre
     */
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

    /**
     * Limpia el formulario
     */
    private void limpiarFormulario() {
        txtTitle.clear();
        txtGenre.clear();
        txtLength.clear();
        txtRating.clear();
        txtDirector.clear();
        txtPoster.clear();
        movieSeleccionada = null;
        btnRegisterMovie.setText("Registrar");
        txtTitle.requestFocus();
    }
}