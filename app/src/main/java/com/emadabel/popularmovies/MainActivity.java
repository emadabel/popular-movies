package com.emadabel.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
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

import com.emadabel.popularmovies.adapters.MoviesAdapter;
import com.emadabel.popularmovies.data.FavoritesContract;
import com.emadabel.popularmovies.model.Movie;
import com.emadabel.popularmovies.utils.NetworkUtils;
import com.emadabel.popularmovies.utils.TmdbJsonUtils;
import com.emadabel.popularmovies.utils.Utils;

import java.net.URL;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements
        LoaderCallbacks<List<Movie>>,
        MoviesAdapter.MovieAdapterOnClickHandler {

    private static final int TMDB_LOADER_ID = 110;
    private static final int FAVORITES_LOADER_ID = 120;

    @BindView(R.id.movies_list_rv)
    RecyclerView mRecyclerView;
    @BindView(R.id.error_message_tv)
    TextView mErrorMessageTv;
    @BindView(R.id.loading_indicator_pb)
    ProgressBar mLoadingIndicatorPb;

    private MoviesAdapter moviesAdapter;
    private Spinner mSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        moviesAdapter = new MoviesAdapter(this, this);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(moviesAdapter);

        getSupportLoaderManager().initLoader(TMDB_LOADER_ID, null, this);
    }

    /**
     * @param menu The options menu in which you place your items
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

                if (pos == 0) {
                    value = getString(R.string.pref_sort_popular);
                }

                if (pos == 1) {
                    value = getString(R.string.pref_sort_top_rated);
                }

                if (pos == 2) {
                    // query
                    getSupportLoaderManager().restartLoader(FAVORITES_LOADER_ID, null, MainActivity.this);
                    return;
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

            if (sortType.equals(getString(R.string.pref_sort_popular))) {
                mSpinner.setSelection(0);
            }

            if (sortType.equals(getString(R.string.pref_sort_top_rated))) {
                mSpinner.setSelection(1);
            }
        }
    }

    @Override
    public Loader<List<Movie>> onCreateLoader(final int loaderId, final Bundle loaderArgs) {
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

                switch (loaderId) {
                    case TMDB_LOADER_ID:
                        URL tmdbRequestUrl = NetworkUtils.buildUrl(getSortType(), false);

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
                        }
                        break;

                    case FAVORITES_LOADER_ID:
                        Uri favoritesQueryUri = FavoritesContract.FavoritesEntry.CONTENT_URI;

                        Cursor cursor = getContentResolver().query(
                                favoritesQueryUri,
                                null,
                                null,
                                null,
                                null);

                        if (cursor != null) {
                            return Utils.cursorToMovies(cursor);
                        }
                        break;

                    default:
                        throw new RuntimeException("Loader Not Implemented: " + loaderId);
                }

                return null;
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
            mRecyclerView.smoothScrollToPosition(0);
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

    private void showMoviesData() {
        mRecyclerView.setVisibility(View.VISIBLE);
        mErrorMessageTv.setVisibility(View.INVISIBLE);
    }

    private void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageTv.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(int movieId) {
        Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
        intent.putExtra(DetailsActivity.EXTRA_MOVIE_ID, movieId);
        startActivity(intent);
    }
}
