package com.jamescha.spotifystreamer;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.jamescha.spotifystreamer.mediaPlayer.MediaPlayerService;
import com.squareup.picasso.Picasso;

public class MediaPlayerFragment extends DialogFragment {
    private static final String LOG_TAG = MediaPlayerFragment.class.getSimpleName();
    private Intent mediaPlayerIntent;
    private Boolean playPause = true;
    private BroadcastReceiver seekReceiver;
    private SeekBar scrubBar;
    TextView duration;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        seekReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int currentTime = intent.getIntExtra(MediaPlayerService.CURRENT_POSITION, 0);

                if(scrubBar != null) {
                    scrubBar.setProgress(currentTime);
                }

                if(duration != null) {
                    String result = String.format("%02d:%02d", (currentTime % (60*60*1000))/(60*1000), (currentTime % (60*1000))/1000);
                    duration.setText(result);
                }
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_media_player, container, false);

        mediaPlayerIntent = new Intent(getActivity(), MediaPlayerService.class);

        final String songUrl = getArguments().getString(SongsActivity.SELECTED_SONG_URL);
        final String songImage = getArguments().getString(SongsActivity.SELECTED_SONG_IMAGE);
        final String sArtistName = getArguments().getString(SongsActivity.SELECTED_ARTIST_NAME);
        final String sAlbumName = getArguments().getString(SongsActivity.SELECTED_ALBUM_NAME);
        final String sTrackName = getArguments().getString(SongsActivity.SELECTED_SONG_NAME);
        final int mDuration = getArguments().getInt(SongsActivity.SELECTED_DURATION);

        final TextView artistName = (TextView) view.findViewById(R.id.media_player_artist_name);
        final TextView albumName = (TextView) view.findViewById(R.id.media_player_album_name);
        final TextView trackName = (TextView) view.findViewById(R.id.media_player_track_name);
        final TextView fullTrackDuration = (TextView) view.findViewById(R.id.media_player_song_full_length);
        final ImageButton previousButton = (ImageButton) view.findViewById(R.id.media_player_previous);
        final ImageButton nextButton = (ImageButton) view.findViewById(R.id.media_player_next);
        final ImageView songImageView = (ImageView) view.findViewById(R.id.media_player_song_image);
        final ImageButton playPauseButton = (ImageButton) view.findViewById(R.id.media_player_play_pause);

        duration = (TextView) view.findViewById(R.id.media_player_duration);
        scrubBar = (SeekBar) view.findViewById(R.id.media_player_scrub_bar);

        scrubBar.setMax(mDuration);
        scrubBar.setClickable(false);

        Picasso.with(getActivity().getApplicationContext()).load(songImage).into(songImageView);

        duration.setText("00:00");
        String result = String.format("%02d:%02d", (mDuration % (60*60*1000))/(60*1000), (mDuration % (60*1000))/1000);
        fullTrackDuration.setText(result);
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

        playPauseButton.setImageResource(android.R.drawable.ic_media_play);

        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (playPause) {

                    mediaPlayerIntent.putExtra(SongsActivity.SELECTED_SONG_URL, songUrl);
                    mediaPlayerIntent.setAction(MediaPlayerService.ACTION_PLAY);
                    getActivity().getApplicationContext().startService(mediaPlayerIntent);
                    playPauseButton.setImageResource(android.R.drawable.ic_media_pause);
                    playPause = false;

                } else if (playPause == false){
                    playPause = true;
                    mediaPlayerIntent.setAction(MediaPlayerService.ACTION_PAUSE);
                    getActivity().getApplicationContext().startService(mediaPlayerIntent);
                    playPauseButton.setImageResource(android.R.drawable.ic_media_play);
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
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(seekReceiver,
                new IntentFilter(MediaPlayerService.MEDIA_PLAYER_SEEK));
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(seekReceiver);
    }
}
