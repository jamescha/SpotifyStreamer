package com.jamescha.spotifystreamer.mediaPlayer;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import com.jamescha.spotifystreamer.SongsActivity;

import java.io.IOException;

/**
 * Created by jamescha on 7/10/15.
 */
public class MediaPlayerService extends Service implements MediaPlayer.OnPreparedListener {
    private final String LOG_TAG = MediaPlayerService.class.getSimpleName();
    private static final String ACTION_PLAY = "com.jamescha.spotifystreamer.action.PLAY";
    MediaPlayer mMediaPlayer = null;

    public int onStartCommand(Intent intent, int flags, int songId) {
        if (intent.getAction().equals(ACTION_PLAY)) {
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
        }
        return 0;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
