package com.emadabel.popularmovies.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.emadabel.popularmovies.model.Movie;

import java.util.ArrayList;
import java.util.List;

import static com.emadabel.popularmovies.data.FavoritesContract.FavoritesEntry;
import static com.emadabel.popularmovies.data.FavoritesContract.FavoritesEntry.buildFavoritesUriWithMovieId;

public class Utils {

    public static boolean movieIsFavorite(Context context, int movieId) {
        Cursor cursor = context.getContentResolver().query(
                buildFavoritesUriWithMovieId(movieId),
                null,
                null,
                null,
                null);

        if (cursor == null)
            return false;

        if (cursor.getCount() == 0) {
            cursor.close();
            return false;
        }

        cursor.close();
        return true;
    }

    public static ContentValues movieToContentValues(Movie movie) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(FavoritesEntry.COLUMN_MOVIE_ID, movie.getId());
        contentValues.put(FavoritesEntry.COLUMN_POSTER, movie.getPosterPath());
        contentValues.put(FavoritesEntry.COLUMN_TITLE, movie.getTitle());
        contentValues.put(FavoritesEntry.COLUMN_ORIGINAL_TITLE, movie.getOriginalTitle());
        contentValues.put(FavoritesEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
        contentValues.put(FavoritesEntry.COLUMN_OVERVIEW, movie.getOverview());
        contentValues.put(FavoritesEntry.COLUMN_VOTE_AVERAGE, movie.getVoteAverage());
        contentValues.put(FavoritesEntry.COLUMN_VOTE_COUNT, movie.getVoteCount());

        return contentValues;
    }

    public static List<Movie> cursorToMovies(Cursor cursor) {
        List<Movie> movies = new ArrayList<>();

        while (cursor.moveToNext()) {
            Movie movieData = new Movie();
            movieData.setId(cursor.getInt(cursor.getColumnIndex(FavoritesEntry.COLUMN_MOVIE_ID)));
            movieData.setPosterPath(cursor.getString(cursor.getColumnIndex(FavoritesEntry.COLUMN_POSTER)));
            movieData.setTitle(cursor.getString(cursor.getColumnIndex(FavoritesEntry.COLUMN_TITLE)));
            movieData.setOriginalTitle(cursor.getString(cursor.getColumnIndex(FavoritesEntry.COLUMN_ORIGINAL_TITLE)));
            movieData.setReleaseDate(cursor.getString(cursor.getColumnIndex(FavoritesEntry.COLUMN_RELEASE_DATE)));
            movieData.setOverview(cursor.getString(cursor.getColumnIndex(FavoritesEntry.COLUMN_OVERVIEW)));
            movieData.setVoteAverage(cursor.getString(cursor.getColumnIndex(FavoritesEntry.COLUMN_VOTE_AVERAGE)));
            movieData.setVoteCount(cursor.getString(cursor.getColumnIndex(FavoritesEntry.COLUMN_VOTE_COUNT)));

            movies.add(movieData);
        }

        cursor.close();
        return movies;
    }
}
