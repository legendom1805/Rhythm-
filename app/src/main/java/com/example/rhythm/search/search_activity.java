package com.example.rhythm.search;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.rhythm.R;
import com.example.rhythm.account_activity;
import com.example.rhythm.home.home_activity;
import com.example.rhythm.login_activity;
import com.example.rhythm.myexoplayer;
import com.example.rhythm.mymusic.mymusic_activity;
import com.example.rhythm.player_activity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class search_activity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseFirestore fstore;
    TextView emailtext, usernametext;
    String userId;
    ExoPlayer exoPlayer;
    TextView songTitle, songSubTitile;
    ImageView songimg,userimg;
    PlayerView playerView;
    SearchView searchView;
    RecyclerView searchResultsRecyclerView;
    songslistadapter songsAdapter;
    List<String> songList = new ArrayList<>();
    CollectionReference songsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search);
        auth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        songsRef = fstore.collection("songs");

        // Search
        searchView = findViewById(R.id.search_view);
        searchResultsRecyclerView = findViewById(R.id.search_results_recyclerview);

        searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        songsAdapter = new songslistadapter(search_activity.this, songList);
        searchResultsRecyclerView.setAdapter(songsAdapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchSongs(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchSongs(newText);
                return false;
            }
        });

        // Navigation Drawer
        NavigationView navigationView = findViewById(R.id.navigationView);

        View header = navigationView.getHeaderView(0);

        emailtext = header.findViewById(R.id.emailview2);
        usernametext = header.findViewById(R.id.usernameview);
        userId = auth.getCurrentUser().getUid();
        DocumentReference documentReference = fstore.collection("user").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                emailtext.setText(documentSnapshot.getString("Email"));
                usernametext.setText(documentSnapshot.getString("Username"));
            }
        });

        userimg = header.findViewById(R.id.imageView6);

        // Retrieve image URL from Realtime Database and load it into userimg using Glide
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("ProfileImage");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String imageUrl = snapshot.getValue(String.class);
                    Glide.with(search_activity.this).load(imageUrl).circleCrop().into(userimg);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(search_activity.this, "Failed to load profile image", Toast.LENGTH_SHORT).show();
            }
        });

        DrawerLayout drawerLayout = findViewById(R.id.drawerlayout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        ImageButton drawernavtoggle = findViewById(R.id.drawernavtoggle);
        ImageButton acctoggle = findViewById(R.id.accounttogglle);
        setSupportActionBar(toolbar);

        acctoggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), account_activity.class));
            }
        });

        drawernavtoggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                if (item.getItemId() == R.id.accountnav) {
                    startActivity(new Intent(getApplicationContext(), account_activity.class));
                } else if (item.getItemId() == R.id.logoutnav) {
                    FirebaseAuth.getInstance().signOut();
                    Intent logint = new Intent(search_activity.this, login_activity.class);
                    startActivity(logint);
                    finish();
                }

                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        songTitle = findViewById(R.id.song_title_text_player_bottom);
        songSubTitile = findViewById(R.id.song_subtitle_text_player_bottom);
        songimg = findViewById(R.id.song_cover_image_player_bottom);
        playerView = findViewById(R.id.player_view_bottom);

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

        LinearLayout bottomplayer;
        bottomplayer = findViewById(R.id.player_fragment);
        bottomplayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(search_activity.this, player_activity.class));
            }
        });

        // Bottom navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.search);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.home) {
                    startActivity(new Intent(getApplicationContext(), home_activity.class));
                    return true;
                } else if (item.getItemId() == R.id.search) {
                    startActivity(new Intent(getApplicationContext(), search_activity.class));
                    return true;
                } else if (item.getItemId() == R.id.mymusic) {
                    startActivity(new Intent(getApplicationContext(), mymusic_activity.class));
                    return true;
                }
                return false;
            }
        });

        user = auth.getCurrentUser();
        if (user == null) {
            Intent logint = new Intent(search_activity.this, login_activity.class);
            startActivity(logint);
            finish();
        }

        EventChangeListener();
    }

    private void EventChangeListener() {
        FirebaseFirestore.getInstance().collection("category")
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
                    List<category_model> categoriesarr = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        category_model categoryModel = document.toObject(category_model.class);
                        categoriesarr.add(categoryModel);
                        SetupCategoryRecyclerView(categoriesarr);
                    }
                });
    }

    public void SetupCategoryRecyclerView(List<category_model> categorylist) {
        RecyclerView recyclerView = findViewById(R.id.recyclerviewcontact);
        categories_adapter categoriesAdapter = new categories_adapter(this, (ArrayList<category_model>) categorylist);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(categoriesAdapter);
    }

    private void searchSongs(String query) {
        if (query.isEmpty()) {
            songList.clear();
            songsAdapter.notifyDataSetChanged();
            return;
        }

        songsRef.whereGreaterThanOrEqualTo("title_lowercase", query.toLowerCase())
                .whereLessThanOrEqualTo("title_lowercase", query.toLowerCase() + '\uf8ff')
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        songList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String song_id = document.getId();
                            songList.add(song_id);
                        }
                        songsAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(search_activity.this, "Error getting documents: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
