//Gabriel Ignacio
package com.example.photos;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class Photos extends AppCompatActivity {
    private GridView albumGrid;
    private EditText albumNameInput;

    private ArrayList<Album> albumList;

    private EditText renameInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        albumGrid = findViewById(R.id.albumGrid);
        Button addButton = findViewById(R.id.addAlbum);
        Button deleteButton = findViewById(R.id.deleteAlbum);
        Button renameButton = findViewById(R.id.renameAlbum);
        try {
            if(hasData()){
                loadContent();
            }
            else{
                albumList = new ArrayList<>();
                writeApp();
                loadContent();
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        AlbumGVAdapter albumGVAdapter = new AlbumGVAdapter(Photos.this, albumList);
        albumGrid.setAdapter(albumGVAdapter);

        //create alert
        AlertDialog.Builder b = new AlertDialog.Builder(Photos.this);
        b.setTitle("Album name");
        b.setMessage("Please enter an album name");
        albumNameInput = new EditText(Photos.this);
        b.setView(albumNameInput);
        b.setPositiveButton("Enter", (dialog, which) -> {
            String s = albumNameInput.getText().toString();
            if(!hasAlbum(s)){
                albumList.add(new Album(s));
                albumGrid.setAdapter(albumGVAdapter);
                dialog.dismiss();
            }
            else{
                albumNameInput.setError("Duplicate album name");
            }
        });
        b.setNegativeButton("Cancel", (dialog, which) -> {
            albumNameInput.setText(null);
            dialog.dismiss();
        });
        AlertDialog a = b.create();

        //rename input
        AlertDialog.Builder r = new AlertDialog.Builder(Photos.this);
        r.setTitle("Album name");
        r.setMessage("Please enter an album name");
        renameInput = new EditText(Photos.this);
        r.setView(renameInput);
        r.setPositiveButton("Enter", (dialog, which) -> {
            String s = renameInput.getText().toString();
            if(!hasAlbum(s)){
                if(albumGVAdapter.selected != null) {
                    String text = ((TextView) albumGVAdapter.selected.findViewById(R.id.item_name)).getText().toString();
                    String name = text.substring(0, text.indexOf("\n"));
                    Toast.makeText(Photos.this, "Album renamed", Toast.LENGTH_SHORT).show();
                    albumGVAdapter.renameItem(name, renameInput.getText().toString());
                    try {
                        writeApp();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                dialog.dismiss();
                renameInput.setText(null);
            }
            else{
                renameInput.setError("Duplicate album name");
            }
        });
        r.setNegativeButton("Cancel", (dialog, which) -> {
            renameInput.setText(null);
            dialog.dismiss();
        });
        AlertDialog rename = r.create();

        //add album
        addButton.setOnClickListener(v -> {
            a.show();
            a.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v1 -> {
                boolean wantToCloseDialog = (albumNameInput.getText().toString().trim().isEmpty());
                if (!wantToCloseDialog) {
                    if(!hasAlbum(albumNameInput.getText().toString())){
                        albumList.add(new Album(albumNameInput.getText().toString()));
                        albumGrid.setAdapter(albumGVAdapter);
                        try {
                            writeApp();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        a.dismiss();
                        albumNameInput.setText(null);
                    }
                    else{
                        albumNameInput.setError("Duplicate album name");
                    }
                }
                else
                    albumNameInput.setError("Please enter a valid name");
            });
        });

        //delete album
        deleteButton.setOnClickListener(v -> {
            if(albumGVAdapter.selected != null){
                String text =((TextView)albumGVAdapter.selected.findViewById(R.id.item_name)).getText().toString();
                String name = text.substring(0, text.indexOf("\n"));
                Toast.makeText(Photos.this, name + " was deleted", Toast.LENGTH_SHORT).show();
                albumGVAdapter.removeItem(name);
                try {
                    writeApp();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                albumGVAdapter.deselect();
            }
            else{
                Toast.makeText(Photos.this, "Please select an album", Toast.LENGTH_SHORT).show();

            }
        });

        //rename album
        renameButton.setOnClickListener(v2 -> {
            if(albumGVAdapter.selected != null) {
                rename.show();
                rename.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v3 -> {
                    String s = renameInput.getText().toString();
                    if(!hasAlbum(s)){
                        String text = ((TextView) albumGVAdapter.selected.findViewById(R.id.item_name)).getText().toString();
                            String name = text.substring(0, text.indexOf("\n"));
                            Toast.makeText(Photos.this, "Album renamed", Toast.LENGTH_SHORT).show();
                            albumGVAdapter.renameItem(name, renameInput.getText().toString());
                            try {
                                writeApp();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        rename.dismiss();
                        albumNameInput.setText(null);
                    }
                    else{
                        renameInput.setError("Duplicate album name");
                    }
                });
            }
            else {
                Toast.makeText(Photos.this, "Please select an album", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void loadContent() throws IOException, ClassNotFoundException {
        albumList = readApp();
    }

    protected void onDestroy() {
        try {
            writeApp();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        super.onDestroy();
    }

    public void writeApp() throws IOException {
        FileOutputStream stream = Photos.this.getApplicationContext().openFileOutput("data.dat", Context.MODE_PRIVATE);
        ObjectOutputStream oos = new ObjectOutputStream(stream);
        oos.writeObject(albumList);
        oos.close();
        stream.close();
    }

    public ArrayList<Album> readApp() throws IOException, ClassNotFoundException {
        FileInputStream stream = Photos.this.getApplicationContext().openFileInput("data.dat");
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

    @SuppressLint("SdCardPath")
    public Boolean hasData() throws IOException{
        return new File("/data/user/0/com.example.photos/files/data.dat").exists();
    }

    public Boolean hasAlbum(String name){
        for(Album a : albumList){
            if(a.getAlbumName().equals(name)){
                return true;
            }
        }
        return false;
    }
}