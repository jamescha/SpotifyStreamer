package com.jamescha.spotifystreamer.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Created by jamescha on 6/10/15.
 */
public class SpotifyProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private SpotifyDbHelper mOpenHelper;

    static final int ARTIST = 100;
    static final int ARTIST_WITH_NAME = 101;
    static final int SONGS = 200;
    static final int SONGS_WITH_ARTIST = 201;

    private static final SQLiteQueryBuilder sArtistbyNameQueryBuilder;
    private static final SQLiteQueryBuilder sSongsbyAristQueryBuilder;

    static {
        sArtistbyNameQueryBuilder = new SQLiteQueryBuilder();
        sSongsbyAristQueryBuilder = new SQLiteQueryBuilder();

        
    }

    @Override
    public boolean onCreate() {
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
