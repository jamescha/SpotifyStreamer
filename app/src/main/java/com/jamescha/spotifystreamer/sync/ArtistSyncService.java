package com.jamescha.spotifystreamer.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by jamescha on 6/24/15.
 */
public class ArtistSyncService extends Service {

    private String LOG_TAG = ArtistSyncService.class.getSimpleName();
    private static final Object sSyncAdapterLock = new Object();
    private static ArtistSyncAdapter sArtistSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d(LOG_TAG, "onCreate");
        synchronized (sSyncAdapterLock) {
            if (sArtistSyncAdapter == null) {
                sArtistSyncAdapter = new ArtistSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sArtistSyncAdapter.getSyncAdapterBinder();
    }


}
