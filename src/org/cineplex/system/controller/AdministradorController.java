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

public class AdministradorController {

    @FXML
    private Label lblBienvenida;
    private Usuario usuarioLogueado;

    public void setUsuarioLogueado(Usuario usuario) {
        this.usuarioLogueado = usuario;
        lblBienvenida.setText("Bienvenido, " + usuario.getNombreUsuario());
    }

    @FXML
    public void abrirCartelera() {
        System.out.println("HU3: Administrador accede a Cartelera - PERMITIDO");
        mostrarMensaje("Cartelera", "Módulo de Cartelera abierto (Gestión completa)");
    }

    @FXML
    public void abrirUsuarios() {
        System.out.println("HU3: Administrador accede a Gestión de Usuarios - PERMITIDO");
        mostrarMensaje("Usuarios", "Módulo de Gestión de Usuarios abierto");
    }

    @FXML
    public void abrirReportes() {
        System.out.println("HU3: Administrador accede a Reportes - PERMITIDO");
        mostrarMensaje("Reportes", "Módulo de Reportes abierto");
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
