package org.cineplex.system.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.cineplex.system.model.RoleType;
import org.cineplex.system.model.User;
import org.cineplex.system.service.AuthService;
import org.cineplex.system.utils.Session;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label lblErrorMessage;

    private final AuthService authService = new AuthService();

    @FXML
    public void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        AuthService.LoginResult loginResult = authService.authenticate(username, password);

        if (!loginResult.success) {
            lblErrorMessage.setText(loginResult.message);
            return;
        }

        lblErrorMessage.setText("");
        User user = loginResult.user;
        Session.setCurrentUser(user);

        if (user.getRole().getRoleType() == RoleType.ADMINISTRATOR) {
            openAdministratorModule(user);
        } else {
            openManagerModule(user);
        }
    }

    private void openAdministratorModule(User loggedUser) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/cineplex/system/view/Admin.fxml"));
            Parent root = loader.load();

            AdminController controller = loader.getController();
            controller.setLoggedUser(loggedUser);

            Stage stage = (Stage) usernameField.getScene().getWindow();

            stage.setScene(new Scene(root, 480, 620));
            stage.setTitle("Panel de Administrador - CinePlex");
            stage.centerOnScreen();
        } catch (Exception e) {
            lblErrorMessage.setText("Error al cargar el módulo de Administrador: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void openManagerModule(User loggedUser) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/cineplex/system/view/Manager.fxml"));
            Parent root = loader.load();

            ManagerController controller = loader.getController();
            controller.setLoggedUser(loggedUser);

            Stage stage = (Stage) usernameField.getScene().getWindow();

            stage.setScene(new Scene(root, 480, 500));
            stage.setTitle("Panel de Gerente - CinePlex");
            stage.centerOnScreen();
        } catch (Exception e) {
            lblErrorMessage.setText("Error al cargar el módulo de Gerente: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
