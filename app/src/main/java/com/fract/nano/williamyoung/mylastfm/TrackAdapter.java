package com.fract.nano.williamyoung.mylastfm;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.TrackViewHolder> {
    private ArrayList<Track> mTrackList;
    private Context mContext;
    private ClickListener listener;

    public TrackAdapter(Context context) {
        this.mTrackList = null;
        this.mContext = context;
    }

    public TrackAdapter(Context context, ArrayList<Track> trackList) {
        this.mTrackList = trackList;
        this.mContext = context;
    }

    @Override
    public TrackViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_track_item, null);

        TrackViewHolder viewHolder = new TrackViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TrackViewHolder viewHolder, int i) {
        Track track = mTrackList.get(i);

        viewHolder.trackTextView.setText(track.getTrackName());
        viewHolder.artistTextView.setText(track.getArtist());

        Picasso.with(mContext)
            .load(track.getImage())
            .error(R.drawable.error)
            .placeholder(R.drawable.placeholder)
            .into(viewHolder.imageView);
    }

    public void setOnItemClickListener(ClickListener listener) {
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return (null != mTrackList) ? mTrackList.size() : 0;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
    }

    public class TrackViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected ImageView imageView;
        protected TextView trackTextView;
        protected TextView artistTextView;

        public TrackViewHolder(View view) {
            super(view);
            this.imageView = (ImageView) view.findViewById(R.id.imageView);
            this.trackTextView = (TextView) view.findViewById(R.id.trackText);
            this.artistTextView = (TextView) view.findViewById(R.id.artistText);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onItemClick(getAdapterPosition(), v);
        }
    }
}