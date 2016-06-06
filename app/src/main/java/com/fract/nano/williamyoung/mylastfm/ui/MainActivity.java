package com.fract.nano.williamyoung.mylastfm.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.fract.nano.williamyoung.mylastfm.R;
import com.fract.nano.williamyoung.mylastfm.data.TrackContract;
import com.fract.nano.williamyoung.mylastfm.data.TrackHelper;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class MainActivity extends AppCompatActivity implements
    NavigationView.OnNavigationItemSelectedListener,
    SearchFragment.OnSearchQueryListener {

    Toolbar mToolbar;
    DrawerLayout mDrawer;
    NavigationView mNavigation;
    private TrackHelper mHelper;

    // Used to store the last screen title
    private CharSequence title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHelper = new TrackHelper(this);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawer, mToolbar, R.string.nav_open, R.string.nav_close);
        title = getTitle();

        if (mDrawer != null) { mDrawer.addDrawerListener(toggle); }
        toggle.syncState();

        // Sets up AdMob View at bottom of MainActivity
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
            .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
            .build();
        if (mAdView != null) { mAdView.loadAd(adRequest); }

        // Default Fragment to be loaded
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        int defID = Integer.parseInt(preferences.getString("pref_startFragment", "6"));

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.container);

        // Is there no fragment present?
        if (fragment == null) {
            fragment = TrackListFragment.newInstance(defID, "", "");

            // Insert Fragment into layout
            fragmentManager.beginTransaction()
                .add(R.id.container, fragment)
                .commit();
        }

        mNavigation = (NavigationView) findViewById(R.id.nav_view);
        if (mNavigation != null) { mNavigation.setNavigationItemSelectedListener(this); }
    }

    @Override
    public void onBackPressed() {
        if (mDrawer != null && mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        } else if (id == R.id.action_clear) {
            new AlertDialog.Builder(this)
                .setTitle("Clear My Playlist")
                .setMessage("Are you sure you want to clear My Playlist?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SQLiteDatabase db = mHelper.getWritableDatabase();

                        if (db != null) {
                            db.delete(TrackContract.TrackEntry.TABLE_NAME,
                                null,
                                null
                            );
                        }

                        Toast.makeText(getApplicationContext(), "Playlist Cleared", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Handles Navigation Drawer Item Navigation
     * Selects fragment to replace within container layout
     * @param item : Drawer item selected
     * @return : success(?)
     */
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment nextFragment = null;

        switch(id) {
            case R.id.nav_browse:
                nextFragment = TrackListFragment.newInstance(0, "", "");
                break;
            case R.id.nav_browse_country:
                nextFragment = TrackListFragment.newInstance(1, "", "");
                break;
            case R.id.nav_search:
                nextFragment = new SearchFragment();
                break;
            case R.id.nav_playlist:
                nextFragment = TrackListFragment.newInstance(6, "", "");
                break;
            default:
        }

        // replace container layout with fragment
        if (nextFragment != null) {
            fragmentManager.beginTransaction()
                .replace(R.id.container, nextFragment)
                .addToBackStack((String) title)
                .commit();
        }

        if (mDrawer != null) { mDrawer.closeDrawer(GravityCompat.START); }
        return true;
    }

    /**
     * Callback function for SearchFragment to load search results
     * @param fragID   : Fragment ID (search query type)
     * @param queryOne : String query one
     * @param queryTwo : String query two
     */
     @Override
     public void onSearchQuery(int fragID, String queryOne, String queryTwo) {
         FragmentManager fragmentManager = getSupportFragmentManager();
         Fragment nextFragment = TrackListFragment.newInstance(fragID, queryOne, queryTwo);

         if (nextFragment != null) {
             fragmentManager.beginTransaction()
                 .replace(R.id.container, nextFragment)
                 .addToBackStack((String) title)
                 .commit();
         }
     }
}