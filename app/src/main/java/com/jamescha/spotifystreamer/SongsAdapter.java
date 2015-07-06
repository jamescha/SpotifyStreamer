package com.jamescha.spotifystreamer;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by jamescha on 7/2/15.
 */
public class SongsAdapter extends CursorAdapter {
    private static final String LOG_TAG = SongsAdapter.class.getSimpleName();


    private static final int VIEW_TYPE_COUNT = 1;

    public SongsAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    public static class ViewHolder {
        public final ImageView albumImage;
        public final TextView albumName;
        public final TextView songName;

        public ViewHolder(View view) {
            albumImage = (ImageView) view.findViewById(R.id.list_item_song_album_image);
            albumName = (TextView) view.findViewById(R.id.list_item_album_name_textView);
            songName = (TextView) view.findViewById(R.id.list_item_song_name_textView);
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        Log.d(LOG_TAG, "New view created");
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_song, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Log.d(LOG_TAG, "View Binded");
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        String songName = cursor.getString(SongsFragment.COL_SONG_NAME);
        String albumName = cursor.getString(SongsFragment.COL_ALBUM_NAME);

        Log.d(LOG_TAG, "Song Name: " + songName);
        Log.d(LOG_TAG, "Album Name: " + albumName);

        Picasso.with(context).load(cursor.getString(SongsFragment.COL_ALBUM_IMAGE)).into(viewHolder.albumImage);
        viewHolder.songName.setText(songName);
        viewHolder.albumName.setText(albumName);
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }
}
