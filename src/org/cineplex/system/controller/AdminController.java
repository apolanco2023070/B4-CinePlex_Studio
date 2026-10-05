package org.cineplex.system.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.cineplex.system.model.User;
import org.cineplex.system.utils.Session;

public class AdminController {

    private static final String VIEW_PATH = "/org/cineplex/system/view/";

    @FXML
    private Label lblWelcome;
    private User loggedUser;

    /**
     * Runs on every load of Admin.fxml, so the welcome text and the session
     * survive navigating to other screens and coming back.
     */
    @FXML
    public void initialize() {
        User user = Session.getCurrentUser();
        if (user != null) {
            this.loggedUser = user;
            lblWelcome.setText("Bienvenido, " + user.getUserName());
        }
    }

    public void setLoggedUser(User user) {
        if (user == null) {
            return;
        }
        this.loggedUser = user;
        lblWelcome.setText("Bienvenido, " + user.getUserName());
    }

    /**
     * Loads a view into the current window and returns its controller.
     */
    private Object navigate(String fxmlName, String title) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(VIEW_PATH + fxmlName));
        Parent root = loader.load();
        Stage stage = (Stage) lblWelcome.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle(title);
        return loader.getController();
    }

    private void showNavigationError(Exception e) {
        e.printStackTrace();
        showMessage(Alert.AlertType.ERROR, "Error de Navegación", "No se pudo cargar la vista:\n" + e.getMessage());
    }

    @FXML
    public void openMovieRegister() {
        try {
            navigate("MovieRegister.fxml", "Gestión de Películas - CinePlex");
        } catch (Exception e) {
            showNavigationError(e);
        }
    }

    @FXML
    public void openUserManagement() {
        try {
            navigate("UserManagement.fxml", "Gestión de Usuarios - CinePlex");
        } catch (Exception e) {
            showNavigationError(e);
        }
    }

    @FXML
    public void openReports() {
        showMessage(Alert.AlertType.INFORMATION, "Reportes", "Módulo de Reportes abierto");
    }

    @FXML
    public void manageSeatsAndAuditoriums() {
        try {
            navigate("SeatsAndRoomsManagment.fxml", "CinePlex - Gestión de Salas y Asientos");
        } catch (Exception e) {
            showNavigationError(e);
        }
    }

    @FXML
    public void manageScreening() {
        try {
            navigate("ScreeningView.fxml", "CinePlex - Gestión de Funciones");
        } catch (Exception e) {
            showNavigationError(e);
        }
    }

    @FXML
    public void seatReservation() {
        try {
            Object controller = navigate("SeatReservation.fxml", "CinePlex - Reserva de Asientos");
            if (controller instanceof SeatReservationController) {
                ((SeatReservationController) controller).setLoggedUser(this.loggedUser);
            }
        } catch (Exception e) {
            showNavigationError(e);
        }
    }

    @FXML
    public void consultScreenings() {
        try {
            navigate("ScreeningConsultation.fxml", "CinePlex - Consultar Funciones");
        } catch (Exception e) {
            showNavigationError(e);
        }
    }

    @FXML
    public void logout() {
        try {
            Session.clear();
            navigate("Login.fxml", "CinePlex - Iniciar Sesión");
        } catch (Exception e) {
            e.printStackTrace();
            showMessage(Alert.AlertType.ERROR, "Error", "No se pudo cerrar la sesión:\n" + e.getMessage());
        }
    }

    private void showMessage(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
