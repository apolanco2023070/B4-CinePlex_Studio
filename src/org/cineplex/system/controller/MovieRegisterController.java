package org.cineplex.system.controller;

import javafx.fxml.FXML;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.cineplex.system.model.Movie;
import org.cineplex.system.repository.MovieRepository;
import org.cineplex.system.utils.AlertInformation;
import org.cineplex.system.utils.Validations;
import java.util.List;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class MovieRegisterController {

    @FXML
    private TableView<Movie> tableMovies;
    @FXML
    private TableColumn<Movie, String> colTitulo;
    @FXML
    private TableColumn<Movie, String> colGenero;
    @FXML
    private TableColumn<Movie, Integer> colDuracion;
    @FXML
    private TableColumn<Movie, String> colClasificacion;
    @FXML
    private TableColumn<Movie, String> colDirector;
    @FXML
    private TableColumn<Movie, String> colUrlPoster;

    @FXML
    private Label lblTitle, lblGenre, lblLength, lblRating, lblDirector, lblPoster;
    @FXML
    private TextField txtTitle, txtLength, txtRating, txtDirector, txtPoster;

    @FXML
    private ComboBox<MovieRepository.GenreOption> cmbGenre;

    @FXML
    private Button btnRegisterMovie, btnVerPoster;

    private final MovieRepository movieRepository;

    private final AlertInformation alertInfo;
    private final Validations validations;

    public MovieRegisterController() {
        this.movieRepository = new MovieRepository();
        this.alertInfo = new AlertInformation();
        this.validations = new Validations();
    }

    @FXML
    public void initialize() {
        configurarTabla();
        cargarGenerosEnComboBox();
        cargarPeliculas();
    }

    private void configurarTabla() {
        colTitulo.setCellValueFactory(data -> data.getValue().titleProperty());
        colGenero.setCellValueFactory(data -> data.getValue().genreNameProperty());
        colDuracion.setCellValueFactory(data -> data.getValue().durationProperty().asObject());
        colClasificacion.setCellValueFactory(data -> data.getValue().ratingProperty());
        colDirector.setCellValueFactory(data -> data.getValue().directorProperty());
        colUrlPoster.setCellValueFactory(data -> data.getValue().posterUrlProperty());
    }

    private void cargarGenerosEnComboBox() {
        try {
            cmbGenre.getItems().addAll(movieRepository.getAllGenres());
        } catch (Exception e) {
            alertInfo.viewAlert("ERROR", "Error de carga", "No se pudieron cargar los géneros", e.getMessage());
        }
    }

    private void cargarPeliculas() {
        try {
            List<Movie> movies = movieRepository.getAllMovies();
            tableMovies.getItems().setAll(movies);
        } catch (Exception e) {
            alertInfo.viewAlert("ERROR", "Error de carga", "No se pudieron cargar las películas", e.getMessage());
        }
    }

    @FXML
    private void registerMovies() {
        limpiarErroresVisuales();
        String title = txtTitle.getText().trim();
        String lengthText = txtLength.getText().trim();
        String director = txtDirector.getText().trim();
        String rating = txtRating.getText().trim().toUpperCase();
        String posterUrl = txtPoster.getText().trim();

        StringBuilder errores = new StringBuilder();
        boolean hayErrores = false;

        if (validations.emptyText(title) || !validations.validateLengthText(title, 200)) {
            errores.append("• Título obligatorio (máx 200 caracteres).\n");
            hayErrores = true;
        }
        if (!validations.isPositiveNumber(lengthText)) {
            errores.append("• Duración debe ser un número entero mayor a 0.\n");
            hayErrores = true;
        }
        if (validations.emptyText(director) || !validations.validateLengthText(director, 150)) {
            errores.append("• Director obligatorio (máx 150 caracteres).\n");
            hayErrores = true;
        }
        if (!validations.isValidRating(rating)) {
            errores.append("• Clasificación debe ser A, B o C.\n");
            hayErrores = true;
        }

        if (cmbGenre.getValue() == null) {
            errores.append("• Debe seleccionar un género de la lista.\n");
            hayErrores = true;
        }

        if (!validations.emptyText(posterUrl) && !validations.validateLengthText(posterUrl, 500)) {
            errores.append("• La URL del póster no puede exceder 500 caracteres.\n");
            hayErrores = true;
        }

        if (hayErrores) {
            alertInfo.viewAlert("ERROR", "Datos inválidos", "Error de validación", errores.toString());
            return;
        }

        try {
            int duration = Integer.parseInt(lengthText);
            int genreId = cmbGenre.getValue().getId();

            Movie movie = new Movie(0, title, duration, director, genreId, rating, posterUrl);
            movieRepository.saveMovie(movie);

            limpiarFormulario();
            cargarPeliculas();
            alertInfo.viewAlert("INFORMATION", "Éxito", "Registro completado", "La película se guardó correctamente.");
        } catch (Exception e) {
          
            alertInfo.viewAlert("ERROR", "Error al registrar", "Error de sistema", "Detalle: " + e.getMessage());
             
             alertInfo.viewAlert("ERROR", "Error al registrar", "Error de sistema",
                            "No se pudo registrar la película. Detalle: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void regresarMenu() {
        try {
            Stage stageActual = (Stage) btnRegisterMovie.getScene().getWindow();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/cineplex/system/view/Administrador.fxml"));
            Parent root = loader.load();

            Scene escenaNueva = new Scene(root);

            stageActual.setScene(escenaNueva);
            stageActual.show();

        } catch (Exception e) {
            e.printStackTrace();
            alertInfo.viewAlert("ERROR", "Error de navegación", "No se pudo cargar la vista",
                    "Detalle: " + e.getMessage() + "\nVerifica la ruta del archivo Administrador.fxml");
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
        cmbGenre.setValue(null);
        txtLength.clear();
        txtRating.clear();
        txtDirector.clear();
        txtPoster.clear();
        limpiarErroresVisuales();
        txtTitle.requestFocus();
    }

    private void limpiarErroresVisuales() {
        lblTitle.setStyle("-fx-text-fill: black; -fx-font-weight: normal;");
        lblLength.setStyle("-fx-text-fill: black; -fx-font-weight: normal;");
        lblDirector.setStyle("-fx-text-fill: black; -fx-font-weight: normal;");
        lblRating.setStyle("-fx-text-fill: black; -fx-font-weight: normal;");
        lblGenre.setStyle("-fx-text-fill: black; -fx-font-weight: normal;");
        lblPoster.setStyle("-fx-text-fill: black; -fx-font-weight: normal;");
    }
}
