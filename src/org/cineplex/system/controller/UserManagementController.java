package org.cineplex.system.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.cineplex.system.repository.UsuarioFactory;
import org.cineplex.system.utils.Validations;

public class UserManagementController {

    @FXML
    private TextField txtFullName;
    @FXML
    private TextField txtUsername;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private TextField txtEmail;
    @FXML
    private Label lblMensaje;

    private final UsuarioFactory usuarioDAO = new UsuarioFactory();
    private final Validations validaciones = new Validations();

    @FXML
    public void registrarGerente() {
        // Obtener valores
        String fullName = txtFullName.getText().trim();
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();
        String email = txtEmail.getText().trim();

        // Validaciones básicas
        if (validaciones.emptyText(fullName) || validaciones.emptyText(username) || 
            validaciones.emptyText(password) || validaciones.emptyText(email)) {
            mostrarAlerta("Error", "Todos los campos son obligatorios.");
            return;
        }

        if (password.length() < 6) {
            mostrarAlerta("Error", "La contraseña debe tener al menos 6 caracteres.");
            return;
        }

        if (!email.contains("@") || !email.contains(".")) {
            mostrarAlerta("Error", "El email no es válido.");
            return;
        }

        // Intentar registrar (role_id = 2 es MANAGER/Gerente)
        boolean exito = usuarioDAO.registrarUsuario(fullName, username, password, email, 2);

        if (exito) {
            mostrarAlerta("Éxito", "Gerente registrado correctamente.");
            limpiarFormulario();
        } else {
            mostrarAlerta("Error", "No se pudo registrar. El usuario o email ya existe.");
        }
    }

    @FXML
    public void volverPanel() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/cineplex/system/view/Administrador.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) lblMensaje.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Panel Administrador - CinePlex");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void limpiarFormulario() {
        txtFullName.clear();
        txtUsername.clear();
        txtPassword.clear();
        txtEmail.clear();
        lblMensaje.setText("");
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}