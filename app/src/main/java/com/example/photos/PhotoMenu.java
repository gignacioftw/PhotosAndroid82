package com.example.photos;

import static com.example.photos.Photos.abbrev;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PhotoMenu extends AppCompatActivity {

    GridView photoGrid;

    TextView barLabel;

    ActivityResultLauncher<Intent> launcher;

    ArrayList<Album> albumList;

    List<Photo> photoList;

    Album album;

    ImageView previewURI;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_activity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        photoGrid = findViewById(R.id.photoGrid);
        barLabel = findViewById(R.id.photoLabel);
        previewURI = findViewById(R.id.previewSelect);
        Button uploadButton = findViewById(R.id.uploadPhoto);
        Intent intent = this.getIntent();
        albumList = (ArrayList<Album>) intent.getExtras().getSerializable("albumList");
        album = (Album)intent.getExtras().getSerializable("album");
        Photo[] p = album.getPhotos();
        photoList = Arrays.asList(p);
        barLabel.setText(abbrev(album.getAlbumName()));
        registerResult();
        uploadButton.setOnClickListener(v -> pickImage());
        PhotoGVAdapter photoGVAdapter = new PhotoGVAdapter(this, photoList);
        photoGrid.setAdapter(photoGVAdapter);
    }

    private void pickImage(){
        Intent i = new Intent(MediaStore.ACTION_PICK_IMAGES);
        launcher.launch(i);
    }

    private void registerResult(){
        launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        try {
                            Uri imageUri = result.getData().getData();
                            DocumentFile file = DocumentFile.fromSingleUri(PhotoMenu.this, imageUri);
                            photoList.add(new Photo(file.getName(), imageUri));
                            previewURI.setImageURI(imageUri);
                        }
                        catch (Exception e){
                            Toast.makeText(PhotoMenu.this, "No image selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }
}
