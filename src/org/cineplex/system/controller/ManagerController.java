package org.cineplex.system.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.cineplex.system.model.User;

public class ManagerController {

    @FXML
    private Label lblWelcome;

    private User loggedUser;

    public void setLoggedUser(User user) {
        this.loggedUser = user;
        lblWelcome.setText("Welcome, " + user.getUserName());
    }

    @FXML
    public void viewBoxOffice() {
        System.out.println("HU3: Manager accesses Box Office - ALLOWED (Read-only)");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/cineplex/system/view/Cartelera.fxml")); // O BoxOffice.fxml si también renombraste el archivo
            Parent root = loader.load();

            CarteleraController controller = loader.getController();
            controller.setLoggedUser(loggedUser);

            Stage stage = (Stage) lblWelcome.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Box Office - CinePlex");
        } catch (Exception e) {
            e.printStackTrace();
            showMessage("Error", "Could not load box office: " + e.getMessage());
        }
    }

    @FXML
    public void viewSales() {
        System.out.println("HU3: Manager accesses Sales - ALLOWED");
        showMessage("Sales", "Viewing daily sales");
    }

    @FXML
    public void attemptAccessUserManagement() {
        System.out.println("HU3: Manager attempts to access User Management - DENIED");
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Access Denied");
        alert.setHeaderText("Insufficient Permissions");
        alert.setContentText("Only the Administrator can manage users.");
        alert.showAndWait();
    }

    @FXML
    public void logout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/cineplex/system/view/Login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) lblWelcome.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("CinePlex - Login");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showMessage(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
