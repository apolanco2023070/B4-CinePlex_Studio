package org.cineplex.system.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.cineplex.system.config.DatabaseConnection;
import org.cineplex.system.model.Auditorium;
import org.cineplex.system.model.Movie;
import org.cineplex.system.repository.AuditoriumRepository;
import org.cineplex.system.repository.MovieRepository;
import javafx.util.StringConverter;

import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class ScreeningConsultationController implements Initializable {

    @FXML
    private ComboBox<String> comboBoxMovies;
    @FXML
    private ComboBox<String> comboBoxAuditoriums;
    @FXML
    private Button btnFilter;
    @FXML
    private Button btnClear;
    @FXML
    private TableView<ScreeningRow> tableViewScreenings;
    @FXML
    private TableColumn<ScreeningRow, String> colMovie;
    @FXML
    private TableColumn<ScreeningRow, String> colAuditorium;
    @FXML
    private TableColumn<ScreeningRow, String> colDate;
    @FXML
    private TableColumn<ScreeningRow, String> colTime;
    @FXML
    private TableColumn<ScreeningRow, String> colDuration;
    @FXML
    private TableColumn<ScreeningRow, Void> colActions;
    @FXML
    private Button btnRefresh;
    @FXML
    private Button btnBack;

    private ObservableList<ScreeningRow> screeningsList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            configureTable();
            loadCombos();
            loadScreenings();
        } catch (Exception e) {
            showError("Error de inicialización", "No se pudo cargar la vista: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void configureTable() {
        colMovie.setCellValueFactory(new PropertyValueFactory<>("movie"));
        colAuditorium.setCellValueFactory(new PropertyValueFactory<>("auditorium"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("time"));
        colDuration.setCellValueFactory(new PropertyValueFactory<>("duration"));

        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button btnEdit = new Button("Editar");

            {
                
                btnEdit.setOnAction(event -> {
                    int index = getIndex();
                    if (index >= 0 && index < getTableView().getItems().size()) {
                        editScreening(getTableView().getItems().get(index));
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnEdit);
            }
        });

        screeningsList = FXCollections.observableArrayList();
        tableViewScreenings.setItems(screeningsList);
    }

    private void loadCombos() {
        loadComboFromSP(comboBoxMovies, "{CALL sp_get_movies_for_combo()}", "Todas las películas");
        loadComboFromSP(comboBoxAuditoriums, "{CALL sp_get_auditoriums_for_combo()}", "Todas las salas");
    }

    /**
     * Generic helper method to load a ComboBox using stored procedures.
     */
    private void loadComboFromSP(ComboBox<String> combo, String spCall, String defaultText) {
        combo.getItems().add(defaultText);
        combo.setValue(defaultText);

        try (Connection conn = DatabaseConnection.getDatabaseConnectionInstance().getConnection(); CallableStatement cstmt = conn.prepareCall(spCall); ResultSet rs = cstmt.executeQuery()) {

            while (rs.next()) {
                combo.getItems().add(rs.getString(2));
            }
        } catch (SQLException e) {
            showError("Error de carga", "No se pudieron cargar los datos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void loadScreenings() {
        filterScreenings();
    }

    @FXML
    private void filterScreenings() {
        screeningsList.clear();

        String selectedMovie = comboBoxMovies.getValue();
        String selectedAuditorium = comboBoxAuditoriums.getValue();

        String pMovie = ("Todas las películas".equals(selectedMovie)) ? null : selectedMovie;
        String pAuditorium = ("Todas las salas".equals(selectedAuditorium)) ? null : selectedAuditorium;

        String sql = "{CALL sp_get_screenings_filtered(?, ?)}";

        try (Connection conn = DatabaseConnection.getDatabaseConnectionInstance().getConnection(); CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.setString(1, pMovie);
            cstmt.setString(2, pAuditorium);

            try (ResultSet rs = cstmt.executeQuery()) {
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

                while (rs.next()) {
                    LocalDate date = rs.getDate("show_date").toLocalDate();
                    LocalTime time = rs.getTime("show_time").toLocalTime();

                    ScreeningRow screening = new ScreeningRow(
                            rs.getInt("screening_id"),
                            rs.getInt("movie_id"),
                            rs.getInt("auditorium_id"),
                            rs.getString("title"),
                            rs.getString("auditorium_name"),
                            date.format(dateFormatter),
                            time.format(timeFormatter),
                            rs.getInt("duration") + " min"
                    );
                    screeningsList.add(screening);
                }
            }
        } catch (SQLException e) {
            showError("Error al cargar funciones", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void clearFilters() {
        comboBoxMovies.setValue("Todas las películas");
        comboBoxAuditoriums.setValue("Todas las salas");
        loadScreenings();
    }

 
    private void editScreening(ScreeningRow screening) {
        ObservableList<Movie> movies;
        ObservableList<Auditorium> auditoriums;
        try {
            movies = FXCollections.observableArrayList(new MovieRepository().getAllMovies());
            auditoriums = FXCollections.observableArrayList(new AuditoriumRepository().getAuditoriums());
        } catch (Exception e) {
            showError("Error de carga", "No se pudieron cargar películas y salas: " + e.getMessage());
            return;
        }

        Dialog<ScreeningRow> dialog = new Dialog<>();
        dialog.setTitle("Editar Función");
        dialog.setHeaderText("Modificar: " + screening.getMovie());

        ButtonType btnSave = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnSave, ButtonType.CANCEL);

        ComboBox<Movie> cmbMovies = new ComboBox<>(movies);
        cmbMovies.setConverter(new StringConverter<Movie>() {
            @Override
            public String toString(Movie m) {
                return m == null ? "" : m.getTitle();
            }

            @Override
            public Movie fromString(String s) {
                return null;
            }
        });
        ComboBox<Auditorium> cmbAuditoriums = new ComboBox<>(auditoriums);
        cmbAuditoriums.setConverter(new StringConverter<Auditorium>() {
            @Override
            public String toString(Auditorium a) {
                return a == null ? "" : a.getName();
            }

            @Override
            public Auditorium fromString(String s) {
                return null;
            }
        });

        DatePicker dpDate = new DatePicker(LocalDate.parse(screening.getDate(), DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        TextField txtTime = new TextField(screening.getTime());
        txtTime.setPromptText("HH:mm");

        for (Movie m : movies) {
            if (m.getMovieId() == screening.getMovieId()) {
                cmbMovies.setValue(m);
                break;
            }
        }
        for (Auditorium a : auditoriums) {
            if (a.getAuditoriumId() != null && a.getAuditoriumId() == screening.getAuditoriumId()) {
                cmbAuditoriums.setValue(a);
                break;
            }
        }

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Película:"), 0, 0);
        grid.add(cmbMovies, 1, 0);
        grid.add(new Label("Sala:"), 0, 1);
        grid.add(cmbAuditoriums, 1, 1);
        grid.add(new Label("Fecha:"), 0, 2);
        grid.add(dpDate, 1, 2);
        grid.add(new Label("Hora:"), 0, 3);
        grid.add(txtTime, 1, 3);

        dialog.getDialogPane().setContent(grid);

        javafx.scene.Node btnSaveNode = dialog.getDialogPane().lookupButton(btnSave);
        Runnable validate = () -> btnSaveNode.setDisable(
                cmbMovies.getValue() == null || cmbAuditoriums.getValue() == null
                || dpDate.getValue() == null || !txtTime.getText().matches("([01]\\d|2[0-3]):[0-5]\\d")
        );
        validate.run();
        cmbMovies.valueProperty().addListener((o, ov, nv) -> validate.run());
        cmbAuditoriums.valueProperty().addListener((o, ov, nv) -> validate.run());
        dpDate.valueProperty().addListener((o, ov, nv) -> validate.run());
        txtTime.textProperty().addListener((o, ov, nv) -> validate.run());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnSave) {
                return new ScreeningRow(screening.getId(),
                        cmbMovies.getValue().getMovieId(),
                        cmbAuditoriums.getValue().getAuditoriumId(),
                        cmbMovies.getValue().getTitle(),
                        cmbAuditoriums.getValue().getName(),
                        dpDate.getValue().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                        txtTime.getText(),
                        cmbMovies.getValue().getDuration() + " min");
            }
            return null;
        });

        dialog.showAndWait().ifPresent(editedScreening -> {
            if (saveChanges(editedScreening)) {
                showSuccess("Éxito", "La función se actualizó correctamente.");
                loadScreenings();
            }
        });
    }

    private boolean saveChanges(ScreeningRow f) {
        String sql = "{CALL sp_update_screening(?, ?, ?, ?, ?)}";

        try (Connection conn = DatabaseConnection.getDatabaseConnectionInstance().getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.setInt(1, f.getId());
            cstmt.setInt(2, f.getMovieId());
            cstmt.setInt(3, f.getAuditoriumId());
            cstmt.setDate(4, Date.valueOf(LocalDate.parse(f.getDate(), DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
            cstmt.setTime(5, Time.valueOf(LocalTime.parse(f.getTime())));

            cstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            String message = e.getMessage() == null ? "" : e.getMessage();
            if (message.contains("CONFLICTO")) {
                showError("Conflicto de horario", "Ya existe una función en esa sala, fecha y hora.");
            } else if (message.contains("RESERVAS")) {
                showError("Función con reservas", "La función tiene reservas activas; no se puede cambiar de sala.");
            } else {
                showError("Error", "No se pudo actualizar: " + message);
            }
            e.printStackTrace();
            return false;
        } catch (RuntimeException e) {
            showError("Error", "Datos de fecha u hora inválidos: " + e.getMessage());
            return false;
        }
    }

    @FXML
    private void goBackToPanel() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/cineplex/system/view/Admin.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) btnBack.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Panel de Administrador - CinePlex");
        } catch (Exception e) {
            showError("Error de navegación", "No se pudo volver al panel: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showSuccess(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

  
    public static class ScreeningRow {

        private final int id;
        private final int movieId;
        private final int auditoriumId;
        private final SimpleStringProperty movie;
        private final SimpleStringProperty auditorium;
        private final SimpleStringProperty date;
        private final SimpleStringProperty time;
        private final SimpleStringProperty duration;

        public ScreeningRow(int id, int movieId, int auditoriumId, String movie, String auditorium,
                String date, String time, String duration) {
            this.id = id;
            this.movieId = movieId;
            this.auditoriumId = auditoriumId;
            this.movie = new SimpleStringProperty(movie);
            this.auditorium = new SimpleStringProperty(auditorium);
            this.date = new SimpleStringProperty(date);
            this.time = new SimpleStringProperty(time);
            this.duration = new SimpleStringProperty(duration);
        }

        public int getId() {
            return id;
        }

        public int getMovieId() {
            return movieId;
        }

        public int getAuditoriumId() {
            return auditoriumId;
        }

        public String getMovie() {
            return movie.get();
        }

        public String getAuditorium() {
            return auditorium.get();
        }

        public String getDate() {
            return date.get();
        }

        public String getTime() {
            return time.get();
        }

        public String getDuration() {
            return duration.get();
        }

        public StringProperty movieProperty() {
            return movie;
        }

        public StringProperty auditoriumProperty() {
            return auditorium;
        }

        public StringProperty dateProperty() {
            return date;
        }

        public StringProperty timeProperty() {
            return time;
        }

        public StringProperty durationProperty() {
            return duration;
        }
    }
}