/*

* Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license

* Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

*/

package org.cineplex.system.model;
 
import java.time.LocalDate;

import java.time.LocalTime;

import javafx.beans.property.SimpleStringProperty;
 
public class Screening {

    private int screeningId;

    private int movieId;

    private int auditoriumId;

    private LocalDate showDate;
    private LocalTime showTime;
    private String movieTitle;

    private String auditoriumName;

    public Screening() {}

    public Screening(int screeningId, int movieId, int auditoriumId, 

                     LocalDate showDate, LocalTime showTime) {

        this.screeningId = screeningId;

        this.movieId = movieId;

        this.auditoriumId = auditoriumId;

        this.showDate = showDate;

        this.showTime = showTime;

    }
 
    public int getScreeningId() {

        return screeningId;

    }
 
    public void setScreeningId(int screeningId) {

        this.screeningId = screeningId;

    }
 
    public int getMovieId() {

        return movieId;

    }
 
    public void setMovieId(int movieId) {

        this.movieId = movieId;

    }
 
    public int getAuditoriumId() {

        return auditoriumId;

    }
 
    public void setAuditoriumId(int auditoriumId) {

        this.auditoriumId = auditoriumId;

    }
 
    public LocalDate getShowDate() {

        return showDate;

    }
 
    public void setShowDate(LocalDate showDate) {

        this.showDate = showDate;

    }
 
    public LocalTime getShowTime() {

        return showTime;

    }
 
    public void setShowTime(LocalTime showTime) {

        this.showTime = showTime;

    }
 
    public String getMovieTitle() {

        return movieTitle;

    }
 
    public void setMovieTitle(String movieTitle) {

        this.movieTitle = movieTitle;

    }
 
    public String getAuditoriumName() {

        return auditoriumName;

    }
 
    public void setAuditoriumName(String auditoriumName) {

        this.auditoriumName = auditoriumName;

    }
 
    public SimpleStringProperty auditoriumNameProperty() {

        return new SimpleStringProperty(getAuditoriumName() != null ? getAuditoriumName() : "Sala " + getAuditoriumId());

    }

    public SimpleStringProperty movieTitleProperty() {

        return new SimpleStringProperty(getMovieTitle() != null ? getMovieTitle() : "Película ID: " + getMovieId());

    }

    public SimpleStringProperty showDateProperty() {

        return new SimpleStringProperty(getShowDate() != null ? getShowDate().toString() : "");

    }

    public SimpleStringProperty showTimeProperty() {

        return new SimpleStringProperty(getShowTime() != null ? getShowTime().toString() : "");

    }

    @Override
    public String toString() {
        return (getMovieTitle() != null ? getMovieTitle() : "Película " + getMovieId())
                + " - " + (getAuditoriumName() != null ? getAuditoriumName() : "Sala " + getAuditoriumId())
                + " (" + getShowDate() + " " + getShowTime() + ")";
    }

}
 