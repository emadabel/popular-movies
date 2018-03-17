package com.emadabel.popularmovies.model;

import java.util.List;

/**
 * Created by Emad on 24/02/2018.
 */

public class Movie {

    private int id;
    private String posterPath;
    private String title;
    private String originalTitle;
    private String releaseDate;
    private String overview;
    private String voteAverage;
    private String voteCount;
    private List<Trial> trials;
    private List<Review> reviews;

    public Movie(int id, String posterPath, String title, String originalTitle, String releaseDate,
                 String overview, String voteAverage, String voteCount, List<Trial> trials, List<Review> reviews) {
        this.id = id;
        this.posterPath = posterPath;
        this.title = title;
        this.originalTitle = originalTitle;
        this.releaseDate = releaseDate;
        this.overview = overview;
        this.voteAverage = voteAverage;
        this.voteCount = voteCount;
        this.trials = trials;
        this.reviews = reviews;
    }

    /**
     * No args constructor for use in serialization
     */
    public Movie() {
    }

    public List<Trial> getTrials() {
        return trials;
    }

    public void setTrials(List<Trial> trials) {
        this.trials = trials;
    }

    public String getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(String voteCount) {
        this.voteCount = voteCount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(String voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }
}
