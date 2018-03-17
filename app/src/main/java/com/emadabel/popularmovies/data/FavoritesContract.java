package com.emadabel.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by lenovo on 15/03/2018.
 */

public class FavoritesContract {

    public static final String CONTENT_AUTHORITY = "com.emadabel.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String FAVORITES_PATH = "favorites";

    private FavoritesContract() {
    }

    public static class FavoritesEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(FAVORITES_PATH)
                .build();

        public static final String TABLE_NAME = "favorites";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_POSTER = "poster_path";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_ORIGINAL_TITLE = "original_title";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_VOTE_COUNT = "vote_count";

        public static Uri buildFavoritesUriWithMovieId(int movieId) {
            return CONTENT_URI.buildUpon()
                    .appendPath(Integer.toString(movieId))
                    .build();
        }
    }
}
