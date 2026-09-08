/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package org.cineplex.system;

import javafx.application.Application;
import javafx.stage.Stage;
import org.cineplex.system.utils.SceneManager;
import org.cineplex.system.utils.ViewFactory;

/**
 *
 * @author Polanco
 */
public class MainClass extends Application {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        launch(args);
    }

    @Override
    public void start(Stage stageRoot) {
        
        SceneManager.getSceneManagerInstance().setPrimaryStage(stageRoot);
        ViewFactory viewFactory = new ViewFactory();
        viewFactory.viewMovieRegister();
    }
}
