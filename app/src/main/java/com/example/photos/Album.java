package com.example.photos;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

    public void addCaption(String photoName, String caption){
        photos.get(photoName).addCaption(caption);
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

    public String getDateRange(){
        Photo[] p = getPhotos();
        if(p.length == 0){
            return "";
        }
        ArrayList<Calendar> c = new ArrayList<>();
        for(Photo photo : p){
            c.add(photo.getDate());
        }
        Calendar greatest = c.get(0);
        Calendar lowest = c.get(0);
        if(c.size() > 1){
            for(int i = 1; i < c.size(); i++){
                if(c.get(i).compareTo(greatest) > 0){
                    greatest = c.get(i);
                }
                else if(c.get(i).compareTo(lowest) < 0){
                    lowest = c.get(i);
                }
            }
        }
        SimpleDateFormat d = new SimpleDateFormat("yyyy-MM-dd");
        String g = d.format(greatest.getTime());
        String l = d.format(lowest.getTime());
        return l +" - " +g;
    }

    @Override
    public String toString() {
        return albumName;
    }

}
