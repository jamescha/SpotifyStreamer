package com.jamescha.spotifystreamer;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;

/**
 * Created by jamescha on 7/10/15.
 */
public class MediaPlayerService extends Service implements MediaPlayer.OnPreparedListener {
    private final String LOG_TAG = MediaPlayerService.class.getSimpleName();
    private final IBinder iBinder = new LocalBinder();
    public static final String ACTION_PLAY = "com.jamescha.spotifystreamer.action.PLAY";
    public static final String ACTION_PAUSE = "com.jamescha.spotifystreamer.action.PAUSE";
    public static final String MEDIA_PLAYER_SEEK = "com.jamescha.spotifystream.MediaPlayerService.SEEKBAR";
    public static final String SONG_DURATION = "com.jamescha.spotifystream.MediaPlayerService.SONGDURATION";
    public static final String CURRENT_POSITION = "current_position";
    public static final String SONG_LENGTH = "song_length";
    private MediaPlayer mMediaPlayer = null;
//    private int currentPosition;
//    private int song_length;
    private Handler mHandler = new Handler();
    //private Intent songLengthIntent = new Intent(SONG_DURATION);
   // private LocalBroadcastManager broadcastManager;
    private SeekBar srubBar;
    private TextView duration;
    private String url;
    private Boolean isPrepared = false;

    public class LocalBinder extends Binder {
        MediaPlayerService getService() {
            return MediaPlayerService.this;
        }
    }

    public int onStartCommand(Intent intent, int flags, int songId) {

       // broadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
        if (intent != null) {
            if (intent.getAction().equals(ACTION_PLAY)) {
                url = intent.getStringExtra(SongsActivity.SELECTED_SONG_URL);
                mMediaPlayer = new MediaPlayer();
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                try {
                    mMediaPlayer.setDataSource(url);
                } catch (IOException ex) {
                    Log.d(LOG_TAG, ex.toString());
                }
                isPrepared = false;
                mMediaPlayer.setOnPreparedListener(this);
                mMediaPlayer.prepareAsync();
            }

        }
        return 0;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        isPrepared = true;
        updateRunTime.run();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    public int getDuration () {
        if (mMediaPlayer != null && isPrepared) {
            return mMediaPlayer.getDuration();
        } else {
            return 0;
        }
    }

    public int getCurrentPosition () {
        if (mMediaPlayer != null && isPrepared) {
            return mMediaPlayer.getCurrentPosition();
        } else {
            return 0;
        }
    }

    public Boolean pauseSong () {
        if (mMediaPlayer.isPlaying() && mMediaPlayer != null) {
            Log.d(LOG_TAG, "Pause Song");
            mMediaPlayer.pause();
            return true;
        }
        return false;
    }

    public Boolean playSong (TextView duration, SeekBar scrubBar, TextView fullTrackDuration) {
        if (mMediaPlayer != null && isPrepared) {
            Log.d(LOG_TAG, "Play Song");
            mMediaPlayer.start();
            this.srubBar = scrubBar;
            this.duration = duration;
            int mDuration = mMediaPlayer.getDuration();
            scrubBar.setMax(mDuration);
            String result = String.format("%02d:%02d", (mDuration % (60 * 60 * 1000)) / (60 * 1000), (mDuration % (60 * 1000)) / 1000);
            fullTrackDuration.setText(result);
            return true;
        }
        Log.d(LOG_TAG, "MediaPlayer not ready to play.");
        return false;
    }

    public Boolean mediaPlayerState() {
       return isPrepared;
    }

    public MediaPlayer getmMediaPlayer () {
        return mMediaPlayer;
    }

    public void seekTo (int position) {
        if (mMediaPlayer != null && isPrepared) {
            mMediaPlayer.seekTo(position);
            if (!mMediaPlayer.isPlaying()) {
                mMediaPlayer.start();
            }
        }
    }

    private Runnable updateRunTime = new Runnable() {

        @Override
        public void run() {
            if(mMediaPlayer != null) {
                int currentPosition = mMediaPlayer.getCurrentPosition();
                if(srubBar != null && duration != null) {
                    srubBar.setProgress(currentPosition);
                    String result = String.format("%02d:%02d", (currentPosition % (60*60*1000))/(60*1000), (currentPosition % (60*1000))/1000);
                    duration.setText(result);
                }
                mHandler.postDelayed(updateRunTime, 100);
            }
        }
    };

    @Override
    public void onDestroy() {
        if(mMediaPlayer != null) {
            mMediaPlayer.release();
            isPrepared = false;
            mMediaPlayer = null;
        }
        super.onDestroy();
    }
}
