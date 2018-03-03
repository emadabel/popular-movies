package com.emadabel.popularmovies;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;

import com.emadabel.popularmovies.databinding.ActivityDetailsBinding;
import com.emadabel.popularmovies.model.Movie;
import com.emadabel.popularmovies.utils.NetworkUtils;
import com.emadabel.popularmovies.utils.TmdbJsonUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;

public class DetailsActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Movie> {

    public static final String EXTRA_MOVIE_ID = "extra_id";
    private static final int DETAILS_LOADER_ID = 120;
    private ActivityDetailsBinding mDetailBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_details);

        Toolbar toolbar = mDetailBinding.detailsToolbar;
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        int extraMovieId = getIntent().getIntExtra(EXTRA_MOVIE_ID, 0);

        getSupportLoaderManager().initLoader(DETAILS_LOADER_ID, null, this);

        loadMovieData(extraMovieId);
    }

    private void loadMovieData(int movieId) {
        if (movieId == 0) return;

        Bundle queryBundle = new Bundle();
        queryBundle.putInt(EXTRA_MOVIE_ID, movieId);

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<Movie> moviesSearchLoader = loaderManager.getLoader(DETAILS_LOADER_ID);
        if (moviesSearchLoader == null) {
            loaderManager.initLoader(DETAILS_LOADER_ID, queryBundle, this);
        } else {
            loaderManager.restartLoader(DETAILS_LOADER_ID, queryBundle, this);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public Loader<Movie> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<Movie>(this) {

            Movie movie = null;

            @Override
            protected void onStartLoading() {
                if (args == null) {
                    return;
                }

                if (movie != null) {
                    deliverResult(movie);
                } else {
                    mDetailBinding.loadingDetailsPb.setVisibility(View.VISIBLE);
                    mDetailBinding.detailsContainer.setVisibility(View.INVISIBLE);
                    forceLoad();
                }
            }

            @Override
            public Movie loadInBackground() {

                int query = args.getInt(EXTRA_MOVIE_ID);

                if (query == 0) {
                    return null;
                }

                URL tmdbRequestUrl = NetworkUtils.buildUrl(Integer.toString(query));

                try {
                    String jsonResponse = NetworkUtils
                            .getResponseFromHttpUrl(DetailsActivity.this, tmdbRequestUrl);

                    if (TextUtils.isEmpty(jsonResponse)) {
                        return null;
                    }

                    return TmdbJsonUtils
                            .getMovieDataFromJson(jsonResponse);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void deliverResult(Movie data) {
                movie = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Movie> loader, Movie data) {
        mDetailBinding.loadingDetailsPb.setVisibility(View.INVISIBLE);

        if (data == null) {
            showErrorMessage();
        } else {
            populateUI(data);
            showMovieDetails();
        }
    }

    private void populateUI(Movie movie) {
        mDetailBinding.movieTitleTv.setText(movie.getTitle());
        mDetailBinding.originalTitleTv.setText(movie.getOriginalTitle());
        mDetailBinding.movieInfoTv.setText(movie.getReleaseDate());

        String posterUrl = NetworkUtils.buildPosterUrl(
                movie.getPosterPath());
        Picasso.with(this).load(posterUrl)
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_error)
                .into(mDetailBinding.moviePicIv);

        mDetailBinding.plotTv.setText(movie.getOverview());
        mDetailBinding.tmdbRatingTv.setText(movie.getVoteAverage());
        mDetailBinding.tmdbVotesTv.setText(movie.getVoteCount());

        setTitle(movie.getTitle());
    }

    @Override
    public void onLoaderReset(Loader<Movie> loader) {

    }

    private void showMovieDetails() {
        mDetailBinding.detailsContainer.setVisibility(View.VISIBLE);
        mDetailBinding.errorDetailsTv.setVisibility(View.INVISIBLE);
    }

    private void showErrorMessage() {
        mDetailBinding.detailsContainer.setVisibility(View.INVISIBLE);
        mDetailBinding.errorDetailsTv.setVisibility(View.VISIBLE);
    }
}