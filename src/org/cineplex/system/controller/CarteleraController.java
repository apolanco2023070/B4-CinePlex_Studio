package org.cineplex.system.controller;

import java.util.List;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import org.cineplex.system.model.Movie;
import javafx.stage.Stage;
import org.cineplex.system.model.User;
import org.cineplex.system.repository.MovieRepository;
import org.cineplex.system.utils.AlertInformation;

public class CarteleraController {

    @FXML
    private FlowPane posterFlowPane;
    @FXML
    private ComboBox<MovieRepository.GenreOption> cmbGenreFilter;

    @FXML
    private Button btnBackToMenu;
    
    private final MovieRepository movieRepository = new MovieRepository();
    private final AlertInformation alertInfo = new AlertInformation();
    private User loggedUser;
    private List<Movie> allMovies;

    private static final String DEFAULT_POSTER = "https://via.placeholder.com/180x260.png?text=Sin+Poster";

    public void setLoggedUser(User user) {
        this.loggedUser = user;
    }

    @FXML
    public void initialize() {
        initializeGenreFilter();
        loadBoxOffice();
    }

    private void initializeGenreFilter() {
        try {
            MovieRepository.GenreOption allGenres = new MovieRepository.GenreOption(0, "Todos los géneros");
            cmbGenreFilter.getItems().add(allGenres);
            cmbGenreFilter.getItems().addAll(movieRepository.getAllGenres());
            cmbGenreFilter.setValue(allGenres);

            cmbGenreFilter.setOnAction(e -> filterByGenre());
        } catch (Exception e) {
            alertInfo.viewAlert("ERROR", "Error", "No se pudieron cargar los géneros", e.getMessage());
        }
    }

    private void loadBoxOffice() {
        try {
            allMovies = movieRepository.getAllMovies();
            displayMovies(allMovies);
        } catch (Exception e) {
            alertInfo.viewAlert("ERROR", "Load Error", "No se pudo cargar la cartelera", e.getMessage());
        }
    }

    private void filterByGenre() {
        try {
            MovieRepository.GenreOption selected = cmbGenreFilter.getValue();
            if (selected.getId() == 0) {
                displayMovies(allMovies);
            } else {
                List<Movie> filteredMovies = movieRepository.getMoviesByGenreId(selected.getId());
                displayMovies(filteredMovies);
            }
        } catch (Exception e) {
            alertInfo.viewAlert("ERROR", "Filter Error", "No se pudo filtrar", e.getMessage());
        }
    }

    private void displayMovies(List<Movie> movies) {
        posterFlowPane.getChildren().clear();

        if (movies == null || movies.isEmpty()) {
            Label lblEmpty = new Label("No hay películas en esta categoría.");
            lblEmpty.setStyle("-fx-text-fill: #7f8c8d;");
            posterFlowPane.getChildren().add(lblEmpty);
            return;
        }

        for (Movie movie : movies) {
            posterFlowPane.getChildren().add(createMovieCard(movie));
        }
    }

    private VBox createMovieCard(Movie movie) {
        String url = movie.getPosterUrl();
        if (url == null || url.trim().isEmpty()) {
            url = DEFAULT_POSTER;
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
                    imageView.setImage(new Image(DEFAULT_POSTER, 150, 220, true, true));
                }
            });
        } catch (Exception e) {
            imageView.setImage(new Image(DEFAULT_POSTER, 150, 220, true, true));
        }

        Label lblTitle = new Label(movie.getTitle());
        lblTitle.setWrapText(true);
        lblTitle.setMaxWidth(150);
        Label lblGenre = new Label(movie.getGenreName() != null ? movie.getGenreName() : "Sin género");

        VBox card = new VBox(6, imageView, lblTitle, lblGenre);
        card.setAlignment(Pos.TOP_CENTER);
        card.setPrefWidth(170);
        return card;
    }

    @FXML
    public void goBackToMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/cineplex/system/view/Gerente.fxml"));
            Parent root = loader.load();

            ManagerController controller = loader.getController();
            controller.setLoggedUser(loggedUser);

            Stage stage = (Stage) posterFlowPane.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Manager Panel - CinePlex");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
