package org.cineplex.system.model;

import javafx.beans.property.*;

public class Movie {
    private final IntegerProperty movieId;
    private final StringProperty title;
    private final IntegerProperty duration;
    private final StringProperty director;
    private final IntegerProperty genreId;
    private final StringProperty genreName;
    private final StringProperty rating;
    private final StringProperty posterUrl;

    public Movie() {
        this.movieId = new SimpleIntegerProperty();
        this.title = new SimpleStringProperty();
        this.duration = new SimpleIntegerProperty();
        this.director = new SimpleStringProperty();
        this.genreId = new SimpleIntegerProperty();
        this.genreName = new SimpleStringProperty();
        this.rating = new SimpleStringProperty();
        this.posterUrl = new SimpleStringProperty();
    }

    public Movie(int movieId, String title, int duration, String director, int genreId, String rating, String posterUrl) {
        this();
        this.movieId.set(movieId);
        this.title.set(title);
        this.duration.set(duration);
        this.director.set(director);
        this.genreId.set(genreId);
        this.rating.set(rating);
        this.posterUrl.set(posterUrl);
    }

    // Getters y Setters
    public int getMovieId() { 
        return movieId.get(); }
    
    public void setMovieId(int value) {
        movieId.set(value); }
    
    public IntegerProperty movieIdProperty() {
        return movieId; }

    public String getTitle() { 
        return title.get(); }
    public void setTitle(String value) { 
        title.set(value); }
    public StringProperty titleProperty() { 
        return title; }

    public int getDuration() { 
        return duration.get(); }
    public void setDuration(int value) { 
        duration.set(value); }
    public IntegerProperty durationProperty() { 
        return duration; }

    public String getDirector() {
        return director.get(); }
    
    public void setDirector(String value) { 
        director.set(value); }
    
    public StringProperty directorProperty() {
        return director; }

    public int getGenreId() {
        return genreId.get(); }
    
    public void setGenreId(int value) {
        genreId.set(value); }
    
    public IntegerProperty genreIdProperty() { 
        return genreId; }

    public String getGenreName() {
        return genreName.get(); }
    
    public void setGenreName(String value) { 
        genreName.set(value); }
    
    public StringProperty genreNameProperty() { 
        return genreName; }

    public String getRating() {
        return rating.get(); }
    
    public void setRating(String value) { 
        rating.set(value); }
    
    public StringProperty ratingProperty() { 
        return rating; }

    public String getPosterUrl() { 
        return posterUrl.get(); }
    
    public void setPosterUrl(String value) {
        posterUrl.set(value); }
    
    public StringProperty posterUrlProperty() { 
        return posterUrl; }
}