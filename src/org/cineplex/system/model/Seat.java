/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cineplex.system.model;

/**
 *
 * @author informatica
 */
public class Seat {

    private Integer seatId;
    private Integer seatNumber;
    private Integer auditoriumId;

    public Seat() {
    }

    public Seat(Integer seatNumber, Integer auditoriumId) {
        this.seatNumber = seatNumber;
        this.auditoriumId = auditoriumId;
    }

    // Getters y Setters
    public Integer getSeatId() {
        return seatId;
    }

    public void setSeatId(Integer seatId) {
        this.seatId = seatId;
    }

    public Integer getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(Integer seatNumber) {
        this.seatNumber = seatNumber;
    }

    public Integer getAuditoriumId() {
        return auditoriumId;
    }

    public void setAuditoriumId(Integer auditoriumId) {
        this.auditoriumId = auditoriumId;
    }

    public String getStatus() {
        return "Disponible";
    }
}
