package com.example.rhythm;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;

import com.example.rhythm.search.song_model;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class myexoplayer {


    public static song_model currentsong = null;
    static ExoPlayer exoPlayer = null;
    private static final String SHARED_PREFS = "rhythm_prefs";
    private static final String LAST_SONG_ID = "last_song_id";

    public ExoPlayer getInstance() {
        return exoPlayer;
    }

    public song_model getCurrentsong() {
        return currentsong;
    }

    public static void startplaying(Context context, song_model song) {
        if (exoPlayer == null)
            exoPlayer = new ExoPlayer.Builder(context).build();

        if (currentsong != song) {
            currentsong = song;
            updateCount();


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


    public static void updateCount() {
        if (currentsong != null && currentsong.getId() != null) {
            String id = currentsong.getId();
            FirebaseFirestore.getInstance().collection("songs")
                    .document(id)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        Long latestCount = documentSnapshot.getLong("count");
                        if (latestCount == null) {
                            latestCount = 1L;
                        } else {
                            latestCount = latestCount + 1;
                        }

                        FirebaseFirestore.getInstance().collection("songs")
                                .document(id)
                                .update("count", latestCount);
                    });
        }
    }


}
