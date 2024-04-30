package com.example.photos;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.GridView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Search extends AppCompatActivity {

    ArrayList<String> tagList;
    ArrayList<Photo> photoList;

    ArrayList<Photo> filteredList;
    GridView searchGrid;

    AutoCompleteTextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        Intent intent = getIntent();
        Button searchButton = findViewById(R.id.searchPhoto);
        Button clearButton = findViewById(R.id.clear);
        Button backButton = findViewById(R.id.searchBack);
        searchGrid = findViewById(R.id.searchGrid);
        photoList = (ArrayList<Photo>) intent.getExtras().getSerializable("photoList");
        filteredList = new ArrayList<>();
        tagList = new ArrayList<>();
        textView = new AutoCompleteTextView(this);
        for(Photo p : photoList){
            for(String t : p.returnTags()){
                if(!tagList.contains(t)){
                    tagList.add(t);
                }
            }
        }
        /*for(int i = 0; i < tagList.size(); i++){
            for(int j = 0; j < tagList.size(); j++){
                String firstTag = tagList.get(i);
                String secondTag = tagList.get(j);
                String firstType = firstTag.substring(0, firstTag.indexOf(":"));
                String secondType = secondTag.substring(0, secondTag.indexOf(":"));
                if(!firstTag.equals(secondTag)) {
                    if (!firstType.equals("location") && !secondType.equals("location") || firstType.equals("location") && !secondType.equals("location") || !firstType.equals("location")) {
                        tagList.add(firstTag + " and " + secondTag);
                        tagList.add(firstTag + " or " + secondTag);
                    }
                }
            }
        }*/
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, tagList);
        PhotoGVAdapter photoGVAdapter = new PhotoGVAdapter(this, photoList);
        textView.setAdapter(adapter);
        b.setTitle("Tag Search");
        b.setMessage("Please enter a tag name");
        b.setView(textView);
        b.setPositiveButton("Enter", (dialog, which) ->{
            String input = textView.getText().toString();
            if(input.contains(" and ")) {
                String firstCheck = input.substring(0, input.indexOf(" and "));
                String secondCheck = input.substring(input.indexOf("and ") + 4);
                photoList.stream().filter(s -> (Arrays.asList(s.returnTags()).contains(firstCheck) && Arrays.asList(s.returnTags()).contains(secondCheck))).forEach(filteredList::add);

            }
            else if(input.contains(" or ")){
                String firstCheck = input.substring(0, input.indexOf(" or "));
                String secondCheck = input.substring(input.indexOf("or ") + 3);
                photoList.stream().filter(s -> (Arrays.asList(s.returnTags()).contains(firstCheck) || Arrays.asList(s.returnTags()).contains(secondCheck))).forEach(filteredList::add);
            }
            else{
                photoList.stream().filter(s -> (Arrays.asList(s.returnTags()).contains(input))).forEach(filteredList::add);
            }
            PhotoGVAdapter searchAdapter = new PhotoGVAdapter(this, filteredList);
            searchGrid.setAdapter(searchAdapter);
            textView.setText("");
        });
        b.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.dismiss();
        });
        AlertDialog a = b.create();
        searchButton.setOnClickListener(v -> a.show());
        clearButton.setOnClickListener(v1 -> {
            searchGrid.setAdapter(null);
            filteredList.clear();
        });
        backButton.setOnClickListener(v2 -> super.onBackPressed());
    }
}
