package com.emadabel.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.emadabel.popularmovies.adapters.ReviewsAdapter;
import com.emadabel.popularmovies.adapters.TrailsAdapter;
import com.emadabel.popularmovies.data.FavoritesContract;
import com.emadabel.popularmovies.model.Movie;
import com.emadabel.popularmovies.model.Review;
import com.emadabel.popularmovies.utils.NetworkUtils;
import com.emadabel.popularmovies.utils.TmdbJsonUtils;
import com.emadabel.popularmovies.utils.Utils;
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
    @BindView(R.id.reviews_title_tv)
    TextView reviewsTitleTv;
    @BindView(R.id.trails_list_rv)
    RecyclerView trailsListRv;
    @BindView(R.id.reviews_list_rv)
    RecyclerView reviewsListRv;
    @BindView(R.id.favorite_fab)
    FloatingActionButton favoriteFab;

    private TrailsAdapter trailsAdapter;
    private ReviewsAdapter reviewsAdapter;

    boolean isFavoriteMovie;
    Movie movie = null;

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

        favoriteFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFavoriteMovie) {
                    int row = getContentResolver().delete(FavoritesContract.FavoritesEntry.buildFavoritesUriWithMovieId(movie.getId()), null, null);
                    if (row > 0) {
                        isFavoriteMovie = false;
                        favoriteFab.setImageResource(R.drawable.ic_fav_off);
                        Toast.makeText(getBaseContext(), "Successfully removed the movie from favorites list", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Uri uri = getContentResolver().insert(FavoritesContract.FavoritesEntry.CONTENT_URI, Utils.movieToContentValues(movie));
                    if (uri != null) {
                        isFavoriteMovie = true;
                        favoriteFab.setImageResource(R.drawable.ic_fav_on);
                        Toast.makeText(getBaseContext(), "Successfully added new favorite movie", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        trailsAdapter = new TrailsAdapter(this, new TrailsAdapter.TrailsAdapterOnClickHandler() {
            @Override
            public void onClick(String trialUrl) {
                Uri webpage = Uri.parse(trialUrl);
                Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

        reviewsAdapter = new ReviewsAdapter(new ReviewsAdapter.ReviewsAdapterOnClickHandler() {
            @Override
            public void onClick(Review review) {
                Intent intent = new Intent(DetailsActivity.this, ReviewActivity.class);
                intent.putExtra(ReviewActivity.EXTRA_REVIEW_AUTHOR, review.getReviewAuthor());
                intent.putExtra(ReviewActivity.EXTRA_REVIEW_CONTENT, review.getReviewContent());
                startActivity(intent);
            }
        });

        IntiRecyclerViews(this, trailsListRv, LinearLayoutManager.HORIZONTAL, trailsAdapter);
        IntiRecyclerViews(this, reviewsListRv, LinearLayoutManager.VERTICAL, reviewsAdapter);

        int extraMovieId = getIntent().getIntExtra(EXTRA_MOVIE_ID, 0);

        isFavoriteMovie = Utils.movieIsFavorite(this, extraMovieId);

        if (isFavoriteMovie) {
            favoriteFab.setImageResource(R.drawable.ic_fav_on);
        } else {
            favoriteFab.setImageResource(R.drawable.ic_fav_off);
        }

        getSupportLoaderManager().initLoader(DETAILS_LOADER_ID, null, this);

        loadMovieData(extraMovieId);
    }

    private void IntiRecyclerViews(Context context, RecyclerView recyclerView, int orientation, RecyclerView.Adapter adapter) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, orientation, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
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
    public Loader<Movie> onCreateLoader(int loaderId, final Bundle loaderArgs) {
        return new AsyncTaskLoader<Movie>(this) {

            @Override
            protected void onStartLoading() {
                if (loaderArgs == null) {
                    return;
                }

                if (movie != null) {
                    deliverResult(movie);
                } else {
                    loadingDetailsPb.setVisibility(View.VISIBLE);
                    detailsContainer.setVisibility(View.INVISIBLE);
                    favoriteFab.setVisibility(View.INVISIBLE);
                    forceLoad();
                }
            }

            @Override
            public Movie loadInBackground() {

                int query = loaderArgs.getInt(EXTRA_MOVIE_ID);

                if (query == 0) {
                    return null;
                }

                URL tmdbRequestUrl = NetworkUtils.buildUrl(Integer.toString(query), true);

                try {
                    String jsonResponse = NetworkUtils
                            .getResponseFromHttpUrl(DetailsActivity.this, tmdbRequestUrl);

                    /*if (jsonResponse == null) {
                        Cursor cursor = getContentResolver().query(
                                FavoritesContract.FavoritesEntry.buildFavoritesUriWithMovieId(query),
                                null, null, null, null);
                    }*/

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

        if (movie.getTrials().size() > 0) {
            trailsAdapter.setTrailsData(movie.getTrials());
            trailsListRv.setVisibility(View.VISIBLE);
        } else {
            trailsListRv.setVisibility(View.GONE);
        }

        if (movie.getReviews().size() > 0) {
            reviewsAdapter.setReviewsData(movie.getReviews());
            reviewsListRv.setVisibility(View.VISIBLE);
            reviewsTitleTv.setVisibility(View.VISIBLE);

        } else {
            reviewsListRv.setVisibility(View.GONE);
            reviewsTitleTv.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Movie> loader) {

    }

    private void showMovieDetails() {
        detailsContainer.setVisibility(View.VISIBLE);
        favoriteFab.setVisibility(View.VISIBLE);
        errorDetailsTv.setVisibility(View.INVISIBLE);
    }

    private void showErrorMessage() {
        detailsContainer.setVisibility(View.INVISIBLE);
        favoriteFab.setVisibility(View.INVISIBLE);
        errorDetailsTv.setVisibility(View.VISIBLE);
    }
}
