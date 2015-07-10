package com.jamescha.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;

import com.jamescha.spotifystreamer.sync.ArtistSyncAdapter;


public class SongsActivity extends ActionBarActivity implements SongsFragment.Callback {
    private static final String LOG_TAG = SongsActivity.class.getSimpleName();
    public static final String SELECTED_ARTIST_ID = "selected_artist";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "Created.");

        setContentView(R.layout.activity_songs_actvity);

        Intent intent = getIntent();
        Bundle bundle = new Bundle();
        String artistId = intent.getStringExtra(SELECTED_ARTIST_ID);
        bundle.putString(ArtistSyncAdapter.ARTIST_ID_KEY, artistId);
        bundle.putInt(ArtistSyncAdapter.SEARCH_TYPE, ArtistSyncAdapter.SONG_SEARCH);

        ArtistSyncAdapter.syncImmediately(this, bundle, null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                NavUtils.navigateUpFromSameTask(this);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected() {

    }
}
