/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cineplex.system.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import model.Usuario;
import service.AuthService;

public class LoginController {

    @FXML private TextField campoUsuario;
    @FXML private PasswordField campoPassword;
    @FXML private Label mensajeError;

    private final AuthService authService = new AuthService();

    @FXML
    public void iniciarSesion() {
        String usuario = campoUsuario.getText();
        String password = campoPassword.getText();

        AuthService.ResultadoLogin resultado = authService.login(usuario, password, "Administrador");

        if (resultado.exito) {
            mensajeError.setText("");
            abrirModuloCartelera(resultado.usuario);
        } else {
            mensajeError.setText(resultado.mensaje);
        }
    }

    private void abrirModuloCartelera(Usuario usuarioLogueado) {
        System.out.println("Acceso concedido a: " + usuarioLogueado);
        // TODO: cargar Cartelera.fxml (se implementa junto con HU7/HU8)
    }
}
