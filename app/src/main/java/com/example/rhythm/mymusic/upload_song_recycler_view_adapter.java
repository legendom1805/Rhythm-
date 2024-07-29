package com.example.rhythm.mymusic;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.rhythm.R;
import com.example.rhythm.myexoplayer;
import com.example.rhythm.player_activity;
import com.example.rhythm.search.song_model;

import java.util.List;

public class upload_song_recycler_view_adapter extends RecyclerView.Adapter<upload_song_recycler_view_adapter.SongViewHolder> {

    private Context mContext;
    private List<song_model> mSongs;

    public upload_song_recycler_view_adapter(Context context, List<song_model> songs) {
        mContext = context;
        mSongs = songs;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.song_item, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        song_model song = mSongs.get(position);
        holder.titleTextView.setText(song.getTitle());
        holder.artistTextView.setText(song.getSubtitle());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myexoplayer.startplaying(mContext, song);
                mContext.startActivity(new Intent(mContext, player_activity.class));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSongs.size();
    }

    public class SongViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView;
        public TextView artistTextView;

        public SongViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.song_title);
            artistTextView = itemView.findViewById(R.id.song_artist);
        }
    }
}
