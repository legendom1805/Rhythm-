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


            if (currentsong != null) {
                SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(LAST_SONG_ID, currentsong.getId());
                editor.apply();
            }


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

    public static void initializePlayer(Context context) {
        if (exoPlayer == null)
            exoPlayer = new ExoPlayer.Builder(context).build();

        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        String lastSongId = sharedPreferences.getString(LAST_SONG_ID, null);

        if (lastSongId != null) {
            // Fetch song metadata from Firebase using the last song ID
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference songRef = db.collection("songs").document(lastSongId);
            songRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        song_model lastSong = documentSnapshot.toObject(song_model.class);
                        if (lastSong != null && lastSong.getUrl() != null) {
                            currentsong = lastSong;
                            MediaItem mediaItem = MediaItem.fromUri(lastSong.getUrl());
                            if (exoPlayer != null) {
                                exoPlayer.setMediaItem(mediaItem);
                                exoPlayer.prepare();
                            }
                        }
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    Log.e("Music","No Song");
                }
            });


        }
    }
}
