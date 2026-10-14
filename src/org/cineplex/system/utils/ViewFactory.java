package org.cineplex.system.utils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.cineplex.system.MainClass;

public class ViewFactory {

    private final String PATH_VIEWS = "/org/cineplex/system/view/";

    public Scene loadFileFXML(String nameFXML, int width, int height) {
        String filePath = PATH_VIEWS + nameFXML;
        try {
            FXMLLoader loader = new FXMLLoader();

            URL urlFile = MainClass.class.getResource(filePath);
            if (urlFile == null) {
                throw new IllegalStateException("No se encontró la vista: " + filePath);
            }
            loader.setBuilderFactory(new JavaFXBuilderFactory());
            loader.setLocation(urlFile);

            return new Scene(loader.load(), width, height);

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void prepareStage(String title) {
        Stage stage = SceneManager.getSceneManagerInstance().getPrimaryStage();
        stage.setTitle(title);
        stage.setResizable(false);
    }

    public void loadScene(String FXMLname) {
        Scene scene;
        try {
            switch (FXMLname) {
                case "register" -> {
                    prepareStage("Registro de Películas");
                    scene = loadFileFXML("MovieRegister.fxml", 700, 500);
                }
                case "login" -> {
                    prepareStage("Inicio de sesión");
                    scene = loadFileFXML("Login.fxml", 400, 380);
                }
                case "seatManagment" -> {
                    prepareStage("Gestión de Salas y Asientos");
                    scene = loadFileFXML("SeatsAndRoomsManagment.fxml", 700, 500);
                }
                case "screeningView" -> {
                    prepareStage("Gestión de Funciones");
                    scene = loadFileFXML("ScreeningView.fxml", 650, 450);
                }
                case "seatReservation" -> {
                    prepareStage("Reserva de Asientos");
                    scene = loadFileFXML("SeatReservation.fxml", 700, 500);
                }
                default -> {
                    prepareStage("Inicio de sesión");
                    scene = loadFileFXML("Login.fxml", 400, 380);
                }
            }
            SceneManager.getSceneManagerInstance().changeScene(scene);
        } catch (RuntimeException e) {
            System.err.println("Error loading the scene '" + FXMLname + "': " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void viewMovieRegister() {
        loadScene("register");
    }

    public void viewLogin() {
        loadScene("login");
    }

    public void viewSeatsAndAuditoriumManagment() {
        loadScene("seatManagment");
    }

    public void viewScreeningView() {
        loadScene("screeningView");
    }

    public void viewSeatReservation() {
        loadScene("seatReservation");
    }
}
