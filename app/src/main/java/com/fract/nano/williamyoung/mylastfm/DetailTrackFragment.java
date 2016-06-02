package com.fract.nano.williamyoung.mylastfm;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class DetailTrackFragment extends Fragment {
    private static final String SINGLE_TRACK = "single_track";
    public static final String RESULT_VALUE = "resultValue";

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
        View view = inflater.inflate(R.layout.fragment_detail_track, container, false);



        return view;
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
                }
            }
        });
    }

    private void updateViews() {
        Log.w("updateViews", mTrack.getAlbum() + ":" + mTrack.getAlbumCover() + ":" + mTrack.getFormattedLength());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        getActivity().setTitle("Track Detail");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        if (mTrack.getAlbumCover().equals("") || mTrack.getYear() != 0 || mTrack.getLength() != 0) {
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