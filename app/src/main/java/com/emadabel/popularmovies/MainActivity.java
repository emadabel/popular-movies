package com.emadabel.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.emadabel.popularmovies.model.Movie;
import com.emadabel.popularmovies.utils.NetworkUtils;
import com.emadabel.popularmovies.utils.TmdbJsonUtils;

import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        LoaderCallbacks<List<Movie>>,
        SharedPreferences.OnSharedPreferenceChangeListener,
        MoviesAdapter.MovieAdapterOnClickHandler {

    private static final int TMDB_LOADER_ID = 110;

    private static boolean PREFERENCES_HAVE_BEEN_UPDATED = false;

    private RecyclerView mRecyclerView;
    private TextView mErrorMessageTv;
    private ProgressBar mLoadingIndicatorPb;
    private MoviesAdapter moviesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        mRecyclerView = findViewById(R.id.movies_list_rv);
        mErrorMessageTv = findViewById(R.id.error_message_tv);
        mLoadingIndicatorPb = findViewById(R.id.loading_indicator_pb);

        moviesAdapter = new MoviesAdapter(this, this);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(moviesAdapter);

        getSupportLoaderManager().initLoader(TMDB_LOADER_ID, null, this);

        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(startSettingsActivity);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<List<Movie>> onCreateLoader(int id, final Bundle loaderArgs) {
        return new AsyncTaskLoader<List<Movie>>(this) {

            List<Movie> mMovies = null;

            @Override
            protected void onStartLoading() {
                if (mMovies != null) {
                    deliverResult(mMovies);
                } else {
                    mLoadingIndicatorPb.setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.INVISIBLE);
                    forceLoad();
                }
            }

            @Override
            public List<Movie> loadInBackground() {

                URL tmdbRequestUrl = NetworkUtils.buildUrl(getSortType());

                try {
                    String jsonResponse = NetworkUtils
                            .getResponseFromHttpUrl(MainActivity.this, tmdbRequestUrl);

                    if (TextUtils.isEmpty(jsonResponse)) {
                        return null;
                    }

                    return TmdbJsonUtils
                            .getMoviesListFromJson(jsonResponse);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void deliverResult(List<Movie> data) {
                mMovies = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> data) {
        mLoadingIndicatorPb.setVisibility(View.INVISIBLE);
        moviesAdapter.setMoviesData(data);
        if (data == null) {
            showErrorMessage();
        } else {
            showMoviesData();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) {

    }

    private String getSortType() {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(this);
        String keyForSort = getString(R.string.pref_sort_key);
        String defaultSortType = getString(R.string.pref_sort_popular);
        return prefs.getString(keyForSort, defaultSortType);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (PREFERENCES_HAVE_BEEN_UPDATED) {
            getSupportLoaderManager().restartLoader(TMDB_LOADER_ID, null, this);
            PREFERENCES_HAVE_BEEN_UPDATED = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    private void showMoviesData() {
        mRecyclerView.setVisibility(View.VISIBLE);
        mErrorMessageTv.setVisibility(View.INVISIBLE);
    }

    private void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageTv.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        PREFERENCES_HAVE_BEEN_UPDATED = true;
    }

    @Override
    public void onClick(int movieId) {
        Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
        intent.putExtra(DetailsActivity.EXTRA_MOVIE_ID, movieId);
        startActivity(intent);
    }
}
