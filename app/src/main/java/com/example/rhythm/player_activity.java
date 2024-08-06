package com.example.rhythm;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.rhythm.search.song_model;

public class player_activity extends AppCompatActivity {

    ExoPlayer exoPlayer;
    TextView songTitle,songSubTitile;
    ImageView songimg,playergif;
    ImageButton backbtn;
    PlayerView playerView;
    Player.Listener playerlistner = new Player.Listener() {
        @Override
        public void onIsPlayingChanged(boolean isPlaying) {
            Player.Listener.super.onIsPlayingChanged(isPlaying);
            showgif(isPlaying);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_player);

        songTitle = findViewById(R.id.song_title_text_player);
        songSubTitile = findViewById(R.id.song_subtitle_text_player);
        songimg = findViewById(R.id.song_cover_image_player);
        playerView = findViewById(R.id.player_view);
        playergif = findViewById(R.id.song_cover_gif_player);
        backbtn = findViewById(R.id.backbutton);

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        song_model currentSong = myexoplayer.currentsong;
        if (currentSong != null) {

            songTitle.setText(currentSong.getTitle());
            songSubTitile.setText(currentSong.getSubtitle());
            Glide.with(songimg).load(currentSong.getCoverurl())
                    .circleCrop()
                    .into(songimg);
            Glide.with(playergif).load(R.drawable.pngif)
                    .circleCrop()
                    .into(playergif);

            exoPlayer = new myexoplayer().getInstance();
            playerView.showController();
            playerView.setPlayer(exoPlayer);
            exoPlayer.addListener(playerlistner);

        }




    }

    public void showgif(Boolean show){

        if(show)
            playergif.setVisibility(View.VISIBLE);
        else
            playergif.setVisibility(View.INVISIBLE);


    }
}