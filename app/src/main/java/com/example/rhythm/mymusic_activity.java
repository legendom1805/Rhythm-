package com.example.rhythm;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.customview.widget.Openable;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.util.ArrayList;
import java.util.List;

public class mymusic_activity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseFirestore fstore;
    TextView emailtext, usernametext;
    String userId;
    TextView textviewimg;
    ProgressBar progressBar;
    Uri audioUri;
    StorageReference mStoragef;
    StorageTask muploadtask;
    DatabaseReference referencesongs;
    String songsCategory;
    MediaMetadataRetriever mediaMetadataRetriever;
    byte[] art;
    String title1, artist1, album_art = "", duration1;
    TextView title, artist, album, duration, dataa;
    ImageView albumart;

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {

                @Override
                public void onActivityResult(ActivityResult activityResult) {

                    int result = activityResult.getResultCode();
                    Intent data = activityResult.getData();

                    if (activityResult.getResultCode() == RESULT_OK && activityResult.getData() != null) {

                        audioUri = data.getData();
                        mediaMetadataRetriever.setDataSource(getApplicationContext(), audioUri);
                        art = mediaMetadataRetriever.getEmbeddedPicture();
                        Bitmap bitmap = BitmapFactory.decodeByteArray(art, 0, art.length);
                        albumart.setImageBitmap(bitmap);
                        album.setText(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM));
                        artist.setText(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
                        dataa.setText(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE));
                        duration.setText(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
                        title.setText(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));

                        artist1 = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                        title1 = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                        duration1 = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

                    }

                }


            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mymusic);


        textviewimg = findViewById(R.id.textsongfileselected);
        progressBar = findViewById(R.id.progressbar);
        title = findViewById(R.id.title);
        artist = findViewById(R.id.artist);
        duration = findViewById(R.id.duration);
        album = findViewById(R.id.album);
        dataa = findViewById(R.id.dataa);
        albumart = findViewById(R.id.imageView);

        mediaMetadataRetriever = new MediaMetadataRetriever();
        referencesongs = FirebaseDatabase.getInstance().getReference().child("songs");
        mStoragef = FirebaseStorage.getInstance().getReference().child("songs");

        Spinner spinner = findViewById(R.id.spinner);

        spinner.setOnItemSelectedListener(this);

        List<String> categories = new ArrayList<>();


        categories.add("Hindi Songs");
        categories.add("Punjabi Songs");
        categories.add("Western Songs");
        categories.add("Kpop Songs");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);


        // Navigation drawer
        auth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView);

        View header = navigationView.getHeaderView(0);

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
                drawerLayout.open();
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


    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        songsCategory = adapterView.getItemAtPosition(i).toString();
        Toast.makeText(this, "Selected:" + songsCategory, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void openAudioFiles(View v) {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("audio/");
        activityResultLauncher.launch(i);

    }

    @SuppressLint("Range")
    private String getFileNAme(Uri uri) {

        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);

            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
            finally {
                cursor.close();
            }
        }

        if(result == null){
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if(cut != -1)
            {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
}


