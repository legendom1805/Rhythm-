package com.example.rhythm;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class account_activity extends AppCompatActivity {
    TextView unameaccview, emailaccview;

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

        auth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();

        userId = auth.getCurrentUser().getUid();

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