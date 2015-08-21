package com.jamescha.spotifystreamer.mediaPlayer;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.jamescha.spotifystreamer.SongsActivity;

import java.io.IOException;

/**
 * Created by jamescha on 7/10/15.
 */
public class MediaPlayerService extends Service implements MediaPlayer.OnPreparedListener {
    private final String LOG_TAG = MediaPlayerService.class.getSimpleName();
    public static final String ACTION_PLAY = "com.jamescha.spotifystreamer.action.PLAY";
    public static final String ACTION_PAUSE = "com.jamescha.spotifystreamer.action.PAUSE";
    public static final String MEDIA_PLAYER_TRACK_LENGTH= "com.jamescha.spottifystream.MediaPlayerService.TRACKLENGTH";
    public static final String MEDIA_PLAYER_SEEK = "com.jamescha.spottifystream.MediaPlayerService.SEEKBAR";
    public static final String CURRENT_POSITION = "current_position";
    public static final String TRACK_LENGTH = "track_length";
    MediaPlayer mMediaPlayer = null;
    Integer currentPosition;

    LocalBroadcastManager broadcastManager;

    public int onStartCommand(Intent intent, int flags, int songId) {

        broadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
        if (intent != null) {
            if (intent.getAction().equals(ACTION_PLAY) && mMediaPlayer == null) {
                String url = intent.getStringExtra(SongsActivity.SELECTED_SONG_URL);
                mMediaPlayer = new MediaPlayer();
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                try {
                    mMediaPlayer.setDataSource(url);
                } catch (IOException ex) {
                    Log.d(LOG_TAG, ex.toString());
                }
                mMediaPlayer.setOnPreparedListener(this);
                mMediaPlayer.prepareAsync();
                updateRunTime.run();

            } else if (intent.getAction().equals(ACTION_PLAY) && mMediaPlayer != null) {
                mMediaPlayer.start();

            } else if (mMediaPlayer.isPlaying() && intent.getAction().equals(ACTION_PAUSE)) {
                mMediaPlayer.pause();
            }
        }
        return 0;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Intent intent = new Intent(MEDIA_PLAYER_TRACK_LENGTH);
        intent.putExtra(TRACK_LENGTH, mp.getDuration());
        broadcastManager.sendBroadcast(intent);
        mp.start();


    }



    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Runnable updateRunTime = new Runnable() {

        Intent intent = new Intent(MEDIA_PLAYER_SEEK);

        @Override
        public void run() {
            if(mMediaPlayer != null) {
                currentPosition = mMediaPlayer.getCurrentPosition();
                intent.putExtra(CURRENT_POSITION, (int) currentPosition);
                broadcastManager.sendBroadcast(intent);
            }
        }
    };
}
