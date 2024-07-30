package com.example.rhythm;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.rhythm.search.song_model;

public class player_bottom_activity extends Fragment {

    ExoPlayer exoPlayer;
    TextView songTitle,songSubTitile;
    ImageView songimg;
    PlayerView playerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_player_bottom_activity, container, false);

        songTitle = view.findViewById(R.id.song_title_text_player_bottom);
        songSubTitile = view.findViewById(R.id.song_subtitle_text_player_bottom);
        songimg = view.findViewById(R.id.song_cover_image_player_bottom);
        playerView = view.findViewById(R.id.player_view_bottom);

        song_model currentSong = myexoplayer.currentsong;
        if (currentSong != null) {

            songTitle.setText(currentSong.getTitle());
            songSubTitile.setText(currentSong.getSubtitle());
            Glide.with(songimg).load(currentSong.getCoverurl())
                    .circleCrop()
                    .into(songimg);

            exoPlayer = new myexoplayer().getInstance();
            playerView.showController();
            playerView.setPlayer(exoPlayer);



        }


        return view;
    }
}