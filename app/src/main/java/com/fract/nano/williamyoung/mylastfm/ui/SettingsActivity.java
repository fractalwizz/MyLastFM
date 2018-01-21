package com.fract.nano.williamyoung.mylastfm.ui;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.fract.nano.williamyoung.mylastfm.R;

// TODO - Convert to Fragment-based PreferenceActivity
public class SettingsActivity extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}