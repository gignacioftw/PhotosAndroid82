package com.example.photos;

import static com.example.photos.Photos.abbrev;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class DisplayMenu extends AppCompatActivity {
    public static final int TAG_REVIEWER = 1;
    Spinner tagSelect;

    Spinner deleteSelect;
    EditText addInput;
    ImageView image;

    ArrayList<Photo> photoList;

    ArrayList<Album> albumList;

    TextView tagsText;
    Photo photo;

    Album album;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_photo);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        image = findViewById(R.id.photo);
        tagsText = findViewById(R.id.tags);
        Button addButton = findViewById(R.id.addTag);
        Button deleteButton = findViewById(R.id.deleteTag);
        TextView photoLabel = findViewById(R.id.displayLabel);
        Button backButton = findViewById(R.id.displayBack);
        Button slideshowButton = findViewById(R.id.slideshow);
        Intent intent = this.getIntent();
        album = (Album) intent.getExtras().getSerializable("album");
        photoList = (ArrayList<Photo>) intent.getExtras().getSerializable("photoList");
        albumList = (ArrayList<Album>) intent.getExtras().getSerializable("albumList");
        photo = (Photo)intent.getExtras().getSerializable("photo");
        photoLabel.setText(abbrev(photo.getName()));
        image.setImageURI(Uri.parse(photo.getPath()));
        String[] tags = photo.returnTags();
        StringBuilder tag = new StringBuilder();
        for(String s : tags){
            tag.append(s + "\n");
        }
        tagSelect = new Spinner(this);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        String[] tagTypes = photo.returnTagTypes();
        arrayAdapter.add("");
        for(String s : tagTypes){
            if(!photo.hasType(s)) {
                arrayAdapter.add(s);
            }
            else if(photo.canAdd(s, photo)){
                arrayAdapter.add(s);
            }
        }
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tagSelect.setAdapter(arrayAdapter);
        AlertDialog.Builder b = new AlertDialog.Builder(DisplayMenu.this);
        b.setTitle("Add Tag");
        b.setMessage("Please pick a tag type and enter value");
        addInput = new EditText(DisplayMenu.this);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(tagSelect);
        layout.addView(addInput);
        b.setView(layout);
        b.setPositiveButton("Enter", (dialog,  which) -> {
            String moveTo = tagSelect.getSelectedItem().toString();
            if(!addInput.getText().toString().isEmpty() && !moveTo.isEmpty()){
                for(Album a : albumList){
                    if(a.getAlbumName().equals(album.getAlbumName())) {
                        for(Photo p : a.getPhotos()) {
                            if (p.getPath().equals(photo.getPath())) {
                                if (p.canAdd(tagSelect.getSelectedItem().toString(), p)) {
                                    p.addTag(new Tag(tagSelect.getSelectedItem().toString(), addInput.getText().toString()));
                                    for(Photo photo1 : photoList){
                                        if(photo1.getPath().equals(photo.getPath())){
                                            photo1.addTag(new Tag(tagSelect.getSelectedItem().toString(), addInput.getText().toString()));
                                        }
                                    }
                                    tag.append(tagSelect.getSelectedItem().toString() + ": " + addInput.getText().toString() + "\n");
                                    tagsText.setText(tag);
                                    if(!p.canAdd(tagSelect.getSelectedItem().toString(), p)){
                                        arrayAdapter.remove(tagSelect.getSelectedItem().toString());
                                        tagSelect.setAdapter(arrayAdapter);
                                    }
                                }
                            }
                        }
                    }
                }
                Toast.makeText(this, "Tag added", Toast.LENGTH_SHORT).show();
            }
            else if(addInput.getText().toString().isEmpty() && !moveTo.isEmpty()){
                Toast.makeText(this, "Please enter a value", Toast.LENGTH_SHORT).show();
            }
            else if(!addInput.getText().toString().isEmpty() && moveTo.isEmpty()){
                Toast.makeText(this, "Please select a tag type", Toast.LENGTH_SHORT).show();
            }
            else if(addInput.getText().toString().isEmpty() && moveTo.isEmpty()){
                Toast.makeText(this, "Please select a tag type and enter a value", Toast.LENGTH_SHORT).show();
            }
            addInput.setText("");
            tagSelect.setSelection(0);
            try {
                writeApp();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        b.setNegativeButton("Cancel", (dialog, which) -> {
            addInput.setText("");
            tagSelect.setSelection(0);
            dialog.dismiss();
        });
        AlertDialog a = b.create();
        String tagString = tag.toString();
        tagsText.setText(tagString);
        AlertDialog.Builder b1 = new AlertDialog.Builder(DisplayMenu.this);
        ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        arrayAdapter1.add("");
        for(String t : photo.returnTags()){
            arrayAdapter1.add(t);
        }
        deleteSelect = new Spinner(this);
        deleteSelect.setAdapter(arrayAdapter1);
        b1.setTitle("Delete tag");
        b1.setMessage("Please select a tag");
        b1.setPositiveButton("Enter", (dialog, which) ->{
            if(!deleteSelect.getSelectedItem().toString().isEmpty()) {
                for(Album a1 : albumList){
                    for(Photo p1 : a1.getPhotos()){
                        if(p1.getPath().equals(photo.getPath())){
                            if(!photo.canAdd(deleteSelect.getSelectedItem().toString().substring(0, deleteSelect.getSelectedItem().toString().indexOf(":")), photo)){
                                arrayAdapter.add(deleteSelect.getSelectedItem().toString().substring(0, deleteSelect.getSelectedItem().toString().indexOf(":")));
                                tagSelect.setAdapter(arrayAdapter);
                            }
                            for(Photo photo1 : photoList){
                                if(photo1.getPath().equals(photo.getPath())){
                                    photo1.removeTag(deleteSelect.getSelectedItem().toString());
                                }
                            }
                            p1.removeTag(deleteSelect.getSelectedItem().toString());
                            photo.removeTag(deleteSelect.getSelectedItem().toString());
                            String text = tagsText.getText().toString();
                            String texts = text.replace(deleteSelect.getSelectedItem().toString(), "");
                            tagsText.setText(texts);
                            arrayAdapter1.remove(deleteSelect.getSelectedItem().toString());
                            deleteSelect.setAdapter(arrayAdapter1);
                        }
                    }
                }
                try {
                    writeApp();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            else{
                Toast.makeText(this, "Please select a tag", Toast.LENGTH_SHORT).show();
            }
        });
        b1.setNegativeButton("Cancel", (dialog, which) ->{
           dialog.dismiss();
        });
        b1.setView(deleteSelect);
        AlertDialog a1 = b1.create();
        addButton.setOnClickListener(v -> {
            a.show();
        });
        deleteButton.setOnClickListener(v1 -> {
            a1.show();
        });
        backButton.setOnClickListener(v2 -> {
            Intent backPhotos = new Intent();
            Bundle args = new Bundle();
            args.putSerializable("photoList", photoList);
            args.putSerializable("albumList", albumList);
            backPhotos.putExtras(args);
            setResult(TAG_REVIEWER, backPhotos);
            try {
                writeApp();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            DisplayMenu.super.onBackPressed();
        });
        slideshowButton.setOnClickListener(v3 -> {
            Intent i = new Intent(this, Slideshow.class);
            Bundle args = new Bundle();
            args.putSerializable("photoList", photoList);
            args.putSerializable("photo", photo);
            i.putExtras(args);
            this.startActivity(i);
        });
    }

    public void writeApp() throws IOException {
        @SuppressLint("SdCardPath") FileOutputStream stream = new FileOutputStream("/data/user/0/com.example.photos/files/data.dat");
        ObjectOutputStream oos = new ObjectOutputStream(stream);
        oos.writeObject(albumList);
        oos.close();
        stream.close();
    }
}
