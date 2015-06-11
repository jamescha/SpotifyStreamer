package com.jamescha.spotifystreamer.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;

/**
 * Created by jamescha on 6/9/15.
 */
public class SpotifyStreamerSyncAdapter extends AbstractThreadedSyncAdapter {

    public SpotifyStreamerSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    private final String LOG_TAG = SpotifyStreamerSyncAdapter.class.getSimpleName();

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

    }


}
