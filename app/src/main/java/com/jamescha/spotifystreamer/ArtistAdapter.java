package com.jamescha.spotifystreamer;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by jamescha on 6/25/15.
 */
public class ArtistAdapter extends CursorAdapter {

    private static final int VIEW_TYPE_COUNT = 1;

    public static class ViewHolder {
        public final ImageView artistPicture;
        public final TextView artistName;

        public ViewHolder(View view) {
            artistPicture = (ImageView) view.findViewById(R.id.list_item_icon);
            artistName = (TextView) view.findViewById(R.id.list_item_artist_name_textview);
        }
    }

    public ArtistAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View view = LayoutInflater.from(context).inflate(R.layout.list_item_artist, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        String artistName = cursor.getString(ArtistFragment.COL_ARTIST_NAME);

        Picasso.with(context).load(cursor.getString(ArtistFragment.COL_ARTIST_IMAGE)).into(viewHolder.artistPicture);
        viewHolder.artistName.setText(artistName);
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }
}
