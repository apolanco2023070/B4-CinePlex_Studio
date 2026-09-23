/*

* Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license

* Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

*/

package org.cineplex.system.controller;
 
import javafx.fxml.FXML;

import javafx.scene.control.*;

import javafx.collections.FXCollections;

import javafx.collections.ObservableList;

import javafx.stage.Stage;

import javafx.fxml.FXMLLoader;

import javafx.scene.Parent;

import javafx.scene.Scene;

import org.cineplex.system.model.Screening;

import org.cineplex.system.model.Movie;

import org.cineplex.system.model.Auditorium;

import org.cineplex.system.service.ScreeningService;

import org.cineplex.system.repository.MovieRepository;

import org.cineplex.system.repository.AuditoriumRepository;

import org.cineplex.system.utils.AlertInformation;
 
import java.time.LocalDate;

import java.time.LocalTime;

import java.time.format.DateTimeFormatter;

import java.util.List;
 
public class ScreeningController {
 
    @FXML private TableView<Screening> tableScreenings;

    @FXML private TableColumn<Screening, String> colSala;

    @FXML private TableColumn<Screening, String> colPelicula;

    @FXML private TableColumn<Screening, String> colFecha;

    @FXML private TableColumn<Screening, String> colHora;

    @FXML private Label lblPelicula, lblSala, lblFecha, lblHora;

    @FXML private ComboBox<Movie> cmbPelicula;

    @FXML private ComboBox<Auditorium> cmbSala;

    @FXML private DatePicker datePicker;

    @FXML private TextField txtHora;

    @FXML private Button btnProgramar, btnRegresar;
 
    private final ScreeningService screeningService;

    private final MovieRepository movieRepository;

    private final AuditoriumRepository auditoriumRepository;

    private final AlertInformation alertInfo;
 
    public ScreeningController() {

        this.screeningService = new ScreeningService();

        this.movieRepository = new MovieRepository();

        this.auditoriumRepository = new AuditoriumRepository();

        this.alertInfo = new AlertInformation();

    }
 
    @FXML

    public void initialize() {

        configurarTabla();

        cargarPeliculas();

        cargarSalas();

    }
 
    private void configurarTabla() {

        colSala.setCellValueFactory(data -> data.getValue().auditoriumNameProperty());

        colPelicula.setCellValueFactory(data -> data.getValue().movieTitleProperty());

        colFecha.setCellValueFactory(data -> data.getValue().showDateProperty());

        colHora.setCellValueFactory(data -> data.getValue().showTimeProperty());

    }
 
    private void cargarPeliculas() {

        try {

            List<Movie> movies = movieRepository.getAllMovies();

            cmbPelicula.getItems().addAll(movies);

            // Configurar cómo se muestra la película en el ComboBox

            cmbPelicula.setCellFactory(param -> new ListCell<Movie>() {

                @Override

                protected void updateItem(Movie movie, boolean empty) {

                    super.updateItem(movie, empty);

                    if (empty || movie == null) {

                        setText(null);

                    } else {

                        setText(movie.getTitle());

                    }

                }

            });

            cmbPelicula.setButtonCell(new ListCell<Movie>() {

                @Override

                protected void updateItem(Movie movie, boolean empty) {

                    super.updateItem(movie, empty);

                    if (empty || movie == null) {

                        setText(null);

                    } else {

                        setText(movie.getTitle());

                    }

                }

            });

        } catch (Exception e) {

            alertInfo.viewAlert("ERROR", "Error de carga", 

                "No se pudieron cargar las películas", e.getMessage());

        }

    }
 
    private void cargarSalas() {

        try {

            List<Auditorium> auditoriums = auditoriumRepository.getAuditoriums();

            cmbSala.getItems().addAll(auditoriums);

            // Configurar cómo se muestra la sala en el ComboBox

            cmbSala.setCellFactory(param -> new ListCell<Auditorium>() {

                @Override

                protected void updateItem(Auditorium auditorium, boolean empty) {

                    super.updateItem(auditorium, empty);

                    if (empty || auditorium == null) {

                        setText(null);

                    } else {

                        setText("Sala " + auditorium.getName() + " (Capacidad: " + auditorium.getCapacity() + ")");

                    }

                }

            });

            cmbSala.setButtonCell(new ListCell<Auditorium>() {

                @Override

                protected void updateItem(Auditorium auditorium, boolean empty) {

                    super.updateItem(auditorium, empty);

                    if (empty || auditorium == null) {

                        setText(null);

                    } else {

                        setText("Sala " + auditorium.getName());

                    }

                }

            });

        } catch (Exception e) {

            alertInfo.viewAlert("ERROR", "Error de carga", 

                "No se pudieron cargar las salas", e.getMessage());

        }

    }
 
