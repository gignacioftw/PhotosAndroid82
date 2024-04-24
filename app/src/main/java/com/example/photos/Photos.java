//Gabriel Ignacio
package com.example.photos;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TableLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class Photos extends AppCompatActivity {
    private GridView albumGrid;
    private Button addButton;
    private EditText albumNameInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        albumGrid = findViewById(R.id.albumGrid);
        addButton = findViewById(R.id.addAlbum);
        ArrayList<Album> albumList = new ArrayList<Album>();
        AlbumGVAdapter albumGVAdapter = new AlbumGVAdapter(Photos.this, albumList);
        AlertDialog.Builder b = new AlertDialog.Builder(Photos.this);
        b.setTitle("Album name");
        b.setMessage("Please enter an album name");
        albumNameInput = new EditText(Photos.this);
        b.setView(albumNameInput);
        b.setPositiveButton("Enter", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String s = albumNameInput.getText().toString();
                albumList.add(new Album(s));
                albumGrid.setAdapter(albumGVAdapter);
                dialog.dismiss();
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
                            albumList.add(new Album(albumNameInput.getText().toString()));
                            albumGrid.setAdapter(albumGVAdapter);
                            a.dismiss();
                            albumNameInput.setText(null);
                        }
                        else
                            albumNameInput.setError("Please enter a valid name");
                    }
                });
            }
        });

    }
}