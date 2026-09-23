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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.cineplex.system.config.ConexionDB;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.scene.Node;

public class ScreeningConsultationController implements Initializable {

    @FXML private ComboBox<String> comboBoxMovies;
    @FXML private ComboBox<String> comboBoxAuditoriums;
    @FXML private TableView<Funcion> tableViewScreenings;
    @FXML private TableColumn<Funcion, String> colMovie;
    @FXML private TableColumn<Funcion, String> colAuditorium;
    @FXML private TableColumn<Funcion, String> colDate;
    @FXML private TableColumn<Funcion, String> colTime;
    @FXML private TableColumn<Funcion, String> colDuration;
    @FXML private TableColumn<Funcion, Void> colActions; // Nueva columna
    @FXML private Button btnBack;

    private Connection connection;
    private ObservableList<Funcion> funcionesList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            connection = ConexionDB.getInstanciaConexionDB().getConnection();
            configurarTabla();
            cargarCombos();
            cargarFunciones();
        } catch (Exception e) {
            mostrarError("Error de conexión", "No se pudo conectar a la base de datos: " + e.getMessage());
        }
    }

    private void configurarTabla() {
        colMovie.setCellValueFactory(new PropertyValueFactory<>("pelicula"));
        colAuditorium.setCellValueFactory(new PropertyValueFactory<>("sala"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("hora"));
        colDuration.setCellValueFactory(new PropertyValueFactory<>("duracion"));
        
        // Configurar el botón de editar en cada fila
        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button btnEdit = new Button("✏️ Editar");
            {
                btnEdit.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
                btnEdit.setOnAction(event -> editarFuncion(getTableView().getItems().get(getIndex())));
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnEdit);
            }
        });
        
        funcionesList = FXCollections.observableArrayList();
        tableViewScreenings.setItems(funcionesList);
    }

    private void cargarCombos() {
        cargarCombo(comboBoxMovies, "SELECT title FROM movie ORDER BY title", "Todas las películas");
        cargarCombo(comboBoxAuditoriums, "SELECT name FROM auditorium ORDER BY name", "Todas las salas");
    }

    private void cargarCombo(ComboBox<String> combo, String sql, String defaultText) {
        combo.getItems().add(defaultText);
        combo.setValue(defaultText);
        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) combo.getItems().add(rs.getString(1));
        } catch (SQLException e) {
            mostrarError("Error", "No se pudieron cargar los datos: " + e.getMessage());
        }
    }

    @FXML
    private void cargarFunciones() {
        funcionesList.clear();
        String pelicula = comboBoxMovies.getValue();
        String sala = comboBoxAuditoriums.getValue();
        
        StringBuilder sql = new StringBuilder(
            "SELECT s.screening_id, m.title, a.name AS auditorium_name, s.show_date, s.show_time, m.duration " +
            "FROM screening s INNER JOIN movie m ON s.movie_id = m.movie_id " +
            "INNER JOIN auditorium a ON s.auditorium_id = a.auditorium_id WHERE 1=1"
        );
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql.toString())) {
            ResultSet rs = pstmt.executeQuery();
            DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            DateTimeFormatter tf = DateTimeFormatter.ofPattern("HH:mm");
            
            while (rs.next()) {
                funcionesList.add(new Funcion(
                    rs.getInt("screening_id"),
                    rs.getString("title"),
                    rs.getString("auditorium_name"),
                    rs.getDate("show_date").toLocalDate().format(df),
                    rs.getTime("show_time").toLocalTime().format(tf),
                    rs.getInt("duration") + " min"
                ));
            }
        } catch (SQLException e) {
            mostrarError("Error", "No se pudieron cargar las funciones: " + e.getMessage());
        }
    }

    @FXML
    private void limpiarFiltros() {
        comboBoxMovies.setValue("Todas las películas");
        comboBoxAuditoriums.setValue("Todas las salas");
        cargarFunciones();
    }

    // ===== LÓGICA DE EDICIÓN (HU27) =====
    private void editarFuncion(Funcion funcion) {
        Dialog<Funcion> dialog = new Dialog<>();
        dialog.setTitle("Editar Función");
        dialog.setHeaderText("Modificar: " + funcion.getPelicula());

        ButtonType btnGuardar = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnGuardar, ButtonType.CANCEL);

        ComboBox<String> cmbPeliculas = new ComboBox<>(comboBoxMovies.getItems());
        ComboBox<String> cmbSalas = new ComboBox<>(comboBoxAuditoriums.getItems());
        DatePicker dpFecha = new DatePicker(LocalDate.parse(funcion.getFecha(), DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        TextField txtHora = new TextField(funcion.getHora());
        txtHora.setPromptText("HH:mm");

        cmbPeliculas.setValue(funcion.getPelicula());
        cmbSalas.setValue(funcion.getSala());

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.add(new Label("Película:"), 0, 0); grid.add(cmbPeliculas, 1, 0);
        grid.add(new Label("Sala:"), 0, 1); grid.add(cmbSalas, 1, 1);
        grid.add(new Label("Fecha:"), 0, 2); grid.add(dpFecha, 1, 2);
        grid.add(new Label("Hora:"), 0, 3); grid.add(txtHora, 1, 3);

        dialog.getDialogPane().setContent(grid);

        // Validación en tiempo real
        Node btnGuardarNode = dialog.getDialogPane().lookupButton(btnGuardar);
        btnGuardarNode.setDisable(true);
        Runnable validar = () -> btnGuardarNode.setDisable(
            cmbPeliculas.getValue() == null || cmbSalas.getValue() == null || 
            dpFecha.getValue() == null || !txtHora.getText().matches("\\d{2}:\\d{2}")
        );
        cmbPeliculas.valueProperty().addListener((o,ov,nv) -> validar.run());
        cmbSalas.valueProperty().addListener((o,ov,nv) -> validar.run());
        dpFecha.valueProperty().addListener((o,ov,nv) -> validar.run());
        txtHora.textProperty().addListener((o,ov,nv) -> validar.run());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnGuardar) {
                return new Funcion(funcion.getId(), cmbPeliculas.getValue(), cmbSalas.getValue(),
                        dpFecha.getValue().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                        txtHora.getText(), funcion.getDuracion());
            }
            return null;
        });

        dialog.showAndWait().ifPresent(funcionEditada -> {
            if (guardarCambios(funcionEditada)) {
                mostrarExito("Éxito", "La función se actualizó correctamente.");
                cargarFunciones();
            }
        });
    }

    private boolean guardarCambios(Funcion f) {
        try {
            CallableStatement cstmt = connection.prepareCall("{CALL sp_update_screening(?, ?, ?, ?, ?)}");
            cstmt.setInt(1, f.getId());
            cstmt.setInt(2, obtenerId("SELECT movie_id FROM movie WHERE title = ?", f.getPelicula()));
            cstmt.setInt(3, obtenerId("SELECT auditorium_id FROM auditorium WHERE name = ?", f.getSala()));
            cstmt.setDate(4, Date.valueOf(LocalDate.parse(f.getFecha(), DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
            cstmt.setTime(5, Time.valueOf(LocalTime.parse(f.getHora())));
            
            cstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            if (e.getMessage().contains("CONFLICTO")) {
                mostrarError("Conflicto de horario", "Ya existe una función en esa sala, fecha y hora.");
            } else {
                mostrarError("Error", "No se pudo actualizar: " + e.getMessage());
            }
            return false;
        }
    }

    private int obtenerId(String sql, String valor) {
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, valor);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    @FXML
    private void volverAlPanel() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/org/cineplex/system/view/Administrador.fxml"));
            Stage stage = (Stage) btnBack.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            mostrarError("Error", "No se pudo volver al panel.");
        }
    }

    private void mostrarError(String t, String c) {
        new Alert(Alert.AlertType.ERROR, c) {{ setTitle(t); showAndWait(); }};
    }
    private void mostrarExito(String t, String c) {
        new Alert(Alert.AlertType.INFORMATION, c) {{ setTitle(t); showAndWait(); }};
    }

    // Clase interna para los datos de la tabla
    public static class Funcion {
        private final int id;
        private final javafx.beans.property.SimpleStringProperty pelicula, sala, fecha, hora, duracion;

        public Funcion(int id, String p, String s, String f, String h, String d) {
            this.id = id;
            this.pelicula = new javafx.beans.property.SimpleStringProperty(p);
            this.sala = new javafx.beans.property.SimpleStringProperty(s);
            this.fecha = new javafx.beans.property.SimpleStringProperty(f);
            this.hora = new javafx.beans.property.SimpleStringProperty(h);
            this.duracion = new javafx.beans.property.SimpleStringProperty(d);
        }
        public int getId() { return id; }
        public String getPelicula() { return pelicula.get(); }
        public String getSala() { return sala.get(); }
        public String getFecha() { return fecha.get(); }
        public String getHora() { return hora.get(); }
        public String getDuracion() { return duracion.get(); }
    }
}