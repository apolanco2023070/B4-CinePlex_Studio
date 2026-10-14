package org.cineplex.system;

import javafx.application.Application;
import javafx.collections.ListChangeListener;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.cineplex.system.utils.SceneManager;
import org.cineplex.system.utils.ViewFactory;

/**
 *
 * @author Polanco
 */
public class MainClass extends Application {

    private static final String STYLESHEET = "/org/cineplex/system/resources/styles/cineplex.css";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stageRoot) {
        registerGlobalStylesheet();

        SceneManager.getSceneManagerInstance().setPrimaryStage(stageRoot);
        ViewFactory viewFactory = new ViewFactory();
        viewFactory.viewLogin();
    }

  
    private void registerGlobalStylesheet() {
        java.net.URL resource = getClass().getResource(STYLESHEET);
        if (resource == null) {
            System.err.println("Stylesheet not found: " + STYLESHEET);
            return;
        }
        final String css = resource.toExternalForm();

        Window.getWindows().addListener((ListChangeListener<Window>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    for (Window window : change.getAddedSubList()) {
                        applyStylesheet(window.getScene(), css);
                        if (window.getProperties().putIfAbsent("cineplexStyled", Boolean.TRUE) == null) {
                            window.sceneProperty().addListener((obs, oldScene, newScene) -> applyStylesheet(newScene, css));
                        }
                    }
                }
            }
        });
    }

    private void applyStylesheet(Scene scene, String css) {
        if (scene != null && !scene.getStylesheets().contains(css)) {
            scene.getStylesheets().add(css);
        }
    }
}