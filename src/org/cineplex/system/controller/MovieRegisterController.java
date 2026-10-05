package org.cineplex.system.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.cineplex.system.model.Movie;
import org.cineplex.system.repository.MovieRepository;
import org.cineplex.system.utils.AlertInformation;
import org.cineplex.system.utils.Validations;
import java.util.List;
import javafx.scene.input.MouseEvent;

/**
 * Controller for registering and editing movies
 */
public class MovieRegisterController {

    @FXML
    private TableView<Movie> tableMovies;
    
    @FXML
    private TableColumn<Movie, String> colTitle;
    
    @FXML
    private TableColumn<Movie, String> colGenre;
    
    @FXML
    private TableColumn<Movie, Integer> colDuration;
    
    @FXML
    private TableColumn<Movie, String> colRating;
    
    @FXML
    private TableColumn<Movie, String> colDirector;
    
    @FXML
    private TableColumn<Movie, String> colPosterUrl;
    

    @FXML
    private Label lblTitle, lblGenre, lblLength, lblRating, lblDirector, lblPoster;
    
    @FXML
    private TextField txtTitle, txtLength, txtRating, txtDirector, txtPoster;
    
    @FXML
    private ComboBox<MovieRepository.GenreOption> cmbGenre;
    
    @FXML
    private Button btnRegisterMovie, btnViewPoster;

    private final MovieRepository movieRepository;
    private final AlertInformation alertInfo;
    private final Validations validations;

    private Movie selectedMovie;

    public MovieRegisterController() {
        this.movieRepository = new MovieRepository();
        this.alertInfo = new AlertInformation();
        this.validations = new Validations();
    }

    @FXML
    public void initialize() {
        configureTable();
        loadGenresIntoComboBox();
        loadMovies();
    }

    private void configureTable() {
        colTitle.setCellValueFactory(data -> data.getValue().titleProperty());
        colGenre.setCellValueFactory(data -> data.getValue().genreNameProperty());
        colDuration.setCellValueFactory(data -> data.getValue().durationProperty().asObject());
        colRating.setCellValueFactory(data -> data.getValue().ratingProperty());
        colDirector.setCellValueFactory(data -> data.getValue().directorProperty());
        colPosterUrl.setCellValueFactory(data -> data.getValue().posterUrlProperty());
    }

    private void loadGenresIntoComboBox() {
        try {
            cmbGenre.getItems().addAll(movieRepository.getAllGenres());
        } catch (Exception e) {
            alertInfo.viewAlert("ERROR", "Error de carga", "No se pudieron cargar los géneros", e.getMessage());
        }
    }

    private void loadMovies() {
        try {
            List<Movie> movies = movieRepository.getAllMovies();
            tableMovies.getItems().setAll(movies);
        } catch (Exception e) {
            alertInfo.viewAlert("ERROR", "Error de carga", "No se pudieron cargar las películas", e.getMessage());
        }
    }

    /**
     * @param registerMovies
     * Registers the movies 
     */
    
