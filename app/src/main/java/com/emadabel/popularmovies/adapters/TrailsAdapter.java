package com.emadabel.popularmovies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.emadabel.popularmovies.R;
import com.emadabel.popularmovies.model.Trial;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by lenovo on 11/03/2018.
 */

public class TrailsAdapter extends RecyclerView.Adapter<TrailsAdapter.TrailsViewHolder> {

    private final TrailsAdapterOnClickHandler mClickHandler;
    private final Context mContext;
    private List<Trial> mTrailsList;

    public TrailsAdapter(Context context, TrailsAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
    }

    @Override
    public TrailsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.trails_list, parent, false);
        return new TrailsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailsViewHolder trailsViewHolder, int position) {
        String trialImage = mTrailsList.get(position).getTrialImage();

        Picasso.with(mContext).load(trialImage)
                .into(trailsViewHolder.mTrailImageView);
    }

    @Override
    public int getItemCount() {
        if (mTrailsList == null) return 0;
        return mTrailsList.size();
    }

    public void setTrailsData(List<Trial> trailsList) {
        mTrailsList = trailsList;
        notifyDataSetChanged();
    }

    public interface TrailsAdapterOnClickHandler {
        void onClick(String trialUrl);
    }

    public class TrailsViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        final ImageView mTrailImageView;

        public TrailsViewHolder(View itemView) {
            super(itemView);
            mTrailImageView = itemView.findViewById(R.id.video_preview);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            String trialUrl = mTrailsList.get(adapterPosition).getTrialUrl();
            mClickHandler.onClick(trialUrl);
        }
    }
}
