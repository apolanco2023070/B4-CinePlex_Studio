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

        AuthService.ResultadoLogin loginResult = authService.login(username, password, RoleType.ADMINISTRATOR);

        if (loginResult.exito) {
            lblErrorMessage.setText("");
            openAdministratorModule(loginResult.usuario);
        } else {
            loginResult = authService.login(username, password, RoleType.MANAGER);
            if (loginResult.exito) {
                lblErrorMessage.setText("");
                openManagerModule(loginResult.usuario);
            } else {
                lblErrorMessage.setText(loginResult.mensaje);
            }
        }
    }

    private void openAdministratorModule(User loggedUser) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/cineplex/system/view/Administrador.fxml"));
            Parent root = loader.load();
            AdministradorController controller = loader.getController();
            controller.setUsuarioLogueado(loggedUser);
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Administrator Panel - CinePlex");
        } catch (Exception e) {
            lblErrorMessage.setText("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ✅ AQUÍ ESTÁ LA CORRECCIÓN CLAVE
    private void openManagerModule(User loggedUser) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/cineplex/system/view/Gerente.fxml"));
            Parent root = loader.load();
            
            // ✅ CAMBIO 1: GerenteController en lugar de ManagerController
            GerenteController controller = loader.getController();
            
            // ✅ CAMBIO 2: setUsuarioLogueado en lugar de setLoggedUser
            controller.setUsuarioLogueado(loggedUser);
            
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Panel Gerente - CinePlex");
        } catch (Exception e) {
            lblErrorMessage.setText("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}