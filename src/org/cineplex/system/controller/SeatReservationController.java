package org.cineplex.system.controller;

import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import org.cineplex.system.config.DatabaseConnection;
import org.cineplex.system.model.Auditorium;
import org.cineplex.system.model.Movie;
import org.cineplex.system.model.RoleType;
import org.cineplex.system.model.Screening;
import org.cineplex.system.model.Seat;
import org.cineplex.system.model.TicketData;
import org.cineplex.system.model.User;
import org.cineplex.system.repository.AuditoriumRepository;
import org.cineplex.system.repository.MovieRepository;
import org.cineplex.system.repository.ScreeningRepository;
import org.cineplex.system.repository.SeatRepository;
import org.cineplex.system.utils.AlertInformation;
import org.cineplex.system.utils.SceneManager;
import org.cineplex.system.utils.Session;

public class SeatReservationController implements Initializable {

    @FXML
    private ComboBox<Auditorium> cmbAuditorium;
    @FXML
    private ComboBox<Movie> cmbMovie;
    @FXML
    private ComboBox<Screening> cmbScreening;
    @FXML
    private ComboBox<Seat> cmbSeat;
    @FXML
    private TextField txtReservationName;

    @FXML
    private Button btnCreateTicket;
    @FXML
    private Button btnBack;

    private final AuditoriumRepository auditoriumRepo = new AuditoriumRepository();
    private final MovieRepository movieRepo = new MovieRepository();
    private final ScreeningRepository screeningRepo = new ScreeningRepository();
    private final SeatRepository seatRepo = new SeatRepository();

    private User loggedUser;
    private Movie selectedMovie;

    public void setLoggedUser(User user) {
        this.loggedUser = (user != null) ? user : Session.getCurrentUser();
    }

    public void setSelectedMovie(Movie movie) {
        this.selectedMovie = movie;
        if (cmbMovie != null && movie != null && cmbMovie.getItems() != null) {
            for (Movie m : cmbMovie.getItems()) {
                if (m.getMovieId() == movie.getMovieId()) {
                    cmbMovie.getSelectionModel().select(m);
                    break;
                }
            }
        }
    }

    private User currentUser() {
        return (loggedUser != null) ? loggedUser : Session.getCurrentUser();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configureConverters();
        loadInitialData();
        configureListeners();
        loadFilteredScreenings();
    }

    private void configureConverters() {
        cmbScreening.setConverter(new StringConverter<Screening>() {
            @Override
            public String toString(Screening s) {
                if (s == null) {
                    return "";
                }
                String title = s.getMovieTitle() != null ? s.getMovieTitle() : "Función";
                String room = s.getAuditoriumName() != null ? s.getAuditoriumName() : "";
                return title + " - " + room + " - " + s.getShowDate() + " " + s.getShowTime();
            }

            @Override
            public Screening fromString(String string) {
                return null;
            }
        });
    }

