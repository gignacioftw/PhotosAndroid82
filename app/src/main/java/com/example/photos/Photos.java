//Gabriel Ignacio
package com.example.photos;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
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
    String storeDir;

    String storeFile;
    private GridView albumGrid;
    private Button addButton;
    private EditText albumNameInput;

    private Button deleteButton;

    private ArrayList<Album> albumList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        albumGrid = findViewById(R.id.albumGrid);
        addButton = findViewById(R.id.addAlbum);
        deleteButton = findViewById(R.id.deleteAlbum);
        try {
            if(hasData()){
                loadContent();
            }
            else{
                albumList = new ArrayList<>();
                writeApp(albumList);
                loadContent();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        AlbumGVAdapter albumGVAdapter = new AlbumGVAdapter(Photos.this, albumList);
        albumGrid.setAdapter(albumGVAdapter);
        AlertDialog.Builder b = new AlertDialog.Builder(Photos.this);
        b.setTitle("Album name");
        b.setMessage("Please enter an album name");
        albumNameInput = new EditText(Photos.this);
        b.setView(albumNameInput);
        b.setPositiveButton("Enter", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String s = albumNameInput.getText().toString();
                if(!hasAlbum(s)){
                    albumList.add(new Album(s));
                    albumGrid.setAdapter(albumGVAdapter);
                    dialog.dismiss();
                }
                else{
                    albumNameInput.setError("Duplicate album name");
                }
            }
        });
        b.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                albumNameInput.setText(null);
                dialog.dismiss();
            }
        });
        AlertDialog a = b.create();
        addButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                a.show();
                a.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean wantToCloseDialog = (albumNameInput.getText().toString().trim().isEmpty());
                        if (!wantToCloseDialog) {
                            if(!hasAlbum(albumNameInput.getText().toString())){
                                albumList.add(new Album(albumNameInput.getText().toString()));
                                albumGrid.setAdapter(albumGVAdapter);
                                try {
                                    writeApp(albumList);
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
                    }
                });
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(albumGVAdapter.selected != null){
                    String text =((TextView)albumGVAdapter.selected.findViewById(R.id.item_name)).getText().toString();
                    String name = text.substring(0, text.indexOf("\n"));
                    Toast.makeText(Photos.this, name + " was deleted", Toast.LENGTH_SHORT).show();
                    albumGVAdapter.removeItem(name);
                    try {
                        writeApp(albumList);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    albumGVAdapter.deselect();
                }
                else{
                    Toast.makeText(Photos.this, "Please select an album", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    public void loadContent() throws IOException, ClassNotFoundException {
        albumList = readApp();
    }
    protected void onDestroy() {
        try {
            writeApp(albumList);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        super.onDestroy();
    }

    public void writeApp(ArrayList<Album> a) throws IOException {
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
            return new ArrayList<Album>();
        }
        ArrayList<Album> albumArrayList = (ArrayList<Album>) oos.readObject();
        stream.close();
        oos.close();
        return albumArrayList;
    }

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