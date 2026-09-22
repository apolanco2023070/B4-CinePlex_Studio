/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cineplex.system.controller;

import java.time.LocalDate;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.cineplex.system.model.Screening;
import org.cineplex.system.repository.ScreeningRepository;
import org.cineplex.system.utils.AlertInformation;

/**
 *
 * @author informatica
 */
public class ScreeningListController {

    @FXML
    private TableView<Screening> tblScreenings;

    @FXML
    private TableColumn<Screening, String> colMovie;

    @FXML
    private TableColumn<Screening, String> colAuditorium;

    @FXML
    private TableColumn<Screening, LocalDate> colDate;

    @FXML
    private TableColumn<Screening, String> colTime;

    @FXML
    private Button btnViewSeats;

    private final ScreeningRepository screeningRepository;
    private ObservableList<Screening> allScreenings;

    public ScreeningListController() {
        this.screeningRepository = new ScreeningRepository();
    }

    @FXML
    public void initialize() {
        configureTable();
        loadAllScreenings();
    }

    private void configureTable() {
        colMovie.setCellValueFactory(new PropertyValueFactory<>("movieTitle"));
        colAuditorium.setCellValueFactory(new PropertyValueFactory<>("auditoriumName"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("showDate"));

        colTime.setCellValueFactory(new PropertyValueFactory<>("showTimeString"));
    }

    private void loadAllScreenings() {
        try {
            System.out.println("Intentando cargar funciones...");

            List<Screening> screenings = screeningRepository.getAllScreening();
            System.out.println("Funciones encontradas: " + screenings.size());

            allScreenings = FXCollections.observableArrayList(screenings);
            tblScreenings.setItems(allScreenings);

            System.out.println("Tabla actualizada correctamente");

        } catch (Exception e) {
            e.printStackTrace();

            AlertInformation.viewAlert(
                    "ERROR",
                    "Error al cargar funciones",
                    "Detalle: " + e.getClass().getSimpleName(),
                    e.getMessage()
            );
        }
    }

    @FXML
    private void addScreening() {
        try {
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/cineplex/system/view/ScreeningRegisterView.fxml"));
            Parent root = loader.load();

            ScreeningRegisterController registerController = loader.getController();

            registerController.setOnScreeningSaved(() -> {
                loadAllScreenings(); 
            });

            Stage stage = new Stage();
            stage.setTitle("Agregar Nueva Función");
            stage.setScene(new Scene(root, 500, 400));
            stage.setResizable(false);
            stage.showAndWait();

        } catch (Exception e) {
            AlertInformation.viewAlert("ERROR", "Error", "No se pudo abrir el formulario", e.getMessage());
            e.printStackTrace();
        }
    }
    
}
