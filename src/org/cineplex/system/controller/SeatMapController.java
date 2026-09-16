/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cineplex.system.controller;

import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.cineplex.system.model.Screening;
import org.cineplex.system.model.SeatStatus;
import org.cineplex.system.repository.ReservationRepository;
import org.cineplex.system.utils.AlertInformation;

/**
 *
 * @author informatica
 */
public class SeatMapController {

    @FXML
    private Label lblMovieInfo;

    @FXML
    private VBox seatMapContainer;

    @FXML
    private Button btnClose;

    private Screening currentScreening;
    private final ReservationRepository reservationRepository;

    public SeatMapController() {
        this.reservationRepository = new ReservationRepository();
    }

    // Recibe los datos desde la vista anterior
    public void initData(Screening screening) {
        this.currentScreening = screening;
        lblMovieInfo.setText(screening.getMovieTitle() + " | "
                + screening.getAuditoriumName() + " | "
                + screening.getShowDate() + " " + screening.getShowTime());

        loadSeats();
    }

    private void loadSeats() {
        try {
            List<SeatStatus> seats = reservationRepository.getSeatsForScreening(currentScreening.getScreeningId());
            renderSeatMap(seats);
        } catch (Exception e) {
            AlertInformation.viewAlert("ERROR", "Error", "No se pudieron cargar los asientos", e.getMessage());
        }
    }

    private void renderSeatMap(List<SeatStatus> seats) {
        seatMapContainer.getChildren().clear();

        int seatsPerRow = 10;
        char currentRow = 'A';
        HBox currentRowBox = createRowLabel(currentRow);
        seatMapContainer.getChildren().add(currentRowBox);

        for (int i = 0; i < seats.size(); i++) {
            SeatStatus seat = seats.get(i);
            Button seatButton = createSeatButton(seat);
            currentRowBox.getChildren().add(seatButton);

            // Nueva fila cada 10 asientos
            if ((i + 1) % seatsPerRow == 0 && i < seats.size() - 1) {
                currentRow++;
                currentRowBox = createRowLabel(currentRow);
                seatMapContainer.getChildren().add(currentRowBox);
            }
        }
    }

    private HBox createRowLabel(char row) {
        HBox rowBox = new HBox(5);
        Label rowLabel = new Label(String.valueOf(row));
        rowLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-min-width: 30;");
        rowBox.getChildren().add(rowLabel);
        return rowBox;
    }

    private Button createSeatButton(SeatStatus seat) {
        Button btn = new Button(String.valueOf(seat.getSeatNumber()));
        btn.setPrefSize(40, 40);
        btn.setDisable(true); // No se puede hacer clic (solo visualización)

        if ("RESERVED".equals(seat.getStatus())) {
            btn.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-weight: bold;");
        } else {
            btn.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-weight: bold;");
        }

        // Tooltip para mostrar información al pasar el mouse
        btn.setTooltip(new javafx.scene.control.Tooltip(
                "Asiento " + seat.getSeatNumber() + " - " + seat.getStatus()
        ));

        return btn;
    }

    @FXML
    private void closeWindow() {
        ((Stage) btnClose.getScene().getWindow()).close();
    }
}
