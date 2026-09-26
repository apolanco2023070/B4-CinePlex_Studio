package org.cineplex.system.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.cineplex.system.model.User;

public class AdministradorController {

    @FXML
    private Label lblWelcome;
    
    private User usuarioLogueado;

    public void setUsuarioLogueado(User user) {
        this.usuarioLogueado = user;
        if (lblWelcome != null) {
            lblWelcome.setText("Bienvenido, " + user.getUserName());
        }
    }

    @FXML
    public void abrirCartelera() {
        navegarA("/org/cineplex/system/view/MovieRegister.fxml", "Gestión de Cartelera - CinePlex");
    }

    @FXML
    public void abrirUsuarios() {
        navegarA("/org/cineplex/system/view/UserManagement.fxml", "Gestión de Usuarios - CinePlex");
    }

    @FXML
    public void abrirReportes() {
        System.out.println("HU3: Administrador accede a Reportes - PERMITIDO");
        mostrarMensaje("Reportes", "Módulo de Reportes abierto");
    }

    @FXML
    public void manageSeatsAndAuditoriums() {
        navegarA("/org/cineplex/system/view/SeatsAndRoomsManagment.fxml", "CinePlex - Gestión de Salas y Asientos");
    }

    @FXML
    public void manageScreening() {
        navegarA("/org/cineplex/system/view/ScreeningView.fxml", "CinePlex - Gestión de Funciones");
    }

    @FXML
    public void seatReservation() {
        navegarA("/org/cineplex/system/view/SeatReservation.fxml", "CinePlex - Reservar Asientos");
    }

    @FXML
    public void consultarFunciones() {
        navegarA("/org/cineplex/system/view/ScreeningConsultation.fxml", "CinePlex - Consultar Funciones");
    }

    @FXML
    public void cerrarSesion() {
        navegarA("/org/cineplex/system/view/Login.fxml", "CinePlex - Iniciar Sesión");
    }

    private void navegarA(String fxmlPath, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) lblWelcome.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(titulo);
        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensaje("Error de Navegación", "No se pudo cargar la vista:\n" + e.getMessage());
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