/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cineplex.system.model;

import java.time.LocalDateTime;

/**
 * Representa una reserva de un asiento para una función específica.
 *
 * @author informatica
 */
public class Reservation {

    private Integer reservationId;
    private Integer userId;
    private Integer screeningId;
    private Integer seatId;
    private LocalDateTime reservationDate;
    private String status;

    public Reservation() {
    }

    public Reservation(Integer userId, Integer screeningId, Integer seatId) {
        this.userId = userId;
        this.screeningId = screeningId;
        this.seatId = seatId;
        this.status = "RESERVED";
    }

    public Integer getReservationId() {
        return reservationId;
    }

    public void setReservationId(Integer reservationId) {
        this.reservationId = reservationId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getScreeningId() {
        return screeningId;
    }

    public void setScreeningId(Integer screeningId) {
        this.screeningId = screeningId;
    }

    public Integer getSeatId() {
        return seatId;
    }

    public void setSeatId(Integer seatId) {
        this.seatId = seatId;
    }

    public LocalDateTime getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(LocalDateTime reservationDate) {
        this.reservationDate = reservationDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
