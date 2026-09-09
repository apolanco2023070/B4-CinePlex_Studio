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

    private Integer movieId;
    private String title;
    private Integer duration;
    private String director;
    private Integer genreId;
    private String rating;
    private String posterUrl;

    private String genreName;

    public Movie() {
    }

    public Movie(String title, Integer duration, String director,
            Integer genreId, String rating, String posterUrl) {
        this.title = title;
        this.duration = duration;
        this.director = director;
        this.genreId = genreId;
        this.rating = rating;
        this.posterUrl = posterUrl;
    }

    public Movie(Integer movieId, String title, Integer duration, String director,
            String genreName, String rating, String posterUrl) {
        this.movieId = movieId;
        this.title = title;
        this.duration = duration;
        this.director = director;
        this.genreName = genreName;
        this.rating = rating;
        this.posterUrl = posterUrl;
    }

    // Getters y Setters
    public Integer getMovieId() {
        return movieId;
    }

    public void setMovieId(Integer movieId) {
        this.movieId = movieId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public Integer getGenreId() {
        return genreId;
    }

    public void setGenreId(Integer genreId) {
        this.genreId = genreId;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public String getGenreName() {
        return genreName;
    }

    public void setGenreName(String genreName) {
        this.genreName = genreName;
    }
}
