package com.example.photos;

import static com.example.photos.Photos.abbrev;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class Slideshow extends AppCompatActivity {

    Photo photo;
    ImageView slides;

    int position;

    ArrayList<Photo> photoList;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.slideshow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        slides = findViewById(R.id.slidePhoto);
        Button prevButton = findViewById(R.id.prevPhoto);
        Button nextButton = findViewById(R.id.nextPhoto);
        Button backButton = findViewById(R.id.slideBack);
        Intent intent = getIntent();
        photo = (Photo) intent.getExtras().getSerializable("photo");
        photoList = (ArrayList<Photo>) intent.getExtras().getSerializable("photoList");
        slides.setImageURI(Uri.parse(photo.getPath()));
        backButton.setOnClickListener(v -> super.onBackPressed());
        for(int i = 0; i < photoList.size(); i++){
            if(photoList.get(i).getPath().equals(photo.getPath())){
                position = i;
            }
        }
        prevButton.setOnClickListener(v1 -> {
            if(position > 0){
                slides.setImageURI(Uri.parse(photoList.get(position - 1).getPath()));
                position--;
            }
            else{
                Toast.makeText(this, "First image", Toast.LENGTH_SHORT).show();
            }
        });
        nextButton.setOnClickListener(v2 -> {
            if(position < photoList.size() - 1){
                slides.setImageURI(Uri.parse(photoList.get(position + 1).getPath()));
                position++;
            }
            else{
                Toast.makeText(this, "Last image", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
