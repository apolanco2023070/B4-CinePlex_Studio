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
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class ReservationConsultationController implements Initializable {

    @FXML private ComboBox<String> comboBoxMovies;
    @FXML private DatePicker datePickerFecha;
    @FXML private TableView<Reserva> tableViewReservations;
    @FXML private TableColumn<Reserva, String> colUsuario;
    @FXML private TableColumn<Reserva, String> colPelicula;
    @FXML private TableColumn<Reserva, String> colSala;
    @FXML private TableColumn<Reserva, String> colAsiento;
    @FXML private TableColumn<Reserva, String> colFecha;
    @FXML private TableColumn<Reserva, String> colHora;
    @FXML private TableColumn<Reserva, String> colEstado;
    @FXML private Button btnBack;

    private Connection connection;
    private ObservableList<Reserva> reservasList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            connection = ConexionDB.getInstanciaConexionDB().getConnection();
            configurarTabla();
            cargarCombos();
            cargarReservas();
        } catch (Exception e) {
            mostrarError("Error", "No se pudo conectar: " + e.getMessage());
        }
    }

    private void configurarTabla() {
        colUsuario.setCellValueFactory(new PropertyValueFactory<>("usuario"));
        colPelicula.setCellValueFactory(new PropertyValueFactory<>("pelicula"));
        colSala.setCellValueFactory(new PropertyValueFactory<>("sala"));
        colAsiento.setCellValueFactory(new PropertyValueFactory<>("asiento"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaFuncion"));
        colHora.setCellValueFactory(new PropertyValueFactory<>("horaFuncion"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        
        reservasList = FXCollections.observableArrayList();
        tableViewReservations.setItems(reservasList);
    }

    private void cargarCombos() {
        comboBoxMovies.getItems().add("Todas");
        comboBoxMovies.setValue("Todas");
        
        String sql = "SELECT title FROM movie ORDER BY title";
        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                comboBoxMovies.getItems().add(rs.getString("title"));
            }
        } catch (SQLException e) {
            mostrarError("Error", "No se cargaron películas: " + e.getMessage());
        }
    }

    @FXML
    private void cargarReservas() {
        reservasList.clear();
        
        String sql = "{CALL sp_get_all_reservations()}";
        
        try (CallableStatement cstmt = connection.prepareCall(sql);
             ResultSet rs = cstmt.executeQuery()) {
            
            DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            DateTimeFormatter tf = DateTimeFormatter.ofPattern("HH:mm");
            
            while (rs.next()) {
                LocalDate fecha = rs.getDate("fecha_funcion").toLocalDate();
                java.sql.Time horaSql = rs.getTime("hora_funcion");
                String hora = horaSql != null ? horaSql.toLocalTime().format(tf) : "N/A";
                
                reservasList.add(new Reserva(
                    rs.getString("usuario"),
                    rs.getString("pelicula"),
                    rs.getString("sala"),
                    String.valueOf(rs.getInt("asiento")),
                    fecha.format(df),
                    hora,
                    rs.getString("estado")
                ));
            }
        } catch (SQLException e) {
            mostrarError("Error", "No se cargaron reservas: " + e.getMessage());
        }
    }

    @FXML
    private void filtrarReservas() {
        reservasList.clear();
        
        String pelicula = comboBoxMovies.getValue();
        LocalDate fecha = datePickerFecha.getValue();
        
        StringBuilder sql = new StringBuilder(
            "SELECT u.full_name AS usuario, m.title AS pelicula, " +
            "a.name AS sala, s.seat_number AS asiento, " +
            "sc.show_date AS fecha_funcion, sc.show_time AS hora_funcion, " +
            "r.status AS estado " +
            "FROM reservation r " +
            "INNER JOIN users u ON r.user_id = u.user_id " +
            "INNER JOIN screening sc ON r.screening_id = sc.screening_id " +
            "INNER JOIN movie m ON sc.movie_id = m.movie_id " +
            "INNER JOIN auditorium a ON sc.auditorium_id = a.auditorium_id " +
            "INNER JOIN seat s ON r.seat_id = s.seat_id " +
            "WHERE 1=1"
        );
        
        if (pelicula != null && !pelicula.equals("Todas")) {
            sql.append(" AND m.title = ?");
        }
        
        if (fecha != null) {
            sql.append(" AND sc.show_date = ?");
        }
        
        sql.append(" ORDER BY r.reservation_date DESC");
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            
            if (pelicula != null && !pelicula.equals("Todas")) {
                pstmt.setString(paramIndex++, pelicula);
            }
            
            if (fecha != null) {
                pstmt.setDate(paramIndex, Date.valueOf(fecha));
            }
            
            ResultSet rs = pstmt.executeQuery();
            DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            DateTimeFormatter tf = DateTimeFormatter.ofPattern("HH:mm");
            
            while (rs.next()) {
                LocalDate fechaFuncion = rs.getDate("fecha_funcion").toLocalDate();
                java.sql.Time horaSql = rs.getTime("hora_funcion");
                String hora = horaSql != null ? horaSql.toLocalTime().format(tf) : "N/A";
                
                reservasList.add(new Reserva(
                    rs.getString("usuario"),
                    rs.getString("pelicula"),
                    rs.getString("sala"),
                    String.valueOf(rs.getInt("asiento")),
                    fechaFuncion.format(df),
                    hora,
                    rs.getString("estado")
                ));
            }
        } catch (SQLException e) {
            mostrarError("Error", "No se filtraron reservas: " + e.getMessage());
        }
    }

    @FXML
    private void limpiarFiltros() {
        comboBoxMovies.setValue("Todas");
        datePickerFecha.setValue(null);
        cargarReservas();
    }

    @FXML
    private void volverAlPanel() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/org/cineplex/system/view/Gerente.fxml"));
            Stage stage = (Stage) btnBack.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Panel de Gerente - CinePlex");
        } catch (Exception e) {
            mostrarError("Error", "No se pudo volver al panel.");
        }
    }

    private void mostrarError(String t, String c) {
        new Alert(Alert.AlertType.ERROR, c) {{ setTitle(t); showAndWait(); }};
    }

    // Clase interna
    public static class Reserva {
        private final javafx.beans.property.SimpleStringProperty usuario, pelicula, sala, asiento, fechaFuncion, horaFuncion, estado;

        public Reserva(String u, String p, String s, String a, String f, String h, String e) {
            this.usuario = new javafx.beans.property.SimpleStringProperty(u);
            this.pelicula = new javafx.beans.property.SimpleStringProperty(p);
            this.sala = new javafx.beans.property.SimpleStringProperty(s);
            this.asiento = new javafx.beans.property.SimpleStringProperty(a);
            this.fechaFuncion = new javafx.beans.property.SimpleStringProperty(f);
            this.horaFuncion = new javafx.beans.property.SimpleStringProperty(h);
            this.estado = new javafx.beans.property.SimpleStringProperty(e);
        }

        public String getUsuario() { return usuario.get(); }
        public String getPelicula() { return pelicula.get(); }
        public String getSala() { return sala.get(); }
        public String getAsiento() { return asiento.get(); }
        public String getFechaFuncion() { return fechaFuncion.get(); }
        public String getHoraFuncion() { return horaFuncion.get(); }
        public String getEstado() { return estado.get(); }
    }
}