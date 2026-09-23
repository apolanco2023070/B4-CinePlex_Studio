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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
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
    private Button btnRegresar;

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

        colTime.setCellValueFactory(data -> data.getValue().showTimeProperty());
    }

    private void loadAllScreenings() {
        try {
            System.out.println("Intentando cargar funciones...");

            List<Screening> screenings = screeningRepository.getAllScreenings();
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

    @FXML
    private void eliminarFuncion() {
        Screening seleccionada = tblScreenings.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            AlertInformation.viewAlert("WARNING", "Atención", "Sin selección",
                    "Selecciona una función de la tabla para eliminarla.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Seguro que deseas eliminar la función de \""
                + seleccionada.getMovieTitle() + "\" (" + seleccionada.getShowDate()
                + " " + seleccionada.getShowTime() + ")? Esta acción no se puede deshacer.");

        confirmacion.showAndWait().ifPresent(respuesta -> {
            if (respuesta == ButtonType.OK) {
                try {
                    screeningRepository.deleteScreening(seleccionada.getScreeningId());
                    AlertInformation.viewAlert("INFORMATION", "Éxito", "Función eliminada",
                            "La función se eliminó correctamente.");
                    loadAllScreenings();
                } catch (Exception e) {
                    AlertInformation.viewAlert("ERROR", "Error", "No se pudo eliminar la función", e.getMessage());
                }
            }
        });
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
                    "Detalle: " + e.getMessage() + "\nVerifica la ruta del archivo Administrador.fxml en el código Java.");
        }
    }

}
