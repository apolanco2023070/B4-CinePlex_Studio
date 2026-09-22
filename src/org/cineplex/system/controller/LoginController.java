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
    private TextField usernameField; // Antes: campoUsuario

    @FXML
    private PasswordField passwordField; // Antes: campoPassword

    @FXML
    private Label lblErrorMessage; // Antes: mensajeError

    private final AuthService authService = new AuthService();

    @FXML
    public void handleLogin() { // Antes: iniciarSesion
        String username = usernameField.getText();
        String password = passwordField.getText();

        // 1. Intentar login como ADMINISTRATOR
        AuthService.ResultadoLogin loginResult = authService.login(username, password, RoleType.ADMINISTRATOR);

        if (loginResult.exito) {
            lblErrorMessage.setText("");
            openAdministratorModule(loginResult.usuario);
        } else {
            // 2. Si falla, intentar login como MANAGER
            loginResult = authService.login(username, password, RoleType.MANAGER);

            if (loginResult.exito) {
                lblErrorMessage.setText("");
                openManagerModule(loginResult.usuario);
            } else {
                // 3. Si ambos fallan, mostrar el mensaje de error
                lblErrorMessage.setText(loginResult.mensaje);
            }
        }
    }

    private void openAdministratorModule(User loggedUser) { // Antes: abrirModuloAdministrador
        try {
            System.out.println("HU1: Access granted to ADMINISTRATOR: " + loggedUser);

            // Asegúrate de que el archivo FXML se llame Administrator.fxml 
            // (o cámbialo a "Administrador.fxml" si no renombraste el archivo)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/cineplex/system/view/Administrator.fxml"));
            Parent root = loader.load();

            AdministradorController controller = loader.getController();
            controller.setUsuarioLogueado(loggedUser); // Antes: setUsuarioLogueado

            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Administrator Panel - CinePlex");
        } catch (Exception e) {
            lblErrorMessage.setText("Error loading module: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void openManagerModule(User loggedUser) { // Antes: abrirModuloGerente
        try {
            System.out.println("HU2: Access granted to MANAGER: " + loggedUser);

            // Asegúrate de que el archivo FXML se llame Manager.fxml
            // (o cámbialo a "Gerente.fxml" si no renombraste el archivo)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/cineplex/system/view/Manager.fxml"));
            Parent root = loader.load();

            ManagerController controller = loader.getController();
            controller.setLoggedUser(loggedUser);

            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Manager Panel - CinePlex");
        } catch (Exception e) {
            lblErrorMessage.setText("Error loading module: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
