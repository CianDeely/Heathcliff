package com.example.heathcliff.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.heathcliff.R;
import com.example.heathcliff.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterUser extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;
    private ImageView banner;
    private EditText editTextEmailSignup, editTextPasswordSignup, editTextConfirmPasswordSignup,
            editTextFirstNameSignup, editTextLastNameSignup;
    private Button signupButton;
    private ProgressBar progressBar;
    boolean active = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);
        mAuth = FirebaseAuth.getInstance();
        banner = (ImageView) findViewById(R.id.imageHeathcliffBannerCreateProfile);
        banner.setOnClickListener(this);
        progressBar = (ProgressBar) findViewById(R.id.progressBarRegister);
        signupButton = (Button) findViewById(R.id.buttonRegister);
        signupButton.setOnClickListener(this);
        editTextEmailSignup = (EditText) findViewById(R.id.editTextEmailSignup);
        editTextPasswordSignup = (EditText) findViewById(R.id.editTextPasswordSignup);
        editTextConfirmPasswordSignup = (EditText) findViewById(R.id.editTextConfirmPassword);
        editTextFirstNameSignup = (EditText) findViewById(R.id.editTextFirstName);
        editTextLastNameSignup = (EditText) findViewById(R.id.editTextLastName);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageHeathcliffBannerCreateProfile:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.buttonRegister:
                 registerUser();
                break;
        }

    }

    public void registerUser() {
        String email = editTextEmailSignup.getText().toString().trim();
        String password = editTextPasswordSignup.getText().toString().trim();
        String confirmPassword = editTextConfirmPasswordSignup.getText().toString().trim();
        String firstName = editTextFirstNameSignup.getText().toString().trim();
        String lastName = editTextLastNameSignup.getText().toString().trim();
        if (firstName.isEmpty()) {
            editTextFirstNameSignup.setError("First name is required");
            editTextFirstNameSignup.requestFocus();
            return;
        }
        if (lastName.isEmpty()) {
            editTextLastNameSignup.setError("Last name is required");
            editTextLastNameSignup.requestFocus();
            return;
        }
        if (email.isEmpty()) {
            editTextEmailSignup.setError("Email address is required");
            editTextEmailSignup.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmailSignup.setError("Please enter a valid email");
            editTextEmailSignup.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            editTextPasswordSignup.setError("Password is required");
            editTextPasswordSignup.requestFocus();
            return;
        }
        if (!confirmPassword.equals(password)) {
            editTextConfirmPasswordSignup.setError("Passwords must match");
            editTextConfirmPasswordSignup.requestFocus();
            return;
        }
        if (password.length() < 6) {
            editTextPasswordSignup.setError("Password must be atleast 6 characters");
            editTextPasswordSignup.requestFocus();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    User user = new User(firstName, lastName, email, active);
                    FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(RegisterUser.this, "User has been registered successfully!", Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
                            } else {
                                Toast.makeText(RegisterUser.this, "Failed to register an account. Please try again", Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
                } else {
                    Toast.makeText(RegisterUser.this, "Failed to register an account. Please try again", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }
}

