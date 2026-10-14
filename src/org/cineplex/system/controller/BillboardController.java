package org.cineplex.system.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import org.cineplex.system.model.Movie;
import org.cineplex.system.model.RoleType;
import org.cineplex.system.model.User;
import org.cineplex.system.repository.MovieRepository;
import org.cineplex.system.repository.MovieRepository.GenreOption;
import org.cineplex.system.utils.AlertInformation;
import org.cineplex.system.utils.Session;

public class BillboardController implements Initializable {

    @FXML
    private FlowPane posterFlowPane;
    @FXML
    private ComboBox<GenreOption> cmbGenreFilter;
    @FXML
    private Button btnBackToMenu;

    private final MovieRepository movieRepo = new MovieRepository();
    private User loggedUser;
    private List<Movie> allMovies = new ArrayList<>();

    public void setLoggedUser(User user) {
        this.loggedUser = user;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadGenres();
        loadMovies();
    }

    private void loadGenres() {
        if (cmbGenreFilter == null) return;
        try {
            List<GenreOption> genres = movieRepo.getAllGenres();
            cmbGenreFilter.getItems().clear();
            
            GenreOption defaultOption = new GenreOption(0, "Todos los géneros");
            cmbGenreFilter.getItems().add(defaultOption);
            
            if (genres != null) {
                cmbGenreFilter.getItems().addAll(genres);
            }
            
            cmbGenreFilter.setValue(defaultOption);
            cmbGenreFilter.setOnAction(event -> filterMovies());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadMovies() {
        if (posterFlowPane == null) return;

        try {
            allMovies = movieRepo.getAllMovies();
            displayMovies(allMovies);
        } catch (Exception e) {
            e.printStackTrace();
            AlertInformation.viewAlert("ERROR", "Error de Carga", null, "Error al cargar la cartelera: " + e.getMessage());
        }
    }

    private void filterMovies() {
        GenreOption selected = cmbGenreFilter.getValue();
        if (selected == null || selected.getId() == 0) {
            displayMovies(allMovies);
        } else {
            try {
                List<Movie> filtered = movieRepo.getMoviesByGenreId(selected.getId());
                displayMovies(filtered);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void displayMovies(List<Movie> movies) {
        posterFlowPane.getChildren().clear();

        if (movies != null && !movies.isEmpty()) {
            for (Movie movie : movies) {
                VBox card = createMovieCard(movie);
                posterFlowPane.getChildren().add(card);
            }
            posterFlowPane.requestLayout();
        } else {
            Label lblEmpty = new Label("No hay películas disponibles.");
            lblEmpty.setStyle("-fx-text-fill: #701121; -fx-font-size: 14px; -fx-font-weight: bold;");
            posterFlowPane.getChildren().add(lblEmpty);
        }
    }

    private VBox createMovieCard(Movie movie) {
        VBox card = new VBox(8);
        card.getStyleClass().add("movie-card");
        card.setAlignment(Pos.CENTER);
        
        card.setPrefWidth(180);
        card.setPrefHeight(290);
        card.setStyle("-fx-background-color: #ffffff; -fx-padding: 10; -fx-background-radius: 10; -fx-border-color: #d4af37; -fx-border-radius: 10; -fx-border-width: 1;");

        ImageView posterView = new ImageView();
        posterView.setFitWidth(150);
        posterView.setFitHeight(200);
        posterView.setPreserveRatio(true);
        posterView.setSmooth(true);

        String posterUrl = movie.getPosterUrl();
        if (posterUrl != null && !posterUrl.trim().isEmpty()) {
            try {
                Image img = new Image(posterUrl, 150, 200, true, true, true);
                if (!img.isError()) {
                    posterView.setImage(img);
                }
            } catch (Exception ignored) {}
        }

        
        String titleText = (movie.getTitle() != null && !movie.getTitle().isEmpty()) ? movie.getTitle() : "Sin Título";
        Label lblTitle = new Label(titleText);
        lblTitle.getStyleClass().add("movie-title");
        lblTitle.setWrapText(true);
        lblTitle.setAlignment(Pos.CENTER);
        lblTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #333333;");

        String genreText = (movie.getGenreName() != null && !movie.getGenreName().isEmpty()) ? movie.getGenreName() : "General";
        Label lblGenre = new Label(genreText);
        lblGenre.getStyleClass().add("movie-genre");
        lblGenre.setStyle("-fx-text-fill: #8a1529; -fx-font-size: 11px;");

        card.getChildren().addAll(posterView, lblTitle, lblGenre);

        card.setOnMouseClicked(event -> openMovieDetailModal(movie, event));

        return card;
    }

    private void openMovieDetailModal(Movie movie, javafx.scene.input.MouseEvent event) {
        try {
            Stage modalStage = new Stage();
            modalStage.initModality(Modality.APPLICATION_MODAL);
            
            Window currentWindow = ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            modalStage.initOwner(currentWindow);
            modalStage.setTitle("CinePlex - Información de Película");

            VBox root = new VBox(12);
            root.setAlignment(Pos.CENTER);
            root.setStyle("-fx-background-color: #fcf9f0; -fx-padding: 20; -fx-border-color: #d4af37; -fx-border-width: 2; -fx-background-radius: 8;");
            root.setPrefWidth(360);

            // Póster grande en el diálogo
            ImageView posterView = new ImageView();
            posterView.setFitWidth(150);
            posterView.setFitHeight(210);
            posterView.setPreserveRatio(true);
            if (movie.getPosterUrl() != null && !movie.getPosterUrl().trim().isEmpty()) {
                try {
                    posterView.setImage(new Image(movie.getPosterUrl(), 150, 210, true, true, true));
                } catch (Exception ignored) {}
            }

            // Título de la película
            Label lblTitle = new Label(movie.getTitle() != null ? movie.getTitle() : "Sin Título");
            lblTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #8a1529; -fx-alignment: CENTER;");
            lblTitle.setWrapText(true);

            // Detalles (Director, Duración, Género, Clasificación)
            VBox detailsBox = new VBox(6);
            detailsBox.setAlignment(Pos.CENTER_LEFT);
            detailsBox.setStyle("-fx-padding: 5 15; -fx-background-color: #ffffff; -fx-background-radius: 6; -fx-border-color: #e0e0e0; -fx-border-radius: 6;");
            
            String detailStyle = "-fx-font-size: 13px; -fx-text-fill: #333333; -fx-font-weight: bold;";
            
            Label lblDirector = new Label("Director: " + (movie.getDirector() != null ? movie.getDirector() : "N/A"));
            Label lblDuration = new Label("Duración: " + movie.getDuration() + " minutos");
            Label lblGenre = new Label("Género: " + (movie.getGenreName() != null ? movie.getGenreName() : "General"));
            Label lblRating = new Label("Clasificación: " + (movie.getRating() != null ? movie.getRating() : "N/A"));
            
            lblDirector.setStyle(detailStyle);
            lblDuration.setStyle(detailStyle);
            lblGenre.setStyle(detailStyle);
            lblRating.setStyle(detailStyle);
            
            detailsBox.getChildren().addAll(lblDirector, lblDuration, lblGenre, lblRating);

            // Botones de acción: Reservar Asiento y Cancelar
            HBox btnBox = new HBox(12);
            btnBox.setAlignment(Pos.CENTER);
            btnBox.setStyle("-fx-padding: 10 0 0 0;");

            Button btnReserve = new Button("Reservar Asiento");
            btnReserve.setStyle("-fx-background-color: #8a1529; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 8 14; -fx-cursor: hand;");
            
            Button btnCancel = new Button("Cancelar");
            btnCancel.setStyle("-fx-background-color: #d1d1d1; -fx-text-fill: #333333; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 8 14; -fx-cursor: hand;");

            // Acción Cancelar: Cierra el modal
            btnCancel.setOnAction(e -> modalStage.close());

            // Acción Reservar: Cierra el modal y abre la vista de selección de asientos
            btnReserve.setOnAction(e -> {
                modalStage.close();
                navigateToSeatReservation(movie, currentWindow);
            });

            btnBox.getChildren().addAll(btnReserve, btnCancel);
            root.getChildren().addAll(posterView, lblTitle, detailsBox, btnBox);

            Scene scene = new Scene(root);
            modalStage.setScene(scene);
            modalStage.setResizable(false);
            modalStage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            AlertInformation.viewAlert("ERROR", "Detalle", null, "Error al mostrar la información: " + e.getMessage());
        }
    }

    private void navigateToSeatReservation(Movie movie, Window currentWindow) {
        try {
            URL fxmlUrl = getClass().getResource("/org/cineplex/system/view/SeatsView.fxml");
            if (fxmlUrl == null) {
                fxmlUrl = getClass().getResource("/org/cineplex/system/view/SeatReservation.fxml");
            }
            if (fxmlUrl == null) {
                throw new RuntimeException("No se encontró el archivo FXML de asientos.");
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            SeatReservationController controller = loader.getController();
            if (controller != null) {
                controller.setLoggedUser(this.loggedUser);
                controller.setSelectedMovie(movie);
            }

            if (currentWindow instanceof Stage) {
                Stage stage = (Stage) currentWindow;
                stage.setScene(new Scene(root));
                stage.setTitle("CinePlex - Reserva de Asientos");
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertInformation.viewAlert("ERROR", "Navegación", null, "No se pudo abrir la reserva: " + e.getMessage());
        }
    }

    @FXML
    private void goBackToMenu(ActionEvent event) {
        try {
            Window currentWindow = null;
            if (btnBackToMenu != null && btnBackToMenu.getScene() != null) {
                currentWindow = btnBackToMenu.getScene().getWindow();
            } else if (event != null && event.getSource() instanceof javafx.scene.Node) {
                currentWindow = ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            }

            if (!(currentWindow instanceof Stage)) {
                return;
            }
            Stage stage = (Stage) currentWindow;

            User user = (loggedUser != null) ? loggedUser : Session.getCurrentUser();
            String base = "/org/cineplex/system/view/";

            if (user == null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(base + "Login.fxml"));
                Parent root = loader.load();
                stage.setScene(new Scene(root));
                stage.setTitle("CinePlex Studio - Inicio de Sesión");
                return;
            }

            boolean isAdmin = user.getRole() != null && user.getRole().is(RoleType.ADMINISTRATOR);

            if (isAdmin) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(base + "Admin.fxml"));
                Parent root = loader.load();
                AdminController controller = loader.getController();
                controller.setLoggedUser(user);
                stage.setScene(new Scene(root));
                stage.setTitle("Panel de Administrador - CinePlex");
            } else {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(base + "Manager.fxml"));
                Parent root = loader.load();
                ManagerController controller = loader.getController();
                controller.setLoggedUser(user);
                stage.setScene(new Scene(root));
                stage.setTitle("Panel de Gerente - CinePlex");
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertInformation.viewAlert("ERROR", "Navegación", null, "Error al regresar: " + e.getMessage());
        }
    }

    @FXML
    private void goBack(ActionEvent event) {
        goBackToMenu(event);
    }
}