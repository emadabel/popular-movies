package com.emadabel.popularmovies.utils;

import com.emadabel.popularmovies.model.Movie;

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

        final String TMDB_VOTE_COUNT = "vote_count";
        final String TMDB_VOTE_AVERAGE = "vote_average";
        final String TMDB_TITLE = "title";
        final String TMDB_POSTER_PATH = "poster_path";
        final String TMDB_ORIGINAL_TITLE = "original_title";
        final String TMDB_OVERVIEW = "overview";
        final String TMDB_RELEASE_DATE = "release_date";

        JSONObject movieObj = new JSONObject(movieJsonStr);

        Movie movie = new Movie();
        movie.setOriginalTitle(movieObj.optString(TMDB_ORIGINAL_TITLE));
        movie.setOverview(movieObj.optString(TMDB_OVERVIEW));
        movie.setPosterPath(movieObj.optString(TMDB_POSTER_PATH));
        movie.setReleaseDate(movieObj.optString(TMDB_RELEASE_DATE));
        movie.setTitle(movieObj.optString(TMDB_TITLE));
        movie.setVoteAverage(movieObj.optString(TMDB_VOTE_AVERAGE));
        movie.setVoteCount(movieObj.optString(TMDB_VOTE_COUNT));

        return movie;
    }
}
