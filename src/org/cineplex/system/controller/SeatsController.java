/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cineplex.system.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.cineplex.system.model.Auditorium;
import org.cineplex.system.model.Seat;
import org.cineplex.system.repository.AuditoriumRepository;
import org.cineplex.system.repository.SeatRepository;
import org.cineplex.system.utils.AlertInformation;
import org.cineplex.system.utils.Validations;

/**
 *
 * @author informatica
 */
public class SeatsController {

    @FXML
    private Button btnRegisterRoom;
    
    @FXML
    private Button btnRegisterSeats;
    
    @FXML
    private ComboBox<Auditorium> cmbRooms;
    
    @FXML
    private Label lblCapacity;
    
    @FXML
    private Label lblRooms;
    
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
            AlertInformation.viewAlert("ERROR", "Error", "Load Error", "Could not load auditoriums: " + e.getMessage());
        }
    }

    @FXML
    private void saveAuditorium() {
        String auditoriumName = "Room " + (cmbRooms.getItems().size() + 1);
        String capacityText = txtCapacity.getText().trim();

        if (validations.emptyText(capacityText) || !validations.isPositiveNumber(capacityText)) {
            AlertInformation.viewAlert("ERROR", "Invalid Capacity", "Validation", "Capacity must be a number greater than 0.");
            return;
        }

        try {
            Auditorium auditorium = new Auditorium(auditoriumName, Integer.parseInt(capacityText));
            auditoriumRepository.saveAuditorium(auditorium);

            AlertInformation.viewAlert("INFORMATION", "Success", "Auditorium Registered", "The auditorium was registered successfully.");
            txtCapacity.clear();
            loadAuditoriums();

        } catch (Exception e) {
            AlertInformation.viewAlert("ERROR", "Save Error", "Could not save auditorium", e.getMessage());
        }
    }

    @FXML
    private void generateSeats() {
        Auditorium selectedAuditorium = cmbRooms.getValue();

        if (selectedAuditorium == null) {
            AlertInformation.viewAlert("WARNING", "No Selection", "Attention", "Please select an auditorium from the combo box.");
            return;
        }

        if (seatRepository.existsByAuditoriumId(selectedAuditorium.getAuditoriumId())) {
            AlertInformation.viewAlert("WARNING", "Seats Exist", "Attention", "This auditorium already has generated seats.");
            return;
        }

        try {
            int capacity = selectedAuditorium.getCapacity();
            createSeatsForAuditorium(selectedAuditorium.getAuditoriumId(), capacity);

            AlertInformation.viewAlert("INFORMATION", "Success", "Seats Generated",
                    capacity + " seats were generated for " + selectedAuditorium.getName());

            loadSeats(selectedAuditorium.getAuditoriumId());

        } catch (Exception e) {
            AlertInformation.viewAlert("ERROR", "Generation Error", "Could not generate seats", e.getMessage());
        }
    }

    private void createSeatsForAuditorium(Integer auditoriumId, int capacity) {
        int seatsPerRow = 10;
        int seatNumber = 1;
        char row = 'A';

        for (int i = 0; i < capacity; i++) {
            Seat seat = new Seat(seatNumber, auditoriumId); 
            seatRepository.saveSeat(seat);

            seatNumber++;
            if (seatNumber > seatsPerRow) {
                seatNumber = 1;
                row++;
            }
        }
    }

    private void loadSeats(Integer auditoriumId) {
        try {
            ObservableList<Seat> seats = FXCollections.observableArrayList(
                    seatRepository.findByAuditoriumId(auditoriumId)
            );
            tblSeats.setItems(seats);
        } catch (Exception e) {
            AlertInformation.viewAlert("ERROR", "Load Error", "Could not load seats", e.getMessage());
        }
    }
}
