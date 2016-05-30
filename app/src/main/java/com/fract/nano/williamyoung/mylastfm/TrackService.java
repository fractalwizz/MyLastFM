package com.fract.nano.williamyoung.mylastfm;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.database.CursorJoiner;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.os.ResultReceiver;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class TrackService extends IntentService {
    private final String LOG_TAG = TrackService.class.getSimpleName();

    public static final String RECEIVER = "com.fract.nano.williamyoung.mylastfm.extra.RECEIVER";
    public static final String FRAG_ID = "com.fract.nano.williamyoung.mylastfm.extra.FRAG_ID";
    public static final String QUERY_ONE = "com.fract.nano.williamyoung.mylastfm.extra.QUERY_ONE";
    public static final String QUERY_TWO = "com.fract.nano.williamyoung.mylastfm.extra.QUERY_TWO";
    public static final String RESULT_VALUE = "resultValue";

    private ResultReceiver receiver;
    private int fragID;
    private String queryOne;
    private String queryTwo;

    public TrackService() { super("MyLastFM"); }

    @Override
    protected void onHandleIntent(Intent intent) {
        receiver = intent.getParcelableExtra(RECEIVER);
        fragID = intent.getIntExtra(FRAG_ID, 6);
        queryOne = intent.getStringExtra(QUERY_ONE);
        queryTwo = intent.getStringExtra(QUERY_TWO);

        if (fragID == 6) { return; }
        fetchTrackList();
    }

    private void fetchTrackList() {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String trackJsonStr = null;
        String apiKey = Utility.getAPIKey(getApplicationContext());
        String format = "json";

        String chartTop = "chart.gettoptracks";
        String geoTop = "geo.gettoptracks";
        String tagTop = "tag.gettoptracks";
        String artistTop = "artist.gettoptracks";
        String trackSearch = "track.search";
        String albumInfo = "album.getinfo";

        try {
            final String TRACK_BASE_URL = "https://ws.audioscrobbler.com/2.0/?";
            final String METHOD_PARAM = "method";
            final String COUNTRY_PARAM = "country";
            final String TAG_PARAM = "tag";
            final String ARTIST_PARAM = "artist";
            final String TRACK_PARAM = "track";
            final String ALBUM_PARAM = "album";
            final String KEY_PARAM = "api_key";
            final String FORMAT_PARAM = "format";

            Uri builtUri = null;

            switch(fragID) {
                case 0:
                    builtUri = Uri.parse(TRACK_BASE_URL).buildUpon()
                        .appendQueryParameter(METHOD_PARAM, chartTop)
                        .appendQueryParameter(KEY_PARAM, apiKey)
                        .appendQueryParameter(FORMAT_PARAM, format)
                        .build();
                    break;
                case 1:
                    builtUri = Uri.parse(TRACK_BASE_URL).buildUpon()
                        .appendQueryParameter(METHOD_PARAM, geoTop)
                        .appendQueryParameter(COUNTRY_PARAM, queryOne)
                        .appendQueryParameter(KEY_PARAM, apiKey)
                        .appendQueryParameter(FORMAT_PARAM, format)
                        .build();
                    break;
                case 2:
                    builtUri = Uri.parse(TRACK_BASE_URL).buildUpon()
                        .appendQueryParameter(METHOD_PARAM, tagTop)
                        .appendQueryParameter(TAG_PARAM, queryOne)
                        .appendQueryParameter(KEY_PARAM, apiKey)
                        .appendQueryParameter(FORMAT_PARAM, format)
                        .build();
                    break;
                case 3:
                    builtUri = Uri.parse(TRACK_BASE_URL).buildUpon()
                        .appendQueryParameter(METHOD_PARAM, artistTop)
                        .appendQueryParameter(ARTIST_PARAM, queryOne)
                        .appendQueryParameter(KEY_PARAM, apiKey)
                        .appendQueryParameter(FORMAT_PARAM, format)
                        .build();
                    break;
                case 4:
                    builtUri = Uri.parse(TRACK_BASE_URL).buildUpon()
                        .appendQueryParameter(METHOD_PARAM, trackSearch)
                        .appendQueryParameter(TRACK_PARAM, queryOne)
                        .appendQueryParameter(KEY_PARAM, apiKey)
                        .appendQueryParameter(FORMAT_PARAM, format)
                        .build();
                    break;
                case 5:
                    builtUri = Uri.parse(TRACK_BASE_URL).buildUpon()
                        .appendQueryParameter(METHOD_PARAM, albumInfo)
                        .appendQueryParameter(ARTIST_PARAM, queryOne)
                        .appendQueryParameter(ALBUM_PARAM, queryTwo)
                        .appendQueryParameter(KEY_PARAM, apiKey)
                        .appendQueryParameter(FORMAT_PARAM, format)
                        .build();
                    break;
            }

            URL url = new URL(builtUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();

            if (inputStream == null) { return; }

            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;

            while ((line = reader.readLine()) != null) { buffer.append(line + "\n"); }

            if (buffer.length() == 0) { return; }
            trackJsonStr = buffer.toString();
        } catch (IOException e) {
            Log.e(LOG_TAG, "ERROR: ", e);
        } finally {
            if (urlConnection != null) { urlConnection.disconnect(); }

            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "ERROR closing stream: ", e);
                }
            }
        }

        try {
            getTrackDataFromJson(trackJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    private void getTrackDataFromJson(String listJsonStr) throws JSONException {
        final String OWM_TRACKS = "tracks";
        final String OWM_LIST = "track";
        final String OWM_NAME = "name";
        final String OWM_DURATION = "duration";
        final String OWM_ARTIST = "artist";
        final String OWM_BAND = "name";
        final String OWM_URL = "url";
        final String OWM_EGAMI = "image";
        final String OWM_IMAGE = "#text";

        JSONObject listJson = new JSONObject(listJsonStr);
        JSONObject skcart = listJson.optJSONObject(OWM_TRACKS);
        JSONArray trackArray = skcart.getJSONArray(OWM_LIST);

        ArrayList<Track> resultTrack = new ArrayList<>();

        for (int i = 0; i < trackArray.length(); i++) {
            JSONObject singleTrack = trackArray.getJSONObject(i);

            String trackName = singleTrack.getString(OWM_NAME);
            int length = singleTrack.getInt(OWM_DURATION);

            JSONObject artistObject = singleTrack.getJSONObject(OWM_ARTIST);
            String artistName = artistObject.getString(OWM_BAND);
            String bandURL = artistObject.getString(OWM_URL);

            JSONArray imageArray = singleTrack.getJSONArray(OWM_EGAMI);
            JSONObject egamiObject = imageArray.getJSONObject(imageArray.length() - 1);
            String imageURL = egamiObject.getString(OWM_IMAGE);

            String album = "";
            String albumCover = "";
            int year = 0;

            Track track = Track.newInstance(artistName, album, trackName, year, length, imageURL, albumCover, bandURL);
            resultTrack.add(track);
        }

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(RESULT_VALUE, resultTrack);
        receiver.send(Activity.RESULT_OK, bundle);
    }

    private void getTrackSearchFromJson(String trackJsonStr) throws JSONException {

    }

    private void getAlbumTracksFromJson(String trackJsonStr) throws JSONException {

    }
}