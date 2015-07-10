package com.jamescha.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;

import com.jamescha.spotifystreamer.sync.ArtistSyncAdapter;


public class MainActivity extends ActionBarActivity implements ArtistFragment.Callback {

    public static final String SELECTED_ARTIST_ID = "selected_artist";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ArtistSyncAdapter.initializeSyncAdapter(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onItemSelected(String artistId) {
        Intent intent = new Intent(this, SongsActivity.class);
        intent.putExtra(SELECTED_ARTIST_ID, artistId);
        startActivity(intent);
    }
}
