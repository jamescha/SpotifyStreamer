package com.jamescha.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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

//        setContentView(R.layout.activity_media_player);

        Intent intent = getIntent();
        String songUrl = intent.getStringExtra(SongsActivity.SELECTED_SONG_URL);
        String songImage = intent.getStringExtra(SongsActivity.SELECTED_SONG_IMAGE);
        mTwoPane = intent.getBooleanExtra(MainActivity.TWO_PANE, false);

//        TextView artistName = (TextView) findViewById(R.id.media_player_artist_name);
//        TextView albumName = (TextView) findViewById(R.id.media_player_album_name);
//        TextView trackName = (TextView) findViewById(R.id.media_player_track_name);
//        TextView duration = (TextView) findViewById(R.id.media_player_duration);
//        TextView fullTrackDuration = (TextView) findViewById(R.id.media_player_song_full_length);
//
//        ImageButton previousButton = (ImageButton) findViewById(R.id.media_player_previous);
//        ImageButton nextButton = (ImageButton) findViewById(R.id.media_player_next);
//        ImageButton playPauseButton = (ImageButton) findViewById(R.id.media_player_play_pause);
//
//        ImageView scrubBar = (ImageView) findViewById(R.id.media_player_scrub_bar);
//        ImageView songImageView = (ImageView) findViewById(R.id.media_player_song_image);
//
//        songImageView.setImageURI(Uri.parse(songImage));
//
//        previousButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d(LOG_TAG, "Previous Button Pressed");
//            }
//        });
//
//        Intent mediaPlayerIntent = new Intent(this, MediaPlayerService.class);
//
//        mediaPlayerIntent.putExtra(SongsActivity.SELECTED_SONG_URL, songUrl);
//        mediaPlayerIntent.putExtra(SongsActivity.SELECTED_SONG_IMAGE, songImage);
//        mediaPlayerIntent.setAction(ACTION_PLAY);
//        getApplicationContext().startService(mediaPlayerIntent);

        //showDialog();
    }

    public void showDialog() {
        FragmentManager fragmentManager = getSupportFragmentManager();
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
            transaction.add(R.id.media_player, newFragment)
                    .addToBackStack(null).commit();
        }
    }
}
