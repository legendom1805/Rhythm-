package com.example.rhythm.mymusic;

public class uploadsong {

    public String songCategory,songTitle, artist, album_art, songDuration,songLink,mkey;

    public uploadsong(String songLink, String songDuration, String songTitle, String songCategory, String artist, String album_art) {


        if(songTitle.trim().equals(""))
        {
            songTitle = "No Title";
        }

        this.songLink = songLink;
        this.songDuration = songDuration;
        this.songTitle = songTitle;
        this.songCategory = songCategory;
        this.artist = artist;
        this.album_art = album_art;
    }

    public uploadsong() {
    }

    public String getMkey() {
        return mkey;
    }

    public void setMkey(String mkey) {
        this.mkey = mkey;
    }

    public String getSongLink() {
        return songLink;
    }

    public void setSongLink(String songLink) {
        this.songLink = songLink;
    }

    public String getSongCategory() {
        return songCategory;
    }

    public void setSongCategory(String songCategory) {
        this.songCategory = songCategory;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getSongDuration() {
        return songDuration;
    }

    public void setSongDuration(String songDuration) {
        this.songDuration = songDuration;
    }

    public String getAlbum_art() {
        return album_art;
    }

    public void setAlbum_art(String album_art) {
        this.album_art = album_art;
    }

    public String getSongTitle() {
        return songTitle;
    }

    public void setSongTitle(String songTitle) {
        this.songTitle = songTitle;
    }
}
