/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cineplex.system.controller;

import java.time.LocalDate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
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
        colTime.setCellValueFactory(cellData
                -> new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getShowTime().toString()
                )
        );
    }

    private void loadAllScreenings() {
        try {
            allScreenings = FXCollections.observableArrayList(
                    screeningRepository.getAllScreening()
            );
            tblScreenings.setItems(allScreenings);
        } catch (Exception e) {
            AlertInformation.viewAlert("ERROR", "Error", "No se pudieron cargar las funciones", e.getMessage());
        }
    }

    @FXML
    private void viewSeats() {
        Screening selected = tblScreenings.getSelectionModel().getSelectedItem();

        if (selected == null) {
            AlertInformation.viewAlert("WARNING", "Sin selección",
                    "Selecciona una función primero", "");
            return;
        }

        AlertInformation.viewAlert("INFORMATION", "Ver Asientos",
                "Función seleccionada",
                "Película: " + selected.getMovieTitle() + "\n"
                + "Sala: " + selected.getAuditoriumName() + "\n"
                + "Fecha: " + selected.getShowDate() + "\n"
                + "Hora: " + selected.getShowTime());

    }

    @FXML
    private void addScreening() {
        try {
            // Cargar el FXML de registro
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/cineplex/system/view/ScreeningRegisterView.fxml"));
            Parent root = loader.load();

            // Obtener el controller del formulario de registro
            ScreeningRegisterController registerController = loader.getController();

            // Configurar callback para recargar la tabla cuando se guarde
            registerController.setOnScreeningSaved(() -> {
                loadAllScreenings(); // Recargar la tabla de funciones
            });

            // Crear y mostrar la ventana
            Stage stage = new Stage();
            stage.setTitle("Agregar Nueva Función");
            stage.setScene(new Scene(root, 500, 400));
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL); // Bloquea la ventana padre hasta cerrar
            stage.showAndWait(); // Espera a que se cierre para continuar

        } catch (Exception e) {
            AlertInformation.viewAlert("ERROR", "Error", "No se pudo abrir el formulario", e.getMessage());
            e.printStackTrace();
        }
    }

}
