package com.jamescha.spotifystreamer;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.jamescha.spotifystreamer.data.SpotifyContract;
import com.jamescha.spotifystreamer.sync.ArtistSyncAdapter;

public class ArtistFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    final private String LOG_TAG = ArtistFragment.class.getSimpleName();
    public static final int COL_ARTIST_NAME = 1;
    public static final int COL_ARTIST_ID = 2;
    public static final int COL_ARTIST_IMAGE = 3;
    public final static int TASK_COMPLETE = 0;

    private static final String ARTIST_SEARCH_KEY = "artist_search";
    private static final String SELECTED_KEY = "selected_position";
    private static final String ARTIST_NAME_KEY = "artist_name";
    private static final String SELECTED_ARTIST_NAME_KEY = "selected_artist_name";
    private static final int ARTIST_LOADER = 0;
    private static final String[] ARTIST_COLUMNS = {
            SpotifyContract.ArtistEntry.TABLE_NAME + "." + SpotifyContract.ArtistEntry._ID,
            SpotifyContract.ArtistEntry.COLUMN_ARTIST_NAME,
            SpotifyContract.ArtistEntry.COLUMN_ARTIST_ID,
            SpotifyContract.ArtistEntry.COLUMN_ARTIST_IMAGE
    };


    private ArtistAdapter mArtistAdapter;
    private ListView artistListView;
    private Toast mToast;
    private Handler mHandler;
    private int mPosition = ListView.INVALID_POSITION;
    private boolean mUseTwoFragmentView;
    private SearchView artistNameSearch;
    private ConnectivityManager connectivityManager;
    private NetworkInfo activeNetwork;

    public interface Callback {
        void onItemSelected(String id);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        Log.d(LOG_TAG, "Start options menu creation.");
        inflater.inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_search);
        artistNameSearch = (android.support.v7.widget.SearchView) searchItem.getActionView();

        artistNameSearch.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                activeNetwork = connectivityManager.getActiveNetworkInfo();
                if (activeNetwork != null  && activeNetwork.isConnectedOrConnecting()) {
                    Log.d(LOG_TAG, "EDIT FIELD : " + query);
                    Bundle bundle = new Bundle();
                    bundle.putString(ARTIST_NAME_KEY, query);
                    bundle.putBoolean(ARTIST_SEARCH_KEY, true);
                    bundle.putBoolean(ARTIST_SEARCH_KEY, true);
                    ArtistSyncAdapter.syncImmediately(getActivity(), bundle, mHandler);
                } else {
                    if (mToast != null) {
                        mToast.cancel();
                        mToast.setText("No internet connection. Please connect to the internet.");
                        mToast.show();
                    }
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
                activeNetwork = connectivityManager.getActiveNetworkInfo();
                if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
                    Log.d(LOG_TAG, "EDIT FIELD : " + newText);
                    Bundle bundle = new Bundle();
                    bundle.putString(ARTIST_NAME_KEY, newText);
                    bundle.putBoolean(ARTIST_SEARCH_KEY, false);
                    bundle.putInt(ArtistSyncAdapter.SEARCH_TYPE, ArtistSyncAdapter.ARTIST_SEARCH);
                    ArtistSyncAdapter.syncImmediately(getActivity(), bundle, mHandler);

                    mHandler = new Handler(Looper.getMainLooper()) {
                        @Override
                        public void handleMessage(Message msg) {
                            switch (msg.what) {
                                case TASK_COMPLETE: {
                                    if (mToast != null) {
                                        mToast.cancel();
                                    }
                                    mToast = Toast.makeText(getActivity(),
                                            "Artist with Name " +
                                                    newText +
                                                    " not found.",
                                            Toast.LENGTH_SHORT);
                                    mToast.show();
                                }
                                default: {
                                    super.handleMessage(msg);
                                }
                            }
                        }
                    };
                } else {
                    if (mToast != null) {
                        mToast.cancel();
                        mToast.setText("No internet connection. Please connect to the internet.");
                        mToast.show();
                    }
                }
                return true;
            }
        });

        Log.d(LOG_TAG, "Finish options menu creation.");
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             final Bundle savedInstanceState) {

        mArtistAdapter = new ArtistAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        artistListView = (ListView) rootView.findViewById(R.id.listview_artists);

        artistListView.setAdapter(mArtistAdapter);

        artistListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);

                if (cursor != null) {
                    String artistId = cursor.getString(COL_ARTIST_ID);
                    ((Callback) getActivity()).onItemSelected(artistId);
                }
                mPosition = position;
            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        connectivityManager =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        Log.d(LOG_TAG, "View Created");
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(ARTIST_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Uri artistUri = SpotifyContract.ArtistEntry.CONTENT_URI;

        Log.d(LOG_TAG, "Created Loader");

        return new CursorLoader(
                getActivity(),
                artistUri,
                ARTIST_COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mArtistAdapter.swapCursor(data);
        if (mPosition != ListView.INVALID_POSITION) {
            artistListView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mArtistAdapter.swapCursor(null);
    }
}
