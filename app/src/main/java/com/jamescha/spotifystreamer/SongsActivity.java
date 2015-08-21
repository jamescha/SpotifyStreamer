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
    public static final String SELECTED_SONG_URL = "selected_song_url";
    public static final String SELECTED_SONG_IMAGE = "selected_song_image";
    public static final String SELECTED_SONG_NAME = "selected_song_name";
    public static final String SELECTED_ARTIST_NAME = "selected_artist_name";
    public static final String SELECTED_ALBUM_NAME = "selected_album_name";
    public static final String SELECTED_DURATION = "selected_duration";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "Created.");

        setContentView(R.layout.activity_songs_actvity);

        Intent intent = getIntent();
        String artistId = intent.getStringExtra(SELECTED_ARTIST_ID);

        Bundle bundle = new Bundle();
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
    public void onItemSelected(String url, String songImage, Boolean mTwoPane) {
        Intent intent = new Intent(this, MediaPlayerActivity.class);
        intent.putExtra(SELECTED_SONG_URL, url);
        intent.putExtra(SELECTED_SONG_IMAGE, songImage);
        intent.putExtra(MainActivity.TWO_PANE, mTwoPane);
        startActivity(intent);

    }


}
