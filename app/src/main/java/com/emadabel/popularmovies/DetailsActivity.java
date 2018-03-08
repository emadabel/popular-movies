package com.emadabel.popularmovies;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.emadabel.popularmovies.model.Movie;
import com.emadabel.popularmovies.utils.NetworkUtils;
import com.emadabel.popularmovies.utils.TmdbJsonUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailsActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Movie> {

    public static final String EXTRA_MOVIE_ID = "extra_id";
    private static final int DETAILS_LOADER_ID = 120;

    @BindView(R.id.details_toolbar)
    Toolbar toolbar;
    @BindView(R.id.loading_details_pb)
    ProgressBar loadingDetailsPb;
    @BindView(R.id.details_container)
    ViewGroup detailsContainer;
    @BindView(R.id.movie_title_tv)
    TextView movieTitleTv;
    @BindView(R.id.original_title_tv)
    TextView originalTitleTv;
    @BindView(R.id.movie_info_tv)
    TextView movieInfoTv;
    @BindView(R.id.movie_pic_iv)
    ImageView moviePicIv;
    @BindView(R.id.plot_tv)
    TextView plotTv;
    @BindView(R.id.tmdb_rating_tv)
    TextView tmdbRatingTv;
    @BindView(R.id.tmdb_votes_tv)
    TextView tmdbVotesTv;
    @BindView(R.id.error_details_tv)
    TextView errorDetailsTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
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
            onBackPressed();
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
                    loadingDetailsPb.setVisibility(View.VISIBLE);
                    detailsContainer.setVisibility(View.INVISIBLE);
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
        loadingDetailsPb.setVisibility(View.INVISIBLE);

        if (data == null) {
            showErrorMessage();
        } else {
            populateUI(data);
            showMovieDetails();
        }
    }

    private void populateUI(Movie movie) {
        movieTitleTv.setText(movie.getTitle());
        originalTitleTv.setText(movie.getOriginalTitle());
        movieInfoTv.setText(movie.getReleaseDate());

        String posterUrl = NetworkUtils.buildPosterUrl(
                movie.getPosterPath());
        Picasso.with(this).load(posterUrl)
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_error)
                .into(moviePicIv);

        plotTv.setText(movie.getOverview());
        tmdbRatingTv.setText(movie.getVoteAverage());
        tmdbVotesTv.setText(movie.getVoteCount());
    }

    @Override
    public void onLoaderReset(Loader<Movie> loader) {

    }

    private void showMovieDetails() {
        detailsContainer.setVisibility(View.VISIBLE);
        errorDetailsTv.setVisibility(View.INVISIBLE);
    }

    private void showErrorMessage() {
        detailsContainer.setVisibility(View.INVISIBLE);
        errorDetailsTv.setVisibility(View.VISIBLE);
    }
}
