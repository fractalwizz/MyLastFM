package com.fract.nano.williamyoung.mylastfm;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TrackListFragment extends Fragment implements
    GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener {

    private static final String ARG_PARAM1 = "fragID";
    private static final String ARG_PARAM2 = "queryOne";
    private static final String ARG_PARAM3 = "queryTwo";
    public static final String RESULT_VALUE = "resultValue";

    private int mFragID;
    private String mQueryOne;
    private String mQueryTwo;
    private double mLatitude;
    private double mLongitude;

    private ArrayList<Track> mTrackList;
    private RecyclerView mRecyclerView;
    private TrackAdapter adapter;

    public TrackReceiver trackReceiver;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;

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

        mLocationRequest = LocationRequest.create();
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
            .addApi(LocationServices.API)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .build();

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
                        adapter = new TrackAdapter(getActivity(), mTrackList);
                        adapter.setOnItemClickListener(new TrackAdapter.ClickListener() {
                            @Override
                            public void onItemClick(int position, View v) {
                                Track track = mTrackList.get(position);
                                Toast.makeText(getActivity(), track.getTrackName(), Toast.LENGTH_SHORT).show();
                            }
                        });
                        mRecyclerView.setAdapter(adapter);
                    }
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_track_list, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        //

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        if (mFragID != 1 && mFragID != 6) { updateTrack(); }

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

    @Override
    public void onConnected(Bundle bundle) {
        int result = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);

        if (mFragID == 1) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            } else {
                Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                mLatitude = location.getLatitude();
                mLongitude = location.getLongitude();

                new GetLocationTask().execute(mLatitude, mLongitude);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,@NonNull String[] permissions,@NonNull int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                int result = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);

                if (result == PackageManager.PERMISSION_GRANTED) {
                    Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                    mLatitude = location.getLatitude();
                    mLongitude = location.getLongitude();

                    new GetLocationTask().execute(mLatitude, mLongitude);
                }
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {}

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

    public class GetLocationTask extends AsyncTask<Double, Void, String> {
        @Override
        protected String doInBackground(Double... params) {
            if (params.length == 0) { return ""; }

            double lat = params[0];
            double lon = params[1];

            Geocoder geoCoder = new Geocoder(getActivity(), Locale.getDefault());
            List<Address> addressList;

            try {
                addressList = geoCoder.getFromLocation(lat, lon, 1);
            } catch (IOException e) {
                Log.e("GetLocation", e.getMessage());
                return "";
            }

            return addressList.get(0).getCountryName();
        }

        @Override
        public void onPostExecute(String countryName) {
            mQueryOne = countryName;
            updateTrack();
        }
    }
}