package com.jamescha.spotifystreamer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by jamescha on 8/24/15.
 */
public class MusicIntentReceiver extends BroadcastReceiver {
    private static String LOG_TAG = MusicIntentReceiver.class.getSimpleName();
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(
                android.media.AudioManager.ACTION_AUDIO_BECOMING_NOISY)) {
            Log.d(LOG_TAG, "Audio becoming Noisy");
            //context.stopService(new Intent(context.getApplicationContext(), MediaPlayerService.class));
            Intent mediaPlayerIntent = new Intent(context, MediaPlayerService.class);
            context.stopService(mediaPlayerIntent);
        }
    }
}
