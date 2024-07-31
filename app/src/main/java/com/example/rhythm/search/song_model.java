package com.example.rhythm.search;

public class song_model {

    public String id;
    public String title;
    public String subtitle;
    public String url;
    public String coverurl;
    public String title_lowercase;

    public song_model(String id, String title, String url, String coverurl, String subtitle, String title_lowercase) {
        this.id = id;
        this.title = title;
        this.url = url;
        this.coverurl = coverurl;
        this.subtitle = subtitle;
        this.title_lowercase = title_lowercase;
    }

    public song_model(){}

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCoverurl() {
        return coverurl;
    }

    public void setCoverurl(String coverurl) {
        this.coverurl = coverurl;
    }




}
