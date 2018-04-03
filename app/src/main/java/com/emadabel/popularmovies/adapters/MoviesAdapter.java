package com.emadabel.popularmovies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.emadabel.popularmovies.R;
import com.emadabel.popularmovies.model.Movie;
import com.emadabel.popularmovies.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesViewHolder> {

    private final MovieAdapterOnClickHandler mClickHandler;
    private final Context mContext;
    private List<Movie> mMovieList;

    public MoviesAdapter(Context context, MovieAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
        mContext = context;
    }

    @Override
    public MoviesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.movies_list, parent, false);
        return new MoviesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MoviesViewHolder moviesViewHolder, int position) {
        String posterUrl = NetworkUtils.buildPosterUrl(
                mMovieList.get(position).getPosterPath());

        Picasso.with(mContext).load(posterUrl)
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_error)
                .into(moviesViewHolder.mMovieImageView);
    }

    @Override
    public int getItemCount() {
        if (mMovieList == null) return 0;
        return mMovieList.size();
    }

    public void setMoviesData(List<Movie> movieList) {
        mMovieList = movieList;
        notifyDataSetChanged();
    }

    public interface MovieAdapterOnClickHandler {
        void onClick(int movieId);
    }

    public class MoviesViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        final ImageView mMovieImageView;

        public MoviesViewHolder(View itemView) {
            super(itemView);
            mMovieImageView = itemView.findViewById(R.id.movie_poster_iv);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            int movieId = mMovieList.get(adapterPosition).getId();
            mClickHandler.onClick(movieId);
        }
    }
}
