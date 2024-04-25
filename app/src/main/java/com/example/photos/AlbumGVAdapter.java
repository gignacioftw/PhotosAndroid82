package com.example.photos;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.example.photos.Album;

import java.util.ArrayList;

public class AlbumGVAdapter extends ArrayAdapter<Album> {
    ArrayList<View> cards;
    Context context;

    View selected;

    ArrayList<Album> albumList;

    public AlbumGVAdapter(@NonNull Context context, ArrayList<Album> albumList) {
        super(context, 0, albumList);
        this.context = context;
        this.albumList = albumList;
        cards = new ArrayList<View>();
        selected = null;
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

        courseTV.setText(album.getAlbumName() +"\n" +album.getNumOfPhotos()+" photos");
        courseIV.setImageResource(R.drawable.folder);
        View finalListitemView = listitemView;
        cards.add(listitemView);
        listitemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(View view : cards){
                    if(view != finalListitemView){
                        view.setBackgroundColor(Color.parseColor("#ffffff"));
                    }
                }
                finalListitemView.setBackgroundColor(Color.parseColor("#dae7f3"));
                selected = finalListitemView;
            }
        });
        return listitemView;
    }

    public void removeItem(String albumName){
        int position = 0;
        for(Album a : albumList){
            if(a.getAlbumName().equals(albumName)){
                position = albumList.indexOf(a);
            }
        }
        albumList.remove(position);
        selected = null;
        notifyDataSetChanged();
    }

    public void deselect(){
        for(View view : cards){
            view.setBackgroundColor(Color.parseColor("#ffffff"));
        }
    }
}
