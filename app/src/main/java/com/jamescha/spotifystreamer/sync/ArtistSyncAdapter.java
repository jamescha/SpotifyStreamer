package com.jamescha.spotifystreamer.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;

import com.jamescha.spotifystreamer.ArtistFragment;
import com.jamescha.spotifystreamer.R;
import com.jamescha.spotifystreamer.data.SpotifyContract;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by jamescha on 6/9/15.
 */
public class ArtistSyncAdapter extends AbstractThreadedSyncAdapter {
    public final String LOG_TAG = ArtistSyncAdapter.class.getSimpleName();

    private static final String ARTIST_NAME_KEY = "artist_name";
    private static final String ARTIST_SEARCH_KEY = "artist_search";
    private static final String IMAGE_NOT_FOUND = "https://farm1.staticflickr.com/186/382004453_f4b2772254_o.gif";
    public static final String ARTIST_ID_KEY = "artist_id";
    public static final String SEARCH_TYPE = "search_type";
    public static final int ARTIST_SEARCH = 0;
    public static final int SONG_SEARCH = 1;


    private static Handler mHandler;

    private SpotifyApi spotifyApi;
    private SpotifyService spotifyService;

    public ArtistSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        spotifyApi = new SpotifyApi();
        spotifyService = spotifyApi.getService();
    }

    @Override
    public void onPerformSync(Account account, final Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {
        switch (extras.getInt(SEARCH_TYPE)) {
            case ARTIST_SEARCH: {
                Log.d(LOG_TAG, "Starting Artist Sync.");
                spotifyService.searchArtists(extras.getString(ARTIST_NAME_KEY), new Callback<ArtistsPager>() {
                    @Override
                    public void success(ArtistsPager artistsPager, Response response) {
                        getContext().getContentResolver().delete(SpotifyContract.ArtistEntry.CONTENT_URI, null, null);
                        Vector<ContentValues> artistsVector = new Vector<>();

                        if (artistsPager.artists.items.size() == 0 && extras.getBoolean(ARTIST_SEARCH_KEY)) {
                            mHandler.sendEmptyMessage(ArtistFragment.TASK_COMPLETE);
                        }

                        for (Artist artist : artistsPager.artists.items) {
                            ContentValues artistValues = new ContentValues();
                            artistValues.put(SpotifyContract.ArtistEntry.COLUMN_ARTIST_NAME, artist.name);
                            artistValues.put(SpotifyContract.ArtistEntry.COLUMN_ARTIST_ID, artist.id);
                            String imageUrl;
                            if (artist.images.isEmpty() == false) {
                                imageUrl = artist.images.get(0).url;
                            } else {
                                Log.d(LOG_TAG, "No artist image found for : " + artist.name);
                                imageUrl = IMAGE_NOT_FOUND;
                            }
                            artistValues.put(SpotifyContract.ArtistEntry.COLUMN_ARTIST_IMAGE, imageUrl);
                            artistsVector.add(artistValues);
                        }

                        ContentValues[] artistArray = new ContentValues[artistsVector.size()];
                        artistsVector.toArray(artistArray);
                        getContext().getContentResolver().bulkInsert(
                                SpotifyContract.ArtistEntry.CONTENT_URI,
                                artistArray);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        getContext().getContentResolver().delete(SpotifyContract.ArtistEntry.CONTENT_URI, null, null);
                        Log.d(LOG_TAG, "Search Artist Error: " + error);
                    }
                });
                break;
            }

            case SONG_SEARCH: {
                Log.d(LOG_TAG, "Starting Song Sync.");
                Map<String, Object> countryQuery = new HashMap<>();
                countryQuery.put("country", "US");
                getContext().getContentResolver().delete(SpotifyContract.SongsEntry.CONTENT_URI, null, null);
                spotifyService.getArtistTopTrack(extras.getString(ARTIST_ID_KEY), countryQuery, new Callback<Tracks>() {

                    @Override
                    public void success(Tracks tracks, Response response) {
                        Log.d(LOG_TAG, "Artist: " + tracks.tracks.get(0).artists.get(0).name);
                        Integer height;
                        final Vector<ContentValues> songsVector = new Vector<>();
                        for (Track track : tracks.tracks) {
                            ContentValues songValues = new ContentValues();
                            songValues.put(SpotifyContract.SongsEntry.COLUMN_SONG_NAME, track.name);
                            songValues.put(SpotifyContract.SongsEntry.COLUMN_ALBUM_NAME, track.album.name);
                            songValues.put(SpotifyContract.SongsEntry.COLUMN_PREVIEW_URL, track.preview_url);

                            //Try to get optimal sized album cover
                            Pair<String, Integer> largeImageUrl = new Pair<>(IMAGE_NOT_FOUND, 0);
                            Pair<String, Integer> smallImageUrl = new Pair<>(IMAGE_NOT_FOUND, 640);
                            if (track.album.images.isEmpty() == false) {
                                Iterator<Image> trackImageIterator = track.album.images.iterator();
                                while (trackImageIterator.hasNext()) {
                                    Image image = trackImageIterator.next();
                                    height = image.height;

                                    //Largest image possible or 640
                                    if (height > largeImageUrl.second && largeImageUrl.second != 640) {
                                        largeImageUrl = new Pair<>(
                                                image.url,
                                                image.height);
                                        Log.d(LOG_TAG, "Large Image Height: " + largeImageUrl.second);
                                    }
                                    //An image around 200. Greater then 64 less then 640.
                                    if (height < smallImageUrl.second && height > 64 && smallImageUrl.second != 200) {
                                        smallImageUrl = new Pair<>(
                                                image.url,
                                                image.height);
                                        Log.d(LOG_TAG, "Small Image Height: " + smallImageUrl.second);
                                    }
                                }
                            }
                            songValues.put(
                                    SpotifyContract.SongsEntry.COLUMN_ALBUM_ART_LARGE,
                                    largeImageUrl.first);
                            songValues.put(
                                    SpotifyContract.SongsEntry.COLUMN_ALBUM_ART_SMALL,
                                    smallImageUrl.first);

                            songsVector.add(songValues);
                        }

                        ContentValues[] songsArray = new ContentValues[songsVector.size()];
                        songsVector.toArray(songsArray);
                        getContext().getContentResolver().bulkInsert(
                                SpotifyContract.SongsEntry.CONTENT_URI,
                                songsArray);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.d(LOG_TAG, "Failed to get Top Tracks");
                    }
                });
                break;
            }
        }


    }

    public static void syncImmediately(Context context, Bundle bundle, Handler handler) {
        mHandler = handler;
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    public static Account getSyncAccount(Context context) {
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));


        if (null == accountManager.getPassword(newAccount)) {
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
        }

        return newAccount;
    }

    public static void initializeSyncAdapter(Context context) {
        context.getContentResolver().delete(SpotifyContract.ArtistEntry.CONTENT_URI, null, null);
        context.getContentResolver().delete(SpotifyContract.SongsEntry.CONTENT_URI, null, null);
        getSyncAccount(context);
    }
}

