package com.example.rhythm.mymusic;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rhythm.R;
import com.example.rhythm.account_activity;
import com.example.rhythm.home.home_activity;
import com.example.rhythm.login_activity;
import com.example.rhythm.search.search_activity;
import com.example.rhythm.search.song_model;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class mymusic_activity extends AppCompatActivity {

    private Button uploadButton;
    private Uri songUri;
    private RecyclerView recyclerView;
    private upload_song_recycler_view_adapter songAdapter;
    private List<song_model> songList;
    private TextInputEditText txttitle,txtsubtitle;
    String texttitle, textsubtitle;
    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseFirestore fstore;
    TextView emailtext, usernametext;
    String userId;

    private FirebaseStorage storage;
    private FirebaseDatabase database;

    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    songUri = result.getData().getData();
                    uploadSong();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mymusic);


        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView);

        View header = navigationView.getHeaderView(0);
        auth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();

        emailtext = (TextView) header.findViewById(R.id.emailview2);
        usernametext = (TextView) header.findViewById(R.id.usernameview);
        userId = auth.getCurrentUser().getUid();
        DocumentReference documentReference = fstore.collection("user").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                emailtext.setText(documentSnapshot.getString("Email"));
                usernametext.setText(documentSnapshot.getString("Username"));

            }
        });

        //Navigation Drawer
        DrawerLayout drawerLayout;
        //NavigationView navigationView;
        Toolbar toolbar;
        ImageButton drawernavtoggle;
        ImageButton acctoggle;

        drawerLayout = findViewById(R.id.drawerlayout);
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
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {

                if (item.getItemId() == R.id.account) {
                    startActivity(new Intent(getApplicationContext(), account_activity.class));
                } else if (item.getItemId() == R.id.logout) {
                    FirebaseAuth.getInstance().signOut();
                    Intent logint = new Intent(mymusic_activity.this, login_activity.class);
                    startActivity(logint);
                    finish();
                }

                drawerLayout.closeDrawer(GravityCompat.START);


                return true;
            }


        });

        //Bottom navigation
        BottomNavigationView bottomNavigationView;
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.mymusic);

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
            Intent logint = new Intent(mymusic_activity.this, login_activity.class);
            startActivity(logint);
            finish();
        }

        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();

        uploadButton = findViewById(R.id.uploadButton);
        txttitle = findViewById(R.id.textinputtitle);
        txtsubtitle = findViewById(R.id.textinputsubtitle);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                texttitle = String.valueOf(txttitle.getText());
                textsubtitle = String.valueOf(txtsubtitle.getText());

                if (TextUtils.isEmpty(texttitle)) {
                    Toast.makeText(mymusic_activity.this, "Enter Song Name", Toast.LENGTH_SHORT).show();

                }

                if (TextUtils.isEmpty(textsubtitle)) {
                    Toast.makeText(mymusic_activity.this, "Enter Song Subtitle or Artist", Toast.LENGTH_SHORT).show();

                }
                openFileChooser();
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        songList = new ArrayList<>();
        songAdapter = new upload_song_recycler_view_adapter(this, songList);
        recyclerView.setAdapter(songAdapter);

        fetchSongs();
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        activityResultLauncher.launch(intent);
    }

    private void uploadSong() {
        if (songUri != null) {


            StorageReference storageRef = storage.getReference().child("songs/" + System.currentTimeMillis() + ".mp3");
            String finalTitle = texttitle;
            String finalArtist = textsubtitle;
            storageRef.putFile(songUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String songUrl = uri.toString();
                            String songcover = "https://firebasestorage.googleapis.com/v0/b/rhythm-80f02.appspot.com/o/sectionimg%2Fvecteezy_music-party-disco-flyer-with-exceptional-glow-of-lights_.jpg?alt=media&token=dc24e5bc-1c4d-4034-842b-dce44de7762d";
                            saveSongMetadata(finalTitle, finalArtist, songUrl, songcover);
                        });
                    })
                    .addOnFailureListener(e -> Toast.makeText(mymusic_activity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveSongMetadata(String title, String subtitle, String url,String coverurl) {
        DatabaseReference dbRef = database.getReference("songs");
        String id = dbRef.push().getKey();
        song_model song = new song_model(id,title,url,coverurl,subtitle);
        dbRef.child(id).setValue(song)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(mymusic_activity.this, "Song uploaded successfully", Toast.LENGTH_SHORT).show();
                    fetchSongs();
                })
                .addOnFailureListener(e -> Toast.makeText(mymusic_activity.this, "Failed to upload metadata: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void fetchSongs() {
        DatabaseReference dbRef = database.getReference("songs");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                songList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    song_model song = postSnapshot.getValue(song_model.class);
                    if (song != null) {
                        songList.add(song);
                    }
                }
                songAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(mymusic_activity.this, "Failed to load songs", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
