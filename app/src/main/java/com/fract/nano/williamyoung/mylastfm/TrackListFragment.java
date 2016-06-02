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
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Fragment containing a RecyclerView for displaying a List of Track objects
 * Supports Results from 6 different API calls, as well as from a Content Provider
 */
public class TrackListFragment extends Fragment implements
    GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener {

    private static final String ARG_PARAM1 = "fragID";
    private static final String ARG_PARAM2 = "queryOne";
    private static final String ARG_PARAM3 = "queryTwo";
    public static final String RESULT_VALUE = "resultValue";
    private static final String TRACK_LIST = "track_list";
    private static final String SINGLE_TRACK = "single_track";

    private int mFragID;
    private String mQueryOne;
    private String mQueryTwo;
    private double mLatitude;
    private double mLongitude;

    private ArrayList<Track> mTrackList;
    private RecyclerView mRecyclerView;
    private TrackAdapter adapter;
    private TextView mErrorTextView;

    public TrackReceiver trackReceiver;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;

    public TrackListFragment() {}

    /**
     * Creates Factory instance of a TrackListFragment
     * @param param1 : Fragment ID
     * @param param2 : Query String 1
     * @param param3 : Query String 2
     * @return new TrackListFragment
     */
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_track_list, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));

        mErrorTextView = (TextView) view.findViewById(R.id.error_text);

        if (savedInstanceState != null && savedInstanceState.containsKey(TRACK_LIST)) {
            mTrackList = savedInstanceState.getParcelableArrayList(TRACK_LIST);
            setupAdapter();
        }

        return view;
    }

    /**
     * Creates and sets a ServiceReceiver for acquiring the ArrayList of track objects
     * Receiver includes setting up the TrackAdapter + RecyclerView item selection
     */
    private void setupServiceReceiver() {
        trackReceiver = new TrackReceiver(new Handler());
        trackReceiver.setReceiver(new TrackReceiver.Receiver(){
            @Override
            public void onReceiveResult(int resultCode, Bundle resultData) {
                if (resultCode == Activity.RESULT_OK) {
                    mErrorTextView.setVisibility(View.INVISIBLE);

                    mTrackList = resultData.getParcelableArrayList(RESULT_VALUE);

                    if (mTrackList != null && mTrackList.size() > 0) {
                        Log.w("setupSR", "Successfully acquired ArrayList of tracks: " + String.valueOf(mTrackList.size()));
                        setupAdapter();
                    }
                } else if (resultCode == Activity.RESULT_FIRST_USER) {
                    // no results found
                    Log.w("setupSR", "No Results Found");
                    mErrorTextView.setVisibility(View.VISIBLE);
                    mErrorTextView.setText(getResources().getText(R.string.error_results));
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    // not connected
                    Log.w("setupSR", "Device Not Connected");
                    mErrorTextView.setVisibility(View.VISIBLE);
                    mErrorTextView.setText(getResources().getText(R.string.error_connection));
                }
            }
        });
    }

    private void setupAdapter() {
        adapter = new TrackAdapter(getActivity(), mTrackList);
        adapter.setOnItemClickListener(new TrackAdapter.ClickListener() {

            /**
             * Individual RecyclerView item selected
             * @param position : item selected index
             * @param v : view contained within the RecyclerView List
             */
            @Override
            public void onItemClick(int position, View v) {
                Track track = mTrackList.get(position);

                Intent intent = new Intent(getActivity(), DetailTrackActivity.class)
                    .putExtra(SINGLE_TRACK, track);
                ActivityCompat.startActivity(getActivity(), intent, null);
            }
        });
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(TRACK_LIST, mTrackList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        getActivity().setTitle("MyLastFM");
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
        // Don't immediately updateTrack if GEO info needed or data from ContentProvider
        if (mFragID != 1 && mFragID != 6 && mTrackList == null) { updateTrack(); }

        super.onActivityCreated(savedInstanceState);
    }

    /**
     * Creates intent to pass API data to the TrackService.
     * Passes a TrackReceiver, fragment ID, and two Query Strings
     */
    private void updateTrack() {
        Intent trackIntent = new Intent(getActivity(), TrackService.class);
        trackIntent.putExtra(TrackService.RECEIVER, trackReceiver);
        trackIntent.putExtra(TrackService.FRAG_ID, mFragID);
        trackIntent.putExtra(TrackService.QUERY_ONE, mQueryOne);
        trackIntent.putExtra(TrackService.QUERY_TWO, mQueryTwo);
        getActivity().startService(trackIntent);
    }

    // Assistance from http://sanastasov.blogspot.com/2014/12/migrating-from-locationclient-to.html

    /**
     * Used with GoogleApiClient to get GEO location data
     * from FusedLocationApi for GEO Browse track list
     * @param bundle : bundle
     */
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

    /**
     * Callback for instance if permissions were denied + requested
     * @param requestCode : code used within requestPermissions (onConnected)
     * @param permissions : array of permissions requested
     * @param grantResults : int results of permission granted/denied
     */
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
            } else {
                mErrorTextView.setVisibility(View.VISIBLE);
                mErrorTextView.setText(getResources().getText(R.string.error_results));
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {}

    /**
     * AsyncTask used to acquire country name using location coordinates
     */
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

        /**
         * Sets up Query 1 string with GEO data
         * Initiates Track List fetch
         * @param countryName : Country Name string result
         */
        @Override
        public void onPostExecute(String countryName) {
            mQueryOne = countryName;
            if (mTrackList == null) { updateTrack(); }
        }
    }
}