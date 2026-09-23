package org.cineplex.system.controller;

import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.cineplex.system.model.Auditorium;
import org.cineplex.system.model.Movie;
import org.cineplex.system.model.Screening;
import org.cineplex.system.model.Seat;
import org.cineplex.system.model.TicketData;
import org.cineplex.system.repository.AuditoriumRepository;
import org.cineplex.system.repository.MovieRepository;
import org.cineplex.system.repository.ReservationRepository;
import org.cineplex.system.repository.ScreeningRepository;
import org.cineplex.system.repository.SeatRepository;
import org.cineplex.system.utils.AlertInformation;

public class SeatReservationController {

    @FXML
    private ComboBox<Auditorium> cmbAuditorium;

    @FXML
    private ComboBox<Seat> cmbSeat;

    @FXML
    private ComboBox<Movie> cmbMovie;

    @FXML
    private ComboBox<Screening> cmbScreening;

    @FXML
    private Button btnRegresar;
    
    @FXML
    private TextField txtReservationName;

    private final AuditoriumRepository auditoriumRepository;
    private final MovieRepository movieRepository;
    private final ScreeningRepository screeningRepository;
    private final SeatRepository seatRepository;
    private final ReservationRepository reservationRepository;
    private Integer lastReservationId;
    private Screening lastScreening;
    private Seat lastSeat;
    private Auditorium lastAuditorium;
    private Movie lastMovie;
    private String lastClienteName;

    public SeatReservationController() {
        this.auditoriumRepository = new AuditoriumRepository();
        this.movieRepository = new MovieRepository();
        this.screeningRepository = new ScreeningRepository();
        this.seatRepository = new SeatRepository();
        this.reservationRepository = new ReservationRepository();
    }

    @FXML
    public void initialize() {
        configureComboBoxes();
        loadAuditoriums();
        loadMovies();
    }

    private void configureComboBoxes() {
        cmbMovie.valueProperty().addListener((obs, oldVal, newVal) -> loadScreenings());
        cmbAuditorium.valueProperty().addListener((obs, oldVal, newVal) -> loadScreenings());
        cmbScreening.valueProperty().addListener((obs, oldVal, newVal) -> loadAvailableSeats());
    }

    private void loadAuditoriums() {
        try {
            List<Auditorium> auditoriums = auditoriumRepository.getAuditoriums();
            cmbAuditorium.setItems(FXCollections.observableArrayList(auditoriums));
        } catch (Exception e) {
            showAlert("Error", "No se pudieron cargar las salas");
        }
    }

    private void loadMovies() {
        try {
            List<Movie> movies = movieRepository.getAllMovies();
            cmbMovie.setItems(FXCollections.observableArrayList(movies));
        } catch (Exception e) {
            showAlert("Error", "No se pudieron cargar las películas");
        }
    }

    private void loadScreenings() {
        Movie selectedMovie = cmbMovie.getValue();
        Auditorium selectedAuditorium = cmbAuditorium.getValue();

        cmbScreening.getItems().clear();
        cmbSeat.getItems().clear();

        if (selectedMovie == null || selectedAuditorium == null) {
            return;
        }

        try {
            List<Screening> allScreenings = screeningRepository.getAllScreening();
            List<Screening> filtered = new ArrayList<>();

            for (Screening s : allScreenings) {
                if (s.getMovieId() != null && s.getAuditoriumId() != null
                        && s.getMovieId().equals(selectedMovie.getMovieId())
                        && s.getAuditoriumId().equals(selectedAuditorium.getAuditoriumId())) {
                    filtered.add(s);
                }
            }
            cmbScreening.setItems(FXCollections.observableArrayList(filtered));
        } catch (Exception e) {
            showAlert("Error", "No se pudieron cargar los horarios");
        }
    }

