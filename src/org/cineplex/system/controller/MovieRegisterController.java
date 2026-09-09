package org.cineplex.system.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.cineplex.system.model.Movie;
import org.cineplex.system.repository.MovieRepository;
import org.cineplex.system.utils.AlertInformation;
import org.cineplex.system.utils.Validations;
import java.util.List;

public class MovieRegisterController {

    @FXML private TableView<Movie> tableMovies;
    @FXML private TableColumn<Movie, String> colTitulo;
    @FXML private TableColumn<Movie, String> colGenero;
    @FXML private TableColumn<Movie, Integer> colDuracion;
    @FXML private TableColumn<Movie, String> colClasificacion;
    @FXML private TableColumn<Movie, String> colDirector;
    @FXML private TableColumn<Movie, String> colUrlPoster;

    @FXML private Label lblTitle, lblGenre, lblLength, lblRating, lblDirector, lblPoster;
    @FXML private TextField txtTitle, txtGenre, txtLength, txtRating, txtDirector, txtPoster;
    @FXML private Button btnRegisterMovie, btnVerPoster;

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
        String genreText = txtGenre.getText().trim();

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
        
        int genreId = obtenerGenreId(genreText);
        if (genreId == -1) {
            errores.append("• Género no reconocido (Action, Drama, Comedy).\n");
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
            int duration = Integer.parseInt(lengthText); // 100% seguro gracias a isPositiveNumber
            Movie movie = new Movie(0, title, duration, director, genreId, rating, posterUrl);
            movieRepository.saveMovie(movie);
            
            limpiarFormulario();
            cargarPeliculas();
            alertInfo.viewAlert("INFORMATION", "Éxito", "Registro completado", "La película se guardó correctamente.");
        } catch (Exception e) {
            alertInfo.viewAlert("ERROR", "Error al registrar", "Error de sistema", "Detalle: " + e.getMessage());
        }
    }

    @FXML
    private void verPoster() {
        String url = txtPoster.getText().trim();
        if (validations.emptyText(url)) {
            alertInfo.viewAlert("WARNING", "Sin URL", "Campo vacío", "Ingrese una URL de póster antes de visualizarla.");
            lblPoster.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            return;
        }
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            alertInfo.viewAlert("WARNING", "URL inválida", "Formato incorrecto", "La URL debe comenzar con http:// o https://");
            return;
        }

        try {
            Image image = new Image(url, 400, 600, true, true);
            if (image.isError()) {
                alertInfo.viewAlert("ERROR", "Imagen no disponible", "Error de carga", "No se pudo cargar la imagen. Verifique la URL.");
                return;
            }
            ImageView imageView = new ImageView(image);
            imageView.setPreserveRatio(true);
            imageView.setFitWidth(400);
            
            StackPane root = new StackPane();
            root.setStyle("-fx-background-color: black; -fx-padding: 20;");
            root.getChildren().add(imageView);
            
            Stage stage = new Stage();
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setTitle("Vista Previa del Póster");
            stage.setScene(new javafx.scene.Scene(root));
            stage.setResizable(true);
            stage.show();
        } catch (Exception e) {
            alertInfo.viewAlert("ERROR", "Error", "Problema al cargar", "Detalle: " + e.getMessage());
        }
    }

    private int obtenerGenreId(String genreName) {
        if (!validations.isValidGenre(genreName)) return -1;
        String g = genreName.toLowerCase();
        if (g.equals("action") || g.equals("acción")) return 1;
        if (g.equals("drama")) return 2;
        if (g.equals("comedy") || g.equals("comedia")) return 3;
        return -1;
    }

    private void limpiarFormulario() {
        txtTitle.clear(); txtGenre.clear(); txtLength.clear();
        txtRating.clear(); txtDirector.clear(); txtPoster.clear();
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