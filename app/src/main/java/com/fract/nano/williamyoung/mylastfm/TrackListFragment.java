package com.fract.nano.williamyoung.mylastfm;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TrackListFragment extends Fragment {
    private static final String ARG_PARAM1 = "fragID";
    private int mFragID;

//    private OnSearchQueryListener mListener;

    public TrackListFragment() {}

    public static TrackListFragment newInstance(int param1) {
        TrackListFragment fragment = new TrackListFragment();

        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mFragID = getArguments().getInt(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_track_list, container, false);

        String text = getResources().getText(R.string.track_list_name) + ":" + String.valueOf(mFragID);
        TextView textView = (TextView) view.findViewById(R.id.track_list_text);
        textView.setText(text);

        return view;
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