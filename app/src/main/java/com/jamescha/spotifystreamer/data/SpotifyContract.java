package com.jamescha.spotifystreamer.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by jamescha on 6/9/15.
 */
public class SpotifyContract {

    //Name for the entire content provider
    public static final String CONTENT_AUTHORITY = "com.jamescha.spotifystreamer";

    //Base for all URI's which apps will use to contact the content provider
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    //Possible paths (appended to base content URI for possible URI's)
    public static final String PATH_ARTIST = "artists";
    public static final String PATH_SONGS = "songs";

    /* Inner class that defines the table contents of the artist table */
    public static final class ArtistEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ARTIST).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ARTIST;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ARTIST;

        //Table name
        public  static final String TABLE_NAME = "artist";

        //Name of artist
        public static final String COLUMN_ARTIST_NAME = "artist_name";

        public static final String COLUMN_ARTIST_ID = "artist_id";

        public static final String COLUMN_ARTIST_IMAGE = "artist_image";

        public static Uri buildArtistUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }


    public static final class SongsEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_SONGS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SONGS;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SONGS;


        public static final String TABLE_NAME = "songs";

        public static final String COLUMN_ARTIST_NAME = "artist_name";

        public static final String COLUMN_SONG_NAME = "song_name";

        public static final String COLUMN_ALBUM_ART_SMALL = "album_art_small";

        public static final String COLUMN_ALBUM_ART_LARGE = "album_art_large";

        public static final String COLUMN_ALBUM_NAME = "album_name";

        public static final String COLUMN_PREVIEW_URL = "preview_uri";

        public static final String COLUMN_DURATION = "duration";

        public static Uri buildSongsUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }


}
