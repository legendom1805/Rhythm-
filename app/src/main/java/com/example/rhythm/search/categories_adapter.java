package com.example.rhythm.search;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.rhythm.R;
import java.util.ArrayList;

public class categories_adapter extends RecyclerView.Adapter<categories_adapter.ViewHolder> {
    Context context;
    ArrayList<category_model> categoriesarr;

    public categories_adapter(Context context, ArrayList<category_model> categoriesarr) {
        this.context = context;
        this.categoriesarr = categoriesarr;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.categories_cards_recycler_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        category_model modelpos = categoriesarr.get(position);
        Glide.with(context).load(modelpos.coverURL).into(holder.imgbtn);
        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, category_view.class);
            intent.putExtra("category_model", modelpos);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return categoriesarr.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgbtn;

        public ViewHolder(View itemView) {
            super(itemView);
            imgbtn = itemView.findViewById(R.id.cover_image_view);
        }
    }
}
