package com.example.photos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Photo implements Serializable {
    private HashMap<String, Integer> tagTypes;

    protected String name;
    protected String path;
    protected ArrayList<Tag> tags = new ArrayList<>();
    public Photo(){

    }

    public Photo(String name, String path){
        this.name = name;
        this.path = path;
        this.tagTypes = new HashMap<>();
        tagTypes.put("location", 0);
        tagTypes.put("person", 1);
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

    public Boolean canAdd(String type, Photo t){
        return tagTypes.get(type) != 0 || !t.hasType(type);
    }

    public String[] returnTagTypes(){
        return tagTypes.keySet().toArray(new String[0]);
    }
}
