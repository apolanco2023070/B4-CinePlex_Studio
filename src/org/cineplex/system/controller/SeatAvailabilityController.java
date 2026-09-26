package org.cineplex.system.controller;

import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.cineplex.system.model.Reservation;
import org.cineplex.system.model.Screening;
import org.cineplex.system.model.Seat;
import org.cineplex.system.model.User; // ✅ CORREGIDO: Usa User, NO Usuario
import org.cineplex.system.repository.ReservationRepository;
import org.cineplex.system.repository.ScreeningRepository;
import org.cineplex.system.repository.SeatRepository;
import org.cineplex.system.utils.AlertInformation;

public class SeatAvailabilityController {

    @FXML private ComboBox<Screening> cmbFunciones;
    @FXML private Button btnConsultar;
    @FXML private Button btnReservar;
    @FXML private Button btnRegresar;
    @FXML private Label lblFuncion;
    @FXML private TableView<Seat> tblAsientos;
    @FXML private TableColumn<Seat, Integer> colNumero;
    @FXML private TableColumn<Seat, String> colEstado;

    private final ScreeningRepository screeningRepository;
    private final SeatRepository seatRepository;
    private final ReservationRepository reservationRepository;

    private User usuarioLogueado; // ✅ CORREGIDO: Usa User, NO Usuario

    public SeatAvailabilityController() {
        this.screeningRepository = new ScreeningRepository();
        this.seatRepository = new SeatRepository();
        this.reservationRepository = new ReservationRepository();
    }

    public void setUsuarioLogueado(User usuario) { // ✅ CORREGIDO: Usa User
        this.usuarioLogueado = usuario;
    }

    @FXML
    public void initialize() {
        configureTable();
        configureComboBox();
        cargarFunciones();
    }

    private void configureTable() {
        colNumero.setCellValueFactory(new PropertyValueFactory<>("seatNumber"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void configureComboBox() {
        cmbFunciones.setCellFactory(param -> new ListCell<Screening>() {
            @Override
            protected void updateItem(Screening screening, boolean empty) {
                super.updateItem(screening, empty);
                setText(empty || screening == null ? null : formatScreening(screening));
            }
        });
        cmbFunciones.setButtonCell(new ListCell<Screening>() {
            @Override
            protected void updateItem(Screening screening, boolean empty) {
                super.updateItem(screening, empty);
                setText(empty || screening == null ? null : formatScreening(screening));
            }
        });
    }

    private String formatScreening(Screening screening) {
        return screening.getMovieTitle() + " - " + screening.getAuditoriumName()
                + " (" + screening.getShowDate() + " " + screening.getShowTime() + ")";
    }

    private void cargarFunciones() {
        try {
            List<Screening> screenings = screeningRepository.getAllScreenings();
            ObservableList<Screening> items = FXCollections.observableArrayList(screenings);
            cmbFunciones.setItems(items);
        } catch (Exception e) {
            AlertInformation.viewAlert("ERROR", "Error", "No se pudieron cargar las funciones", e.getMessage());
        }
    }

    @FXML
    private void consultarDisponibilidad() {
        Screening screening = cmbFunciones.getValue();
        if (screening == null) {
            AlertInformation.viewAlert("WARNING", "Atención", "Sin selección", "Selecciona una función.");
            return;
        }
        cargarAsientos(screening.getScreeningId());
    }

    private void cargarAsientos(int screeningId) {
        try {
            tblAsientos.getItems().clear();
            List<Seat> seats = seatRepository.findAvailabilityByScreening(screeningId);
            tblAsientos.setItems(FXCollections.observableArrayList(seats));
            tblAsientos.refresh();
        } catch (Exception e) {
            AlertInformation.viewAlert("ERROR", "Error", "No se pudo consultar la disponibilidad", e.getMessage());
        }
    }

    @FXML
    private void reservarAsiento() {
        Screening screening = cmbFunciones.getValue();
        Seat seat = tblAsientos.getSelectionModel().getSelectedItem();

        if (screening == null) {
            AlertInformation.viewAlert("WARNING", "Atención", "Sin función", "Selecciona una función primero.");
            return;
        }
        if (seat == null) {
            AlertInformation.viewAlert("WARNING", "Atención", "Sin asiento", "Selecciona un asiento de la tabla.");
            return;
        }
        if (!"Disponible".equals(seat.getStatus())) {
            AlertInformation.viewAlert("WARNING", "Atención", "Asiento no disponible", "Ese asiento ya está reservado.");
            return;
        }
        if (usuarioLogueado == null) {
            AlertInformation.viewAlert("ERROR", "Error", "Sesión inválida", "No hay un usuario logueado. Por favor, cierra sesión y vuelve a iniciar.");
            return;
        }

        try {
            Reservation reservation = new Reservation(
                    usuarioLogueado.getIdUser(), // ✅ CORREGIDO: Usa getIdUser(), NO getIdUsuario()
                    screening.getScreeningId(),
                    seat.getSeatId()
            );
            reservationRepository.createReservation(reservation);

            AlertInformation.viewAlert("INFORMATION", "Éxito", "Asiento reservado",
                    "El asiento " + seat.getSeatNumber() + " fue reservado correctamente.");

            cargarAsientos(screening.getScreeningId());

        } catch (Exception e) {
            AlertInformation.viewAlert("ERROR", "Error", "No se pudo reservar el asiento", e.getMessage());
        }
    }

    @FXML
    private void regresarMenu() {
        try {
            Stage stageActual = (Stage) btnRegresar.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/cineplex/system/view/Gerente.fxml"));
            Parent root = loader.load();
            GerenteController controller = loader.getController();
            
            // ✅ Protección: solo pasa el usuario si no es null
            if (this.usuarioLogueado != null) {
                controller.setUsuarioLogueado(this.usuarioLogueado);
            }
            
            Scene escenaNueva = new Scene(root);
            stageActual.setScene(escenaNueva);
            stageActual.show();
        } catch (Exception e) {
            e.printStackTrace();
            AlertInformation.viewAlert("ERROR", "Error de navegación", "No se pudo cargar la vista", e.getMessage());
        }
    }
}