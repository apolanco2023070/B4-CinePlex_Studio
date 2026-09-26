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
    @FXML
    private Button btnRegresar;
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
    
    // ✅ Límite máximo de asientos por sala
    private static final int MAX_CAPACITY = 250;

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
                System.out.println("Sala seleccionada: " + newValue.getName() + " (ID: " + newValue.getAuditoriumId() + ")");
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
            AlertInformation.viewAlert("ERROR", "Error", "Load Error", "Could not load auditoriums: " + e.getMessage());
        }
    }

    @FXML
    private void saveAuditorium() {
        String auditoriumName = "Room " + (cmbRooms.getItems().size() + 1);
        String capacityText = txtCapacity.getText().trim();
        
        // ✅ VALIDACIÓN 1: Verificar que la capacidad no exceda 250
        if (validations.emptyText(capacityText) || !validations.isPositiveNumber(capacityText)) {
            AlertInformation.viewAlert("ERROR", "Capacidad Inválida", "Validación", 
                "La capacidad debe ser un número mayor a 0.");
            return;
        }
        
        int capacity = Integer.parseInt(capacityText);
        if (capacity > MAX_CAPACITY) {
            AlertInformation.viewAlert("ERROR", "Capacidad Excedida", "Límite de Asientos", 
                "La capacidad máxima permitida es de " + MAX_CAPACITY + " asientos. " +
                "Por favor, ingrese un valor menor o igual a " + MAX_CAPACITY + ".");
            return;
        }
        
        try {
            Auditorium auditorium = new Auditorium(auditoriumName, capacity);
            auditoriumRepository.saveAuditorium(auditorium);
            AlertInformation.viewAlert("INFORMATION", "Éxito", "Sala Registrada", 
                "La sala fue registrada exitosamente con capacidad para " + capacity + " asientos.");
            txtCapacity.clear();
            loadAuditoriums();
        } catch (Exception e) {
            AlertInformation.viewAlert("ERROR", "Error al Guardar", "No se pudo guardar la sala", e.getMessage());
        }
    }

    @FXML
    private void generateSeats() {
        Auditorium selectedAuditorium = cmbRooms.getValue();
        
        if (selectedAuditorium == null) {
            AlertInformation.viewAlert("WARNING", "Sin Selección", "Atención", 
                "Por favor, seleccione una sala del combo box.");
            return;
        }
        
        // ✅ VALIDACIÓN 2: Verificar que la capacidad de la sala no exceda 250
        if (selectedAuditorium.getCapacity() > MAX_CAPACITY) {
            AlertInformation.viewAlert("ERROR", "Capacidad Excedida", "Límite de Asientos", 
                "La sala \"" + selectedAuditorium.getName() + "\" tiene una capacidad de " + 
                selectedAuditorium.getCapacity() + " asientos, lo cual excede el límite máximo de " + 
                MAX_CAPACITY + " asientos. No se pueden generar los asientos.");
            return;
        }
        
        if (seatRepository.existsByAuditoriumId(selectedAuditorium.getAuditoriumId())) {
            AlertInformation.viewAlert("WARNING", "Asientos Existentes", "Atención", 
                "Esta sala ya tiene asientos generados.");
            return;
        }
        
        try {
            int capacity = selectedAuditorium.getCapacity();
            createSeatsForAuditorium(selectedAuditorium.getAuditoriumId(), capacity);
            AlertInformation.viewAlert("INFORMATION", "Éxito", "Asientos Generados",
                "Se generaron " + capacity + " asientos para " + selectedAuditorium.getName());
            loadSeats(selectedAuditorium.getAuditoriumId());
        } catch (Exception e) {
            AlertInformation.viewAlert("ERROR", "Error de Generación", "No se pudieron generar los asientos", e.getMessage());
        }
    }

    private void createSeatsForAuditorium(Integer auditoriumId, int capacity) {
        for (int i = 1; i <= capacity; i++) {
            Seat seat = new Seat(i, auditoriumId);
            seatRepository.saveSeat(seat);
        }
    }

    private void loadSeats(Integer auditoriumId) {
        try {
            tblSeats.getItems().clear();
            List<Seat> seatsFromDB = seatRepository.findByAuditoriumId(auditoriumId);
            System.out.println("Asientos encontrados en BD: " + seatsFromDB.size());
            ObservableList<Seat> seats = FXCollections.observableArrayList(seatsFromDB);
            tblSeats.setItems(seats);
            tblSeats.refresh();
        } catch (Exception e) {
            AlertInformation.viewAlert("ERROR", "Error de Carga", "No se pudieron cargar los asientos", e.getMessage());
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
            AlertInformation.viewAlert("ERROR", "Error de Navegación", "No se pudo cargar la vista",
                "Detalle: " + e.getMessage() + "\nVerifica la ruta del archivo Administrador.fxml en el código Java.");
        }
    }
}