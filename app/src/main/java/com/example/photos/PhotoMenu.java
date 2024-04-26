package com.example.photos;

import static com.example.photos.Photos.abbrev;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

    String photoName;
    TextView barLabel;

    ActivityResultLauncher<Intent> launcher;

    ArrayList<Album> albumList;

    ArrayList<Photo> photoList;

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
        Button deleteButton = findViewById(R.id.deletePhoto);
        Button deselectButton = findViewById(R.id.photoDeselect);
        Intent intent = this.getIntent();
        albumList = (ArrayList<Album>) intent.getExtras().getSerializable("albumList");
        album = (Album)intent.getExtras().getSerializable("album");
        Photo[] p = album.getPhotos();
        photoList = new ArrayList<>(Arrays.asList(p));
        barLabel.setText(abbrev(album.getAlbumName()));
        registerResultAdd();
        photoGVAdapter = new PhotoGVAdapter(this, photoList);
        photoGrid.setAdapter(photoGVAdapter);
        uploadButton.setOnClickListener(v -> pickImage());
        backButton.setOnClickListener(v1 -> PhotoMenu.super.onBackPressed());
        deleteButton.setOnClickListener(v2 -> {
            if(photoGVAdapter.selected != null){
                photoName = ((TextView)photoGVAdapter.selected.findViewById(R.id.item_name)).getText().toString();
                String photoPath = ((ImageView)photoGVAdapter.selected.findViewById(R.id.grid_image)).getTag().toString();
                for(Album a : albumList){
                    if(a.getAlbumName().equals(album.getAlbumName())){
                        for(int i = 0; i < photoList.size(); i++){
                            if(photoList.get(i).getPath().equals(photoPath)){
                                album.removePhoto(photoList.get(i).getName());
                                a.removePhoto(photoList.get(i).getName());
                                photoList.remove(photoList.get(i));
                                photoGVAdapter.notifyDataSetChanged();
                                i--;
                            }
                        }
                        Intent backPhotos = new Intent();
                        Bundle args = new Bundle();
                        args.putSerializable("albumList", albumList);
                        backPhotos.putExtras(args);
                        setResult(TAG_REVIEWER, backPhotos);
                        try {
                            writeApp();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                Toast.makeText(this, photoName + " has been deleted", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(this, "Please select a photo", Toast.LENGTH_SHORT).show();
            }
        });
        deselectButton.setOnClickListener(v3 -> photoGVAdapter.deselect());
    }

    private void pickImage(){
        Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        i.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        launcher.launch(i);
    }

    private void deleteImage(){
        Intent i = new Intent();
        launcher.launch(i);
    }

    private void registerResultAdd(){
        launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    try {
                        Uri imageUri = result.getData().getData();
                        getContentResolver().takePersistableUriPermission(imageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        String fileName = DocumentFile.fromSingleUri(PhotoMenu.this, imageUri).getName();
                        photoList.add(new Photo(fileName, imageUri.toString()));
                        for(Album a : albumList){
                            if (a.getAlbumName().equals(album.getAlbumName())) {
                                a.addPhoto(new Photo(fileName, imageUri.toString()));
                            }
                        }
                        Intent backPhotos = new Intent();
                        Bundle args = new Bundle();
                        args.putSerializable("albumList", albumList);
                        backPhotos.putExtras(args);
                        setResult(TAG_REVIEWER, backPhotos);
                        photoGrid.setAdapter(photoGVAdapter);
                        Toast.makeText(PhotoMenu.this, "Photo uploaded", Toast.LENGTH_SHORT).show();
                        writeApp();
                    }
                    catch (Exception e){
                        Toast.makeText(PhotoMenu.this, "No image selected", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    public void writeApp() throws IOException {
        @SuppressLint("SdCardPath") FileOutputStream stream = new FileOutputStream("/data/user/0/com.example.photos/files/data.dat");
        ObjectOutputStream oos = new ObjectOutputStream(stream);
        oos.writeObject(albumList);
        oos.close();
        stream.close();
    }
}
