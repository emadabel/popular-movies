package com.emadabel.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by Emad on 23/02/2018.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesViewHolder> {

    private String[] mMoviesData = {"",""};

    public MoviesAdapter() {

    }

    @Override
    public MoviesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.movies_list, parent, false);
        return new MoviesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MoviesViewHolder moviesViewHolder, int position) {
        //String movie = mMoviesData[position];
    }

    @Override
    public int getItemCount() {
        /*if (mMoviesData == null) return 0;
        return mMoviesData.length;*/
        return 20;
    }

    public class MoviesViewHolder extends RecyclerView.ViewHolder {

        final ImageView mMovieImageView;

        public MoviesViewHolder(View view) {
            super(view);
            mMovieImageView = (ImageView) view.findViewById(R.id.movie_poster_iv);
        }
    }
}
