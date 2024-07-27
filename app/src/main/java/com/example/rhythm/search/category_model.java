package com.example.rhythm.search;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class category_model implements Serializable {

    String name;
    String coverURL;
    List<String> songs;

    public category_model(String name,String coverURL,List<String> songs) {
        this.name = name;
        this.coverURL = coverURL;
        this.songs = songs;
    }



    public category_model() {
        this("", "", new ArrayList<>());
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public List<String> getSongs() {
        return songs;
    }

    public void setSongs(List<String> songs) {
        this.songs = songs;
    }



    public String getCoverURL() {
        return coverURL;
    }

    public void setCoverURL(String coverURL) {
        this.coverURL = coverURL;
    }
}

