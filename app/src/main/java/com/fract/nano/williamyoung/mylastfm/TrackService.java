package com.fract.nano.williamyoung.mylastfm;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class TrackService extends IntentService {
    private final String LOG_TAG = TrackService.class.getSimpleName();

    public static final String FRAG_ID = "com.fract.nano.williamyoung.mylastfm.extra.FRAG_ID";
    public static final String QUERY_ONE = "com.fract.nano.williamyoung.mylastfm.extra.QUERY_ONE";
    public static final String QUERY_TWO = "com.fract.nano.williamyoung.mylastfm.extra.QUERY_TWO";

    public TrackService() { super("MyLastFM"); }

    @Override
    protected void onHandleIntent(Intent intent) {
        final int fragID = intent.getIntExtra(FRAG_ID, 6);
        final String queryOne = intent.getStringExtra(QUERY_ONE);
        final String queryTwo = intent.getStringExtra(QUERY_TWO);

        fetchTrackList(fragID, queryOne, queryTwo);
    }

    private void fetchTrackList(int id, String queryOne, String queryTwo) {
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

            Uri builtUri = Uri.parse(TRACK_BASE_URL);

            switch(id) {
                case 0:
                    builtUri.buildUpon()
                        .appendQueryParameter(METHOD_PARAM, chartTop)
                        .appendQueryParameter(KEY_PARAM, apiKey)
                        .appendQueryParameter(FORMAT_PARAM, format)
                        .build();
                    break;
                case 1:
                    builtUri.buildUpon()
                        .appendQueryParameter(METHOD_PARAM, geoTop)
                        .appendQueryParameter(COUNTRY_PARAM, queryOne)
                        .appendQueryParameter(KEY_PARAM, apiKey)
                        .appendQueryParameter(FORMAT_PARAM, format)
                        .build();
                    break;
                case 2:
                    builtUri.buildUpon()
                        .appendQueryParameter(METHOD_PARAM, tagTop)
                        .appendQueryParameter(TAG_PARAM, queryOne)
                        .appendQueryParameter(KEY_PARAM, apiKey)
                        .appendQueryParameter(FORMAT_PARAM, format)
                        .build();
                    break;
                case 3:
                    builtUri.buildUpon()
                        .appendQueryParameter(METHOD_PARAM, artistTop)
                        .appendQueryParameter(ARTIST_PARAM, queryOne)
                        .appendQueryParameter(KEY_PARAM, apiKey)
                        .appendQueryParameter(FORMAT_PARAM, format)
                        .build();
                    break;
                case 4:
                    builtUri.buildUpon()
                        .appendQueryParameter(METHOD_PARAM, trackSearch)
                        .appendQueryParameter(TRACK_PARAM, queryOne)
                        .appendQueryParameter(KEY_PARAM, apiKey)
                        .appendQueryParameter(FORMAT_PARAM, format)
                        .build();
                    break;
                case 5:
                    builtUri.buildUpon()
                        .appendQueryParameter(METHOD_PARAM, albumInfo)
                        .appendQueryParameter(ARTIST_PARAM, queryOne)
                        .appendQueryParameter(ALBUM_PARAM, queryTwo)
                        .appendQueryParameter(KEY_PARAM, apiKey)
                        .appendQueryParameter(FORMAT_PARAM, format)
                        .build();
                    break;
                default:
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

    private void getTrackDataFromJson(String trackJsonStr) throws JSONException {

    }
}