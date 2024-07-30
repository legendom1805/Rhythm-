package com.example.rhythm;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.rhythm.home.home_activity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class account_activity extends AppCompatActivity {
    TextView unameaccview, emailaccview;
    Button logoutbtn;
    ImageButton backbtn;

    FirebaseAuth auth;
    FirebaseFirestore fstore;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_account);
        unameaccview = findViewById(R.id.usernameacc);
        emailaccview = findViewById(R.id.emailacc);
        logoutbtn = findViewById(R.id.logoutbtn);
        backbtn = findViewById(R.id.backbtn);

        auth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        userId = auth.getCurrentUser().getUid();

        logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent logint = new Intent(getApplicationContext(), login_activity.class);
                startActivity(logint);
                finish();
            }
        });

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        DocumentReference documentReference = fstore.collection("user").document(userId);
        documentReference.addSnapshotListener(account_activity.this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                emailaccview.setText(documentSnapshot.getString("Email"));
                unameaccview.setText(documentSnapshot.getString("Username"));
            }
        });
    }
}