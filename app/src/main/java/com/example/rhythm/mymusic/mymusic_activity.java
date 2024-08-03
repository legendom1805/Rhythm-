package com.example.rhythm.mymusic;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.rhythm.R;
import com.example.rhythm.account_activity;
import com.example.rhythm.home.home_activity;
import com.example.rhythm.login_activity;
import com.example.rhythm.myexoplayer;
import com.example.rhythm.player_activity;
import com.example.rhythm.search.search_activity;
import com.example.rhythm.search.song_model;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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
    private TextInputEditText txttitle, txtsubtitle;
    String texttitle, textsubtitle;
    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseFirestore fstore;
    TextView emailtext, usernametext;
    String userId;
    ExoPlayer exoPlayer;
    TextView songTitle, songSubTitile;
    ImageView songimg,userimg;
    PlayerView playerView;

    private FirebaseStorage storage;
    private FirebaseDatabase database;

    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    songUri = result.getData().getData();
                    showInputDialog();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mymusic);

        NavigationView navigationView = findViewById(R.id.navigationView);

        View header = navigationView.getHeaderView(0);
        auth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();

        emailtext = header.findViewById(R.id.emailview2);
        usernametext = header.findViewById(R.id.usernameview);
        userId = auth.getCurrentUser().getUid();
        DocumentReference documentReference = fstore.collection("user").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot != null) {
                    emailtext.setText(documentSnapshot.getString("Email"));
                    usernametext.setText(documentSnapshot.getString("Username"));
                }
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
                    Glide.with(mymusic_activity.this).load(imageUrl).circleCrop().into(userimg);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(mymusic_activity.this, "Failed to load profile image", Toast.LENGTH_SHORT).show();
            }
        });

        // Navigation Drawer
        DrawerLayout drawerLayout = findViewById(R.id.drawerlayout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        ImageButton drawernavtoggle = findViewById(R.id.drawernavtoggle);
        ImageButton acctoggle = findViewById(R.id.accounttogglle);
        setSupportActionBar(toolbar);

        acctoggle.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), account_activity.class)));

        drawernavtoggle.setOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));

        navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.accountnav) {
                startActivity(new Intent(getApplicationContext(), account_activity.class));
            } else if (item.getItemId() == R.id.logoutnav) {
                FirebaseAuth.getInstance().signOut();
                Intent logint = new Intent(mymusic_activity.this, login_activity.class);
                startActivity(logint);
                finish();
            }

            drawerLayout.closeDrawer(GravityCompat.START);

            return true;
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

        LinearLayout bottomplayer = findViewById(R.id.player_fragment);
        bottomplayer.setOnClickListener(view -> startActivity(new Intent(mymusic_activity.this, player_activity.class)));

        // Bottom navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.mymusic);

        bottomNavigationView.setOnItemSelectedListener(item -> {
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

        uploadButton.setOnClickListener(view -> openFileChooser());

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

    private void showInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Song Details");

        View viewInflated = getLayoutInflater().inflate(R.layout.dialog_song_details, null);
        final TextInputEditText inputTitle = viewInflated.findViewById(R.id.textinputtitle);
        final TextInputEditText inputSubtitle = viewInflated.findViewById(R.id.textinputsubtitle);

        builder.setView(viewInflated);

        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
            texttitle = inputTitle.getText().toString();
            textsubtitle = inputSubtitle.getText().toString();

            if (TextUtils.isEmpty(texttitle)) {
                Toast.makeText(mymusic_activity.this, "Enter Song Name", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(textsubtitle)) {
                Toast.makeText(mymusic_activity.this, "Enter Song Subtitle or Artist", Toast.LENGTH_SHORT).show();
            } else {
                uploadSong();
            }
        });

        builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void uploadSong() {
        if (songUri != null) {
            StorageReference storageRef = storage.getReference().child("songs/" + userId + "/" + System.currentTimeMillis() + ".mp3");
            String finalTitle = texttitle;
            String finalArtist = textsubtitle;
            storageRef.putFile(songUri)
                    .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String songUrl = uri.toString();
                        String songcover = "https://firebasestorage.googleapis.com/v0/b/rhythm-80f02.appspot.com/o/sectionimg%2Fvecteezy_music-party-disco-flyer-with-exceptional-glow-of-lights_.jpg?alt=media&token=dc24e5bc-1c4d-4034-842b-dce44de7762d";
                        saveSongMetadata(finalTitle, finalArtist, songUrl, songcover);
                    }))
                    .addOnFailureListener(e -> Toast.makeText(mymusic_activity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveSongMetadata(String title, String subtitle, String url, String coverurl) {
        DatabaseReference dbRef = database.getReference("songs").child(userId);
        String id = dbRef.push().getKey();
        String lowertitle = texttitle.toLowerCase();
        song_model song = new song_model(id, title, url, coverurl, subtitle, lowertitle);
        dbRef.child(id).setValue(song)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(mymusic_activity.this, "Song uploaded successfully", Toast.LENGTH_SHORT).show();
                    fetchSongs();
                })
                .addOnFailureListener(e -> Toast.makeText(mymusic_activity.this, "Failed to upload metadata: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void fetchSongs() {
        DatabaseReference dbRef = database.getReference("songs").child(userId);
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