    private void loadAvailableSeats() {
        Screening selectedScreening = cmbScreening.getValue();
        cmbSeat.getItems().clear();

        if (selectedScreening == null) {
            return;
        }

        try {
            List<Seat> availableSeats = seatRepository.getAvailableSeatsForScreening(selectedScreening.getScreeningId());
            cmbSeat.setItems(FXCollections.observableArrayList(availableSeats));
        } catch (Exception e) {
            showAlert("Error", "No se pudieron cargar los asientos disponibles");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void reserveSeat() {

        if (cmbAuditorium.getValue() == null || cmbMovie.getValue() == null
                || cmbScreening.getValue() == null || cmbSeat.getValue() == null) {
            showAlert("Campos incompletos", "Selecciona todos los campos");
            return;
        }

        String nombreCliente = txtReservationName.getText().trim();
        if (nombreCliente.isEmpty()) {
            showAlert("Campo requerido", "Por favor ingrese el nombre del cliente");
            return;
        }

        try {
            Screening screening = cmbScreening.getValue();
            Seat seat = cmbSeat.getValue();
            Auditorium auditorium = cmbAuditorium.getValue();
            Movie movie = cmbMovie.getValue();
            int currentUserId = 1;

            System.out.println("   - User ID: " + currentUserId);
            System.out.println("   - Screening ID: " + screening.getScreeningId());
            System.out.println("   - Seat ID: " + seat.getSeatId());
            System.out.println("   - Cliente: " + nombreCliente);

            System.out.println(" [DEBUG] Creando reserva en BD...");
            reservationRepository.createReservation(
                    currentUserId,
                    screening.getScreeningId(),
                    seat.getSeatId(),
                    "RESERVED"
            );

            lastReservationId = reservationRepository.getLastReservationId(
                    screening.getScreeningId(),
                    seat.getSeatId()
            );

            System.out.println(" [DEBUG] LastReservationId obtenido: " + lastReservationId);

            if (lastReservationId == null) {
                showAlert("Error", "No se pudo obtener el ID de la reserva. Verifica que el Stored Procedure sp_get_last_reservation_id exista en la BD.");
                return;
            }

            lastScreening = screening;
            lastSeat = seat;
            lastAuditorium = auditorium;
            lastMovie = movie;
            lastClienteName = nombreCliente;

            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Éxito");
            successAlert.setContentText("Asiento " + seat.getSeatNumber()
                    + " reservado correctamente para " + nombreCliente + ".\n\n"
                    + "Ahora puedes generar el ticket.");
            successAlert.showAndWait();

            cmbSeat.getSelectionModel().clearSelection();
            loadAvailableSeats();

        } catch (Exception e) {
            showAlert("Error", "No se pudo realizar la reserva: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void regresarMenu() {
        try {
            Stage stageActual = (Stage) btnRegresar.getScene().getWindow();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/cineplex/system/view/Administrador.fxml"));
            Parent root = loader.load();

            Scene escenaNueva = new Scene(root);

            stageActual.setScene(escenaNueva);
            stageActual.show();

        } catch (Exception e) {
            e.printStackTrace();
            AlertInformation.viewAlert("ERROR", "Error de navegación", "No se pudo cargar la vista",
                    "Detalle: " + e.getMessage() + "\nVerifica la ruta del archivo Administrador.fxml");
        }
    }

    @FXML
    private void generateTicket() {
        if (lastReservationId == null) {
            showAlert("Sin reserva", "Primero debes reservar un asiento antes de generar el ticket.");
            return;
        }

        if (lastScreening == null || lastSeat == null || lastAuditorium == null || lastMovie == null) {
            showAlert("Error", "No se encontraron los datos de la reserva.");
            return;
        }

        if (lastClienteName == null || lastClienteName.trim().isEmpty()) {
            showAlert("Campo requerido", "Por favor ingrese el nombre del cliente");
            return;
        }

        try {
            TicketData ticketData = new TicketData();
            ticketData.setTicketNumber(lastReservationId);
            ticketData.setMovieTitle(lastMovie.getTitle());
            ticketData.setAuditoriumName(lastAuditorium.getName());
            ticketData.setShowDate(lastScreening.getShowDate());
            ticketData.setShowTime(lastScreening.getShowTime());
            ticketData.setSeatNumber(lastSeat.getSeatNumber());
            ticketData.setUserName(lastClienteName);
            ticketData.setIssueDate(java.time.LocalDateTime.now());

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/cineplex/system/view/TicketView.fxml"));
            Parent root = loader.load();

            TicketController controller = loader.getController();
            controller.initData(ticketData);

            Stage stage = new Stage();
            stage.setTitle("Ticket - CinePlex");
            stage.setScene(new Scene(root, 500, 650));
            stage.setResizable(false);
            stage.showAndWait();

        } catch (Exception e) {
            showAlert("Error", "No se pudo generar el ticket: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
