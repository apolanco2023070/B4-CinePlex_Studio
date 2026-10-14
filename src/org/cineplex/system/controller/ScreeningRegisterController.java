package org.cineplex.system.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
            @Override
            public String toString(Movie m) {
                return m == null ? "" : m.getTitle();
            }

            @Override
            public Movie fromString(String s) {
                return null;
            }
        });
        cmbAuditoriums.setConverter(new javafx.util.StringConverter<Auditorium>() {
            @Override
            public String toString(Auditorium a) {
                return a == null ? "" : a.getName();
            }

            @Override
            public Auditorium fromString(String s) {
                return null;
            }
        });
    }

    private void loadMovies() {
        try {
            cmbMovies.setItems(FXCollections.observableArrayList(movieRepository.getAllMovies()));
        } catch (Exception e) {
            AlertInformation.viewAlert("ERROR", "Error de carga", "No se pudieron cargar las películas", e.getMessage());
        }
    }

    private void loadAuditoriums() {
        try {
            cmbAuditoriums.setItems(FXCollections.observableArrayList(auditoriumRepository.getAuditoriums()));
        } catch (Exception e) {
            AlertInformation.viewAlert("ERROR", "Error de carga", "No se pudieron cargar las salas", e.getMessage());
        }
    }

    /**
     * True if the new screening overlaps another one in the same room and date,
     * taking each movie's duration into account.
     */
    private boolean overlapsExisting(Movie movie, Auditorium auditorium, LocalDate date, LocalTime time) {
        Map<Integer, Integer> durationByMovie = new HashMap<>();
        for (Movie m : cmbMovies.getItems()) {
            durationByMovie.put(m.getMovieId(), m.getDuration());
        }

        LocalDateTime newStart = LocalDateTime.of(date, time);
        LocalDateTime newEnd = newStart.plusMinutes(movie.getDuration());

        List<Screening> existing = screeningRepository.getAllScreening();
        for (Screening s : existing) {
            if (s.getAuditoriumId() == null
                    || s.getAuditoriumId().intValue() != auditorium.getAuditoriumId().intValue()
                    || !date.equals(s.getShowDate())) {
                continue;
            }
            int duration = durationByMovie.getOrDefault(s.getMovieId(), 0);
            LocalDateTime start = LocalDateTime.of(s.getShowDate(), s.getShowTime());
            LocalDateTime end = start.plusMinutes(duration);

            if (newStart.isBefore(end) && start.isBefore(newEnd)) {
                return true;
            }
        }
        return false;
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
            // "9:30" is accepted by the regex but LocalTime.parse needs "09:30".
            String[] parts = timeText.split(":");
            LocalTime time = LocalTime.of(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));

            if (LocalDateTime.of(date, time).isBefore(LocalDateTime.now())) {
                AlertInformation.viewAlert("ERROR", "Fecha inválida", "Validación", "No se puede programar una función en el pasado.");
                return;
            }

            if (overlapsExisting(selectedMovie, selectedAuditorium, date, time)) {
                AlertInformation.viewAlert("ERROR", "Conflicto de horario", "Validación",
                        "La sala ya tiene una función que se traslapa con ese horario.");
                return;
            }

            Screening screening = new Screening(selectedMovie.getMovieId(), selectedAuditorium.getAuditoriumId(), date, time);
            screeningRepository.saveScreening(screening);

            AlertInformation.viewAlert("INFORMATION", "Éxito", "Función registrada", "La función se programó correctamente.");

            if (onScreeningSaved != null) {
                onScreeningSaved.run();
            }

            ((Stage) btnSave.getScene().getWindow()).close();

        } catch (Exception e) {
            String message = e.getMessage() == null ? "" : e.getMessage();
            if (message.contains("Duplicate entry")) {
                message = "Ya existe una función en esa sala, fecha y hora.";
            }
            AlertInformation.viewAlert("ERROR", "Error", "No se pudo guardar", message);
        }
    }

    @FXML
    private void cancel() {
        ((Stage) btnCancel.getScene().getWindow()).close();
    }

}
