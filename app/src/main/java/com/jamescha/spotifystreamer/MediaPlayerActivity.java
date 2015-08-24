package com.jamescha.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by jamescha on 8/6/15.
 */
public class MediaPlayerActivity extends ActionBarActivity {
    private final String LOG_TAG = MediaPlayerActivity.class.getSimpleName();
    private static final String ACTION_PLAY = "com.jamescha.spotifystreamer.action.PLAY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "Created.");

        setContentView(R.layout.activity_media_player);

        Intent intent = getIntent();
        ArrayList<HashMap<String, String>> songList =
                (ArrayList<HashMap<String, String>>)intent.getSerializableExtra(SongsFragment.SONG_LIST);
        int position = intent.getIntExtra(SongsFragment.POSITION, 0);
        String songUrl = intent.getStringExtra(SongsActivity.SELECTED_SONG_URL);

//        Bundle bundle = new Bundle();
//        bundle.putSerializable(SongsFragment.SONG_LIST, songList);
//        bundle.putInt(SongsFragment.POSITION, position);
//        bundle.putString(SongsActivity.SELECTED_SONG_URL, songUrl);

        MediaPlayerFragment newFragment = MediaPlayerFragment.newInstance(position,songList,songUrl);

//        newFragment.setArguments(bundle);
//        newFragment.setShowsDialog(false);
//        newFragment.show(getSupportFragmentManager(), "test" );

        // The device is smaller, so show the fragment fullscreen
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        // For a little polish, specify a transition animation
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        // To make it fullscreen, use the 'content' root view as the container
        // for the fragment, which is always the root view for the activity
        transaction.add(R.id.media_fragment, newFragment).commit();
    }
}
