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
import org.cineplex.system.config.ConexionDB;

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
    private TableColumn<Funcion, Void> colActions;
    @FXML
    private Button btnRefresh;
    @FXML
    private Button btnBack;

    // ===== Variables de instancia =====
    private Connection connection;
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

    // ===== Configurar tabla =====
    private void configurarTabla() {
        colMovie.setCellValueFactory(new PropertyValueFactory<>("pelicula"));
        colAuditorium.setCellValueFactory(new PropertyValueFactory<>("sala"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("hora"));
        colDuration.setCellValueFactory(new PropertyValueFactory<>("duracion"));
        // Configurar el botón de editar en cada fila
        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button btnEdit = new Button("️ Editar");

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
        cargarComboSP(comboBoxMovies, "{CALL sp_get_movies_for_combo()}", "Todas las películas");
        cargarComboSP(comboBoxAuditoriums, "{CALL sp_get_auditoriums_for_combo()}", "Todas las salas");
    }

    /**
     * Método auxiliar genérico para cargar ComboBox usando Stored Procedures.
     */
    private void cargarComboSP(ComboBox<String> combo, String spCall, String defaultText) {
        combo.getItems().add(defaultText);
        combo.setValue(defaultText);

        try (Connection conn = ConexionDB.getInstanciaConexionDB().getConnection(); CallableStatement cstmt = conn.prepareCall(spCall); ResultSet rs = cstmt.executeQuery()) {

            while (rs.next()) {
                // Ambos SP devuelven: (1: id, 2: nombre/title)
                combo.getItems().add(rs.getString(2));
            }
        } catch (SQLException e) {
            mostrarError("Error de carga", "No se pudieron cargar los datos: " + e.getMessage());
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

        // Si el usuario selecciona "Todas...", enviamos null al SP para que ignore ese filtro
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
                            rs.getInt("screening_id"),
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

    // ==========================================
    // LÓGICA DE EDICIÓN (HU27)
    // ==========================================
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
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Película:"), 0, 0);
        grid.add(cmbPeliculas, 1, 0);
        grid.add(new Label("Sala:"), 0, 1);
        grid.add(cmbSalas, 1, 1);
        grid.add(new Label("Fecha:"), 0, 2);
        grid.add(dpFecha, 1, 2);
        grid.add(new Label("Hora:"), 0, 3);
        grid.add(txtHora, 1, 3);

        dialog.getDialogPane().setContent(grid);

        // Validación en tiempo real
        javafx.scene.Node btnGuardarNode = dialog.getDialogPane().lookupButton(btnGuardar);
        btnGuardarNode.setDisable(true);
        Runnable validar = () -> btnGuardarNode.setDisable(
                cmbPeliculas.getValue() == null || cmbSalas.getValue() == null
                || dpFecha.getValue() == null || !txtHora.getText().matches("\\d{2}:\\d{2}")
        );
        cmbPeliculas.valueProperty().addListener((o, ov, nv) -> validar.run());
        cmbSalas.valueProperty().addListener((o, ov, nv) -> validar.run());
        dpFecha.valueProperty().addListener((o, ov, nv) -> validar.run());
        txtHora.textProperty().addListener((o, ov, nv) -> validar.run());

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
        try (Connection conn = ConexionDB.getInstanciaConexionDB().getConnection()) {

            // Obtener IDs de película y sala
            int movieId = obtenerIdPelicula(conn, f.getPelicula());
            int auditoriumId = obtenerIdSala(conn, f.getSala());

            if (movieId == 0 || auditoriumId == 0) {
                mostrarError("Error", "No se pudieron obtener los IDs de película o sala.");
                return false;
            }

            // Llamar al SP de actualización
            CallableStatement cstmt = conn.prepareCall("{CALL sp_update_screening(?, ?, ?, ?, ?)}");
            cstmt.setInt(1, f.getId());
            cstmt.setInt(2, movieId);
            cstmt.setInt(3, auditoriumId);
            cstmt.setDate(4, Date.valueOf(LocalDate.parse(f.getFecha(), DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
            cstmt.setTime(5, Time.valueOf(LocalTime.parse(f.getHora())));

            cstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            if (e.getMessage() != null && e.getMessage().contains("CONFLICTO")) {
                mostrarError("Conflicto de horario", "Ya existe una función en esa sala, fecha y hora.");
            } else {
                mostrarError("Error", "No se pudo actualizar: " + e.getMessage());
            }
            e.printStackTrace();
            return false;
        }
    }

    private int obtenerIdPelicula(Connection conn, String titulo) {
        try (CallableStatement cstmt = conn.prepareCall("{CALL sp_get_movie_id_by_title(?, ?)}")) {
            cstmt.setString(1, titulo);
            cstmt.registerOutParameter(2, java.sql.Types.INTEGER);
            cstmt.execute();
            return cstmt.getInt(2);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private int obtenerIdSala(Connection conn, String nombre) {
        try (CallableStatement cstmt = conn.prepareCall("{CALL sp_get_auditorium_id_by_name(?, ?)}")) {
            cstmt.setString(1, nombre);
            cstmt.registerOutParameter(2, java.sql.Types.INTEGER);
            cstmt.execute();
            return cstmt.getInt(2);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
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


    private void mostrarExito(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }

    // ==========================================
    // Clase interna para el modelo de la tabla
    // ==========================================
    public static class Funcion {

        private final int id;
        private final SimpleStringProperty pelicula;
        private final SimpleStringProperty sala;
        private final SimpleStringProperty fecha;
        private final SimpleStringProperty hora;
        private final SimpleStringProperty duracion;

        public Funcion(int id, String pelicula, String sala, String fecha, String hora, String duracion) {
            this.id = id;
            this.pelicula = new SimpleStringProperty(pelicula);
            this.sala = new SimpleStringProperty(sala);
            this.fecha = new SimpleStringProperty(fecha);
            this.hora = new SimpleStringProperty(hora);
            this.duracion = new SimpleStringProperty(duracion);
        }

        public int getId() {
            return id;
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
