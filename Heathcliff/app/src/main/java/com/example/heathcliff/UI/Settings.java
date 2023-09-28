package com.example.heathcliff.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.heathcliff.R;
import com.example.heathcliff.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

public class Settings extends AppCompatActivity implements View.OnClickListener {
    public static final String ACTION_WIRELESS_SETTINGS = "";
    EditText editTextEmail, editTextCurrentPassword, editTextNewPassword, editTextConfirmNewPassword, editTextCurrentEmail;
    Button buttonUpdateCredentials, buttonDeactivateAccount;
    private FirebaseUser user;
    FloatingActionButton fabDone;
    private FirebaseAuth mAuth;
    DatabaseReference dbReference;
    FirebaseDatabase database;
    DatabaseReference dbAdoptersReference;
    DatabaseReference dbPicturesReference;
    DatabaseReference dbLocationsReference;
    DatabaseReference deleteReference;
    FirebaseStorage storage;
    StorageReference storageRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        storage = FirebaseStorage.getInstance();
        editTextEmail = findViewById(R.id.editTextEmailSettings);
        editTextCurrentPassword = findViewById(R.id.editTextCurrentPassword);
        editTextNewPassword = findViewById(R.id.editTextNewPassword);
        editTextConfirmNewPassword = findViewById(R.id.editTextConfirmNewPassword);
        buttonUpdateCredentials = findViewById(R.id.buttonUpdateCredentials);
        buttonDeactivateAccount = findViewById(R.id.buttonDeactivateAccount);
        editTextCurrentEmail = findViewById(R.id.editTextCurrentEmail);
        database = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        fabDone = findViewById(R.id.buttonEditProfile);
        fabDone.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();
        editTextCurrentEmail.addTextChangedListener(listener);
        editTextCurrentPassword.addTextChangedListener(listener);
        buttonUpdateCredentials.setOnClickListener(this);
        buttonDeactivateAccount.setOnClickListener(this);
        dbReference = database.getReference("Users");
        dbAdoptersReference = database.getReference("Adopters");
        dbPicturesReference = database.getReference("Pictures");
        dbLocationsReference = database.getReference("UserLocations");
    }

    private final TextWatcher listener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String email = editTextCurrentEmail.getText().toString().trim();
            String password = editTextCurrentPassword.getText().toString().trim();
            if (!email.isEmpty() && !password.isEmpty()) {
                buttonUpdateCredentials.setEnabled(true);
                buttonDeactivateAccount.setEnabled(true);
            } else if (email.isEmpty() || password.isEmpty()) {
                buttonUpdateCredentials.setEnabled(false);
                buttonDeactivateAccount.setEnabled(false);
            }
        }
        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
    @Override
    public void onClick(View view) {
        String email = editTextCurrentEmail.getText().toString().trim();
        String password = editTextCurrentPassword.getText().toString().trim();
        String newEmail = editTextEmail.getText().toString().trim();
        String newPassword = editTextNewPassword.getText().toString().trim();
        String confirmNewPassword = editTextConfirmNewPassword.getText().toString().trim();
        storageRef = FirebaseStorage.getInstance().getReference().child("images/"+user.getUid());
        switch (view.getId()) {
            case R.id.buttonUpdateCredentials:
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if (!newEmail.isEmpty()) {
                                if (!Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
                                    editTextEmail.setError("Please enter a valid email address");
                                    editTextEmail.requestFocus();
                                    return;
                                } else {
                                    user.updateEmail(newEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                deleteReference = dbReference.child(user.getUid());
                                                deleteReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        User account = snapshot.getValue(User.class);
                                                        account.setEmail(newEmail);
                                                        deleteReference.setValue(account);
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });

                                                Toast.makeText(Settings.this, "Your account's email has been changed successfully", Toast.LENGTH_LONG).show();
                                            } else {
                                                Toast.makeText(Settings.this, "Failed to change your account's email, please try again", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                }
                            }
                            if (newPassword.isEmpty()) {
                                return;
                            } else {
                                if (confirmNewPassword.isEmpty()) {
                                    editTextConfirmNewPassword.setError("Please confirm your new password");
                                    editTextConfirmNewPassword.requestFocus();
                                    return;
                                }
                                if (newPassword.length() < 6) {
                                    editTextNewPassword.setError("Please enter a password that is greater than 6 characters");
                                    editTextNewPassword.requestFocus();
                                    return;
                                }
                                if (!newPassword.equals(confirmNewPassword)) {
                                    editTextConfirmNewPassword.setError("Both passwords must match");
                                    editTextConfirmNewPassword.requestFocus();
                                    return;
                                }
                                user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(Settings.this, "Your account's password has been changed successfully", Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(Settings.this, "Failed to change your password, please try again", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }
                        } else {
                            editTextCurrentEmail.setError("Please enter your account credentials to make changes to your account");
                            editTextCurrentEmail.requestFocus();
                            return;
                        }
                    }
                });
                break;
            case R.id.buttonEditProfile:
                startActivity(new Intent(this, LandingPage.class));
                break;
            case R.id.buttonDeactivateAccount:
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            new AlertDialog.Builder(Settings.this).setTitle("Delete Account").setMessage("Are you sure you want to delete your account? This action cannot be reversed").setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dbReference.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if(snapshot.hasChildren()){
                                                        deleteReference = dbReference;
                                                        deleteReference.child(user.getUid()).removeValue();
                                                    }
                                                }
                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                            dbAdoptersReference.orderByChild("userID").equalTo(user.getUid()).addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    for(DataSnapshot ds : snapshot.getChildren()){
                                                        deleteReference = dbAdoptersReference;
                                                        deleteReference.child(ds.getKey()).removeValue();
                                                    }
                                                }
                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                            dbPicturesReference.orderByChild("userID").equalTo(user.getUid()).addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                   for(DataSnapshot ds : snapshot.getChildren()){
                                                       deleteReference = dbPicturesReference;
                                                       deleteReference.child(ds.getKey()).removeValue();
                                                   }
                                                }
                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                            dbLocationsReference.orderByChild("userID").equalTo(user.getUid()).addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    for(DataSnapshot ds : snapshot.getChildren()){
                                                        deleteReference = dbLocationsReference;
                                                        deleteReference.child(ds.getKey()).removeValue();
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                            storageRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                                                @Override
                                                public void onSuccess(@NonNull ListResult listResult) {
                                                    for(StorageReference fileRef : listResult.getItems()){
                                                        fileRef.delete();
                                                    }
                                                }
                                            });
                                            user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        Toast.makeText(Settings.this, "Your account has been deleted successfully", Toast.LENGTH_LONG).show();
                                                        FirebaseAuth.getInstance().signOut();
                                                        startActivity(new Intent(Settings.this, MainActivity.class));
                                                    }
                                                }
                                            });
                                        }
                                    }).setNegativeButton(android.R.string.no, null).setIcon(android.R.drawable.ic_dialog_alert).show();
                        }
                        else{
                            editTextCurrentEmail.setError("Please enter your current account credentials before attempting to deactivate your acccount");
                            editTextCurrentEmail.requestFocus();
                            return;
                        }
                    }
                });

        }
    }
}