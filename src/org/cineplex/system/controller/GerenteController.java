package org.cineplex.system.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.cineplex.system.model.User;

public class GerenteController {

    @FXML
    private Label lblBienvenida;
    private User usuarioLogueado;

    public void setUsuarioLogueado(User user) {
        this.usuarioLogueado = user;
        lblBienvenida.setText("Bienvenido, " + user.getUserName());
    }

    @FXML
    public void abrirCartelera() {
        try {
            String fxmlPath = "/org/cineplex/system/view/MovieRegister.fxml";
            java.net.URL fxmlLocation = getClass().getResource(fxmlPath);
            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent root = loader.load();
            Stage stage = (Stage) lblBienvenida.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestión de Cartelera - CinePlex");
        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensaje("Error de Navegación", "No se pudo cargar la vista:\n" + e.getMessage());
        }
    }

    @FXML
    public void consultarReservas() {
        try {
            String fxmlPath = "/org/cineplex/system/view/ReservationConsultation.fxml";
            java.net.URL fxmlLocation = getClass().getResource(fxmlPath);
            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent root = loader.load();
            Stage stage = (Stage) lblBienvenida.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("CinePlex - Consultar Reservas");
        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensaje("Error de Navegación", "No se pudo cargar la vista:\n" + e.getMessage());
        }
    }

    @FXML
    public void cerrarSesion() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/cineplex/system/view/Login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) lblBienvenida.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("CinePlex - Iniciar Sesión");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mostrarMensaje(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
}