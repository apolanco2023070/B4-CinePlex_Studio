package org.cineplex.system.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.cineplex.system.config.ConexionDB;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class ScreeningConsultationController implements Initializable {

    // ===== Componentes FXML =====
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

    // ===== Variables de instancia =====
    private Connection connection;
    private ObservableList<Funcion> funcionesList;

    // ===== Inicialización =====
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            connection = ConexionDB.getInstanciaConexionDB().getConnection();
            configurarTabla();
            cargarCombos();
            cargarFunciones();
        } catch (Exception e) {
            mostrarError("Error de conexión", "No se pudo conectar a la base de datos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ===== Configurar tabla =====
    private void configurarTabla() {
        colMovie.setCellValueFactory(new PropertyValueFactory<>("pelicula"));
        colAuditorium.setCellValueFactory(new PropertyValueFactory<>("sala"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("hora"));
        colDuration.setCellValueFactory(new PropertyValueFactory<>("duracion"));
        
        funcionesList = FXCollections.observableArrayList();
        tableViewScreenings.setItems(funcionesList);
    }

    // ===== Cargar combos (películas y salas) =====
    private void cargarCombos() {
        // Cargar películas
        String sqlMovies = "SELECT movie_id, title FROM movie ORDER BY title";
        try (PreparedStatement pstmt = connection.prepareStatement(sqlMovies);
             ResultSet rs = pstmt.executeQuery()) {
            
            comboBoxMovies.getItems().add("Todas las películas");
            comboBoxMovies.setValue("Todas las películas");
            
            while (rs.next()) {
                comboBoxMovies.getItems().add(rs.getString("title"));
            }
        } catch (SQLException e) {
            mostrarError("Error al cargar películas", e.getMessage());
            e.printStackTrace();
        }

        // Cargar salas
        String sqlAuditoriums = "SELECT auditorium_id, name FROM auditorium ORDER BY name";
        try (PreparedStatement pstmt = connection.prepareStatement(sqlAuditoriums);
             ResultSet rs = pstmt.executeQuery()) {
            
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

    // ===== Cargar funciones =====
    public void cargarFunciones() {
        filtrarFunciones();
    }

    // ===== Filtrar funciones =====
    @FXML
    private void filtrarFunciones() {
        funcionesList.clear();
        
        String peliculaSeleccionada = comboBoxMovies.getValue();
        String salaSeleccionada = comboBoxAuditoriums.getValue();
        
        StringBuilder sql = new StringBuilder(
            "SELECT s.screening_id, m.title, a.name AS auditorium_name, " +
            "s.show_date, s.show_time, m.duration " +
            "FROM screening s " +
            "INNER JOIN movie m ON s.movie_id = m.movie_id " +
            "INNER JOIN auditorium a ON s.auditorium_id = a.auditorium_id " +
            "WHERE 1=1"
        );
        
        if (peliculaSeleccionada != null && !peliculaSeleccionada.equals("Todas las películas")) {
            sql.append(" AND m.title = ?");
        }
        
        if (salaSeleccionada != null && !salaSeleccionada.equals("Todas las salas")) {
            sql.append(" AND a.name = ?");
        }
        
        sql.append(" ORDER BY s.show_date, s.show_time");
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            
            if (peliculaSeleccionada != null && !peliculaSeleccionada.equals("Todas las películas")) {
                pstmt.setString(paramIndex++, peliculaSeleccionada);
            }
            
            if (salaSeleccionada != null && !salaSeleccionada.equals("Todas las salas")) {
                pstmt.setString(paramIndex++, salaSeleccionada);
            }
            
            ResultSet rs = pstmt.executeQuery();
            
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
            
        } catch (SQLException e) {
            mostrarError("Error al cargar funciones", e.getMessage());
            e.printStackTrace();
        }
    }

    // ===== Limpiar filtros =====
    @FXML
    private void limpiarFiltros() {
        comboBoxMovies.setValue("Todas las películas");
        comboBoxAuditoriums.setValue("Todas las salas");
        cargarFunciones();
    }

    // ===== Volver al Panel de Administrador =====
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

    // ===== Mostrar mensaje de error =====
    private void mostrarError(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }

    // ===== Clase interna para representar una Función =====
    public static class Funcion {
        private final javafx.beans.property.SimpleStringProperty pelicula;
        private final javafx.beans.property.SimpleStringProperty sala;
        private final javafx.beans.property.SimpleStringProperty fecha;
        private final javafx.beans.property.SimpleStringProperty hora;
        private final javafx.beans.property.SimpleStringProperty duracion;

        public Funcion(String pelicula, String sala, String fecha, String hora, String duracion) {
            this.pelicula = new javafx.beans.property.SimpleStringProperty(pelicula);
            this.sala = new javafx.beans.property.SimpleStringProperty(sala);
            this.fecha = new javafx.beans.property.SimpleStringProperty(fecha);
            this.hora = new javafx.beans.property.SimpleStringProperty(hora);
            this.duracion = new javafx.beans.property.SimpleStringProperty(duracion);
        }

        public String getPelicula() { return pelicula.get(); }
        public String getSala() { return sala.get(); }
        public String getFecha() { return fecha.get(); }
        public String getHora() { return hora.get(); }
        public String getDuracion() { return duracion.get(); }

        public javafx.beans.property.StringProperty peliculaProperty() { return pelicula; }
        public javafx.beans.property.StringProperty salaProperty() { return sala; }
        public javafx.beans.property.StringProperty fechaProperty() { return fecha; }
        public javafx.beans.property.StringProperty horaProperty() { return hora; }
        public javafx.beans.property.StringProperty duracionProperty() { return duracion; }
    }
}