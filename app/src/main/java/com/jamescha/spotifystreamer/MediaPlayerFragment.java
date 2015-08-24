package com.jamescha.spotifystreamer;

import android.app.Dialog;
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

import java.util.ArrayList;
import java.util.HashMap;

public class MediaPlayerFragment extends DialogFragment {
    private static final String LOG_TAG = MediaPlayerFragment.class.getSimpleName();
    private static final String PLAY_PAUSE_STATE = "play_pause_state";
    private static final String SONG_POSTION = "song_position";
    private Boolean mBound = false;
    private MediaPlayerService mediaPlayerService;
    private Intent mediaPlayerIntent;
    private Boolean playPause = true;
    private ImageButton playPauseButton;
    public static TextView fullTrackDuration;
    public static SeekBar scrubBar;
    public static TextView duration;
    private int position;
    private ArrayList<HashMap<String, String>> songList;
    public static HashMap<String,String> songInfo;
    private TextView artistName;
    private TextView albumName;
    private TextView trackName;
    private ImageView songImageView;

    public static MediaPlayerFragment newInstance(int position,  ArrayList<HashMap<String, String>> songList, String url) {
        MediaPlayerFragment mediaPlayerFragment = new MediaPlayerFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(SongsFragment.SONG_LIST, songList);
        bundle.putInt(SongsFragment.POSITION, position);
        bundle.putString(SongsActivity.SELECTED_SONG_URL, url);
        mediaPlayerFragment.setArguments(bundle);
        return mediaPlayerFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            playPause = savedInstanceState.getBoolean(PLAY_PAUSE_STATE);
            position = savedInstanceState.getInt(SONG_POSTION);
        } else {
            if (getArguments() != null) {
                position = getArguments().getInt(SongsFragment.POSITION);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_media_player, container, false);

        songList = (ArrayList<HashMap<String, String>>) getArguments().getSerializable(SongsFragment.SONG_LIST);
        songInfo = songList.get(position);

        artistName = (TextView) view.findViewById(R.id.media_player_artist_name);
        albumName = (TextView) view.findViewById(R.id.media_player_album_name);
        trackName = (TextView) view.findViewById(R.id.media_player_track_name);
        songImageView = (ImageView) view.findViewById(R.id.media_player_song_image);

        final ImageButton previousButton = (ImageButton) view.findViewById(R.id.media_player_previous);
        final ImageButton nextButton = (ImageButton) view.findViewById(R.id.media_player_next);

        playPauseButton = (ImageButton) view.findViewById(R.id.media_player_play_pause);

        duration = (TextView) view.findViewById(R.id.media_player_duration);
        scrubBar = (SeekBar) view.findViewById(R.id.media_player_scrub_bar);
        fullTrackDuration = (TextView) view.findViewById(R.id.media_player_song_full_length);

        scrubBar.setClickable(false);

        duration.setText("00:00");
        fullTrackDuration.setText("00:00");

        setUpView();

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LOG_TAG, "Previous Button Pressed");
                if(position > 0) {
                    position--;
                    songInfo = songList.get(position);
                }
                setUpView();
                startSong(songInfo.get(SongsActivity.SELECTED_SONG_URL));
                playPauseButton.setImageResource(android.R.drawable.ic_media_play);
                playPause = true;
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(LOG_TAG, "Next Button Pressed");
                if(position < songList.size()) {
                    position++;
                    songInfo = songList.get(position);
                }
                setUpView();
                startSong(songInfo.get(SongsActivity.SELECTED_SONG_URL));
                playPauseButton.setImageResource(android.R.drawable.ic_media_play);
                playPause = true;
            }
        });

        scrubBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
              if (fromUser) {
                  mediaPlayerService.seekTo(progress);
                  playPauseButton.setImageResource(android.R.drawable.ic_media_pause);
                  playPause = false;
              }

              if (progress == seekBar.getMax()) {
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
                    if (mediaPlayerService.playSong(duration,
                                                    scrubBar,
                                                    fullTrackDuration,
                                                    songInfo)) {
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

    private void startSong (String songUrl) {
        mediaPlayerIntent.putExtra(SongsActivity.SELECTED_SONG_URL, songUrl);
        mediaPlayerIntent.setAction(MediaPlayerService.ACTION_PLAY);
        getActivity().getApplicationContext().startService(mediaPlayerIntent);
    }

    private void setUpView () {
        artistName.setText(songInfo.get(SongsActivity.SELECTED_ARTIST_NAME));
        albumName.setText(songInfo.get(SongsActivity.SELECTED_ALBUM_NAME));
        trackName.setText(songInfo.get(SongsActivity.SELECTED_SONG_NAME));
        Picasso.with(getActivity()
                .getApplicationContext())
                .load(songInfo.get(SongsActivity.SELECTED_SONG_IMAGE))
                .into(songImageView);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(LOG_TAG, "Start Fragment");
        mediaPlayerIntent = new Intent(getActivity(), MediaPlayerService.class);
        String songUrl = getArguments().getString(SongsActivity.SELECTED_SONG_URL);
        startSong(songUrl);
        getActivity().bindService(mediaPlayerIntent, mConnection, Context.BIND_AUTO_CREATE);
        playPauseButton.setImageResource(android.R.drawable.ic_media_pause);
        playPause = false;
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mBound) {
            getActivity().unbindService(mConnection);
            mBound = false;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(PLAY_PAUSE_STATE, playPause);
        outState.putInt(SONG_POSTION, position);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
