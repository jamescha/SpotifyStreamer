package com.jamescha.spotifystreamer;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.jamescha.spotifystreamer.mediaPlayer.MediaPlayerService;

public class MediaPlayerFragment extends DialogFragment {
    private static final String ACTION_PLAY = "com.jamescha.spotifystreamer.action.PLAY";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_media_player, container, false);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        Intent intent = getActivity().getIntent();
        String songUrl = intent.getStringExtra(SongsActivity.SELECTED_SONG_URL);
        String songImage = intent.getStringExtra(SongsActivity.SELECTED_SONG_IMAGE);

        Intent mediaPlayerIntent = new Intent(getActivity(), MediaPlayerService.class);

        mediaPlayerIntent.putExtra(SongsActivity.SELECTED_SONG_URL, songUrl);
        mediaPlayerIntent.putExtra(SongsActivity.SELECTED_SONG_IMAGE, songImage);
        mediaPlayerIntent.setAction(ACTION_PLAY);
        getActivity().getApplicationContext().startService(mediaPlayerIntent);

        return dialog;
    }


}