    private void loadInitialData() {
        try {
            List<Auditorium> auditoriums = auditoriumRepo.getAuditoriums();
            if (auditoriums != null && !auditoriums.isEmpty()) {
                cmbAuditorium.getItems().setAll(auditoriums);
            }
        } catch (Exception e) {
            AlertInformation.viewAlert("ERROR", "Error de Carga", null, "Error al cargar salas: " + e.getMessage());
        }

        try {
            List<Movie> movies = movieRepo.getAllMovies();
            if (movies != null && !movies.isEmpty()) {
                cmbMovie.getItems().setAll(movies);

                if (this.selectedMovie != null) {
                    for (Movie m : movies) {
                        if (m.getMovieId() == this.selectedMovie.getMovieId()) {
                            cmbMovie.getSelectionModel().select(m);
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            AlertInformation.viewAlert("ERROR", "Error de Carga", null, "Error al cargar películas: " + e.getMessage());
        }
    }

    private void configureListeners() {
        cmbMovie.valueProperty().addListener((obs, oldVal, newVal) -> loadFilteredScreenings());
        cmbAuditorium.valueProperty().addListener((obs, oldVal, newVal) -> loadFilteredScreenings());

        cmbScreening.valueProperty().addListener((obs, oldVal, selectedScreening) -> {
            if (selectedScreening != null && selectedScreening.getScreeningId() != null) {
                loadAvailableSeats(selectedScreening.getScreeningId());
            } else {
                cmbSeat.getItems().clear();
            }
        });
    }

    private void loadFilteredScreenings() {
        Movie movie = cmbMovie.getValue();
        Auditorium auditorium = cmbAuditorium.getValue();

        cmbScreening.getItems().clear();
        cmbSeat.getItems().clear();

        try {
            List<Screening> allScreenings = screeningRepo.getAllScreening();
            if (allScreenings == null || allScreenings.isEmpty()) {
                return;
            }

            List<Screening> filtered = allScreenings.stream()
                    .filter(s -> movie == null
                            || (s.getMovieId() != null && s.getMovieId().intValue() == movie.getMovieId()))
                    .filter(s -> auditorium == null
                            || (s.getAuditoriumId() != null
                            && s.getAuditoriumId().intValue() == auditorium.getAuditoriumId().intValue()))
                    .toList();

            cmbScreening.getItems().setAll(filtered);

        } catch (Exception e) {
            AlertInformation.viewAlert("ERROR", "Error de Filtrado", null, "Error al filtrar funciones: " + e.getMessage());
        }
    }

    private void loadAvailableSeats(Integer screeningId) {
        try {
            List<Seat> seats = seatRepo.getAvailableSeatsForScreening(screeningId);
            if (seats != null) {
                cmbSeat.getItems().setAll(seats);
            } else {
                cmbSeat.getItems().clear();
            }
        } catch (Exception e) {
            AlertInformation.viewAlert("ERROR", "Error de Asientos", null, "Error al obtener asientos disponibles: " + e.getMessage());
        }
    }

    @FXML
    private void reserveSeat(ActionEvent event) {
        Screening screening = cmbScreening.getValue();
        Seat seat = cmbSeat.getValue();
        String customer = txtReservationName.getText().trim();

        if (screening == null || seat == null || customer.isEmpty()) {
            AlertInformation.viewAlert("WARNING", "Campos Incompletos", null, "Seleccione la función, el asiento e ingrese el nombre del cliente.");
            return;
        }

        if (customer.length() > 100) {
            AlertInformation.viewAlert("WARNING", "Nombre demasiado largo", null, "El nombre del cliente no puede exceder 100 caracteres.");
            return;
        }

        if (screening.getShowDate() != null && screening.getShowTime() != null
                && LocalDateTime.of(screening.getShowDate(), screening.getShowTime()).isBefore(LocalDateTime.now())) {
            AlertInformation.viewAlert("WARNING", "Función no disponible", null, "No se puede reservar una función que ya comenzó o ya pasó.");
            return;
        }

        User user = currentUser();
        if (user == null) {
            AlertInformation.viewAlert("ERROR", "Sesión", null, "Sesión no válida. Inicie sesión de nuevo.");
            return;
        }

        String sql = "{CALL sp_insert_reservation(?, ?, ?, ?, ?)}";
        try (Connection conn = DatabaseConnection.getDatabaseConnectionInstance().getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.setInt(1, user.getIdUser());
            cstmt.setInt(2, screening.getScreeningId());
            cstmt.setInt(3, seat.getSeatId());
            cstmt.setString(4, "RESERVED");
            cstmt.setString(5, customer);
            cstmt.executeUpdate();

            AlertInformation.viewAlert("INFORMATION", "Reserva Exitosa", null, "Se ha registrado la reserva para: " + customer);

            loadAvailableSeats(screening.getScreeningId());
            cmbSeat.getSelectionModel().clearSelection();

        } catch (SQLException e) {
            AlertInformation.viewAlert("ERROR", "Error al Reservar", null, "No se pudo guardar la reserva: " + e.getMessage());
            // The seat may have been taken by someone else meanwhile.
            loadAvailableSeats(screening.getScreeningId());
        }
    }

    @FXML
    private void generateTicket(ActionEvent event) {
        String customer = txtReservationName.getText().trim();

        if (customer.isEmpty()) {
            AlertInformation.viewAlert("WARNING", "Nombre Requerido", null, "Ingrese el nombre del cliente para generar el ticket.");
            return;
        }

        processAndShowTicket(customer);
    }

    @FXML
    private void searchAndReprintTicket(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Buscar Ticket");
        dialog.setHeaderText("Búsqueda / Reimpresión de Ticket");
        dialog.setContentText("Ingrese el nombre del cliente:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            if (!name.trim().isEmpty()) {
                processAndShowTicket(name.trim());
            }
        });
    }

    /**
     * Finds the window to navigate in: first from the clicked node, then from
     * the back button, and finally the application's primary stage.
     */
    private Stage resolveStage(ActionEvent event) {
        if (event != null && event.getSource() instanceof javafx.scene.Node) {
            javafx.scene.Node source = (javafx.scene.Node) event.getSource();
            if (source.getScene() != null && source.getScene().getWindow() instanceof Stage) {
                return (Stage) source.getScene().getWindow();
            }
        }
        if (btnBack != null && btnBack.getScene() != null
                && btnBack.getScene().getWindow() instanceof Stage) {
            return (Stage) btnBack.getScene().getWindow();
        }
        return SceneManager.getSceneManagerInstance().getPrimaryStage();
    }

    @FXML
    private void goBackToMenu(ActionEvent event) {
        try {
            Stage stage = resolveStage(event);
            if (stage == null) {
                AlertInformation.viewAlert("ERROR", "Navegación", null, "No se pudo recuperar la ventana principal.");
                return;
            }

            User user = currentUser();
            boolean isAdmin = user != null
                    && user.getRole() != null
                    && user.getRole().is(RoleType.ADMINISTRATOR);

            if (isAdmin) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/cineplex/system/view/Admin.fxml"));
                Parent root = loader.load();

                AdminController controller = loader.getController();
                if (controller != null) {
                    controller.setLoggedUser(user);
                }

                stage.setScene(new Scene(root, 480, 620));
                stage.setTitle("Panel de Administrador - CinePlex");
                stage.centerOnScreen();
            } else {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/cineplex/system/view/Billboard.fxml"));
                Parent root = loader.load();

                BillboardController controller = loader.getController();
                if (controller != null) {
                    controller.setLoggedUser(user);
                }

                stage.setScene(new Scene(root, 700, 500));
                stage.setTitle("CinePlex - Cartelera");
                stage.centerOnScreen();
            }

        } catch (Exception e) {
            e.printStackTrace();
            AlertInformation.viewAlert("ERROR", "Navegación", null, "No se pudo volver: " + e.getMessage());
        }
    }

    private void processAndShowTicket(String customer) {
        TicketData ticketData = null;
        String sql = "{CALL sp_get_ticket_by_customer(?)}";

        try (Connection conn = DatabaseConnection.getDatabaseConnectionInstance().getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.setString(1, customer);

            try (ResultSet rs = cstmt.executeQuery()) {
                if (rs.next()) {
                    ticketData = new TicketData();
                    ticketData.setTicketNumber(rs.getInt("ticket_number"));
                    ticketData.setMovieTitle(rs.getString("movie_title"));
                    ticketData.setAuditoriumName(rs.getString("auditorium_name"));
                    ticketData.setSeatNumber(rs.getInt("seat_number"));
                    ticketData.setUserName(rs.getString("customer_name"));
                    ticketData.setShowDate(rs.getDate("show_date").toLocalDate());
                    ticketData.setShowTime(rs.getTime("show_time").toLocalTime());
                    ticketData.setIssueDate(rs.getTimestamp("issue_date").toLocalDateTime());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            AlertInformation.viewAlert("ERROR", "Error de Ticket", null, "No se pudo consultar el ticket: " + e.getMessage());
            return;
        }

        if (ticketData == null) {
            AlertInformation.viewAlert("WARNING", "Ticket no encontrado", null,
                    "No se encontró ninguna reserva activa a nombre de: " + customer);
            return;
        }

        openTicketWindow(ticketData);
    }

    private void openTicketWindow(TicketData data) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/cineplex/system/view/TicketView.fxml"));
            Parent root = loader.load();

            TicketController controller = loader.getController();
            if (controller != null) {
                controller.initData(data);
            }

            Stage ticketStage = new Stage();
            ticketStage.setTitle("CinePlex Studio - Comprobante de Ticket");
            ticketStage.initModality(Modality.APPLICATION_MODAL);
            ticketStage.setScene(new Scene(root));
            ticketStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            AlertInformation.viewAlert("ERROR", "Error de Vista", null, "No se pudo abrir el ticket: " + e.getMessage());
        }
    }
}