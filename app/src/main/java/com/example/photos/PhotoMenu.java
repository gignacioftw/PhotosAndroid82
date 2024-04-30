package com.example.photos;

import static com.example.photos.Photos.abbrev;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Spinner;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class PhotoMenu extends AppCompatActivity {

    public static final int TAG_REVIEWER = 1;

    Spinner albumSelect;
    GridView photoGrid;

    String photoName;
    TextView barLabel;

    ActivityResultLauncher<Intent> launcher;

    ArrayList<Album> albumList;

    ArrayList<Photo> photoList;

    Album album;

    PhotoGVAdapter photoGVAdapter;
    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_activity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        photoGrid = findViewById(R.id.photoGrid);
        barLabel = findViewById(R.id.photoLabel);
        Button uploadButton = findViewById(R.id.uploadPhoto);
        Button backButton = findViewById(R.id.photoBack);
        Button deleteButton = findViewById(R.id.deletePhoto);
        Button deselectButton = findViewById(R.id.photoDeselect);
        Button moveButton = findViewById(R.id.movePhoto);
        Button displayButton = findViewById(R.id.displayPhoto);
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
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("Album name");
        b.setMessage("Please select an album");
        albumSelect = new Spinner(this);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        albumSelect.setAdapter(arrayAdapter);
        arrayAdapter.add("");
        b.setView(albumSelect);
        b.setPositiveButton("Enter", (dialog, which) -> {
            String moveTo = albumSelect.getSelectedItem().toString();
            if(!moveTo.isEmpty()) {
                Album move = new Album();
                for (int i = 0; i < albumList.size(); i++) {
                    if (albumList.get(i).getAlbumName().equals(moveTo)) {
                        move = albumList.get(i);
                    }
                }
                String photoPath = ((ImageView) photoGVAdapter.selected.findViewById(R.id.grid_image)).getTag().toString();
                for (int i = 0; i < photoList.size(); i++) {
                    if (photoList.get(i).getPath().equals(photoPath)) {
                        for(Album a : albumList) {
                            if (a.getAlbumName().equals(album.getAlbumName())) {
                                move.addPhoto(photoList.get(i));
                                a.removePhoto(photoList.get(i).getName());
                                photoList.remove(photoList.get(i));
                                photoGVAdapter.notifyDataSetChanged();
                                i--;
                                Toast.makeText(this, "Photo moved to: " + moveTo, Toast.LENGTH_SHORT).show();}
                        }
                    }
                }
            }
            else{
                Toast.makeText(this, "Please select an album", Toast.LENGTH_SHORT).show();
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
            arrayAdapter.clear();
            dialog.dismiss();

        });
        b.setNegativeButton("Cancel", (dialog, which) -> {
            arrayAdapter.clear();
            dialog.dismiss();
        });
        AlertDialog a = b.create();
        deselectButton.setOnClickListener(v3 -> photoGVAdapter.deselect());
        moveButton.setOnClickListener(v4 -> {
            if(photoGVAdapter.selected != null) {
                if(albumList.size() > 1) {
                    for (int i = 0; i < albumList.size(); i++) {
                        if(arrayAdapter.isEmpty()){
                            arrayAdapter.add("");
                        }
                        else if(!album.getAlbumName().equals(albumList.get(i).getAlbumName())){
                            arrayAdapter.add(albumList.get(i).getAlbumName());
                        }
                        albumSelect.setAdapter(arrayAdapter);
                    }
                    a.show();
                }
                else{
                    Toast.makeText(this, "No other albums", Toast.LENGTH_SHORT).show();
                }
            }
            else{
                Toast.makeText(this, "Please select a photo", Toast.LENGTH_SHORT).show();
            }
        });
        displayButton.setOnClickListener(v5 -> {
            if(photoGVAdapter.selected != null){
                String text = ((TextView) photoGVAdapter.selected.findViewById(R.id.item_name)).getText().toString();
                Toast.makeText(PhotoMenu.this, "Opening " + text, Toast.LENGTH_SHORT).show();
                photoGVAdapter.deselect();
                Intent intent1 = new Intent(PhotoMenu.this, DisplayMenu.class);
                Bundle args = new Bundle();
                for (Photo photo : photoList) {
                    if (abbrev(photo.getName()).equals(text)) {
                        args.putSerializable("photo", photo);
                    }
                }
                args.putSerializable("photoList", photoList);
                args.putSerializable("albumList", albumList);
                args.putSerializable("album", album);
                intent1.putExtras(args);
                catcherForResult.launch(intent1);
            }
            else{
                Toast.makeText(this, "Please select a photo", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void pickImage(){
        Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        i.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
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

    public ArrayList<Album> readApp() throws IOException, ClassNotFoundException {
        FileInputStream stream =  PhotoMenu.this.getApplicationContext().openFileInput("data.dat");
        ObjectInputStream oos;
        try {
            oos = new ObjectInputStream(stream);
        } catch (EOFException e) {
            return new ArrayList<>();
        }
        ArrayList<Album> albumArrayList = (ArrayList<Album>) oos.readObject();
        stream.close();
        oos.close();
        return albumArrayList;
    }

    ActivityResultLauncher<Intent> catcherForResult =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            if(result.getResultCode() == DisplayMenu.TAG_REVIEWER){
                                Intent catcher_intent = result.getData();
                                if(catcher_intent != null){
                                    photoList = (ArrayList<Photo>) catcher_intent.getExtras().getSerializable("photoList");
                                }
                            }
                            try {
                                albumList = readApp();
                            } catch (IOException | ClassNotFoundException e) {
                                throw new RuntimeException(e);
                            }
                            photoGVAdapter = new PhotoGVAdapter(PhotoMenu.this, photoList);
                            photoGrid.setAdapter(photoGVAdapter);
                        }
                    }
            );
}
