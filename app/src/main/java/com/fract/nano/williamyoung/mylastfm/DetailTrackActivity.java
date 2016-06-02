package com.fract.nano.williamyoung.mylastfm;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public class DetailTrackActivity extends AppCompatActivity {
    private static final String SINGLE_TRACK = "single_track";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_track);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (savedInstanceState == null) {
            Bundle args = new Bundle();
            args.putParcelable(SINGLE_TRACK, getIntent().getParcelableExtra(SINGLE_TRACK));

            DetailTrackFragment fragment = new DetailTrackFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                .add(R.id.containerDetail, fragment)
                .commit();
        }

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}