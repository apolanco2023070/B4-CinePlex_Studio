/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cineplex.system.controller;

import java.time.format.DateTimeFormatter;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.cineplex.system.model.TicketData;

/**
 *
 * @author informatica
 */
public class TicketController {

    @FXML private Label lblTicketNumber;
    @FXML private Label lblMovie;
    @FXML private Label lblAuditorium;
    @FXML private Label lblDate;
    @FXML private Label lblTime;
    @FXML private Label lblSeat;
    @FXML private Label lblUser;
    @FXML private Label lblIssueDate;
    @FXML private Button btnClose;

    private TicketData ticketData;

    public void initData(TicketData ticketData) {
        this.ticketData = ticketData;
        cargarDatosTicket();
    }

    private void cargarDatosTicket() {
        // Número de ticket
        lblTicketNumber.setText("TICKET #" + String.format("%03d", ticketData.getTicketNumber()));

        // Información de la función
        lblMovie.setText(ticketData.getMovieTitle());
        lblAuditorium.setText(ticketData.getAuditoriumName());
        lblDate.setText(ticketData.getShowDate().toString());
        lblTime.setText(ticketData.getShowTime().toString());

        // Asiento
        lblSeat.setText("Asiento " + ticketData.getSeatNumber());

        // Usuario
        lblUser.setText(ticketData.getUserName());

        // Fecha de emisión
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        lblIssueDate.setText(ticketData.getIssueDate().format(formatter));
    }

    @FXML
    private void cerrarTicket() {
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
    }
}
    
