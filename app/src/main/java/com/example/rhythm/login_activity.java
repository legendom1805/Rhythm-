package com.example.rhythm;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.rhythm.home.home_activity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class login_activity extends AppCompatActivity {
    FirebaseAuth mAuth;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent logint = new Intent(getApplicationContext(), home_activity.class);
            startActivity(logint);

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);




            mAuth = FirebaseAuth.getInstance();
            TextInputEditText emaillogin,passwordlogin;
            Button login;
            emaillogin = findViewById(R.id.emaillogin);
            passwordlogin = findViewById(R.id.passwordlogin);
            login = findViewById(R.id.loginbtn);


            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String emaillogintext, passwordlogintext;

                    emaillogintext = String.valueOf(emaillogin.getText());
                    passwordlogintext = String.valueOf(passwordlogin.getText());

                    if (TextUtils.isEmpty(emaillogintext)) {
                        Toast.makeText(login_activity.this, "Enter Email", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (TextUtils.isEmpty(passwordlogintext)) {
                        Toast.makeText(login_activity.this, "Enter Password", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    mAuth.signInWithEmailAndPassword(emaillogintext, passwordlogintext)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Toast.makeText(login_activity.this, "Login Succesful", Toast.LENGTH_SHORT).show();
                                        Intent logint = new Intent(getApplicationContext(), home_activity.class);
                                        startActivity(logint);


                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Toast.makeText(login_activity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                }
            });


            TextView text;
            text = findViewById(R.id.textView2);
            text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent signup;
                    signup = new Intent(login_activity.this, signup_activity.class);
                    startActivity(signup);

                }
            });



        }
    }