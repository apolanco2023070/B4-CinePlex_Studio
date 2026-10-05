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
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.cineplex.system.config.DatabaseConnection;
import org.cineplex.system.model.User;
import org.cineplex.system.utils.Session;

import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class ReservationConsultationController implements Initializable {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    @FXML
    private ComboBox<String> comboBoxMovies;
    @FXML
    private DatePicker datePickerDate;
    @FXML
    private TableView<Reservation> tableViewReservations;
    @FXML
    private TableColumn<Reservation, String> colUser;
    @FXML
    private TableColumn<Reservation, String> colMovie;
    @FXML
    private TableColumn<Reservation, String> colAuditorium;
    @FXML
    private TableColumn<Reservation, String> colSeat;
    @FXML
    private TableColumn<Reservation, String> colDate;
    @FXML
    private TableColumn<Reservation, String> colTime;
    @FXML
    private TableColumn<Reservation, String> colStatus;
    @FXML
    private TableColumn<Reservation, Void> colActions;
    @FXML
    private Button btnBack;

    private ObservableList<Reservation> reservationsList;
    private User loggedUser;

    public void setLoggedUser(User user) {
        this.loggedUser = (user != null) ? user : Session.getCurrentUser();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configureTable();
        loadCombos();
        loadReservations();
    }

    private void configureTable() {
        colUser.setCellValueFactory(new PropertyValueFactory<>("user"));
        colMovie.setCellValueFactory(new PropertyValueFactory<>("movie"));
        colAuditorium.setCellValueFactory(new PropertyValueFactory<>("auditorium"));
        colSeat.setCellValueFactory(new PropertyValueFactory<>("seat"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("showDate"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("showTime"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button btnCancel = new Button("Cancelar");

            {
                btnCancel.setOnAction(event -> {
                    int index = getIndex();
                    if (index >= 0 && index < getTableView().getItems().size()) {
                        cancelReservation(getTableView().getItems().get(index));
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                int index = getIndex();
                if (empty || index < 0 || index >= getTableView().getItems().size()) {
                    setGraphic(null);
                } else {
                    Reservation reservation = getTableView().getItems().get(index);
                    btnCancel.setDisable("CANCELLED".equals(reservation.getStatus()));
                    setGraphic(btnCancel);
                }
            }
        });

        reservationsList = FXCollections.observableArrayList();
        tableViewReservations.setItems(reservationsList);
    }

    private void loadCombos() {
        comboBoxMovies.getItems().add("Todas");
        comboBoxMovies.setValue("Todas");

        String sql = "SELECT DISTINCT title FROM movie ORDER BY title";
        try (Connection conn = DatabaseConnection.getDatabaseConnectionInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                comboBoxMovies.getItems().add(rs.getString("title"));
            }
        } catch (SQLException e) {
            showError("Error", "No se cargaron películas: " + e.getMessage());
        }
    }

    @FXML
    private void loadReservations() {
        reservationsList.clear();

        String sql = "{CALL sp_get_all_reservations()}";

        try (Connection conn = DatabaseConnection.getDatabaseConnectionInstance().getConnection();
             CallableStatement cstmt = conn.prepareCall(sql);
             ResultSet rs = cstmt.executeQuery()) {

            while (rs.next()) {
                reservationsList.add(readReservation(rs));
            }
        } catch (SQLException e) {
            showError("Error", "No se cargaron reservas: " + e.getMessage());
        }
    }

    private Reservation readReservation(ResultSet rs) throws SQLException {
        String date = rs.getDate("show_date").toLocalDate().format(DATE_FORMAT);
        java.sql.Time sqlTime = rs.getTime("show_time");
        String time = sqlTime != null ? sqlTime.toLocalTime().format(TIME_FORMAT) : "N/A";

        return new Reservation(
                rs.getInt("reservation_id"),
                rs.getString("user_name"),
                rs.getString("movie_title"),
                rs.getString("auditorium_name"),
                String.valueOf(rs.getInt("seat_number")),
                date,
                time,
                rs.getString("status")
        );
    }

    @FXML
    private void filterReservations() {
        reservationsList.clear();

        String movie = comboBoxMovies.getValue();
        LocalDate date = datePickerDate.getValue();

        StringBuilder sql = new StringBuilder(
                "SELECT r.reservation_id, COALESCE(r.customer_name, u.full_name) AS user_name, "
                + "m.title AS movie_title, "
                + "a.name AS auditorium_name, s.seat_number AS seat_number, "
                + "sc.show_date AS show_date, sc.show_time AS show_time, "
                + "r.status AS status "
                + "FROM reservation r "
                + "INNER JOIN users u ON r.user_id = u.user_id "
                + "INNER JOIN screening sc ON r.screening_id = sc.screening_id "
                + "INNER JOIN movie m ON sc.movie_id = m.movie_id "
                + "INNER JOIN auditorium a ON sc.auditorium_id = a.auditorium_id "
                + "INNER JOIN seat s ON r.seat_id = s.seat_id "
                + "WHERE 1=1"
        );

        boolean byMovie = movie != null && !movie.equals("Todas");
        if (byMovie) {
            sql.append(" AND m.title = ?");
        }
        if (date != null) {
            sql.append(" AND sc.show_date = ?");
        }
        sql.append(" ORDER BY r.reservation_date DESC");

        try (Connection conn = DatabaseConnection.getDatabaseConnectionInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            if (byMovie) {
                pstmt.setString(paramIndex++, movie);
            }
            if (date != null) {
                pstmt.setDate(paramIndex, java.sql.Date.valueOf(date));
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    reservationsList.add(readReservation(rs));
                }
            }
        } catch (SQLException e) {
            showError("Error", "No se filtraron reservas: " + e.getMessage());
        }
    }

    @FXML
    private void clearFilters() {
        comboBoxMovies.setValue("Todas");
        datePickerDate.setValue(null);
        loadReservations();
    }

    private void cancelReservation(Reservation reservation) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmar Cancelación");
        confirmation.setHeaderText("¿Estás seguro de cancelar esta reserva?");
        confirmation.setContentText("Cliente: " + reservation.getUser() + "\n"
                + "Película: " + reservation.getMovie() + "\n"
                + "Asiento: " + reservation.getSeat());

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (executeCancellation(reservation.getId())) {
                    showSuccess("Éxito", "La reserva ha sido cancelada y el asiento liberado.");
                    filterReservations();
                }
            }
        });
    }

    private boolean executeCancellation(int reservationId) {
        String sql = "{CALL sp_cancel_reservation(?)}";

        try (Connection conn = DatabaseConnection.getDatabaseConnectionInstance().getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.setInt(1, reservationId);
            cstmt.execute();
            return true;
        } catch (SQLException e) {
            showError("Error", "No se pudo cancelar la reserva: " + e.getMessage());
            return false;
        }
    }

    @FXML
    private void goBackToPanel() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/cineplex/system/view/Manager.fxml"));
            Parent root = loader.load();

            ManagerController controller = loader.getController();
            if (controller != null) {
                controller.setLoggedUser(this.loggedUser);
            }

            Stage stage = (Stage) btnBack.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Panel de Gerente - CinePlex");
        } catch (Exception e) {
            showError("Error", "No se pudo volver al panel.");
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

    // Inner class for the table model
    public static class Reservation {

        private final int id;
        private final javafx.beans.property.SimpleStringProperty user, movie, auditorium, seat, showDate, showTime, status;

        public Reservation(int id, String user, String movie, String auditorium, String seat,
                String showDate, String showTime, String status) {
            this.id = id;
            this.user = new javafx.beans.property.SimpleStringProperty(user);
            this.movie = new javafx.beans.property.SimpleStringProperty(movie);
            this.auditorium = new javafx.beans.property.SimpleStringProperty(auditorium);
            this.seat = new javafx.beans.property.SimpleStringProperty(seat);
            this.showDate = new javafx.beans.property.SimpleStringProperty(showDate);
            this.showTime = new javafx.beans.property.SimpleStringProperty(showTime);
            this.status = new javafx.beans.property.SimpleStringProperty(status);
        }

        public int getId() {
            return id;
        }

        public String getUser() {
            return user.get();
        }

        public String getMovie() {
            return movie.get();
        }

        public String getAuditorium() {
            return auditorium.get();
        }

        public String getSeat() {
            return seat.get();
        }

        public String getShowDate() {
            return showDate.get();
        }

        public String getShowTime() {
            return showTime.get();
        }

        public String getStatus() {
            return status.get();
        }
    }
}
