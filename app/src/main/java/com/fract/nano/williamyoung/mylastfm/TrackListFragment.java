package com.fract.nano.williamyoung.mylastfm;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.os.ResultReceiver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class TrackListFragment extends Fragment {
    private static final String ARG_PARAM1 = "fragID";
    private static final String ARG_PARAM2 = "queryOne";
    private static final String ARG_PARAM3 = "queryTwo";
    public static final String RESULT_VALUE = "resultValue";
    private int mFragID;
    private String mQueryOne;
    private String mQueryTwo;
    private ArrayList<Track> mTrackList;

    public TrackReceiver trackReceiver;

//    private OnSearchQueryListener mListener;

    public TrackListFragment() {}

    public static TrackListFragment newInstance(int param1, String param2, String param3) {
        TrackListFragment fragment = new TrackListFragment();

        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, param3);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mFragID = getArguments().getInt(ARG_PARAM1);
            mQueryOne = getArguments().getString(ARG_PARAM2);
            mQueryTwo = getArguments().getString(ARG_PARAM3);
        }

        setupServiceReceiver();
    }

    public void setupServiceReceiver() {
        trackReceiver = new TrackReceiver(new Handler());
        trackReceiver.setReceiver(new TrackReceiver.Receiver(){
            @Override
            public void onReceiveResult(int resultCode, Bundle resultData) {
                if (resultCode == Activity.RESULT_OK) {
                    mTrackList = resultData.getParcelableArrayList(RESULT_VALUE);

                    if (mTrackList != null && mTrackList.size() > 0) {
                        Log.w("setupSR", "Successfully acquired ArrayList of tracks: " + String.valueOf(mTrackList.size()));
                    }
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_track_list, container, false);

        String text = getResources().getText(R.string.track_list_name) + ":" + String.valueOf(mFragID);
        TextView textView = (TextView) view.findViewById(R.id.track_list_text);
        textView.setText(text);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        updateTrack();

        super.onActivityCreated(savedInstanceState);
    }

    private void updateTrack() {
        Intent trackIntent = new Intent(getActivity(), TrackService.class);
        trackIntent.putExtra(TrackService.RECEIVER, trackReceiver);
        trackIntent.putExtra(TrackService.FRAG_ID, mFragID);
        trackIntent.putExtra(TrackService.QUERY_ONE, mQueryOne);
        trackIntent.putExtra(TrackService.QUERY_TWO, mQueryTwo);
        getActivity().startService(trackIntent);
    }

//    // T0DO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) { mListener.onFragmentInteraction(uri); }
//    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

//        if (context instanceof OnSearchQueryListener) {
//            mListener = (OnSearchQueryListener) context;
//        } else {
//            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
//    public interface OnSearchQueryListener {
//        void onSearchQuery(int fragID, String queryOne, String queryTwo);
//    }
}