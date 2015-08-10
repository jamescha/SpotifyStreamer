package com.jamescha.spotifystreamer;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

/**
 * Created by jamescha on 8/6/15.
 */
public class MediaPlayerActivity extends ActionBarActivity{
    private final String LOG_TAG = MediaPlayerActivity.class.getSimpleName();
    private static final String ACTION_PLAY = "com.jamescha.spotifystreamer.action.PLAY";

    public Boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "Created.");

        Intent intent = getIntent();
        String songUrl = intent.getStringExtra(SongsActivity.SELECTED_SONG_URL);
        String songImage = intent.getStringExtra(SongsActivity.SELECTED_SONG_IMAGE);
        mTwoPane = intent.getBooleanExtra(MainActivity.TWO_PANE, false);

//        Intent mediaPlayerIntent = new Intent(this, MediaPlayerService.class);
//
//        mediaPlayerIntent.putExtra(SongsActivity.SELECTED_SONG_URL, songUrl);
//        mediaPlayerIntent.putExtra(SongsActivity.SELECTED_SONG_IMAGE, songImage);
//        mediaPlayerIntent.setAction(ACTION_PLAY);
//        getApplicationContext().startService(mediaPlayerIntent);

        showDialog();
    }

    public void showDialog() {
        FragmentManager fragmentManager = getFragmentManager();
        MediaPlayerFragment newFragment = new MediaPlayerFragment();

        if (mTwoPane) {
            // The device is using a large layout, so show the fragment as a dialog
            newFragment.show(fragmentManager, "dialog");
        } else {
            // The device is smaller, so show the fragment fullscreen
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            // For a little polish, specify a transition animation
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            // To make it fullscreen, use the 'content' root view as the container
            // for the fragment, which is always the root view for the activity
            transaction.add(android.R.id.content, newFragment)
                    .addToBackStack(null).commit();
        }
    }
}
