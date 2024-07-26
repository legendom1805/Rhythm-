package com.example.rhythm;

public class model {

    String coverURL;

    public  model() {}

    public model(String coverURL) {
        this.coverURL = coverURL;
    }

    public String getCoverURL() {
        return coverURL;
    }

    public void setCoverURL(String coverURL) {
        this.coverURL = coverURL;
    }
}

