package org.cineplex.system.model;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 
 * @author informatica
 */
public class Screening {
    private Integer screeningId;
    private Integer movieId;
    private String movieTitle;
    private Integer auditoriumId;
    private String auditoriumName;
    private LocalDate showDate;
    private LocalTime showTime;

    public Screening() {
    }

    public Screening(Integer movieId, Integer auditoriumId, LocalDate showDate, LocalTime showTime) {
        this.movieId = movieId;
        this.auditoriumId = auditoriumId;
        this.showDate = showDate;
        this.showTime = showTime;
    }

    // Getters y Setters
    public Integer getScreeningId() {
        return screeningId;
    }

    public void setScreeningId(Integer screeningId) {
        this.screeningId = screeningId;
    }

    public Integer getMovieId() {
        return movieId;
    }

    public void setMovieId(Integer movieId) {
        this.movieId = movieId;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public Integer getAuditoriumId() {
        return auditoriumId;
    }

    public void setAuditoriumId(Integer auditoriumId) {
        this.auditoriumId = auditoriumId;
    }

    public String getAuditoriumName() {
        return auditoriumName;
    }

    public void setAuditoriumName(String auditoriumName) {
        this.auditoriumName = auditoriumName;
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

    // Método auxiliar para mostrar la hora como String
    public String getShowTimeString() {
        return showTime != null ? showTime.toString() : "";
    }

    @Override
    public String toString() {
        return (movieTitle != null ? movieTitle : "") + " - " + 
               (auditoriumName != null ? auditoriumName : "") + 
               " (" + showDate + " " + showTime + ")";
    }
}