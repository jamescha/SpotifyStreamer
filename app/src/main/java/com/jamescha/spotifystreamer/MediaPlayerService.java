package com.jamescha.spotifystreamer;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by jamescha on 7/10/15.
 */
public class MediaPlayerService extends Service implements MediaPlayer.OnPreparedListener {
    private final String LOG_TAG = MediaPlayerService.class.getSimpleName();
    private final IBinder iBinder = new LocalBinder();
    public static final String ACTION_PLAY = "com.jamescha.spotifystreamer.action.PLAY";
    private static final int NOTIFICATION_ID = 1;
    private MediaPlayer mMediaPlayer = null;
    private Handler mHandler = new Handler();
    private SeekBar scrubBar;
    private TextView duration;
    private String url;
    private TextView fullTrackDuration;
    private Boolean isPrepared = false;
    private WifiManager.WifiLock wifiLock;
    private PendingIntent pi;
    private Notification.Builder notificationBuilder;

    public class LocalBinder extends Binder {
        MediaPlayerService getService() {
            return MediaPlayerService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        wifiLock = ((WifiManager) getSystemService(Context.WIFI_SERVICE))
                .createWifiLock(WifiManager.WIFI_MODE_FULL, "mylock");
        wifiLock.acquire();
        pi = PendingIntent.getActivity(getApplicationContext(), 0,
                new Intent(getApplicationContext(), MediaPlayerFragment.class),
                PendingIntent.FLAG_UPDATE_CURRENT);

        notificationBuilder = new Notification.Builder(getApplicationContext())
                .setSmallIcon(android.R.drawable.ic_media_play)
                .setContentTitle("Fantastic Spotify App")
                .setContentIntent(pi)
                .setOngoing(true);

    }

    public int onStartCommand(Intent intent, int flags, int songId) {

        if (intent != null) {
            if (intent.getAction().equals(ACTION_PLAY)) {
                url = intent.getStringExtra(SongsActivity.SELECTED_SONG_URL);
                mMediaPlayer.reset();
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

        playSong(MediaPlayerFragment.duration,
                MediaPlayerFragment.scrubBar,
                MediaPlayerFragment.fullTrackDuration,
                MediaPlayerFragment.songInfo);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    public Boolean pauseSong () {
        if (mMediaPlayer.isPlaying() && mMediaPlayer != null) {
            Log.d(LOG_TAG, "Pause Song");
            mMediaPlayer.pause();
            return true;
        }
        return false;
    }

    public Boolean playSong (TextView duration,
                             SeekBar scrubBar,
                             TextView fullTrackDuration,
                             HashMap<String,String> songInfo) {
        if (mMediaPlayer != null && isPrepared) {
            Log.d(LOG_TAG, "Play Song");
            mMediaPlayer.start();
            this.scrubBar = scrubBar;
            this.duration = duration;
            this.fullTrackDuration = fullTrackDuration;
            int mDuration = mMediaPlayer.getDuration();
            scrubBar.setMax(mDuration);
            String result = String.format("%02d:%02d", (mDuration % (60 * 60 * 1000)) / (60 * 1000), (mDuration % (60 * 1000)) / 1000);
            fullTrackDuration.setText(result);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Playing: ")
                    .append(songInfo.get(SongsActivity.SELECTED_SONG_NAME))
                    .append(" by ")
                    .append(songInfo.get(SongsActivity.SELECTED_ARTIST_NAME));


            Picasso.with(getApplicationContext())
                    .load(songInfo.get(SongsActivity.SELECTED_SONG_IMAGE)).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    notificationBuilder.setLargeIcon(bitmap);
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });

            notificationBuilder.setContentText(stringBuilder.toString());
            startForeground(NOTIFICATION_ID, notificationBuilder.build());
            return true;
        }
        Log.d(LOG_TAG, "MediaPlayer not ready to play.");
        return false;
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
            if(mMediaPlayer != null && isPrepared) {
                int currentPosition = mMediaPlayer.getCurrentPosition();
                if(scrubBar != null && duration != null) {
                    int mDuration = mMediaPlayer.getDuration();
                    scrubBar.setMax(mDuration);
                    String result = String.format("%02d:%02d", (mDuration % (60 * 60 * 1000)) / (60 * 1000), (mDuration % (60 * 1000)) / 1000);
                    fullTrackDuration.setText(result);
                    scrubBar.setProgress(currentPosition);
                    result = String.format("%02d:%02d", (currentPosition % (60*60*1000))/(60*1000), (currentPosition % (60*1000))/1000);
                    duration.setText(result);
                }
                mHandler.postDelayed(updateRunTime, 100);
            }
        }
    };

    @Override
    public void onDestroy() {
        if(mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            isPrepared = false;
            mMediaPlayer = null;
        }
        wifiLock.release();
        stopForeground(true);
        Log.d(LOG_TAG, "Service Destroyed");
        super.onDestroy();
    }
}
