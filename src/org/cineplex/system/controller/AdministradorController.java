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
        lblWelcome.setText("Bienvenido, " + user.getUserName());
    }

    @FXML
    public void abrirCartelera() {
        try {
            String fxmlPath = "/org/cineplex/system/view/MovieRegister.fxml";
            java.net.URL fxmlLocation = getClass().getResource(fxmlPath);
            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent root = loader.load();

            Stage stage = (Stage) lblWelcome.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestión de Cartelera - CinePlex");
        } catch (Exception e) {
            System.err.println("Error inesperado al cargar la vista:");
            e.printStackTrace();
            mostrarMensaje("Error de Navegación", "No se pudo cargar la vista:\n" + e.getMessage());
        }
    }

    @FXML
    public void abrirUsuarios() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/cineplex/system/view/UserManagement.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) lblWelcome.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestión de Usuarios - CinePlex");
        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensaje("Error", "No se pudo cargar la pantalla de usuarios.");
        }
    }

    @FXML
    public void abrirReportes() {
        System.out.println("HU3: Administrador accede a Reportes - PERMITIDO");
        mostrarMensaje("Reportes", "Módulo de Reportes abierto");
    }

    @FXML
    public void manageSeatsAndAuditoriums() {
        try {
            String fxmlPath = "/org/cineplex/system/view/SeatsAndRoomsManagment.fxml";
            java.net.URL fxmlLocation = getClass().getResource(fxmlPath);
            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent root = loader.load();

            Stage stage = (Stage) lblWelcome.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("CinePlex - Gestión de Salas y Asientos");
        } catch (Exception e) {
            System.err.println("Error inesperado al cargar la vista:");
            e.printStackTrace();
            mostrarMensaje("Error de Navegación", "No se pudo cargar la vista:\n" + e.getMessage());
        }
    }

    @FXML
    public void manageScreening() {
        try {
            String fxmlPath = "/org/cineplex/system/view/ScreeningView.fxml";
            java.net.URL fxmlLocation = getClass().getResource(fxmlPath);
            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent root = loader.load();

            Stage stage = (Stage) lblWelcome.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("CinePlex - Gestión de Cartelera");
        } catch (Exception e) {
            System.err.println("Error inesperado al cargar la vista:");
            e.printStackTrace();
            mostrarMensaje("Error de Navegación", "No se pudo cargar la vista:\n" + e.getMessage());
        }
    }

    @FXML
    public void seatReservation() {
        try {
            String fxmlPath = "/org/cineplex/system/view/SeatReservation.fxml";
            java.net.URL fxmlLocation = getClass().getResource(fxmlPath);
            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent root = loader.load();

            Stage stage = (Stage) lblWelcome.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("CinePlex - Gestión de Cartelera");
        } catch (Exception e) {
            System.err.println("Error inesperado al cargar la vista:");
            e.printStackTrace();
            mostrarMensaje("Error de Navegación", "No se pudo cargar la vista:\n" + e.getMessage());
        }
    }

    @FXML
    public void cerrarSesion() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/cineplex/system/view/Login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) lblWelcome.getScene().getWindow();
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

    @FXML
    public void consultarFunciones() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/cineplex/system/view/ScreeningConsultation.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) lblWelcome.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("CinePlex - Consultar Funciones");
        } catch (Exception e) {
            System.err.println("Error al cargar la vista:");
            e.printStackTrace();
            mostrarMensaje("Error de Navegación", "No se pudo cargar la vista:\n" + e.getMessage());
        }
    }
}
