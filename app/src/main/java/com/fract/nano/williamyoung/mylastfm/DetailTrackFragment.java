package com.fract.nano.williamyoung.mylastfm;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailTrackFragment extends Fragment {
    private static final String SINGLE_TRACK = "single_track";
    public static final String RESULT_VALUE = "resultValue";

    private View rootView;

    private Track mTrack;
    public TrackReceiver trackReceiver;

    public DetailTrackFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mTrack = getArguments().getParcelable(SINGLE_TRACK);
        }

        setupServiceReceiver();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_detail_track, container, false);

        if (savedInstanceState != null && savedInstanceState.containsKey(SINGLE_TRACK)) {
            mTrack = savedInstanceState.getParcelable(SINGLE_TRACK);
        }

        updateViews();

        return rootView;
    }

    /**
     * Creates and sets a ServiceReceiver for acquiring the filled Track object
     * Receiver includes updating Layout Views
     */
    private void setupServiceReceiver() {
        trackReceiver = new TrackReceiver(new Handler());
        trackReceiver.setReceiver(new TrackReceiver.Receiver() {
            @Override
            public void onReceiveResult(int resultCode, Bundle resultData) {
                if (resultCode == Activity.RESULT_OK) {
                    mTrack = resultData.getParcelable(RESULT_VALUE);
                    updateViews();
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    Log.w("detailSR", "Device Not Connected");
                    Snackbar.make(rootView, getString(R.string.detail_disconnected), Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    private void updateViews() {
        ImageView mTrackImageView = (ImageView) rootView.findViewById(R.id.track_image_view);
        Picasso.with(getActivity())
            .load(mTrack.getImage())
            .error(R.drawable.error)
            .placeholder(R.drawable.placeholder)
            .resize(600, 600).centerCrop()
            .into(mTrackImageView);
        mTrackImageView.setContentDescription(mTrack.getTrackName());

        ImageView mTrackCoverView = (ImageView) rootView.findViewById(R.id.track_cover_view);
        if (!mTrack.getAlbumCover().equals("")) {
            Picasso.with(getActivity())
                .load(mTrack.getAlbumCover())
                .error(R.drawable.error)
                .placeholder(R.drawable.placeholder)
                .resize(600, 600).centerCrop()
                .into(mTrackCoverView);
            mTrackCoverView.setContentDescription(mTrack.getAlbum());
        }

        TextView mTrackTextView = (TextView) rootView.findViewById(R.id.track_name_text);
        mTrackTextView.setText(mTrack.getTrackName());

        TextView mDurationTextView = (TextView) rootView.findViewById(R.id.track_duration_text);
        mDurationTextView.setText(mTrack.getFormattedLength());

        TextView mArtistTextView = (TextView) rootView.findViewById(R.id.detail_text_artist);
        mArtistTextView.setText(String.format(getString(R.string.format_artist), mTrack.getArtist()));

        TextView mAlbumTextView = (TextView) rootView.findViewById(R.id.detail_text_album);
        mAlbumTextView.setText(String.format(getString(R.string.format_album), mTrack.getAlbum()));

        Button mBandButton = (Button) rootView.findViewById(R.id.band_url_button);
        mBandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri url = Uri.parse(mTrack.getBandUrl());
                Intent intent = new Intent(Intent.ACTION_VIEW, url);

                startActivity(intent);
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(SINGLE_TRACK, mTrack);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        getActivity().setTitle("Track Detail");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        if (mTrack.getAlbum().equals("") || mTrack.getAlbumCover().equals("") || mTrack.getLength() == 0) {
            updateTrack();
        }
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * Creates intent to pass Track object to the DetailService
     */
    private void updateTrack() {
        Intent detailIntent = new Intent(getActivity(), DetailService.class);
        detailIntent.putExtra(DetailService.RECEIVER, trackReceiver);
        detailIntent.putExtra(DetailService.TRACK_OBJ, mTrack);
        getActivity().startService(detailIntent);
    }
}