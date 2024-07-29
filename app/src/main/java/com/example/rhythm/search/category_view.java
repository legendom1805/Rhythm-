package com.example.rhythm.search;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.rhythm.R;

public class category_view extends AppCompatActivity {
    ImageView cat_img;
    TextView cat_name;
    ImageButton backbtn;
    public static category_model category;

    public static void setCategory(category_model category) {
        category_view.category = category;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_category_view);

        cat_img = findViewById(R.id.cat_img_view);
        cat_name = findViewById(R.id.cat_name_view);

        Intent intent = getIntent();
        category = (category_model) intent.getSerializableExtra("category_model");

        if (category != null) {
            Glide.with(this).load(category.getCoverURL()).into(cat_img);
            cat_name.setText(category.getName());
        } else {
            Log.e("category_view", "Category model is null");
        }

        backbtn = findViewById(R.id.back_btn);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        setUpsongslistrecyclerview();
    }

    public void setUpsongslistrecyclerview() {
        if (category != null && category.songs != null) {
            RecyclerView recyclerView = findViewById(R.id.songs_list_recyclerview);
            songslistadapter songslistadapter = new songslistadapter(this,category.songs);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
            recyclerView.setAdapter(songslistadapter);
        } else {
            Log.e("category_view", "Category or Category Songs is null");
        }
    }
}
