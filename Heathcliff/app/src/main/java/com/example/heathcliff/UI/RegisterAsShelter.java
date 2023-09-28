package com.example.heathcliff.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.heathcliff.R;
import com.example.heathcliff.model.Pet;
import com.example.heathcliff.model.Picture;
import com.example.heathcliff.model.ShelterRegistrationRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

public class RegisterAsShelter extends AppCompatActivity implements View.OnClickListener {

    FloatingActionButton fabCloseRegisterShelter;
    Button addEvidence, registerShelter;
    EditText editTextShelterName, editTextShelterAddress;
    StorageReference storageReference;
    ImageView documentPreviewImageView;
    private DatabaseReference shelterRegistrationRequestReference;
    FirebaseUser user;
    String accessToken;
    FirebaseStorage storage;
    private final int PICK_IMAGE_REQUEST = 22;
    int SELECT_PICTURE = 200;
    int PERMISSION_ID = 44;
    Uri selectedDocumentUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_as_shelter);
        fabCloseRegisterShelter = findViewById(R.id.fabCloseRegisterShelter);
        addEvidence = findViewById(R.id.buttonSelectEvidenceOfShelter);
        registerShelter = findViewById(R.id.buttonRegisterShelter);
        editTextShelterName = findViewById(R.id.editTextShelterName);
        editTextShelterAddress = findViewById(R.id.editTextShelterAddress);
        user = FirebaseAuth.getInstance().getCurrentUser();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        documentPreviewImageView = findViewById(R.id.imageShelterDocumentationPreview);
        addEvidence.setOnClickListener(this);
        fabCloseRegisterShelter.setOnClickListener(this);
        registerShelter.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.fabCloseRegisterShelter:
                startActivity(new Intent(RegisterAsShelter.this, LandingPage.class));
                break;
            case R.id.buttonRegisterShelter:
                createShelterRequest();
                break;
            case R.id.buttonSelectEvidenceOfShelter:
                documentChooser();
                break;
        }
    }
    public void createShelterRequest() {
        String shelterName = editTextShelterName.getText().toString().trim();
        String documentURI = selectedDocumentUri.toString();
        if (shelterName.isEmpty()) {
            editTextShelterName.setError("Please enter the name of the shelter");
            editTextShelterName.requestFocus();
            return;
        }
        if (documentURI.isEmpty()) {
            Toast.makeText(RegisterAsShelter.this, "Please add a document proving you work for this shelter", Toast.LENGTH_LONG).show();
            return;
        }
        finalizeShelterRequest();
    }
    void documentChooser() {
        Intent intent = new Intent();
            intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Document"), PICK_IMAGE_REQUEST);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST) {
                selectedDocumentUri = data.getData();
                if (null != selectedDocumentUri) {
                        documentPreviewImageView.setImageResource(R.drawable.baseline_receipt_black_48);
                }
            }
        }
    }

    private void finalizeShelterRequest()
    {
        if (selectedDocumentUri != null) {

            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading Document...");
            progressDialog.show();
            StorageReference ref = storageReference.child("documents/" + user.getUid() +"/" + UUID.randomUUID().toString());
            ref.putFile(selectedDocumentUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                                {
                                    Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(@NonNull Uri uri) {
                                            accessToken = uri.toString();
                                            String shelterName = editTextShelterName.getText().toString().trim();
                                            String shelterAddress = editTextShelterAddress.getText().toString();
                                            String userID = user.getUid();
                                            ShelterRegistrationRequest request = new ShelterRegistrationRequest(userID, shelterName, shelterAddress, accessToken, false);
                                            FirebaseDatabase.getInstance().getReference("ShelterRegistrationRequest").push().setValue(request).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(RegisterAsShelter.this, "Shelter request added successfully", Toast.LENGTH_LONG).show();
                                                        startActivity(new Intent(RegisterAsShelter.this, LandingPage.class));
                                                    } else {
                                                        Toast.makeText(RegisterAsShelter.this, "Failed to create shelter request, please try again", Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });
                                        }
                                    });
                                    progressDialog.dismiss();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                            progressDialog.dismiss();
                            Toast.makeText(RegisterAsShelter.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot)
                                {
                                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());progressDialog.setMessage("Uploaded " + (int)progress + "%");
                                }
                            });
        }
    }


}