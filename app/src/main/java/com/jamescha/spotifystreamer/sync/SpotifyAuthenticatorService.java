package com.jamescha.spotifystreamer.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by jamescha on 6/25/15.
 */
public class SpotifyAuthenticatorService extends Service {

    private SpotifyAuthenticator mAuthenticator;

    public void onCreate() {
        mAuthenticator = new SpotifyAuthenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