    @FXML

    private void programarFuncion() {

        limpiarErroresVisuales();

        // Validar campos

        if (!validarCampos()) {

            return;

        }

        try {

            Movie movie = cmbPelicula.getValue();

            Auditorium auditorium = cmbSala.getValue();

            LocalDate showDate = datePicker.getValue();

            LocalTime showTime = LocalTime.parse(txtHora.getText().trim());

            Screening screening = new Screening(0, movie.getMovieId(), 

                auditorium.getAuditoriumId(), showDate, showTime);

            screeningService.createScreening(screening);

            alertInfo.viewAlert("SUCCESS", "Éxito", "Función programada", 

                "La función se ha programado correctamente en la Sala " + 

                auditorium.getName() + " para el " + showDate + " a las " + showTime);

            limpiarFormulario();

            cargarPeliculas(); 

        } catch (Exception e) {

            alertInfo.viewAlert("ERROR", "Error de programación", 

                "Conflicto de horario", e.getMessage());

        }

    }
 
    private boolean validarCampos() {

        boolean hayErrores = false;

        StringBuilder errores = new StringBuilder();

        if (cmbPelicula.getValue() == null) {

            errores.append("• Debe seleccionar una película.\n");

            lblPelicula.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");

            hayErrores = true;

        }

        // Validar sala

        if (cmbSala.getValue() == null) {

            errores.append("• Debe seleccionar una sala.\n");

            lblSala.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");

            hayErrores = true;

        }

        // Validar fecha

        if (datePicker.getValue() == null) {

            errores.append("• Debe seleccionar una fecha.\n");

            lblFecha.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");

            hayErrores = true;

        } else if (datePicker.getValue().isBefore(LocalDate.now())) {

            errores.append("• La fecha no puede ser anterior a hoy.\n");

            lblFecha.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");

            hayErrores = true;

        }

        // Validar hora

        String horaText = txtHora.getText().trim();

        if (horaText.isEmpty()) {

            errores.append("• Debe ingresar una hora.\n");

            lblHora.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");

            hayErrores = true;

        } else {

            try {

                LocalTime.parse(horaText);

            } catch (Exception e) {

                errores.append("• Formato de hora inválido. Use HH:mm (ej: 14:30).\n");

                lblHora.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");

                hayErrores = true;

            }

        }

        if (hayErrores) {

            alertInfo.viewAlert("ERROR", "Datos inválidos", 

                "Error de validación", errores.toString());

        }

        return !hayErrores;

    }
 
    @FXML

    private void regresarMenu() {

        try {

            Stage stageActual = (Stage) btnRegresar.getScene().getWindow();

            FXMLLoader loader = new FXMLLoader(

                getClass().getResource("/org/cineplex/system/view/Administrador.fxml"));

            Parent root = loader.load();

            Scene escenaNueva = new Scene(root);

            stageActual.setScene(escenaNueva);

            stageActual.show();

        } catch (Exception e) {

            e.printStackTrace();

            alertInfo.viewAlert("ERROR", "Error de navegación", 

                "No se pudo cargar la vista", "Detalle: " + e.getMessage());

        }

    }
 
    private void limpiarFormulario() {

        cmbPelicula.setValue(null);

        cmbSala.setValue(null);

        datePicker.setValue(null);

        txtHora.clear();

        limpiarErroresVisuales();

        cmbPelicula.requestFocus();

    }
 
    private void limpiarErroresVisuales() {

        lblPelicula.setStyle("-fx-text-fill: black; -fx-font-weight: normal;");

        lblSala.setStyle("-fx-text-fill: black; -fx-font-weight: normal;");

        lblFecha.setStyle("-fx-text-fill: black; -fx-font-weight: normal;");

        lblHora.setStyle("-fx-text-fill: black; -fx-font-weight: normal;");

    }

}
 