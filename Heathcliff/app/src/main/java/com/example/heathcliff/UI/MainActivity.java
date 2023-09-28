package com.example.heathcliff.UI;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.heathcliff.R;
import com.example.heathcliff.model.Adopter;
import com.example.heathcliff.model.Suspension;
import com.example.heathcliff.model.User;
import com.example.heathcliff.parser.NdefMessageParser;
import com.example.heathcliff.record.ParsedNdefRecord;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView register, resetPassword;
    private EditText editTextEmail, editTextPassword;
    private Button signIn;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    FirebaseDatabase database;
    DatabaseReference adoptersReference;
    DatabaseReference suspensionReference;
    DatabaseReference usersReference;
    boolean loginFail = false;
    boolean movedToLandingPage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        suspensionReference = FirebaseDatabase.getInstance().getReference("Suspension");
        usersReference = FirebaseDatabase.getInstance().getReference("Users");
        register = (TextView) findViewById(R.id.textSignUp);
        register.setOnClickListener(this);
        resetPassword = (TextView) findViewById(R.id.textResetPassword);
        resetPassword.setOnClickListener(this);
        signIn = (Button) findViewById(R.id.buttonSignIn);
        signIn.setOnClickListener(this);
        editTextEmail = (EditText) findViewById(R.id.editTextResetPasswordEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        adoptersReference = database.getReference("Adopters");
        TextView signUp = (TextView) findViewById(R.id.textSignUp);
        String signUpText = "New here? Sign-up";
        SpannableString signupSpannable = new SpannableString(signUpText);
        ForegroundColorSpan fcsOrange = new ForegroundColorSpan(Color.parseColor("#FF5722"));
        signupSpannable.setSpan(fcsOrange, 10, 17, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        signupSpannable.setSpan(new UnderlineSpan(), 10, signupSpannable.length(), 0);
        ClickableSpan signupClickable = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {

            }
        };
        signupSpannable.setSpan(signupClickable, 10, 17, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        signUp.setText(signupSpannable);
        signUp.setMovementMethod(LinkMovementMethod.getInstance());
        TextView forgotPassword = (TextView) findViewById(R.id.textResetPassword);
        String forgotPasswordText = "Forgot Password";
        SpannableString forgotPasswordSpannable = new SpannableString(forgotPasswordText);
        forgotPasswordSpannable = new SpannableString(forgotPasswordText);
        forgotPasswordSpannable.setSpan(new UnderlineSpan(), 0, forgotPasswordSpannable.length(), 0);
        ClickableSpan forgotPasswordClickable = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {

            }
        };
        forgotPasswordSpannable.setSpan(forgotPasswordClickable, 0, forgotPasswordSpannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        forgotPassword.setText(forgotPasswordSpannable);
        forgotPassword.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.textSignUp:
                startActivity(new Intent(this, RegisterUser.class));
                break;
            case R.id.buttonSignIn:
                userLogin();
                break;
            case R.id.textResetPassword:
                startActivity(new Intent(this, ResetPassword.class));
                break;
        }
    }



    private void userLogin(){
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        if (email.isEmpty()){
            editTextEmail.setError("Please enter an email");
            editTextEmail.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextEmail.setError("Please enter a valid email");
            editTextEmail.requestFocus();
            return;
        }
        if(password.isEmpty()){
            editTextPassword.setError("Please enter a password");
            editTextPassword.requestFocus();
            return;
        }
        if(password.length()<6){
            editTextPassword.setError("Password must be atleast 6 characters in length");
            editTextPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);


        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    if(user.isEmailVerified()){
                        adoptersReference.orderByChild("userID").equalTo(user.getUid()).addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                                if (loginFail != true){
                                    movedToLandingPage = true;
                                    startActivity(new Intent(MainActivity.this, LandingPage.class));
                                    finish();
                                }
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
                    }else{
                        user.sendEmailVerification();
                        loginFail = true;
                        Toast.makeText(MainActivity.this, "Your account must be verified. Please check your email for a registration link", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(MainActivity.this, "Failed to login! Please check your credentials", Toast.LENGTH_LONG).show();
                    loginFail = true;
                }
                Handler handler1 = new Handler();
                handler1.postDelayed(new Runnable() {
                    public void run() {
                        if(loginFail!=true && movedToLandingPage!=true) {
                            startActivity(new Intent(MainActivity.this, CreateProfile.class));
                            finish();
                        }
                    }
                }, 1000);
            }

        });
                }
            }







