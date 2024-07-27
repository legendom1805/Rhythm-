package com.example.rhythm;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.rhythm.search.song_model;

public class player_activity extends AppCompatActivity {

    ExoPlayer exoPlayer;
    TextView songTitle,songSubTitile;
    ImageView songimg;
    PlayerView playerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_player);

        songTitle = findViewById(R.id.song_title_text_player);
        songSubTitile = findViewById(R.id.song_subtitle_text_player);
        songimg = findViewById(R.id.song_cover_image_player);
        playerView = findViewById(R.id.player_view);

        song_model currentSong = myexoplayer.currentsong;
        if (currentSong != null) {

            songTitle.setText(currentSong.getTitle());
            songSubTitile.setText(currentSong.getSubtitle());
            Glide.with(songimg).load(currentSong.getCoverurl())
                    .circleCrop()
                    .into(songimg);

            exoPlayer = new myexoplayer().getInstance();
            playerView.setPlayer(exoPlayer);



        }


    }
}