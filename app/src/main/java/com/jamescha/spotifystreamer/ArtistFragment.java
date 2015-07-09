package com.jamescha.spotifystreamer;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jamescha.spotifystreamer.data.SpotifyContract;
import com.jamescha.spotifystreamer.sync.ArtistSyncAdapter;

/**
 * Created by jamescha on 6/25/15.
 */
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
    private String mArtistName = "";
    private boolean mUseTwoFragmentView;
    private EditText artistNameSearch;

    public interface Callback {
        void onItemSelected(String id);
    }

    public ArtistFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
                mArtistName = cursor.getString(COL_ARTIST_NAME);

            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }
        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_ARTIST_NAME_KEY)) {
            mArtistName = savedInstanceState.getString(SELECTED_ARTIST_NAME_KEY);
        }

        artistNameSearch = (EditText) rootView.findViewById(R.id.search);

        artistNameSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(final Editable s) {
                Log.d(LOG_TAG, "EDIT FIELD : " + s.toString());
                Bundle bundle = new Bundle();
                bundle.putString(ARTIST_NAME_KEY, s.toString());
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
                                                s.toString() +
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

            }
        });

        artistNameSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;

                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    Log.d(LOG_TAG, "EDIT FIELD : " + artistNameSearch.getText().toString());
                    Bundle bundle = new Bundle();
                    bundle.putString(ARTIST_NAME_KEY, artistNameSearch.getText().toString());
                    bundle.putBoolean(ARTIST_SEARCH_KEY, true);
                    ArtistSyncAdapter.syncImmediately(getActivity(), bundle, mHandler);
                    handled = true;
                }

                return handled;
            }
        });

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
            outState.putString(SELECTED_ARTIST_NAME_KEY, mArtistName);
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        
        String sortOrder = null;
        Uri artistUri = SpotifyContract.ArtistEntry.CONTENT_URI;

        Log.d(LOG_TAG, "Created Loader");

        return new CursorLoader(
                getActivity(),
                artistUri,
                ARTIST_COLUMNS,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mArtistAdapter.swapCursor(data);
        if (mPosition != ListView.INVALID_POSITION) {
            artistListView.smoothScrollToPosition(mPosition);
        }
        if (mArtistName != "") {
            artistNameSearch.setText(mArtistName);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mArtistAdapter.swapCursor(null);
    }

}
