package com.jamescha.spotifystreamer.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Created by jamescha on 6/10/15.
 */
public class SpotifyProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private SpotifyDbHelper mSpotifyDbHelper;

    static final int ARTIST = 100;
    static final int ARTIST_WITH_NAME = 101;
    static final int SONGS = 200;
    static final int SONGS_WITH_ARTIST_ID = 201;

    private static final SQLiteQueryBuilder sArtistbyNameQueryBuilder;
    private static final SQLiteQueryBuilder sSongsbyAristQueryBuilder;

    static {
        sArtistbyNameQueryBuilder = new SQLiteQueryBuilder();
        sSongsbyAristQueryBuilder = new SQLiteQueryBuilder();

        sArtistbyNameQueryBuilder.setTables(SpotifyContract.ArtistEntry.TABLE_NAME);
        sSongsbyAristQueryBuilder.setTables(SpotifyContract.SongsEntry.TABLE_NAME);
    }

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = SpotifyContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, SpotifyContract.PATH_ARTIST, ARTIST);
        matcher.addURI(authority, SpotifyContract.PATH_SONGS, SONGS);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mSpotifyDbHelper = new SpotifyDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final int match = sUriMatcher.match(uri);
        Cursor retCursor;

        switch (match) {
            case ARTIST: {
                retCursor = mSpotifyDbHelper.getReadableDatabase().query(
                        SpotifyContract.ArtistEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case SONGS: {
                retCursor = mSpotifyDbHelper.getReadableDatabase().query(
                        SpotifyContract.SongsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case ARTIST:
                return SpotifyContract.ArtistEntry.CONTENT_TYPE;
            case SONGS:
                return SpotifyContract.SongsEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase database = mSpotifyDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case ARTIST: {
                long _id = database.insert(SpotifyContract.ArtistEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = SpotifyContract.ArtistEntry.CONTENT_URI;
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }

            case SONGS: {
                long _id = database.insert(SpotifyContract.SongsEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = SpotifyContract.SongsEntry.CONTENT_URI;
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase database = mSpotifyDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        if (null == selection) selection = "1";
        switch (match) {
            case ARTIST:
                rowsDeleted = database.delete(
                        SpotifyContract.ArtistEntry.TABLE_NAME, selection, selectionArgs
                );
                break;
            case SONGS:
                rowsDeleted = database.delete(
                        SpotifyContract.SongsEntry.TABLE_NAME, selection, selectionArgs
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase database = mSpotifyDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case ARTIST:
                rowsUpdated = database.update(SpotifyContract.ArtistEntry.TABLE_NAME, values,
                        selection, selectionArgs);
                break;
            case SONGS:
                rowsUpdated = database.update(SpotifyContract.SongsEntry.TABLE_NAME, values,
                        selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase database = mSpotifyDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int returnCount = 0;
        switch (match) {
            case ARTIST: {
                database.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = database.insert(SpotifyContract.ArtistEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    database.setTransactionSuccessful();
                } finally {
                    database.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }
            case SONGS: {
                database.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = database.insert(SpotifyContract.SongsEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    database.setTransactionSuccessful();
                } finally {
                    database.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }
            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    @TargetApi(11)
    public void shutdown() {
        mSpotifyDbHelper.close();
        super.shutdown();
    }
}
