package com.jamescha.spotifystreamer;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.jamescha.spotifystreamer.data.SpotifyContract;


/**
 * A placeholder fragment containing a simple view.
 */
public class SongsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    final private String LOG_TAG = SongsFragment.class.getSimpleName();

    public static final int COL_ALBUM_NAME = 1;
    public static final int COL_ALBUM_IMAGE = 2;
    public static final int COL_SONG_NAME = 3;

    private SongsAdapter mSongAdapter;
    private ListView songListView;
    private int mPosition = ListView.INVALID_POSITION;

    private static final String SONG_SELECTED_KEY = "song_selected_key";
    private static final int SONG_LOADER = 0;
    private static final String[] SONG_COLUMNS = {
            SpotifyContract.SongsEntry.TABLE_NAME + "." + SpotifyContract.SongsEntry._ID,
            SpotifyContract.SongsEntry.COLUMN_ALBUM_NAME,
            SpotifyContract.SongsEntry.COLUMN_ALBUM_ART_SMALL,
            SpotifyContract.SongsEntry.COLUMN_SONG_NAME
    };

    public interface Callback {
        void onItemSelected();
    }

    public SongsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        mSongAdapter = new SongsAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.fragment_songs, container, false);

        songListView = (ListView) rootView.findViewById(R.id.listview_songs);
        songListView.setAdapter(mSongAdapter);
        songListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                if (cursor != null) {
                    ((Callback) getActivity())
                            .onItemSelected();
                }
                mPosition = position;
            }
        });

        Log.d(LOG_TAG, "View Created");
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(SONG_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mPosition != ListView.INVALID_POSITION) {
            outState.putInt(SONG_SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = null;
        Uri songUri = SpotifyContract.SongsEntry.CONTENT_URI;

        Log.d(LOG_TAG, "Created Loader");

        return new CursorLoader(
                getActivity(),
                songUri,
                SONG_COLUMNS,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mSongAdapter.swapCursor(data);
        if (mPosition != ListView.INVALID_POSITION) {
            songListView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
