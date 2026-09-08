/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package org.cineplex.system;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ClasePrincipal extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        try {
        
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/cineplex/system/view/Login.fxml"));
            Parent root = loader.load();
            
            Scene scene = new Scene(root);
            
            primaryStage.setTitle("CinePlex - Iniciar Sesión");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();
            
        } catch (Exception e) {
            System.err.println("ERROR: No se pudo cargar Login.fxml");
            System.err.println("Ruta buscada: /view/Login.fxml");
            System.err.println("Detalle: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
