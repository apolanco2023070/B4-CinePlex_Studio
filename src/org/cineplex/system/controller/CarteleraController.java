package org.cineplex.system.controller;

import java.util.List;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.cineplex.system.model.Movie;
import org.cineplex.system.model.Usuario;
import org.cineplex.system.repository.MovieRepository;
import org.cineplex.system.utils.AlertInformation;

public class CarteleraController {

    @FXML private FlowPane flowPosters;
    @FXML private ComboBox<MovieRepository.GenreOption> cmbFiltroGenero; 

    private final MovieRepository movieRepository = new MovieRepository();
    private final AlertInformation alertInfo = new AlertInformation();
    private Usuario usuarioLogueado;
    private List<Movie> todasLasPeliculas; 

    private static final String POSTER_DEFECTO = "https://via.placeholder.com/180x260.png?text=Sin+Poster";

    public void setUsuarioLogueado(Usuario usuario) {
        this.usuarioLogueado = usuario;
    }

    @FXML
    public void initialize() {
        inicializarFiltroGenero();
        cargarCartelera();
    }

    private void inicializarFiltroGenero() {
        try {
            MovieRepository.GenreOption todos = new MovieRepository.GenreOption(0, "Todos los géneros");
            cmbFiltroGenero.getItems().add(todos);
            cmbFiltroGenero.getItems().addAll(movieRepository.getAllGenres());
            cmbFiltroGenero.setValue(todos);
            
            cmbFiltroGenero.setOnAction(e -> filtrarPorGenero());
        } catch (Exception e) {
            alertInfo.viewAlert("ERROR", "Error", "No se pudieron cargar los géneros", e.getMessage());
        }
    }

    private void cargarCartelera() {
        try {
            todasLasPeliculas = movieRepository.getAllMovies();
            mostrarPeliculas(todasLasPeliculas);
        } catch (Exception e) {
            alertInfo.viewAlert("ERROR", "Error de carga", "No se pudo cargar la cartelera", e.getMessage());
        }
    }

    private void filtrarPorGenero() {
        try {
            MovieRepository.GenreOption seleccionado = cmbFiltroGenero.getValue();
            if (seleccionado.getId() == 0) {
                mostrarPeliculas(todasLasPeliculas);
            } else {
                List<Movie> filtradas = movieRepository.getMoviesByGenreId(seleccionado.getId());
                mostrarPeliculas(filtradas);
            }
        } catch (Exception e) {
            alertInfo.viewAlert("ERROR", "Error de filtro", "No se pudo filtrar", e.getMessage());
        }
    }

    private void mostrarPeliculas(List<Movie> movies) {
        flowPosters.getChildren().clear();

        if (movies == null || movies.isEmpty()) {
            Label lblVacio = new Label("No hay películas en esta categoría.");
            lblVacio.setStyle("-fx-text-fill: #7f8c8d;");
            flowPosters.getChildren().add(lblVacio);
            return;
        }

        for (Movie movie : movies) {
            flowPosters.getChildren().add(crearTarjetaPelicula(movie));
        }
    }

      private VBox crearTarjetaPelicula(Movie movie) {
        String url = movie.getPosterUrl();
        if (url == null || url.trim().isEmpty()) {
            url = POSTER_DEFECTO;
        }

        ImageView imageView = new ImageView();
        imageView.setFitWidth(150);
        imageView.setFitHeight(220);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);

        try {
            Image image = new Image(url, 150, 220, true, true, true);
            imageView.setImage(image);
            image.errorProperty().addListener((obs, wasError, isError) -> {
                if (isError) {
                    imageView.setImage(new Image(POSTER_DEFECTO, 150, 220, true, true));
                }
            });
        } catch (Exception e) {
            imageView.setImage(new Image(POSTER_DEFECTO, 150, 220, true, true));
        }

        Label lblTitulo = new Label(movie.getTitle());
        lblTitulo.setWrapText(true);
        lblTitulo.setMaxWidth(150);
        lblTitulo.setStyle("-fx-font-weight: bold; -fx-text-alignment: center; -fx-alignment: center;");

        Label lblGenero = new Label(movie.getGenreName() != null ? movie.getGenreName() : "Sin género");
        lblGenero.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 11px;");

        VBox card = new VBox(6, imageView, lblTitulo, lblGenero);
        card.setAlignment(Pos.TOP_CENTER);
        card.setPrefWidth(170);
        card.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-background-radius: 8; "
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 6, 0, 0, 2);");
        
        return card; 
    }

   @FXML
    public void volver() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/cineplex/system/view/Gerente.fxml"));
            Parent root = loader.load();

            GerenteController controller = loader.getController();
            controller.setUsuarioLogueado(usuarioLogueado);

            Stage stage = (Stage) flowPosters.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Panel Gerente - CinePlex");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}   