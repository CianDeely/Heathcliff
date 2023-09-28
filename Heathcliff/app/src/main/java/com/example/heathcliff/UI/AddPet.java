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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.heathcliff.R;
import com.example.heathcliff.model.Pet;
import com.example.heathcliff.model.Picture;
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

public class AddPet extends AppCompatActivity implements View.OnClickListener {
    SeekBar ageSeek;
    TextView ageSeekProgress;
    Spinner typeSpinner;
    Spinner breedSpinner;
    Spinner coatSpinner;
    Button createPet;
    Button addPetProfilePicture;
    ImageView petProfilePicturePreview;
    Uri selectedImageUri;
    FirebaseStorage storage;
    FloatingActionButton fabAddPetsDone;
    StorageReference storageReference;
    private final int PICK_IMAGE_REQUEST = 22;
    int SELECT_PICTURE = 200;
    int PERMISSION_ID = 44;
    String userID;
    FirebaseUser user;
    String accessToken;
    EditText petName, petDescription;
    private DatabaseReference petsReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pet);
        petDescription = findViewById(R.id.editTextShelterAddress);
        fabAddPetsDone = (FloatingActionButton) findViewById(R.id.fabCloseRegisterShelter);
        fabAddPetsDone.setOnClickListener(this);
        petsReference = FirebaseDatabase.getInstance().getReference("Pets");
        petName = (EditText) findViewById(R.id.editTextShelterName);
        user = FirebaseAuth.getInstance().getCurrentUser();
        addPetProfilePicture = (Button) findViewById(R.id.buttonSelectEvidenceOfShelter);
        petProfilePicturePreview = (ImageView) findViewById(R.id.imageShelterDocumentationPreview);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        createPet = (Button) findViewById(R.id.buttonRegisterShelter);
        createPet.setOnClickListener(this);
        addPetProfilePicture.setOnClickListener(this);
        ageSeekProgress = findViewById(R.id.textViewAgeProgress);
        ageSeek = (SeekBar) findViewById(R.id.ageSeekBarPreferences);
        breedSpinner = (Spinner) findViewById(R.id.spinnerBreed);
        coatSpinner = (Spinner) findViewById(R.id.spinnerCoat);
        typeSpinner = (Spinner) findViewById(R.id.spinnerType);
        userID = user.getUid();
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String type = typeSpinner.getSelectedItem().toString();
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(AddPet.this, R.array.dog_breeds_array, android.R.layout.simple_spinner_item);
                ArrayAdapter<CharSequence> coatAdapter = ArrayAdapter.createFromResource(AddPet.this, R.array.dog_coats_array, android.R.layout.simple_spinner_item);

                if(type.equals("Type")){
                    breedSpinner.setEnabled(false);
                    breedSpinner.setClickable(false);
                    coatSpinner.setEnabled(false);
                    coatSpinner.setClickable(false);
                }
               else if(type.equals("Dog")) {
                    adapter = ArrayAdapter.createFromResource(AddPet.this, R.array.dog_breeds_array, android.R.layout.simple_spinner_item);
                    coatAdapter = ArrayAdapter.createFromResource(AddPet.this, R.array.dog_coats_array, android.R.layout.simple_spinner_item);
                    breedSpinner.setEnabled(true);
                    breedSpinner.setClickable(true);
                    coatSpinner.setEnabled(true);
                    coatSpinner.setClickable(true);
                }
                else if(type.equals("Cat")){
                    adapter = ArrayAdapter.createFromResource(AddPet.this, R.array.cat_breeds_array, android.R.layout.simple_spinner_item);
                    coatAdapter = ArrayAdapter.createFromResource(AddPet.this, R.array.cat_coats_array, android.R.layout.simple_spinner_item);
                    breedSpinner.setEnabled(true);
                    breedSpinner.setClickable(true);
                    coatSpinner.setEnabled(true);
                    coatSpinner.setClickable(true);
                }
                else if(type.equals("Bird")){
                    adapter = ArrayAdapter.createFromResource(AddPet.this, R.array.bird_breeds_array, android.R.layout.simple_spinner_item);
                    coatAdapter = ArrayAdapter.createFromResource(AddPet.this, R.array.bird_coats_array, android.R.layout.simple_spinner_item);
                    breedSpinner.setEnabled(true);
                    breedSpinner.setClickable(true);
                    coatSpinner.setEnabled(true);
                    coatSpinner.setClickable(true);
                }
                else if(type.equals("Rodent")){
                    adapter = ArrayAdapter.createFromResource(AddPet.this, R.array.rodent_breeds_array, android.R.layout.simple_spinner_item);
                    coatAdapter = ArrayAdapter.createFromResource(AddPet.this, R.array.rodent_coats_array, android.R.layout.simple_spinner_item);
                    breedSpinner.setEnabled(true);
                    breedSpinner.setClickable(true);
                    coatSpinner.setEnabled(true);
                    coatSpinner.setClickable(true);
                }
                else if(type.equals("Reptile")){
                    adapter = ArrayAdapter.createFromResource(AddPet.this, R.array.reptiles_breeds_array, android.R.layout.simple_spinner_item);
                    coatAdapter = ArrayAdapter.createFromResource(AddPet.this, R.array.reptile_coats_array, android.R.layout.simple_spinner_item);
                    breedSpinner.setEnabled(true);
                    breedSpinner.setClickable(true);
                    coatSpinner.setEnabled(true);
                    coatSpinner.setClickable(true);
                }
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                breedSpinner.setAdapter(adapter);
                coatAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                coatSpinner.setAdapter(coatAdapter);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });


        ageSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int val = (progress * (seekBar.getWidth() - 2 * seekBar.getThumbOffset())) / seekBar.getMax();
                ageSeekProgress.setText("" + progress + " Years");
                ageSeekProgress.setX(seekBar.getX() + val + seekBar.getThumbOffset() / 2);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                ageSeekProgress.setVisibility(View.VISIBLE);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                ageSeekProgress.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.buttonRegisterShelter:
                createNewProfile();
                break;
            case R.id.buttonSelectEvidenceOfShelter:
                imageChooser();
                break;
            case R.id.fabCloseRegisterShelter:
                startActivity(new Intent(AddPet.this, Pets.class));
        }
    }
    void imageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST) {
                selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                        petProfilePicturePreview.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    public void createNewProfile() {
        String name = petName.getText().toString().trim();
        String textAge = ageSeekProgress.getText().toString();
        textAge = textAge.replace("Years", "").trim();
        int age = Integer.parseInt(textAge);
        String type = typeSpinner.getSelectedItem().toString();
        String breed = breedSpinner.getSelectedItem().toString();
        String coat = coatSpinner.getSelectedItem().toString();
        String description = petDescription.getText().toString().trim();
        Drawable petProfilePicture;
        String profileImageURI = petProfilePicturePreview.toString();
        userID = user.getUid();

        if (name.isEmpty()) {
            petName.setError("Please enter the name of the pet");
            petName.requestFocus();
            return;
        }
        if (profileImageURI.isEmpty()) {
            Toast.makeText(AddPet.this, "Please add a photo of this pet", Toast.LENGTH_LONG).show();
            return;
        }
                Pet pet = new Pet(name, age, breed, coat, type, "None", userID, description);
                FirebaseDatabase.getInstance().getReference("Pets").push().setValue(pet).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            uploadImage();
                            Toast.makeText(AddPet.this, "Pet added successfully", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(AddPet.this, Pets.class));
                        } else {
                            Toast.makeText(AddPet.this, "Failed to add pet, please try again", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }



    private void uploadImage()
    {
        if (selectedImageUri != null) {
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading Image...");
            progressDialog.show();
            StorageReference ref = storageReference.child("images/" + userID +"/" + UUID.randomUUID().toString());
            ref.putFile(selectedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                                {
                                    Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                    @Override
                                                                    public void onSuccess(@NonNull Uri uri) {
                                                                        String name = petName.getText().toString().trim();
                                                                        accessToken = uri.toString();
                                                                        petsReference.orderByChild("userID").equalTo(userID).addValueEventListener(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                for (DataSnapshot ds : snapshot.getChildren()) {
                                                                                    Pet pet = ds.getValue(Pet.class);
                                                                                    if (pet.getName().equals(name)) {
                                                                                        Picture picture = new Picture(userID, accessToken, "Pet", 0, true, ds.getKey());
                                                                                        FirebaseDatabase.getInstance().getReference("Pictures").push().setValue(picture);
                                                                                    }
                                                                                }
                                                                            }
                                                                            @Override
                                                                            public void onCancelled(@NonNull DatabaseError error) {

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
                            Toast.makeText(AddPet.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(
                            new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot)
                                {
                                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());progressDialog.setMessage("Uploaded " + (int)progress + "%");
                                }
                            });
        }
    }
}
