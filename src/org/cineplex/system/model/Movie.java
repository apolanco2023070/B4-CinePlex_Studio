/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cineplex.system.model;

/**
 *
 * @author informatica
 */
public class Movie {

    private String movieName;
    private String rating;
    private String genre;
    private Integer length;
    private String director;
    private String posterUrl;

    public Movie() {
    }

    public Movie(String movieName, String rating, String genre, Integer length, String director, String posterUrl) {
        this.movieName = movieName;
        this.rating = rating;
        this.genre = genre;
        this.length = length;
        this.director = director;
        this.posterUrl = posterUrl;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

}
