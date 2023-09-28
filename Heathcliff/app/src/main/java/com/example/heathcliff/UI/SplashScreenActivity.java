package com.example.heathcliff.UI;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.example.heathcliff.R;
import com.example.heathcliff.model.Suspension;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

public class SplashScreenActivity extends AppCompatActivity {
    private final int SPLASH_DISPLAY_LENGTH = 3000;
    FirebaseDatabase database;
    DatabaseReference adoptersReference;
    boolean loginFail = false;
    DatabaseReference suspensionReference;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        database = FirebaseDatabase.getInstance();
        adoptersReference = database.getReference("Adopters");
        suspensionReference = FirebaseDatabase.getInstance().getReference("Suspension");
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user.isEmailVerified()) {
                        adoptersReference.orderByChild("userID").equalTo(user.getUid()).addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                Intent i = new Intent(SplashScreenActivity.this, LandingPage.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                loginFail = true;
                                startActivity(i);
                                SplashScreenActivity.this.finish();
                            }

                            @Override
                            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                            }

                            @Override
                            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                            }

                            @Override
                            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
                    } else {
                        user.sendEmailVerification();
                        loginFail = true;
                        Toast.makeText(SplashScreenActivity.this, "Your account must be verified. Please check your email for a registration link", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                        Log.d("Signed out", "onAuthStateChanged:signed_out");
                        SplashScreenActivity.this.finish();
                    }
                    Handler handler1 = new Handler();
                    handler1.postDelayed(new Runnable() {
                        public void run() {
                if (loginFail != true) {
                    Intent i = new Intent(SplashScreenActivity.this, CreateProfile.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    SplashScreenActivity.this.finish();
                }
                        }
                    }, 1000);
            } else {
                    // User is signed out
                    startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                    Log.d("Signed out", "onAuthStateChanged:signed_out");
                    SplashScreenActivity.this.finish();
                }
            }
        }, SPLASH_DISPLAY_LENGTH); }
    }
