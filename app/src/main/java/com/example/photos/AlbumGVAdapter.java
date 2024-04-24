package com.example.photos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.photos.Album;

import java.util.ArrayList;

public class AlbumGVAdapter extends ArrayAdapter<Album> {

    public AlbumGVAdapter(@NonNull Context context, ArrayList<Album> albumList) {
        super(context, 0, albumList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listitemView = convertView;
        if (listitemView == null) {
            // Layout Inflater inflates each item to be displayed in GridView.
            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.grid_item, parent, false);
        }

        Album album = getItem(position);
        TextView courseTV = listitemView.findViewById(R.id.item_name);
        ImageView courseIV = listitemView.findViewById(R.id.grid_image);

        courseTV.setText(album.getAlbumName());
        courseIV.setImageResource(R.drawable._0down);
        return listitemView;
    }
}
