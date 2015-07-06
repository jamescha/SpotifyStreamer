package com.jamescha.spotifystreamer.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import static com.jamescha.spotifystreamer.data.SpotifyContract.ArtistEntry;
import static com.jamescha.spotifystreamer.data.SpotifyContract.SongsEntry;

/**
 * Created by jamescha on 6/10/15.
 */
public class SpotifyDbHelper extends SQLiteOpenHelper {

    private  static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "spotify.db";

    public SpotifyDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_ARTIST_TABLE = "CREATE TABLE " + ArtistEntry.TABLE_NAME + " (" +
                ArtistEntry._ID + " INTEGER PRIMARY KEY," +
                ArtistEntry.COLUMN_ARTIST_NAME + " TEXT NOT NULL, " +
                ArtistEntry.COLUMN_ARTIST_ID + " TEXT NOT NULL, " +
                ArtistEntry.COLUMN_ARTIST_IMAGE + " TEXT NOT NULL " + ");";

        final String SQL_CREATE_SONGS_TABLE = "CREATE TABLE " + SongsEntry.TABLE_NAME + " (" +
                SongsEntry._ID + " INTEGER PRIMARY KEY, " +
                SongsEntry.COLUMN_SONG_NAME + " TEXT NOT NULL, " +
                SongsEntry.COLUMN_ALBUM_NAME + " TEXT NOT NULL, " +
                SongsEntry.COLUMN_ALBUM_ART_SMALL + " TEXT NOT NULL, " +
                SongsEntry.COLUMN_ALBUM_ART_LARGE + " TEXT NOT NULL, " +
                SongsEntry.COLUMN_PREVIEW_URL + " TEXT NOT NULL " + ");";

        db.execSQL(SQL_CREATE_ARTIST_TABLE);
        db.execSQL(SQL_CREATE_SONGS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ArtistEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + SongsEntry.TABLE_NAME);

        onCreate(db);
    }
}
