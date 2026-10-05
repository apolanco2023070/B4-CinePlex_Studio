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

    @FXML
    private Label lblTicketNumber;
    
    @FXML
    private Label lblMovie;
    
    @FXML
    private Label lblAuditorium;
    
    @FXML
    private Label lblDate;
    
    @FXML
    private Label lblTime;
    
    @FXML
    private Label lblSeat;
    
    @FXML
    private Label lblUser;
    
    @FXML
    private Label lblIssueDate;
    
    @FXML
    private Button btnClose;

    private TicketData ticketData;

    public void initData(TicketData ticketData) {
        this.ticketData = ticketData;
        loadTicketData();
    }

    private void loadTicketData() {
        Integer number = ticketData.getTicketNumber();
        lblTicketNumber.setText("TICKET #" + (number != null ? String.format("%03d", number) : "---"));

        lblMovie.setText(textOrDash(ticketData.getMovieTitle()));
        lblAuditorium.setText(textOrDash(ticketData.getAuditoriumName()));
        lblDate.setText(ticketData.getShowDate() != null ? ticketData.getShowDate().toString() : "-");
        lblTime.setText(ticketData.getShowTime() != null ? ticketData.getShowTime().toString() : "-");

        lblSeat.setText(ticketData.getSeatNumber() != null ? "Asiento " + ticketData.getSeatNumber() : "-");

        lblUser.setText(textOrDash(ticketData.getUserName()));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        lblIssueDate.setText(ticketData.getIssueDate() != null ? ticketData.getIssueDate().format(formatter) : "-");
    }

    private String textOrDash(String text) {
        return (text == null || text.isBlank()) ? "-" : text;
    }

    @FXML
    private void closeTicket() {
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
    }
}
