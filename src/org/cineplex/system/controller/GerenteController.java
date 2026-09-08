/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cineplex.system.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.cineplex.system.model.Usuario;

public class GerenteController {

    @FXML
    private Label lblBienvenida;
    private Usuario usuarioLogueado;

    public void setUsuarioLogueado(Usuario usuario) {
        this.usuarioLogueado = usuario;
        lblBienvenida.setText("Bienvenido, " + usuario.getNombreUsuario());
    }

    @FXML
    public void verCartelera() {
        System.out.println("HU3: Gerente accede a Cartelera - PERMITIDO (Solo lectura)");
        mostrarMensaje("Cartelera", "Viendo cartelera (solo lectura)");
    }

    @FXML
    public void verVentas() {
        System.out.println("HU3: Gerente accede a Ventas - PERMITIDO");
        mostrarMensaje("Ventas", "Viendo ventas del día");
    }

    @FXML
    public void intentarAccederGestionUsuarios() {
        System.out.println("HU3: Gerente intenta acceder a Gestión de Usuarios - DENEGADO");
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Acceso Denegado");
        alert.setHeaderText("Permisos Insuficientes");
        alert.setContentText("Solo el Administrador puede gestionar usuarios.");
        alert.showAndWait();
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
