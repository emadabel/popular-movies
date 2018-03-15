package com.emadabel.popularmovies.model;

/**
 * Created by lenovo on 13/03/2018.
 */

public class Review {

    private String reviewAuthor;
    private String reviewContent;
    private String reviewUrl;

    public Review() {
    }

    public Review(String reviewAuthor, String reviewContent, String reviewUrl) {
        this.reviewAuthor = reviewAuthor;
        this.reviewContent = reviewContent;
        this.reviewUrl = reviewUrl;
    }

    public String getReviewAuthor() {
        return reviewAuthor;
    }

    public void setReviewAuthor(String reviewAuthor) {
        this.reviewAuthor = reviewAuthor;
    }

    public String getReviewContent() {
        return reviewContent;
    }

    public void setReviewContent(String reviewContent) {
        this.reviewContent = reviewContent;
    }

    public String getReviewUrl() {
        return reviewUrl;
    }

    public void setReviewUrl(String reviewUrl) {
        this.reviewUrl = reviewUrl;
    }
}
