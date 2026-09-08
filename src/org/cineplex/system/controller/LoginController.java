/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cineplex.system.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.cineplex.system.model.TypeRol; 
import org.cineplex.system.model.Usuario;
import org.cineplex.system.service.AuthService;

public class LoginController {

    @FXML
    private TextField campoUsuario;

    @FXML
    private PasswordField campoPassword;

    @FXML
    private Label mensajeError;

    private final AuthService authService = new AuthService();

    @FXML
    public void iniciarSesion() {
        String usuario = campoUsuario.getText();
        String password = campoPassword.getText();

    
        AuthService.ResultadoLogin resultado = authService.login(usuario, password, TypeRol.ADMINISTRATOR);

        if (resultado.exito) {
            mensajeError.setText("");
            abrirModuloAdministrador(resultado.usuario);
        } else {
           
            resultado = authService.login(usuario, password, TypeRol.MANAGER);

            if (resultado.exito) {
                mensajeError.setText("");
                abrirModuloGerente(resultado.usuario);
            } else {
                mensajeError.setText(resultado.mensaje);
            }
        }
    }

    private void abrirModuloAdministrador(Usuario usuarioLogueado) {
        try {
            System.out.println("HU1: Acceso concedido a ADMINISTRATOR: " + usuarioLogueado);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/cineplex/system/view/Administrador.fxml"));
            Parent root = loader.load();

            AdministradorController controller = loader.getController();
            controller.setUsuarioLogueado(usuarioLogueado);

            Stage stage = (Stage) campoUsuario.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Panel Administrador - CinePlex");
        } catch (Exception e) {
            mensajeError.setText("Error al cargar el módulo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void abrirModuloGerente(Usuario usuarioLogueado) {
        try {
            System.out.println("HU2: Acceso concedido a MANAGER: " + usuarioLogueado);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/cineplex/system/view/Gerente.fxml"));
            Parent root = loader.load();

            GerenteController controller = loader.getController();
            controller.setUsuarioLogueado(usuarioLogueado);

            Stage stage = (Stage) campoUsuario.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Panel Gerente - CinePlex");
        } catch (Exception e) {
            mensajeError.setText("Error al cargar el módulo: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
