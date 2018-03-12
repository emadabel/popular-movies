package com.emadabel.popularmovies.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.text.TextUtils;

import com.emadabel.popularmovies.BuildConfig;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by Emad on 23/02/2018.
 */

public class NetworkUtils {

    private static final String API_BASE_URL = "https://api.themoviedb.org/3/movie/";

    private static final String API_POPULAR_PATH = "popular";
    private static final String API_TOP_RATED_PATH = "top_rated";

    private static final String API_BASE_IMAGE_URL = "http://image.tmdb.org/t/p/";
    private static final String POSTER_SIZE = "w185";

    private static final String YOUTUBE_BASE_PREVIEW = "http://img.youtube.com/vi/";
    private static final String YOUTUBE_BASE_URL = "https://www.youtube.com/watch?v=";

    private static final String API_KEY = BuildConfig.TMDB_API_KEY;
    private static final String API_APPEND = "videos,reviews";

    private static final String API_KEY_PARAM = "api_key";
    private static final String API_APPEND_PARAM = "append_to_response";

    /**
     * @param context the activity that checking for connectivity
     * @return the value true if there is internet and false if no any internet connection
     * reference: https://developer.android.com/training/monitoring-device-state/connectivity-monitoring.html
     */
    private static boolean isConnectionAvailable(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        }

        return false;
    }

    /**
     * @param request Is one of three options(movieId, popularList, topRated) that will be queried for.
     * @return The URL to use to query the movies database.
     */
    public static URL buildUrl(String request, boolean requestExtra) {

        if (TextUtils.isEmpty(request)) return null;

        String baseUrlBuilder = API_BASE_URL + request;

        Uri builtUri;
        if (requestExtra) {
            builtUri = Uri.parse(baseUrlBuilder).buildUpon()
                    .appendQueryParameter(API_KEY_PARAM, API_KEY)
                    .appendQueryParameter(API_APPEND_PARAM, API_APPEND)
                    .build();
        } else {
            builtUri = Uri.parse(baseUrlBuilder).buildUpon()
                    .appendQueryParameter(API_KEY_PARAM, API_KEY)
                    .build();
        }

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static String buildPosterUrl(String posterPath) {
        return (API_BASE_IMAGE_URL)
                .concat(POSTER_SIZE)
                .concat(posterPath);
    }

    public static String[] buildTrialPreviewUrl(String videoKey) {
        String[] trail = new String[2];
        trail[0] = (YOUTUBE_BASE_PREVIEW)
                .concat(videoKey)
                .concat("/0.jpg");
        trail[1] = (YOUTUBE_BASE_URL)
                .concat(videoKey);
        return trail;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(Context context, URL url) throws IOException {

        if (!isConnectionAvailable(context)) return null;

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
