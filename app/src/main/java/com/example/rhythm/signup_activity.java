package com.example.rhythm;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class signup_activity extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseFirestore fstore;
    String userID;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent logint = new Intent(getApplicationContext(), home_activity.class);
            startActivity(logint);
        }
    }


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        TextInputEditText emailsptext, passwordsptext, usernamesptsext;
        Button signbtn;
        usernamesptsext= findViewById(R.id.usernamesp);
        emailsptext = findViewById(R.id.emailaddresssp);
        passwordsptext = findViewById(R.id.passwordsp);


        signbtn = findViewById(R.id.signupbtn);
        signbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String username,email, password ;
                email = String.valueOf(emailsptext.getText());
                password = String.valueOf(passwordsptext.getText());
                username = String.valueOf(usernamesptsext.getText());


                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(signup_activity.this, "Enter Email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(signup_activity.this, "Enter Password", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(password.length()<6)
                {
                    Toast.makeText(signup_activity.this, "Password must have atleast 6 characters", Toast.LENGTH_SHORT).show();
                    return;
                }



                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(signup_activity.this, "Account Created Successfully", Toast.LENGTH_SHORT).show();
                                    userID = mAuth.getCurrentUser().getUid();
                                    DocumentReference documentReference = fstore.collection("user").document(userID);
                                    Map<String,Object> user= new HashMap<>();
                                    user.put("Username",username);
                                    user.put("Email",email);
                                    documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {

                                            Log.d("TAG","Succes!: User Profile is Created for" + userID);

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d("TAG","Failure:"+e.toString());
                                        }
                                    });

                                    Intent logint = new Intent(getApplicationContext(), login_activity.class);
                                    startActivity(logint);
                                    finish();
                                }
                                else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(signup_activity.this, "Error Occurred!" + task.getException().getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        TextView text;
        text = findViewById(R.id.loginintent);
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent login;
                login = new Intent(signup_activity.this, login_activity.class);
                startActivity(login);
            }
        });
    }
}
