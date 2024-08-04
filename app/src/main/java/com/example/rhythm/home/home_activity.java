package com.example.rhythm.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.rhythm.R;
import com.example.rhythm.account_activity;
import com.example.rhythm.login_activity;
import com.example.rhythm.myexoplayer;
import com.example.rhythm.mymusic.mymusic_activity;
import com.example.rhythm.player_activity;
import com.example.rhythm.search.categories_adapter;
import com.example.rhythm.search.category_model;
import com.example.rhythm.search.category_view;
import com.example.rhythm.search.search_activity;
import com.example.rhythm.search.song_model;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class home_activity extends AppCompatActivity {
    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseFirestore fstore;
    TextView emailtext,usernametext;
    String userId;
    ExoPlayer exoPlayer;
    TextView songTitle,songSubTitile;
    ImageView songimg,userimg;
    PlayerView playerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        auth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationViewhome);

        View header =navigationView.getHeaderView(0);

        emailtext = (TextView)header.findViewById(R.id.emailview2);
        usernametext = (TextView)header.findViewById(R.id.usernameview);

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
                    Glide.with(home_activity.this).load(imageUrl).circleCrop().into(userimg);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(home_activity.this, "Failed to load profile image", Toast.LENGTH_SHORT).show();
            }
        });





        //Navigation Drawer
        DrawerLayout drawerLayout;
        drawerLayout = findViewById(R.id.drawerlayout);
        Toolbar toolbar;
        ImageButton drawernavtoggle;
        ImageButton acctoggle;
        toolbar = findViewById(R.id.toolbar);
        drawernavtoggle = findViewById(R.id.drawernavtoggle);
        acctoggle = findViewById(R.id.accounttogglle);
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
                drawerLayout.open();
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {

                if(item.getItemId()==R.id.accountnav)
                {
                    startActivity(new Intent(getApplicationContext(), account_activity.class));
                }
                else if (item.getItemId()==R.id.logoutnav) {
                    FirebaseAuth.getInstance().signOut();
                    Intent logint = new Intent(home_activity.this, login_activity.class);
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
                startActivity(new Intent(home_activity.this, player_activity.class));
            }
        });





        //Bottom navigation
        BottomNavigationView bottomNavigationView;
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.home);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if(item.getItemId() == R.id.home)
                {
                    startActivity(new Intent(getApplicationContext(), home_activity.class));
                    return true;
                }
                else if (item.getItemId() == R.id.search) {

                    startActivity(new Intent(getApplicationContext(), search_activity.class));
                    return true;
                }
                else if (item.getItemId() == R.id.mymusic) {

                    startActivity(new Intent(getApplicationContext(), mymusic_activity.class));
                    return true;
                }

                return false;
            }
        });

        user = auth.getCurrentUser();
        if (user == null)
        {
            Intent logint = new Intent(home_activity.this, login_activity.class);
            startActivity(logint);
            finish();
        }

        else {

            Toast.makeText(home_activity.this,"Welcome to Rhythm!",Toast.LENGTH_SHORT).show();



        }
        EventChangeListner();
        myexoplayer.initializePlayer(this);
        //Section1
         RelativeLayout section1layout = findViewById(R.id.section_1_main_layout);
         TextView sectitle = findViewById(R.id.section1title);
         RecyclerView sectionrecyclerview = findViewById(R.id.section1recyclerview);
         SetUpSection("section_1", section1layout,sectitle,sectionrecyclerview);


    }

    private void EventChangeListner() {

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
    //categories
    public void SetupCategoryRecyclerView(List<category_model> categorylist){

        RecyclerView recyclerView = findViewById(R.id.recyclerviewcontact);
        categories_adapter categoriesAdapter = new categories_adapter(this, (ArrayList<category_model>) categorylist);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,RecyclerView.HORIZONTAL,false));
        recyclerView.setAdapter(categoriesAdapter);

    }

    //sections
    public void SetUpSection(String id, RelativeLayout mainlayout,TextView titleView,RecyclerView recyclerView){
       FirebaseFirestore.getInstance().collection("sections").document(id)
               .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                   @Override
                   public void onSuccess(DocumentSnapshot documentSnapshot) {

                       category_model section = documentSnapshot.toObject(category_model.class);
                       if(section != null){
                           mainlayout.setVisibility(View.VISIBLE);
                           titleView.setText(section.getName());
                           recyclerView.setLayoutManager(new LinearLayoutManager(home_activity.this,RecyclerView.HORIZONTAL,false));
                           sectionsonglistadapter sectionsonglistadapter = new sectionsonglistadapter(home_activity.this,section.getSongs());
                           recyclerView.setAdapter(sectionsonglistadapter);
                           mainlayout.setOnClickListener(new View.OnClickListener() {
                               @Override
                               public void onClick(View view) {
                                   Intent intent = new Intent(home_activity.this, category_view.class);
                                   intent.putExtra("category_model",section);
                                   startActivity(intent);
                               }
                           });

                       }
                   }
               });
    }


}