package com.example.photos;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Album has a name and a HashMap of photo values.
 * @author Gigna
 */
public class Album implements Serializable {
    protected String albumName;

    protected HashMap<String, Photo> photos;
    public Album(){

    }

    public Album(String albumName){
        this.albumName = albumName;
        photos = new HashMap<>();
    }

    public String getAlbumName(){
        return albumName;
    }
    public void changeName(String albumName){
        this.albumName = albumName;
    }

    public void addPhoto(Photo p){
        photos.put(p.getName(), p);
    }

    public void removePhoto(String photoName){
        photos.remove(photoName);
    }

    public int getNumOfPhotos(){
        return photos.size();
    }

    public Photo getPhoto(String photoName){
        return photos.get(photoName);
    }
    public Photo[] getPhotos(){
        return photos.values().toArray(new Photo[0]);
    }

    public void renamePhoto(String originalName, String newName){
        Photo p = photos.get(originalName);
        p.changeName(newName);
        photos.remove(originalName);
        photos.put(newName, p);
    }

    public Boolean hasPhoto(String photoName){
        return photos.containsKey(photoName);
    }

    @Override
    public String toString() {
        return albumName;
    }

}
