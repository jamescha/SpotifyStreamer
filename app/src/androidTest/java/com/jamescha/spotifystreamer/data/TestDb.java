package com.jamescha.spotifystreamer.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;

/**
 * Created by jamescha on 7/8/15.
 */
public class TestDb extends AndroidTestCase {
    public static final String LOG_TAG = TestDb.class.getSimpleName();

    void deleteDatabase() {
        mContext.deleteDatabase(SpotifyDbHelper.DATABASE_NAME);
    }

    public void setUp() {
        deleteDatabase();
    }

    public void testCreateDb() throws Throwable {

        //Build a HashSet of all of the table names
        final HashSet<String> tableNameHashSet = new HashSet<>();
        tableNameHashSet.add(SpotifyContract.ArtistEntry.TABLE_NAME);
        tableNameHashSet.add(SpotifyContract.SongsEntry.TABLE_NAME);

        mContext.deleteDatabase(SpotifyDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new SpotifyDbHelper(this.mContext).getWritableDatabase();
        assertTrue(db.isOpen());

        // See if our tables are here
        Cursor c = db.rawQuery("SELECT name FROM sqlite_Master WHERE type='table'", null);
        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        do {
            tableNameHashSet.remove(c.getString(0));
        } while( c.moveToNext() );

        assertTrue("Error: Your database was created without both the artist entry and songs entry tables",
                tableNameHashSet.isEmpty());

        c = db.rawQuery("PRAGMA table_info(" + SpotifyContract.ArtistEntry.TABLE_NAME + ")", null);

        // If this fails, it means that your database doesn't contain both song and artist entry tables
        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build as HashSet of all of the column names we want to look for
        final  HashSet<String> artistColumnHashSet = new HashSet<>();
        artistColumnHashSet.add(SpotifyContract.ArtistEntry._ID);
        artistColumnHashSet.add(SpotifyContract.ArtistEntry.COLUMN_ARTIST_IMAGE);
        artistColumnHashSet.add(SpotifyContract.ArtistEntry.COLUMN_ARTIST_NAME);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            artistColumnHashSet.remove(columnName);
        } while (c.moveToNext());

        // if this fails, it means the database doesn't contain all of the required artist entry column
        assertTrue("Error: The database doesn't contain all of the required artist entry columns",
                artistColumnHashSet.isEmpty());

        db.close();
    }
}
