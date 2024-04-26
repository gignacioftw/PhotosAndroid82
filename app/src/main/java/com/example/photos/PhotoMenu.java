package com.example.photos;

import static com.example.photos.Photos.abbrev;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PhotoMenu extends AppCompatActivity {

    public static final int TAG_REVIEWER = 1;
    GridView photoGrid;

    TextView barLabel;

    ActivityResultLauncher<Intent> launcher;

    ArrayList<Album> albumList;

    List<Photo> photoList;

    Album album;

    ImageView previewURI;

    PhotoGVAdapter photoGVAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_activity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        photoGrid = findViewById(R.id.photoGrid);
        barLabel = findViewById(R.id.photoLabel);
        previewURI = findViewById(R.id.previewSelect);
        Button uploadButton = findViewById(R.id.uploadPhoto);
        Button backButton = findViewById(R.id.photoBack);
        Intent intent = this.getIntent();
        albumList = (ArrayList<Album>) intent.getExtras().getSerializable("albumList");
        album = (Album)intent.getExtras().getSerializable("album");
        Photo[] p = album.getPhotos();
        photoList = new ArrayList<>(Arrays.asList(p));
        barLabel.setText(abbrev(album.getAlbumName()));
        registerResult();
        photoGVAdapter = new PhotoGVAdapter(this, photoList);
        photoGrid.setAdapter(photoGVAdapter);
        uploadButton.setOnClickListener(v -> pickImage());
        backButton.setOnClickListener(v1 -> PhotoMenu.super.onBackPressed());
    }

    private void pickImage(){
        Intent i = new Intent(MediaStore.ACTION_PICK_IMAGES);
        launcher.launch(i);
    }

    private void registerResult(){
        launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    try {
                        Uri imageUri = result.getData().getData();
                        String fileName = DocumentFile.fromSingleUri(PhotoMenu.this, imageUri).getName();
                        photoList.add(new Photo(fileName, imageUri.toString()));
                        photoGrid.setAdapter(photoGVAdapter);
                        for(Album a : albumList){
                            if (a.getAlbumName().equals(album.getAlbumName())) {
                                a.addPhoto(new Photo(fileName, imageUri.toString()));
                            }
                        }
                        Intent backtoPhotos = new Intent();
                        Bundle args = new Bundle();
                        args.putSerializable("albumList", albumList);
                        backtoPhotos.putExtras(args);
                        setResult(TAG_REVIEWER, backtoPhotos);
                        writeApp();
                        //Photos.updateAlbumList(albumList);
                    }
                    catch (Exception e){
                        Toast.makeText(PhotoMenu.this, "No image selected", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    public void writeApp() throws IOException, ClassNotFoundException {
        @SuppressLint("SdCardPath") FileOutputStream stream = new FileOutputStream("/data/user/0/com.example.photos/files/data.dat");
        ObjectOutputStream oos = new ObjectOutputStream(stream);
        oos.writeObject(albumList);
        oos.close();
        stream.close();
    }

}
