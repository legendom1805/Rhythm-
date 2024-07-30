package com.example.rhythm;

import android.content.Context;

import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;

import com.example.rhythm.search.song_model;

public class myexoplayer {

    static ExoPlayer exoPlayer = null;
    public static song_model currentsong = null;

    public ExoPlayer getInstance()
    {
        return exoPlayer;
    }

    public song_model getCurrentsong()
    {
        return currentsong;
    }

    public static void startplaying(Context context, song_model song)
    {
        if(exoPlayer==null)
            exoPlayer = new ExoPlayer.Builder(context).build();



        if(currentsong != song){
            currentsong = song;
        if (currentsong != null && currentsong.getUrl() != null) {
            String url = currentsong.getUrl();
            MediaItem mediaItem = MediaItem.fromUri(url);
            if (exoPlayer != null) {
                exoPlayer.setMediaItem(mediaItem);
                exoPlayer.prepare();
                exoPlayer.play();
            }
        }
        }


    }


}
