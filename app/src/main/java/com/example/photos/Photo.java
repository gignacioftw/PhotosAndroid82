package com.example.photos;


import android.net.Uri;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Photo has a name, a path, a caption, and an ArrayList of tags
 * @author Gigna
 */
public class Photo implements Serializable {
    protected String name;
    protected String path;
    protected ArrayList<Tag> tags = new ArrayList<>();
    public Photo(){

    }

    public Photo(String name, String path){
        this.name = name;
        this.path = path;
    }

    public String getPath(){
        return path;
    }

    public String getName(){
        return name;
    }

    public void addTag(Tag tag){
        tags.add(tag);
    }

    public void removeTag(String tagName){
        Tag t = null;
        for(Tag tag: tags){
            if(tag.getValue().equals(tagName.substring(tagName.indexOf(" ") + 1))){
                t = tag;
            }
        }
        tags.remove(t);
    }

    public String[] returnTags(){
        String[] t = new String[tags.size()];
        for(int i = 0; i < tags.size(); i++){
            t[i] = tags.get(i).getName() + ": " + tags.get(i).getValue();
        }
        return t;
    }

    public void changeName(String newName) {
        this.name = newName;
    }

    public Boolean hasType(String type){
        for(int i = 0; i < returnTags().length; i++){
            if(returnTags()[i].contains(type)){
                return true;
            }
        }
        return false;
    }
}
