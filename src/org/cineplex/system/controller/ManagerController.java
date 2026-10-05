package org.cineplex.system.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.cineplex.system.model.User;
import org.cineplex.system.utils.AlertInformation;
import org.cineplex.system.utils.Session;

public class ManagerController {

    @FXML
    private Label lblWelcome;

    private User loggedUser;
    private final AlertInformation alertInfo = new AlertInformation();

    @FXML
    public void initialize() {
        User user = Session.getCurrentUser();
        if (user != null) {
            setLoggedUser(user);
        }
    }

    /**
     * Sets the user session, verifying it is not null
     */
    public void setLoggedUser(User user) {
        this.loggedUser = user;
        if (lblWelcome != null && user != null) {
            lblWelcome.setText("Bienvenido, " + user.getUserName());
        }
    }

    @FXML
    public void viewBillboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/cineplex/system/view/Billboard.fxml"));
            Parent root = loader.load();

            BillboardController controller = loader.getController();
            if (controller != null) {
                controller.setLoggedUser(this.loggedUser);
            }

            Stage stage = (Stage) lblWelcome.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("CinePlex - Cartelera");
        } catch (Exception e) {
            e.printStackTrace();
            alertInfo.viewAlert("ERROR", "Error de Navegación", "No se pudo abrir la Cartelera", e.getMessage());
        }
    }

    @FXML
    public void consultReservations() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/cineplex/system/view/ReservationConsultation.fxml"));
            Parent root = loader.load();

            ReservationConsultationController controller = loader.getController();
            if (controller != null) {
                controller.setLoggedUser(this.loggedUser);
            }

            Stage stage = (Stage) lblWelcome.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("CinePlex - Gestión de Reservas");
        } catch (Exception e) {
            e.printStackTrace();
            alertInfo.viewAlert("ERROR", "Error de Navegación", "No se pudo abrir la gestión de reservas", e.getMessage());
        }
    }

    @FXML
    public void logout() {
        try {
            Session.clear();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/cineplex/system/view/Login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) lblWelcome.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("CinePlex - Inicio de Sesión");
        } catch (Exception e) {
            e.printStackTrace();
            alertInfo.viewAlert("ERROR", "Error de Navegación", "No se pudo cerrar sesión", e.getMessage());
        }
    }
}