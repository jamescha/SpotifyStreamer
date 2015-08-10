package com.jamescha.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;

import com.jamescha.spotifystreamer.sync.ArtistSyncAdapter;


public class MainActivity extends ActionBarActivity implements ArtistFragment.Callback {

    public static final String SELECTED_ARTIST_ID = "selected_artist";
    public static final String TWO_PANE = "two_pane";
    private static final String SONG_TAG = "SONGTAG";

    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (findViewById(R.id.top_ten_songs) != null) {

            mTwoPane = true;

            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.top_ten_songs, new SongsFragment(), SONG_TAG)
                .commit();
            }
        } else {
            mTwoPane = false;
            getSupportActionBar().setElevation(0f);
        }

        ArtistSyncAdapter.initializeSyncAdapter(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onItemSelected(String artistId) {

        if (mTwoPane) {
            Bundle args = new Bundle();
            args.putString(SELECTED_ARTIST_ID, artistId);
            args.putInt(ArtistSyncAdapter.SEARCH_TYPE, ArtistSyncAdapter.SONG_SEARCH);
            args.putBoolean(TWO_PANE, mTwoPane);
            SongsFragment songsFragment = new SongsFragment();
            songsFragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.top_ten_songs, songsFragment, SONG_TAG)
                    .commit();

        } else {
            Intent intent = new Intent(this, SongsActivity.class);
            intent.putExtra(SELECTED_ARTIST_ID, artistId);
            startActivity(intent);
        }


    }
}
