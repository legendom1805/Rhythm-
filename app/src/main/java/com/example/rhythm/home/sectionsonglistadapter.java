package com.example.rhythm.home;

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
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.rhythm.R;
import com.example.rhythm.myexoplayer;
import com.example.rhythm.player_activity;
import com.example.rhythm.search.song_model;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class sectionsonglistadapter extends RecyclerView.Adapter<sectionsonglistadapter.ViewHolder> {

    List<String> songIdList;
    Context context;
    public sectionsonglistadapter(Context context, List<String> songarr) {
        this.songIdList = songarr;
        this.context = context;

    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.section_songlist_recycler_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String songId = songIdList.get(position);

        FirebaseFirestore.getInstance().collection("songs")
                .document(songId).get().addOnSuccessListener(documentSnapshot -> {

                    song_model songs = documentSnapshot.toObject(song_model.class);
                    if (songs != null) {
                        holder.songname.setText(songs.title);
                        holder.songsubtitle.setText(songs.subtitle);
                        Glide.with(holder.songimg).load(songs.coverurl)
                                .transform(new RoundedCorners(32))
                                .into(holder.songimg);

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                myexoplayer.startplaying(context, songs);
                                context.startActivity(new Intent(context, player_activity.class));


                            }
                        });

                    }


                });

    }

    @Override
    public int getItemCount() {
        return songIdList.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView songname;
        TextView songsubtitle;
        ImageView songimg;


        public ViewHolder(View itemView) {
            super(itemView);
            songname = itemView.findViewById(R.id.section_song_cover_title);
            songsubtitle = itemView.findViewById(R.id.section_song_cover_subtitle);
            songimg = itemView.findViewById(R.id.section_song_cover_img);



        }
    }
}



