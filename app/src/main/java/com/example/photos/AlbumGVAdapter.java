package com.example.photos;

import static com.example.photos.Photos.abbrev;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
        cards = new ArrayList<>();
        selected = null;
    }

    @SuppressLint("SetTextI18n")
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

        courseTV.setText(abbrev(album.getAlbumName()) +"\n" +album.getNumOfPhotos()+" photos");
        courseIV.setImageResource(R.drawable.folder);
        View finalListitemView = listitemView;
        cards.add(listitemView);
        listitemView.setOnClickListener(v -> {
            for(View view : cards){
                if(view != finalListitemView){
                    view.setBackgroundColor(Color.parseColor("#ffffff"));
                }
            }
            finalListitemView.setBackgroundColor(Color.parseColor("#dae7f3"));
            selected = finalListitemView;
        });
        return listitemView;
    }

    public void removeItem(String albumName){
        int position = 0;
        for(Album a : albumList){
            if((abbrev(a.getAlbumName())).equals(albumName)){
                position = albumList.indexOf(a);
            }
        }
        albumList.remove(position);
        notifyDataSetChanged();
    }

    public void deselect(){
        for(View view : cards){
            view.setBackgroundColor(Color.parseColor("#ffffff"));
        }
        selected = null;
    }

    public void renameItem(String oldName, String newName){
        for(Album a : albumList){
            if((abbrev(a.getAlbumName())).equals(oldName)){
                a.changeName(newName);
            }
        }
        selected = null;
        notifyDataSetChanged();
    }

}
