package com.example.rhythm;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class home_activity extends AppCompatActivity {
    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseFirestore fstore;
    TextView emailtext,usernametext;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        auth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView);

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

                if(item.getItemId()==R.id.account)
                {
                    startActivity(new Intent(getApplicationContext(), account_activity.class));
                }
                else if (item.getItemId()==R.id.logout) {
                    FirebaseAuth.getInstance().signOut();
                    Intent logint = new Intent(home_activity.this, login_activity.class);
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

    }
}