package org.cineplex.system.controller;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.cineplex.system.model.Auditorium;
import org.cineplex.system.model.Seat;
import org.cineplex.system.repository.AuditoriumRepository;
import org.cineplex.system.repository.SeatRepository;
import org.cineplex.system.utils.AlertInformation;
import org.cineplex.system.utils.Validations;

public class SeatsController {

    private static final int MAX_CAPACITY = 500;
    private static final Pattern ROOM_NUMBER = Pattern.compile("^(?:Sala|Room) (\\d+)$");

    @FXML
    private Button btnBack;

    @FXML
    private ComboBox<Auditorium> cmbRooms;

    @FXML
    private TextField txtCapacity;

    @FXML
    private TableView<Seat> tblSeats;

    @FXML
    private TableColumn<Seat, Integer> colNumber;

    @FXML
    private TableColumn<Seat, String> colStatus;

    private final AuditoriumRepository auditoriumRepository;
    private final SeatRepository seatRepository;
    private final Validations validations;

    public SeatsController() {
        this.auditoriumRepository = new AuditoriumRepository();
        this.seatRepository = new SeatRepository();
        this.validations = new Validations();
    }

    @FXML
    public void initialize() {
        configureTable();
        configureComboBox();
        loadAuditoriums();
    }

    private void configureTable() {
        colNumber.setCellValueFactory(new PropertyValueFactory<>("seatNumber"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void configureComboBox() {
        cmbRooms.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                loadSeats(newValue.getAuditoriumId());
            }
        });
    }

    private void loadAuditoriums() {
        try {
            ObservableList<Auditorium> auditoriums = FXCollections.observableArrayList(
                    auditoriumRepository.getAuditoriums()
            );
            cmbRooms.setItems(auditoriums);
        } catch (Exception e) {
            AlertInformation.viewAlert("ERROR", "Error", "Error de carga", "No se pudieron cargar las salas: " + e.getMessage());
        }
    }

    /**
     * Next free room name ("Sala N"), based on the highest number already used,
     * so it never collides with the UNIQUE constraint on auditorium.name.
     */
    private String nextAuditoriumName() {
        int max = 0;
        for (Auditorium a : cmbRooms.getItems()) {
            if (a.getName() == null) {
                continue;
            }
            Matcher m = ROOM_NUMBER.matcher(a.getName().trim());
            if (m.matches()) {
                max = Math.max(max, Integer.parseInt(m.group(1)));
            }
        }
        return "Sala " + (max + 1);
    }

    @FXML
    private void saveAuditorium() {
        String capacityText = txtCapacity.getText().trim();

        if (validations.emptyText(capacityText) || !validations.isPositiveNumber(capacityText)) {
            AlertInformation.viewAlert("ERROR", "Capacidad inválida", "Validación", "La capacidad debe ser un número entero mayor a 0.");
            return;
        }

        int capacity = Integer.parseInt(capacityText);
        if (capacity > MAX_CAPACITY) {
            AlertInformation.viewAlert("ERROR", "Capacidad inválida", "Validación", "La capacidad máxima por sala es " + MAX_CAPACITY + " asientos.");
            return;
        }

        try {
            Auditorium auditorium = new Auditorium(nextAuditoriumName(), capacity);
            auditoriumRepository.saveAuditorium(auditorium);

            AlertInformation.viewAlert("INFORMATION", "Éxito", "Sala registrada", "La sala se registró correctamente.");
            txtCapacity.clear();
            loadAuditoriums();

        } catch (Exception e) {
            AlertInformation.viewAlert("ERROR", "Error al guardar", "No se pudo guardar la sala", e.getMessage());
        }
    }

    @FXML
    private void generateSeats() {
        Auditorium selectedAuditorium = cmbRooms.getValue();

        if (selectedAuditorium == null) {
            AlertInformation.viewAlert("WARNING", "Sin selección", "Atención", "Seleccione una sala de la lista.");
            return;
        }

        try {
            if (seatRepository.existsByAuditoriumId(selectedAuditorium.getAuditoriumId())) {
                AlertInformation.viewAlert("WARNING", "Asientos existentes", "Atención", "Esta sala ya tiene asientos generados.");
                return;
            }

            int capacity = selectedAuditorium.getCapacity();
            seatRepository.saveSeats(selectedAuditorium.getAuditoriumId(), capacity);

            AlertInformation.viewAlert("INFORMATION", "Éxito", "Asientos generados",
                    "Se generaron " + capacity + " asientos para " + selectedAuditorium.getName());

            loadSeats(selectedAuditorium.getAuditoriumId());

        } catch (Exception e) {
            AlertInformation.viewAlert("ERROR", "Error de generación", "No se pudieron generar los asientos", e.getMessage());
        }
    }

    private void loadSeats(Integer auditoriumId) {
        try {
            tblSeats.getItems().clear();

            List<Seat> seatsFromDB = seatRepository.findByAuditoriumId(auditoriumId);

            ObservableList<Seat> seats = FXCollections.observableArrayList(seatsFromDB);
            tblSeats.setItems(seats);
            tblSeats.refresh();

        } catch (Exception e) {
            AlertInformation.viewAlert("ERROR", "Error de carga", "No se pudieron cargar los asientos", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void goBackToMenu() {
        try {
            Stage currentStage = (Stage) btnBack.getScene().getWindow();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/cineplex/system/view/Admin.fxml"));
            Parent root = loader.load();

            currentStage.setScene(new Scene(root));
            currentStage.setTitle("Panel de Administrador - CinePlex");
            currentStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            AlertInformation.viewAlert("ERROR", "Error de navegación", "No se pudo cargar la vista",
                    "Detalle: " + e.getMessage() + "\nVerifica la ruta del archivo Admin.fxml");
        }
    }
}
