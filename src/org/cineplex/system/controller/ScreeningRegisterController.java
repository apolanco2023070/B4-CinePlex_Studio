/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cineplex.system.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.cineplex.system.model.Auditorium;
import org.cineplex.system.model.Movie;
import org.cineplex.system.model.Screening;
import org.cineplex.system.repository.AuditoriumRepository;
import org.cineplex.system.repository.MovieRepository;
import org.cineplex.system.repository.ScreeningRepository;
import org.cineplex.system.utils.AlertInformation;
import org.cineplex.system.utils.Validations;

/**
 *
 * @author informatica
 */
public class ScreeningRegisterController {

    @FXML
    private ComboBox<Movie> cmbMovies;

    @FXML
    private ComboBox<Auditorium> cmbAuditoriums;

    @FXML
    private DatePicker dpDate;

    @FXML
    private TextField txtTime;

    @FXML
    private Button btnSave;

    @FXML
    private Button btnCancel;

    private final ScreeningRepository screeningRepository;
    private final MovieRepository movieRepository;
    private final AuditoriumRepository auditoriumRepository;
    private final Validations validations;

    private Runnable onScreeningSaved;

    public ScreeningRegisterController() {
        this.screeningRepository = new ScreeningRepository();
        this.movieRepository = new MovieRepository();
        this.auditoriumRepository = new AuditoriumRepository();
        this.validations = new Validations();
    }

    @FXML
    public void initialize() {
        configureComboBoxes();
        loadMovies();
        loadAuditoriums();
    }

    public void setOnScreeningSaved(Runnable callback) {
        this.onScreeningSaved = callback;
    }

    private void configureComboBoxes() {
        cmbMovies.setConverter(new javafx.util.StringConverter<Movie>() {
            public String toString(Movie m) {
                return m == null ? "" : m.getTitle();
            }

            public Movie fromString(String s) {
                return null;
            }
        });
        cmbAuditoriums.setConverter(new javafx.util.StringConverter<Auditorium>() {
            public String toString(Auditorium a) {
                return a == null ? "" : a.getName();
            }

            public Auditorium fromString(String s) {
                return null;
            }
        });
    }

    private void loadMovies() {
        try {
            cmbMovies.setItems(FXCollections.observableArrayList(movieRepository.getAllMovies()));
        } catch (Exception e) {
            System.err.println("Error al cargar películas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadAuditoriums() {
        cmbAuditoriums.setItems(FXCollections.observableArrayList(auditoriumRepository.getAuditoriums()));
    }

    @FXML
    private void saveScreening() {
        Movie selectedMovie = cmbMovies.getValue();
        Auditorium selectedAuditorium = cmbAuditoriums.getValue();
        LocalDate date = dpDate.getValue();
        String timeText = txtTime.getText().trim();

        if (selectedMovie == null || selectedAuditorium == null || date == null || validations.emptyText(timeText)) {
            AlertInformation.viewAlert("ERROR", "Campos incompletos", "Validación", "Todos los campos son obligatorios.");
            return;
        }

        if (!timeText.matches("^([01]?[0-9]|2[0-3]):[0-5][0-9]$")) {
            AlertInformation.viewAlert("ERROR", "Formato de hora inválido", "Validación", "Use el formato HH:mm (ej: 14:30)");
            return;
        }

        try {
            LocalTime time = LocalTime.parse(timeText);
            Screening screening = new Screening(selectedMovie.getMovieId(), selectedAuditorium.getAuditoriumId(), date, time);

            screeningRepository.saveScreening(screening);

            AlertInformation.viewAlert("INFORMATION", "Éxito", "Función Registrada", "La función se programó correctamente.");

            if (onScreeningSaved != null) {
                onScreeningSaved.run();
            }

            ((Stage) btnSave.getScene().getWindow()).close();

        } catch (Exception e) {
            AlertInformation.viewAlert("ERROR", "Error", "No se pudo guardar", e.getMessage());
        }
    }

    @FXML
    private void cancel() {
        ((Stage) btnCancel.getScene().getWindow()).close();
    }

}
