package org.cineplex.system.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.cineplex.system.config.ConexionDB;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

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
    private TableView<Funcion> tableViewScreenings;
    
    @FXML
    private TableColumn<Funcion, String> colMovie;
    
    @FXML
    private TableColumn<Funcion, String> colAuditorium;
    
    @FXML
    private TableColumn<Funcion, String> colDate;
    
    @FXML
    private TableColumn<Funcion, String> colTime;
    
    @FXML
    private TableColumn<Funcion, String> colDuration;
    
    @FXML
    private Button btnRefresh;
    
    @FXML
    private Button btnBack;

    private ObservableList<Funcion> funcionesList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            configurarTabla();
            cargarCombos();
            cargarFunciones();
        } catch (Exception e) {
            mostrarError("Error de inicialización", "No se pudo cargar la vista: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void configurarTabla() {
        colMovie.setCellValueFactory(new PropertyValueFactory<>("pelicula"));
        colAuditorium.setCellValueFactory(new PropertyValueFactory<>("sala"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("hora"));
        colDuration.setCellValueFactory(new PropertyValueFactory<>("duracion"));

        funcionesList = FXCollections.observableArrayList();
        tableViewScreenings.setItems(funcionesList);
    }

    private void cargarCombos() {
        String sqlMovies = "{CALL sp_get_movies_for_combo()}";
        try (Connection conn = ConexionDB.getInstanciaConexionDB().getConnection(); CallableStatement cstmt = conn.prepareCall(sqlMovies); ResultSet rs = cstmt.executeQuery()) {

            comboBoxMovies.getItems().add("Todas las películas");
            comboBoxMovies.setValue("Todas las películas");

            while (rs.next()) {
                comboBoxMovies.getItems().add(rs.getString("title"));
            }
        } catch (SQLException e) {
            mostrarError("Error al cargar películas", e.getMessage());
            e.printStackTrace();
        }

        String sqlAuditoriums = "{CALL sp_get_auditoriums_for_combo()}";
        try (Connection conn = ConexionDB.getInstanciaConexionDB().getConnection(); CallableStatement cstmt = conn.prepareCall(sqlAuditoriums); ResultSet rs = cstmt.executeQuery()) {

            comboBoxAuditoriums.getItems().add("Todas las salas");
            comboBoxAuditoriums.setValue("Todas las salas");

            while (rs.next()) {
                comboBoxAuditoriums.getItems().add(rs.getString("name"));
            }
        } catch (SQLException e) {
            mostrarError("Error al cargar salas", e.getMessage());
            e.printStackTrace();
        }
    }

    public void cargarFunciones() {
        filtrarFunciones();
    }

    @FXML
    private void filtrarFunciones() {
        funcionesList.clear();

        String peliculaSeleccionada = comboBoxMovies.getValue();
        String salaSeleccionada = comboBoxAuditoriums.getValue();

        String pMovie = ("Todas las películas".equals(peliculaSeleccionada)) ? null : peliculaSeleccionada;
        String pAuditorium = ("Todas las salas".equals(salaSeleccionada)) ? null : salaSeleccionada;

        String sql = "{CALL sp_get_screenings_filtered(?, ?)}";

        try (Connection conn = ConexionDB.getInstanciaConexionDB().getConnection(); CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.setString(1, pMovie);
            cstmt.setString(2, pAuditorium);

            try (ResultSet rs = cstmt.executeQuery()) {
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

                while (rs.next()) {
                    LocalDate fecha = rs.getDate("show_date").toLocalDate();
                    LocalTime hora = rs.getTime("show_time").toLocalTime();

                    Funcion funcion = new Funcion(
                            rs.getString("title"),
                            rs.getString("auditorium_name"),
                            fecha.format(dateFormatter),
                            hora.format(timeFormatter),
                            rs.getInt("duration") + " min"
                    );
                    funcionesList.add(funcion);
                }
            }
        } catch (SQLException e) {
            mostrarError("Error al cargar funciones", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void limpiarFiltros() {
        comboBoxMovies.setValue("Todas las películas");
        comboBoxAuditoriums.setValue("Todas las salas");
        cargarFunciones();
    }

    @FXML
    private void volverAlPanel() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/cineplex/system/view/Administrador.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) btnBack.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Panel de Administrador - CinePlex");
        } catch (Exception e) {
            mostrarError("Error de navegación", "No se pudo volver al panel: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void mostrarError(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }

    public static class Funcion {

        private final SimpleStringProperty pelicula;
        private final SimpleStringProperty sala;
        private final SimpleStringProperty fecha;
        private final SimpleStringProperty hora;
        private final SimpleStringProperty duracion;

        public Funcion(String pelicula, String sala, String fecha, String hora, String duracion) {
            this.pelicula = new SimpleStringProperty(pelicula);
            this.sala = new SimpleStringProperty(sala);
            this.fecha = new SimpleStringProperty(fecha);
            this.hora = new SimpleStringProperty(hora);
            this.duracion = new SimpleStringProperty(duracion);
        }

        public String getPelicula() {
            return pelicula.get();
        }

        public String getSala() {
            return sala.get();
        }

        public String getFecha() {
            return fecha.get();
        }

        public String getHora() {
            return hora.get();
        }

        public String getDuracion() {
            return duracion.get();
        }

        public StringProperty peliculaProperty() {
            return pelicula;
        }

        public StringProperty salaProperty() {
            return sala;
        }

        public StringProperty fechaProperty() {
            return fecha;
        }

        public StringProperty horaProperty() {
            return hora;
        }

        public StringProperty duracionProperty() {
            return duracion;
        }
    }
}
