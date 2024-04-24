package com.example.photos;

import java.io.Serializable;

/**
 * Tag has a name and a value
 * @author Gigna
 */
public class Tag implements Serializable {
    protected String name;
    protected String value;

    public Tag(){

    }

    public Tag(String name, String value){
        this.name = name;
        this.value = value;
    }

    public String getName(){
        return name;
    }

    public String getValue(){
        return value;
    }


}
