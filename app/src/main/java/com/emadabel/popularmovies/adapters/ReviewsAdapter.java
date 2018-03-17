package com.emadabel.popularmovies.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.emadabel.popularmovies.R;
import com.emadabel.popularmovies.model.Review;

import java.util.List;

/**
 * Created by lenovo on 13/03/2018.
 */

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewsViewHolder> {

    private final ReviewsAdapterOnClickHandler mClickHandler;
    private List<Review> mReviewsList;

    public ReviewsAdapter(ReviewsAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    @Override
    public ReviewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.reviews_list, parent, false);
        return new ReviewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewsViewHolder reviewsViewHolder, int position) {
        String author = mReviewsList.get(position).getReviewAuthor();
        String content = mReviewsList.get(position).getReviewContent();

        reviewsViewHolder.mReviewAuthorTv.setText(author);
        reviewsViewHolder.mReviewContentTv.setText(content);
    }

    @Override
    public int getItemCount() {
        if (mReviewsList == null) return 0;
        return mReviewsList.size();
    }

    public void setReviewsData(List<Review> reviewsList) {
        mReviewsList = reviewsList;
        notifyDataSetChanged();
    }

    public interface ReviewsAdapterOnClickHandler {
        void onClick(Review review);
    }

    public class ReviewsViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        final TextView mReviewAuthorTv;
        final TextView mReviewContentTv;

        public ReviewsViewHolder(View itemView) {
            super(itemView);
            mReviewAuthorTv = itemView.findViewById(R.id.review_author_tv);
            mReviewContentTv = itemView.findViewById(R.id.review_content_tv);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            Review review = mReviewsList.get(adapterPosition);
            mClickHandler.onClick(review);
        }
    }
}
