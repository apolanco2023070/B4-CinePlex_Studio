package org.cineplex.system.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.cineplex.system.repository.UserFactory;
import org.cineplex.system.utils.AlertInformation;
import org.cineplex.system.utils.Validations;

public class UserManagementController {

    /** role_id of MANAGER in the 'role' table (see ddl.sql initial data). */
    private static final int MANAGER_ROLE_ID = 2;

    @FXML
    private TextField txtFullName;

    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private TextField txtEmail;

    @FXML
    private Label lblMessage;

    private final UserFactory userFactory = new UserFactory();
    private final Validations validations = new Validations();

    @FXML
    public void registerManager() {
        String fullName = txtFullName.getText().trim();
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();
        String email = txtEmail.getText().trim();

        if (validations.emptyText(fullName) || validations.emptyText(username)
                || validations.emptyText(password) || validations.emptyText(email)) {
            showError("Todos los campos son obligatorios.");
            return;
        }

        if (!validations.validateLengthText(fullName, 100)
                || !validations.validateLengthText(username, 50)
                || !validations.validateLengthText(email, 150)) {
            showError("Nombre (máx 100), usuario (máx 50) o correo (máx 150) exceden la longitud permitida.");
            return;
        }

        if (password.length() < 6) {
            showError("La contraseña debe tener al menos 6 caracteres.");
            return;
        }

        if (!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            showError("El correo electrónico no es válido.");
            return;
        }

        try {
            userFactory.registerUser(fullName, username, password, email, MANAGER_ROLE_ID);
            AlertInformation.viewAlert("INFORMATION", "Éxito", null, "Gerente registrado correctamente.");
            clearForm();
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        } catch (RuntimeException e) {
            showError("No se pudo registrar el gerente. " + e.getMessage());
        }
    }

    @FXML
    public void goBackToPanel() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/cineplex/system/view/Admin.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) lblMessage.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Panel de Administrador - CinePlex");
        } catch (Exception e) {
            e.printStackTrace();
            showError("No se pudo volver al panel: " + e.getMessage());
        }
    }

    private void clearForm() {
        txtFullName.clear();
        txtUsername.clear();
        txtPassword.clear();
        txtEmail.clear();
        lblMessage.setText("");
    }

    private void showError(String message) {
        AlertInformation.viewAlert("ERROR", "Error", null, message);
    }
}
