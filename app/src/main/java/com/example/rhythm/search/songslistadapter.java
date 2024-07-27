package com.example.rhythm.search;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.rhythm.R;

import java.util.ArrayList;
import java.util.List;

public class songslistadapter extends RecyclerView.Adapter<songslistadapter.ViewHolder> {

    List<String> songIdList;
    public songslistadapter(List<String> songarr) {
        this.songIdList = songarr;

    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_list_recycler_view, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String songId = songIdList.get(position);
        holder.songname.setText(songId);

    }

    @Override
    public int getItemCount() {
        return songIdList.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView songname;

        public ViewHolder(View itemView) {
            super(itemView);
            songname = itemView.findViewById(R.id.song_title_text_view);

        }
    }
}



