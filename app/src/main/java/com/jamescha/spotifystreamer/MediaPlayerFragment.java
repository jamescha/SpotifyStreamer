package com.jamescha.spotifystreamer;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MediaPlayerFragment extends DialogFragment {
    private static final String LOG_TAG = MediaPlayerFragment.class.getSimpleName();
    private static final String PLAY_PAUSE_STATE = "play_pause_state";
    private Boolean mBound = false;
    private MediaPlayerService mediaPlayerService;
//    private Intent mediaPlayerIntent;
    private Boolean playPause = true;
    private BroadcastReceiver seekReceiver;
    private BroadcastReceiver maxLength;
    private TextView fullTrackDuration;
    private SeekBar scrubBar;
    private TextView duration;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        seekReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                int currentTime = intent.getIntExtra(MediaPlayerService.CURRENT_POSITION, 0);
//
//                if(scrubBar != null) {
//                    scrubBar.setProgress(currentTime);
//                }
//
//                if(duration != null) {
//                    String result = String.format("%02d:%02d", (currentTime % (60*60*1000))/(60*1000), (currentTime % (60*1000))/1000);
//                    duration.setText(result);
//                }
//            }
//        };
//
//        maxLength = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                int mDuration = intent.getIntExtra(MediaPlayerService.SONG_LENGTH, 0);
//                String result = String.format("%02d:%02d", (mDuration % (60*60*1000))/(60*1000), (mDuration % (60*1000))/1000);
//                fullTrackDuration.setText(result);
//            }
//        };


        if (savedInstanceState != null) {
            playPause = savedInstanceState.getBoolean(PLAY_PAUSE_STATE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_media_player, container, false);

//        mediaPlayerIntent = new Intent(getActivity(), MediaPlayerService.class);


//        final String songUrl = getArguments().getString(SongsActivity.SELECTED_SONG_URL);
        final String songImage = getArguments().getString(SongsActivity.SELECTED_SONG_IMAGE);
        final String sArtistName = getArguments().getString(SongsActivity.SELECTED_ARTIST_NAME);
        final String sAlbumName = getArguments().getString(SongsActivity.SELECTED_ALBUM_NAME);
        final String sTrackName = getArguments().getString(SongsActivity.SELECTED_SONG_NAME);

        final TextView artistName = (TextView) view.findViewById(R.id.media_player_artist_name);
        final TextView albumName = (TextView) view.findViewById(R.id.media_player_album_name);
        final TextView trackName = (TextView) view.findViewById(R.id.media_player_track_name);
        final ImageButton previousButton = (ImageButton) view.findViewById(R.id.media_player_previous);
        final ImageButton nextButton = (ImageButton) view.findViewById(R.id.media_player_next);
        final ImageView songImageView = (ImageView) view.findViewById(R.id.media_player_song_image);
        final ImageButton playPauseButton = (ImageButton) view.findViewById(R.id.media_player_play_pause);

        duration = (TextView) view.findViewById(R.id.media_player_duration);
        scrubBar = (SeekBar) view.findViewById(R.id.media_player_scrub_bar);
        fullTrackDuration = (TextView) view.findViewById(R.id.media_player_song_full_length);

        scrubBar.setClickable(false);

        Picasso.with(getActivity().getApplicationContext()).load(songImage).into(songImageView);

        duration.setText("00:00");
        fullTrackDuration.setText("00:00");

        artistName.setText(sArtistName);
        albumName.setText(sAlbumName);
        trackName.setText(sTrackName);

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LOG_TAG, "Previous Button Pressed");
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LOG_TAG, "Next Button Pressed");
            }
        });

        scrubBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
              if (fromUser) {
                  mediaPlayerService.seekTo(progress);
              } else if (progress == seekBar.getMax()) {
                  playPauseButton.setImageResource(android.R.drawable.ic_media_play);
                  playPause = true;
              }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        if (savedInstanceState != null) {
            if (playPause) {
                playPauseButton.setImageResource(android.R.drawable.ic_media_play);
            } else {
                playPauseButton.setImageResource(android.R.drawable.ic_media_pause);
            }
        } else {
            playPauseButton.setImageResource(android.R.drawable.ic_media_play);
        }

        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (playPause) {
                    if (mediaPlayerService.playSong(duration, scrubBar, fullTrackDuration)) {
                        playPauseButton.setImageResource(android.R.drawable.ic_media_pause);
                        playPause = false;
                    }
                } else if (playPause == false){
                    if (mediaPlayerService.pauseSong()) {
                        playPauseButton.setImageResource(android.R.drawable.ic_media_play);
                        playPause = true;
                    }
                }

            }
        });

        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Intent intent = view.getIntent();

        String songUrl = getArguments().getString(SongsActivity.SELECTED_SONG_URL);
        String songImage = getArguments().getString(SongsActivity.SELECTED_SONG_IMAGE);

        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        Intent mediaPlayerIntent = new Intent(getActivity(), MediaPlayerService.class);
        String songUrl = getArguments().getString(SongsActivity.SELECTED_SONG_URL);
        mediaPlayerIntent.putExtra(SongsActivity.SELECTED_SONG_URL, songUrl);
        mediaPlayerIntent.setAction(MediaPlayerService.ACTION_PLAY);
        getActivity().getApplicationContext().startService(mediaPlayerIntent);
        getActivity().bindService(mediaPlayerIntent, mConnection, Context.BIND_AUTO_CREATE);
        getActivity().runOnUiThread(updateSeekPosition);
//        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(seekReceiver,
//                new IntentFilter(MediaPlayerService.MEDIA_PLAYER_SEEK));
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mBound) {
            getActivity().unbindService(mConnection);
            mBound = false;
        }
//        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(seekReceiver);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(PLAY_PAUSE_STATE, playPause);
        super.onSaveInstanceState(outState);
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MediaPlayerService.LocalBinder binder = (MediaPlayerService.LocalBinder) service;
            mediaPlayerService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }
    };

    private Runnable updateSeekPosition = new Runnable() {
        @Override
        public void run() {
            if (mBound) {
                int currentTime = mediaPlayerService.getCurrentPosition();
                scrubBar.setProgress(currentTime);
                String result = String.format("%02d:%02d", (currentTime % (60*60*1000))/(60*1000), (currentTime % (60*1000))/1000);
                duration.setText(result);
            }
        }
    };

}
