package com.jamescha.spotifystreamer;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.jamescha.spotifystreamer.mediaPlayer.MediaPlayerService;
import com.squareup.picasso.Picasso;

public class MediaPlayerFragment extends DialogFragment {
    private static final String LOG_TAG = MediaPlayerFragment.class.getSimpleName();
    private static final String ACTION_PLAY = "com.jamescha.spotifystreamer.action.PLAY";
    private static final String ACTION_PAUSE = "com.jamescha.spotifystreamer.action.PAUSE";
    private Intent mediaPlayerIntent;
    private Boolean playPause = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_media_player, container, false);

        mediaPlayerIntent = new Intent(getActivity(), MediaPlayerService.class);

        final String songUrl = getArguments().getString(SongsActivity.SELECTED_SONG_URL);
        final String songImage = getArguments().getString(SongsActivity.SELECTED_SONG_IMAGE);

        TextView artistName = (TextView) view.findViewById(R.id.media_player_artist_name);
        TextView albumName = (TextView) view.findViewById(R.id.media_player_album_name);
        TextView trackName = (TextView) view.findViewById(R.id.media_player_track_name);
        TextView duration = (TextView) view.findViewById(R.id.media_player_duration);
        TextView fullTrackDuration = (TextView) view.findViewById(R.id.media_player_song_full_length);

        ImageButton previousButton = (ImageButton) view.findViewById(R.id.media_player_previous);
        ImageButton nextButton = (ImageButton) view.findViewById(R.id.media_player_next);
        final ImageButton playPauseButton = (ImageButton) view.findViewById(R.id.media_player_play_pause);

        ImageView scrubBar = (ImageView) view.findViewById(R.id.media_player_scrub_bar);
        ImageView songImageView = (ImageView) view.findViewById(R.id.media_player_song_image);

        Picasso.with(getActivity().getApplicationContext()).load(songImage).into(songImageView);

//        previousButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d(LOG_TAG, "Previous Button Pressed");
//            }
//        });

        playPauseButton.setImageResource(android.R.drawable.ic_media_play);

        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                if (playPause) {

                    mediaPlayerIntent.putExtra(SongsActivity.SELECTED_SONG_URL, songUrl);
                    mediaPlayerIntent.putExtra(SongsActivity.SELECTED_SONG_IMAGE, songImage);
                    mediaPlayerIntent.setAction(ACTION_PLAY);
                    getActivity().getApplicationContext().startService(mediaPlayerIntent);
                    playPauseButton.setImageResource(android.R.drawable.ic_media_pause);
                    playPause = false;

                } else if (playPause == false){
                    playPause = true;
                    mediaPlayerIntent.setAction(ACTION_PAUSE);
                    getActivity().getApplicationContext().stopService(mediaPlayerIntent);
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


}
