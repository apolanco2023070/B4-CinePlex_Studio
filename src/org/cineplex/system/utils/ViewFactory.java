/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cineplex.system.utils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import org.cineplex.system.MainClass;

/**
 *
 * @author informatica
 */
public class ViewFactory {
    private final String PATH_VIEWS = "/org/saulmartinez/system/view/";

    public Scene loadFileFXML(String nameFXML, int width, int height) {
        String filePath = PATH_VIEWS + nameFXML;
        try {
            FXMLLoader loader = new FXMLLoader();

            URL urlFile = MainClass.class.getResource(filePath);
            loader.setBuilderFactory(new JavaFXBuilderFactory());
            loader.setLocation(urlFile);

            return new Scene(loader.load(), width, height);

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

    }

    public void loadScene(String FXMLname) {
        Scene scene = null;
        try {
            switch (FXMLname) {
                case "register" -> {
                    SceneManager.getInstanciaSceneManager().getPrimaryStage().setTitle("Registro de Peliculas");
                    SceneManager.getInstanciaSceneManager().getPrimaryStage().setResizable(false);
                    scene = loadFileFXML("MovieRegister.fxml", 650, 400);
                }

                default ->

                    scene = loadFileFXML("MovieRegister.fxml", 300, 400);
            }
            SceneManager.getInstanciaSceneManager().changeScene(scene);
        } catch (NullPointerException objetonulo) {
            System.out.println("error loading the scene");
        }
    }

    public void viewLogin() {
        loadScene("login");
    }
    
    public void viewMovieRegister(){
        loadScene("register");
    }
}
