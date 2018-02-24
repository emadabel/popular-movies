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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.emadabel.popularmovies.model.Movie;
import com.emadabel.popularmovies.utils.NetworkUtils;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements
        LoaderCallbacks<Movie[]>,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private static final int TMDB_LOADER_ID = 110;

    private static boolean PREFERENCES_HAVE_BEEN_UPDATED = false;

    private RecyclerView mRecyclerView;
    private TextView mErrorMessageTv;
    private ProgressBar mLoadingIndicatorPb;
    private Spinner mSpinner;
    private MoviesAdapter moviesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        mRecyclerView = (RecyclerView) findViewById(R.id.movies_list_rv);
        mErrorMessageTv = (TextView) findViewById(R.id.error_message_tv);
        mLoadingIndicatorPb = (ProgressBar) findViewById(R.id.loading_indicator_pb);

        moviesAdapter = new MoviesAdapter();

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(moviesAdapter);

        getSupportLoaderManager().initLoader(TMDB_LOADER_ID, null, this);

        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
    }

    /**
     * @param menu
     * @return reference: https://stackoverflow.com/questions/37250397/how-to-add-a-spinner-next-to-a-menu-in-the-toolbar
     * reference: https://developer.android.com/guide/topics/ui/controls/spinner.html
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        MenuItem item = menu.findItem(R.id.spinner);
        mSpinner = (Spinner) item.getActionView();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.spinner_list_item_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mSpinner.setAdapter(adapter);

        updateSpinner();

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                String value = "";
                if (pos == 1) {
                    value = getString(R.string.pref_sort_top_rated);
                } else {
                    value = getString(R.string.pref_sort_popular);
                }
                SharedPreferences prefs = PreferenceManager
                        .getDefaultSharedPreferences(MainActivity.this);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(getString(R.string.pref_sort_key), value);
                editor.apply();
                getSupportLoaderManager().restartLoader(TMDB_LOADER_ID, null, MainActivity.this);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return true;
    }

    private void updateSpinner() {
        if (mSpinner != null) {
            String sortType = getSortType();
            if (sortType.equals(getString(R.string.pref_sort_top_rated))) {
                mSpinner.setSelection(1);
            } else {
                mSpinner.setSelection(0);
            }
        }
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
    public Loader<Movie[]> onCreateLoader(int id, final Bundle loaderArgs) {
        return new AsyncTaskLoader<Movie[]>(this) {

            Movie[] mMovies = null;

            @Override
            protected void onStartLoading() {
                if (mMovies != null) {
                    deliverResult(mMovies);
                } else {
                    mLoadingIndicatorPb.setVisibility(View.VISIBLE);
                    forceLoad();
                }
            }

            @Override
            public Movie[] loadInBackground() {

                URL tmdbRequestUrl = NetworkUtils.buildUrl(getSortType());

                try {
                    String jsonResponse = NetworkUtils
                            .getResponseFromHttpUrl(MainActivity.this, tmdbRequestUrl);

                    if (TextUtils.isEmpty(jsonResponse)) {
                        return null;
                    }
                    return new Movie[0];
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void deliverResult(Movie[] data) {
                mMovies = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Movie[]> loader, Movie[] data) {
        mLoadingIndicatorPb.setVisibility(View.INVISIBLE);
        if (data == null) {
            showErrorMessage();
        } else {
            showMoviesData();
        }
    }

    @Override
    public void onLoaderReset(Loader<Movie[]> loader) {

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
            updateSpinner();
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
}