    @FXML
    private void registerMovies() {
        String title = txtTitle.getText().trim();
        String lengthText = txtLength.getText().trim();
        String director = txtDirector.getText().trim();
        String rating = txtRating.getText().trim().toUpperCase();
        String posterUrl = txtPoster.getText().trim();

        StringBuilder errors = new StringBuilder();
        boolean hasErrors = false;

        if (validations.emptyText(title) || !validations.validateLengthText(title, 200)) {
            errors.append("• Título obligatorio (máx 200 caracteres).\n");
            hasErrors = true;
        }
        if (!validations.isPositiveNumber(lengthText)) {
            errors.append("• Duración debe ser un número entero mayor a 0.\n");
            hasErrors = true;
        }
        if (validations.emptyText(director) || !validations.validateLengthText(director, 150)) {
            errors.append("• Director obligatorio (máx 150 caracteres).\n");
            hasErrors = true;
        }
        if (!validations.isValidRating(rating)) {
            errors.append("• Clasificación debe ser A, B o C.\n");
            hasErrors = true;
        }
        if (!validations.emptyText(posterUrl) && !posterUrl.matches("(?i)^https?://\\S+$")) {
            errors.append("• La URL del póster debe comenzar con http:// o https://.\n");
            hasErrors = true;
        }
        if (cmbGenre.getValue() == null) {
            errors.append("• Debe seleccionar un género de la lista.\n");
            hasErrors = true;
        }
        if (!validations.emptyText(posterUrl) && !validations.validateLengthText(posterUrl, 500)) {
            errors.append("• La URL del póster no puede exceder 500 caracteres.\n");
            hasErrors = true;
        }

        if (hasErrors) {
            alertInfo.viewAlert("ERROR", "Datos inválidos", "Error de validación", errors.toString());
            return;
        }

        try {
            int duration = Integer.parseInt(lengthText);
            int genreId = cmbGenre.getValue().getId();
            
            
            int movieId = (selectedMovie != null) ? selectedMovie.getMovieId() : 0;

            Movie movie = new Movie(movieId, title, duration, director, genreId, rating, posterUrl);
            movieRepository.saveMovie(movie);

            clearForm();
            loadMovies();
            
            String successMessage = (selectedMovie != null) 
                ? "La película se actualizó correctamente." 
                : "La película se registró correctamente.";
                
            alertInfo.viewAlert("INFORMATION", "Éxito", "Operación completada", successMessage);
        } catch (Exception e) {
            alertInfo.viewAlert("ERROR", "Error al guardar", "Error de sistema", "No se pudo guardar la película. Detalle: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void viewPoster() {
        String url = txtPoster.getText().trim();
        
        if (url.isEmpty()) {
            Movie selected = tableMovies.getSelectionModel().getSelectedItem();
            if (selected != null) {
                url = selected.getPosterUrl();
            }
        }

        if (url == null || url.isEmpty()) {
            alertInfo.viewAlert("WARNING", "Sin URL", "No se puede ver el póster", 
                "No hay una URL de póster ingresada o la película seleccionada no tiene una.");
            return;
        }

        try {
            java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));
        } catch (Exception e) {
            alertInfo.viewAlert("ERROR", "Error al abrir", "No se pudo abrir el póster", 
                "Verifica que la URL sea válida (debe empezar con http:// o https://). Detalle: " + e.getMessage());
        }
    }

    @FXML
    private void goBackToMenu() {
        try {
            Stage currentStage = (Stage) btnRegisterMovie.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/cineplex/system/view/Admin.fxml"));
            Parent root = loader.load();
            currentStage.setScene(new Scene(root));
            currentStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            alertInfo.viewAlert("ERROR", "Error de navegación", "No se pudo cargar la vista",
                    "Detalle: " + e.getMessage() + "\nVerifica la ruta del archivo Admin.fxml");
        }
    }

    @FXML
    public void selectMovie(MouseEvent event) {
        if (event.getClickCount() == 2) {
            Movie selected = tableMovies.getSelectionModel().getSelectedItem();
            if (selected != null) {
                loadMovieData(selected);
            }
        }
    }

    public void loadMovieData(Movie movie) {
        this.selectedMovie = movie;

        txtTitle.setText(movie.getTitle());
        txtLength.setText(String.valueOf(movie.getDuration()));
        txtDirector.setText(movie.getDirector());
        txtRating.setText(movie.getRating());
        txtPoster.setText(movie.getPosterUrl());

        for (MovieRepository.GenreOption genre : cmbGenre.getItems()) {
            if (genre.getId() == movie.getGenreId()) {
                cmbGenre.setValue(genre);
                break;
            }
        }
        
        btnRegisterMovie.setText("Actualizar Película");
    }

    private void clearForm() {
        txtTitle.clear();
        txtLength.clear();
        txtRating.clear();
        txtDirector.clear();
        txtPoster.clear();
        cmbGenre.setValue(null);

        selectedMovie = null;
        btnRegisterMovie.setText("Registrar");
        txtTitle.requestFocus();
    }

}