package com.fract.nano.williamyoung.mylastfm;

import android.content.Context;

public class Utility {
    /**
     * Returns API Key for the Last.FM API
     * @param context : Activity/Fragment context
     * @return API key string
     */
    public static String getAPIKey(Context context) { return context.getString(R.string.apiKey); }
}