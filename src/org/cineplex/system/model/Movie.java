package org.cineplex.system.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

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

    // --- movieId ---
    public int getMovieId() {
        return movieId.get();
    }

    public void setMovieId(int value) {
        movieId.set(value);
    }

    public IntegerProperty movieIdProperty() {
        return movieId;
    }

    // --- title ---
    public String getTitle() {
        return title.get();
    }

    public void setTitle(String value) {
        title.set(value);
    }

    public StringProperty titleProperty() {
        return title;
    }

    // --- duration ---
    public int getDuration() {
        return duration.get();
    }

    public void setDuration(int value) {
        duration.set(value);
    }

    public IntegerProperty durationProperty() {
        return duration;
    }

    // --- director ---
    public String getDirector() {
        return director.get();
    }

    public void setDirector(String value) {
        director.set(value);
    }

    public StringProperty directorProperty() {
        return director;
    }

    // --- genreId ---
    public int getGenreId() {
        return genreId.get();
    }

    public void setGenreId(int value) {
        genreId.set(value);
    }

    public IntegerProperty genreIdProperty() {
        return genreId;
    }

    // --- genreName ---
    public String getGenreName() {
        return genreName.get();
    }

    public void setGenreName(String value) {
        genreName.set(value);
    }

    public StringProperty genreNameProperty() {
        return genreName;
    }

    // --- rating ---
    public String getRating() {
        return rating.get();
    }

    public void setRating(String value) {
        rating.set(value);
    }

    public StringProperty ratingProperty() {
        return rating;
    }

    // --- posterUrl ---
    public String getPosterUrl() {
        return posterUrl.get();
    }

    public void setPosterUrl(String value) {
        posterUrl.set(value);
    }

    public StringProperty posterUrlProperty() {
        return posterUrl;
    }

    // ✅ CORREGIDO: Ahora devuelve el String real, no el objeto Property
    @Override
    public String toString() {
        return getTitle();
    }
}
