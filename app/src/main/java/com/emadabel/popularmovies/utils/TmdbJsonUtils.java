package com.emadabel.popularmovies.utils;

import com.emadabel.popularmovies.model.Movie;
import com.emadabel.popularmovies.model.Review;
import com.emadabel.popularmovies.model.Trial;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Emad on 24/02/2018.
 */

public class TmdbJsonUtils {

    public static List<Movie> getMoviesListFromJson(String moviesJsonStr)
            throws JSONException{

        final String TMDB_RESULTS = "results";
        final String TMDB_ID = "id";
        final String TMDB_POSTER_PATH = "poster_path";

        List<Movie> parseMoviesData = new ArrayList<>();

        JSONObject moviesJson = new JSONObject(moviesJsonStr);

        if (!moviesJson.has(TMDB_RESULTS)) {
            return null;
        }

        JSONArray resultsArray = moviesJson.getJSONArray(TMDB_RESULTS);

        for (int i = 0; i < resultsArray.length(); i++) {
            Movie movieData = new Movie();
            JSONObject movieObj = resultsArray.getJSONObject(i);
            movieData.setPosterPath(movieObj.optString(TMDB_POSTER_PATH));
            movieData.setId(movieObj.optInt(TMDB_ID));

            parseMoviesData.add(movieData);
        }

        return parseMoviesData;
    }

    public static Movie getMovieDataFromJson(String movieJsonStr)
            throws JSONException {

        final String TMDB_ID = "id";
        final String TMDB_VOTE_COUNT = "vote_count";
        final String TMDB_VOTE_AVERAGE = "vote_average";
        final String TMDB_TITLE = "title";
        final String TMDB_POSTER_PATH = "poster_path";
        final String TMDB_ORIGINAL_TITLE = "original_title";
        final String TMDB_OVERVIEW = "overview";
        final String TMDB_RELEASE_DATE = "release_date";
        final String TMDB_VIDEOS = "videos";
        final String TMDB_REVIEWS = "reviews";
        final String TMDB_RESULTS = "results";
        final String TMDB_VIDEO_KEY = "key";
        final String TMDB_REVIEW_AUTHOR = "author";
        final String TMDB_REVIEW_CONTENT = "content";
        final String TMDB_REVIEW_URL = "url";

        JSONObject movieObj = new JSONObject(movieJsonStr);

        // parsing main movie data
        Movie movie = new Movie();
        movie.setId(movieObj.optInt(TMDB_ID));
        movie.setOriginalTitle(movieObj.optString(TMDB_ORIGINAL_TITLE));
        movie.setOverview(movieObj.optString(TMDB_OVERVIEW));
        movie.setPosterPath(movieObj.optString(TMDB_POSTER_PATH));
        movie.setReleaseDate(movieObj.optString(TMDB_RELEASE_DATE));
        movie.setTitle(movieObj.optString(TMDB_TITLE));
        movie.setVoteAverage(movieObj.optString(TMDB_VOTE_AVERAGE));
        movie.setVoteCount(movieObj.optString(TMDB_VOTE_COUNT));

        //parsing movie's videos/trials
        JSONObject trialsObj = movieObj.getJSONObject(TMDB_VIDEOS);
        JSONArray trialsArray = trialsObj.getJSONArray(TMDB_RESULTS);

        List<Trial> trials = new ArrayList<>();
        for (int i = 0; i < trialsArray.length(); i++) {
            JSONObject trialObj = trialsArray.getJSONObject(i);
            String videoKey = trialObj.optString(TMDB_VIDEO_KEY);
            String[] videoUrls = NetworkUtils.buildTrialPreviewUrl(videoKey);
            Trial trial = new Trial();
            trial.setTrialImage(videoUrls[0]);
            trial.setTrialUrl(videoUrls[1]);
            trials.add(trial);
        }

        movie.setTrials(trials);

        //parsing movie's reviews
        JSONObject reviewsObj = movieObj.getJSONObject(TMDB_REVIEWS);
        JSONArray reviewsArray = reviewsObj.getJSONArray(TMDB_RESULTS);

        List<Review> reviews = new ArrayList<>();
        for (int i = 0; i < reviewsArray.length(); i++) {
            JSONObject reviewObj = reviewsArray.getJSONObject(i);
            String reviewAuthor = reviewObj.optString(TMDB_REVIEW_AUTHOR);
            String reviewContent = reviewObj.optString(TMDB_REVIEW_CONTENT);
            String reviewUrl = reviewObj.optString(TMDB_REVIEW_URL);
            Review review = new Review(reviewAuthor, reviewContent, reviewUrl);
            reviews.add(review);
        }

        movie.setReviews(reviews);

        return movie;
    }
}
