package org.cineplex.system.controller;

import java.util.List;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
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

/**
 * Controlador de la Cartelera de Películas.
 * HU: Como Gerente, quiero visualizar el póster asociado a cada película
 * para identificarla visualmente (acceso de solo lectura).
 */
public class CarteleraController {

    @FXML
    private FlowPane flowPosters;

    private final MovieRepository movieRepository = new MovieRepository();
    private final AlertInformation alertInfo = new AlertInformation();
    private Usuario usuarioLogueado;

    // Poster de reemplazo cuando la película no tiene URL o la imagen no carga
    private static final String POSTER_DEFECTO =
            "https://via.placeholder.com/180x260.png?text=Sin+Poster";

    public void setUsuarioLogueado(Usuario usuario) {
        this.usuarioLogueado = usuario;
    }

    @FXML
    public void initialize() {
        cargarCartelera();
    }

    private void cargarCartelera() {
        try {
            List<Movie> movies = movieRepository.getAllMovies();
            flowPosters.getChildren().clear();

            if (movies.isEmpty()) {
                Label lblVacio = new Label("No hay películas registradas todavía.");
                lblVacio.setStyle("-fx-text-fill: #7f8c8d;");
                flowPosters.getChildren().add(lblVacio);
                return;
            }

            for (Movie movie : movies) {
                flowPosters.getChildren().add(crearTarjetaPelicula(movie));
            }
        } catch (Exception e) {
            alertInfo.viewAlert("ERROR", "Error de carga", "No se pudo cargar la cartelera", e.getMessage());
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
            // true, true, true => preserveRatio, smooth, backgroundLoading
            Image image = new Image(url, 150, 220, true, true, true);
            imageView.setImage(image);
            // Si la carga en segundo plano falla, reemplazar por el póster por defecto
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

        Label lblGenero = new Label(movie.getGenreName() != null ? movie.getGenreName() : "");
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