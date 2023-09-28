package com.example.heathcliff.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.heathcliff.R;
import com.example.heathcliff.model.Pet;
import com.example.heathcliff.model.Picture;
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
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class EditPet extends AppCompatActivity implements View.OnClickListener {

    String petID;
    private DatabaseReference petsReference;
    FloatingActionButton editPetDetails;
    Uri selectedImageUri;
    private DatabaseReference dbPicturesReference;
    ImageView petProfilePicture, imageViewPetPicture2, imageViewPetPicture3, imageViewPetPicture4, imageViewPetPicture5, imageViewPetPicture6;
    FloatingActionButton fabPic1Delete, fabPic2Delete, fabPic3Delete, fabPic4Delete, fabPic5Delete, fabPic6Delete, fabAddPic1, fabAddPic2, fabAddPic3, fabAddPic4, fabAddPic5, fabAddPic6, fabDeletePet;
    private final int PICK_IMAGE_REQUEST = 22;
    StorageReference storageReference;
    FirebaseStorage storage;
    String userID;
    FirebaseUser user;
    Uri image1UriPath, image2UriPath, image3UriPath, image4UriPath, image5UriPath, image6UriPath;
    DatabaseReference deleteReference;
    Boolean pic1Changed;
    Boolean pic2Changed;
    Boolean pic3Changed;
    Boolean pic4Changed;
    Boolean pic5Changed;
    Boolean pic6Changed;
    Spinner breedSpinner;
    Spinner coatSpinner;
    EditText petDescription, petName;
    DatabaseReference dbReference;
    DatabaseReference matchReference;
    DatabaseReference swipeReference;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_pet);
        petName = findViewById(R.id.editTextShelterName);
        petDescription = findViewById(R.id.editTextShelterAddress);
        breedSpinner = (Spinner) findViewById(R.id.spinnerBreed);
        coatSpinner = (Spinner) findViewById(R.id.spinnerReportReason);
        database = FirebaseDatabase.getInstance();
        dbReference = database.getReference("Pets");
        pic1Changed = false;
        pic2Changed = false;
        pic3Changed = false;
        pic4Changed = false;
        pic5Changed = false;
        pic6Changed = false;
        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        editPetDetails = findViewById(R.id.buttonEditProfile);
        editPetDetails.setOnClickListener(this);
        fabPic2Delete = findViewById(R.id.fabPic2Delete);
        fabPic3Delete = findViewById(R.id.fabPic3Delete);
        fabPic4Delete = findViewById(R.id.fabPic4Delete);
        fabPic5Delete = findViewById(R.id.fabPic5Delete);
        fabPic6Delete = findViewById(R.id.fabPic6Delete);
        fabPic1Delete = findViewById(R.id.fabPic1Delete);
        fabDeletePet = findViewById(R.id.fabDeletePet);
        fabDeletePet.setOnClickListener(this);
        fabPic1Delete.setOnClickListener(this);
        fabPic2Delete.setOnClickListener(this);
        fabPic3Delete.setOnClickListener(this);
        fabPic4Delete.setOnClickListener(this);
        fabPic5Delete.setOnClickListener(this);
        fabPic6Delete.setOnClickListener(this);
        fabAddPic1 = findViewById(R.id.fabAddPic1);
        fabAddPic1.setOnClickListener(this);
        fabAddPic2 = findViewById(R.id.fabAddPic2);
        fabAddPic2.setOnClickListener(this);
        fabAddPic3 = findViewById(R.id.fabAddPic3);
        fabAddPic3.setOnClickListener(this);
        fabAddPic4 = findViewById(R.id.fabAddPic4);
        fabAddPic4.setOnClickListener(this);
        fabAddPic5 = findViewById(R.id.fabAddPic5);
        fabAddPic5.setOnClickListener(this);
        fabAddPic6 = findViewById(R.id.fabAddPic6);
        fabAddPic6.setOnClickListener(this);
        petProfilePicture = findViewById(R.id.imageViewUserProfilePicture);
        imageViewPetPicture2 = findViewById(R.id.imageViewUserPicture2);
        imageViewPetPicture3 = findViewById(R.id.imageViewUserPicture3);
        imageViewPetPicture4 = findViewById(R.id.imageViewUserPicture4);
        imageViewPetPicture5 = findViewById(R.id.imageViewUserPicture5);
        imageViewPetPicture6 = findViewById(R.id.imageViewUserPicture6);
        dbPicturesReference = FirebaseDatabase.getInstance().getReference().child("Pictures");
        matchReference = FirebaseDatabase.getInstance().getReference().child("Match");
        swipeReference = FirebaseDatabase.getInstance().getReference().child("Swipes");
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            petID = extras.getString("petID");
        }
        petsReference = FirebaseDatabase.getInstance().getReference().child("Pets").child(petID);
        petsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Pet pet = snapshot.getValue(Pet.class);
                String type = pet.getType();
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(EditPet.this, R.array.dog_breeds_array, android.R.layout.simple_spinner_item);
                ArrayAdapter<CharSequence> coatAdapter = ArrayAdapter.createFromResource(EditPet.this, R.array.dog_coats_array, android.R.layout.simple_spinner_item);

                if (type.equals("Dog")) {
                    adapter = ArrayAdapter.createFromResource(EditPet.this, R.array.dog_breeds_array, android.R.layout.simple_spinner_item);
                    coatAdapter = ArrayAdapter.createFromResource(EditPet.this, R.array.dog_coats_array, android.R.layout.simple_spinner_item);
                } else if (type.equals("Cat")) {
                    adapter = ArrayAdapter.createFromResource(EditPet.this, R.array.cat_breeds_array, android.R.layout.simple_spinner_item);
                    coatAdapter = ArrayAdapter.createFromResource(EditPet.this, R.array.cat_coats_array, android.R.layout.simple_spinner_item);
                } else if (type.equals("Bird")) {
                    adapter = ArrayAdapter.createFromResource(EditPet.this, R.array.bird_breeds_array, android.R.layout.simple_spinner_item);
                    coatAdapter = ArrayAdapter.createFromResource(EditPet.this, R.array.bird_coats_array, android.R.layout.simple_spinner_item);
                } else if (type.equals("Rodent")) {
                    adapter = ArrayAdapter.createFromResource(EditPet.this, R.array.rodent_breeds_array, android.R.layout.simple_spinner_item);
                    coatAdapter = ArrayAdapter.createFromResource(EditPet.this, R.array.rodent_coats_array, android.R.layout.simple_spinner_item);
                } else if (type.equals("Reptile")) {
                    adapter = ArrayAdapter.createFromResource(EditPet.this, R.array.reptiles_breeds_array, android.R.layout.simple_spinner_item);
                    coatAdapter = ArrayAdapter.createFromResource(EditPet.this, R.array.reptile_coats_array, android.R.layout.simple_spinner_item);
                }
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                breedSpinner.setAdapter(adapter);
                coatAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                coatSpinner.setAdapter(coatAdapter);
                if (pet.getBreed() != null) {
                    int spinnerPosition = adapter.getPosition(pet.getBreed());
                    breedSpinner.setSelection(spinnerPosition);
                }
                if (pet.getCoat() != null) {
                    int spinnerPosition = coatAdapter.getPosition(pet.getCoat());
                    coatSpinner.setSelection(spinnerPosition);
                }
                petDescription.setText(pet.getDescription());
                petName.setText(pet.getName());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        dbPicturesReference.orderByChild("profileID").equalTo(petID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Picture> images = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Picture picture = ds.getValue(Picture.class);
                    images.add(picture);
                }

                for (Picture pic : images) {
                    if (pic.isProfilePicture() == true) {
                        Picasso.get().load(pic.getAccessToken()).into(petProfilePicture);
                        fabPic1Delete.setVisibility(View.VISIBLE);
                        fabAddPic1.setVisibility(View.GONE);
                    }
                    if (pic.getOrder() == 2) {
                        Picasso.get().load(pic.getAccessToken()).into(imageViewPetPicture2);
                        fabPic2Delete.setVisibility(View.VISIBLE);
                        fabAddPic2.setVisibility(View.GONE);
                    }
                    if (pic.getOrder() == 3) {
                        Picasso.get().load(pic.getAccessToken()).into(imageViewPetPicture3);
                        fabPic3Delete.setVisibility(View.VISIBLE);
                        fabAddPic3.setVisibility(View.GONE);
                    }
                    if (pic.getOrder() == 4) {
                        Picasso.get().load(pic.getAccessToken()).into(imageViewPetPicture4);
                        fabPic4Delete.setVisibility(View.VISIBLE);
                        fabAddPic4.setVisibility(View.GONE);
                    }
                    if (pic.getOrder() == 5) {
                        Picasso.get().load(pic.getAccessToken()).into(imageViewPetPicture5);
                        fabPic5Delete.setVisibility(View.VISIBLE);
                        fabAddPic5.setVisibility(View.GONE);
                    }
                    if (pic.getOrder() == 6) {
                        Picasso.get().load(pic.getAccessToken()).into(imageViewPetPicture6);
                        fabPic6Delete.setVisibility(View.VISIBLE);
                        fabAddPic6.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fabAddPic1:
                pic1Changed = true;
                imageChooser();
                fabPic1Delete.setVisibility(View.VISIBLE);
                fabAddPic1.setVisibility(View.GONE);
                break;
            case R.id.fabAddPic2:
                pic2Changed = true;
                imageChooser();
                fabPic2Delete.setVisibility(View.VISIBLE);
                fabAddPic2.setVisibility(View.GONE);
                break;
            case R.id.fabAddPic3:
                pic3Changed = true;
                imageChooser();
                fabPic3Delete.setVisibility(View.VISIBLE);
                fabAddPic3.setVisibility(View.GONE);
                break;
            case R.id.fabAddPic4:
                pic4Changed = true;
                imageChooser();
                fabPic4Delete.setVisibility(View.VISIBLE);
                fabAddPic4.setVisibility(View.GONE);
                break;
            case R.id.fabAddPic5:
                pic5Changed = true;
                imageChooser();
                fabPic5Delete.setVisibility(View.VISIBLE);
                fabAddPic5.setVisibility(View.GONE);
                break;
            case R.id.fabAddPic6:
                pic6Changed = true;
                imageChooser();
                fabPic6Delete.setVisibility(View.VISIBLE);
                fabAddPic6.setVisibility(View.GONE);
                break;
            case R.id.buttonEditProfile:
                String newName = petName.getText().toString().trim();
                String breed = breedSpinner.getSelectedItem().toString();
                String coat = coatSpinner.getSelectedItem().toString();
                String description = petDescription.getText().toString().trim();
                petsReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Pet pet = snapshot.getValue(Pet.class);
                        pet.setName(newName);
                        pet.setBreed(breed);
                        pet.setCoat(coat);
                        pet.setDescription(description);
                        petsReference.setValue(pet);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
                dbPicturesReference.orderByChild("profileID").equalTo(petID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds : snapshot.getChildren()){
                            Picture pic = ds.getValue(Picture.class);

                            switch(pic.getOrder()){
                                case 0:
                                    if(image1UriPath != null){
                                        deleteReference = dbPicturesReference;
                                        deleteReference.child(ds.getKey()).removeValue();
                                    }
                                    break;
                                case 2:
                                    if(image2UriPath != null){
                                        deleteReference = dbPicturesReference;
                                        deleteReference.child(ds.getKey()).removeValue();
                                    }
                                    break;
                                case 3:
                                    if(image3UriPath != null){
                                        deleteReference = dbPicturesReference;
                                        deleteReference.child(ds.getKey()).removeValue();
                                    }
                                    break;
                                case 4:
                                    if(image4UriPath != null){
                                        deleteReference = dbPicturesReference;
                                        deleteReference.child(ds.getKey()).removeValue();
                                    }
                                    break;
                                case 5:
                                    if(image5UriPath != null){
                                        deleteReference = dbPicturesReference;
                                        deleteReference.child(ds.getKey()).removeValue();
                                    }
                                    break;
                                case 6:
                                    if(image6UriPath != null){
                                        deleteReference = dbPicturesReference;
                                        deleteReference.child(ds.getKey()).removeValue();
                                    }
                                    break;
                            }
                        }
                        dbPicturesReference.removeEventListener(this);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                if(petProfilePicture.getDrawable() != null && image1UriPath != null){
                    uploadImage(image1UriPath, 0, true);
                    image1UriPath = null;
                }
                if(imageViewPetPicture2.getDrawable() != null && image2UriPath != null){
                    uploadImage(image2UriPath, 2, false);
                    image2UriPath = null;
                }
                if(imageViewPetPicture3.getDrawable() != null && image3UriPath != null){
                    uploadImage(image3UriPath, 3, false);
                    image3UriPath = null;
                }
                if(imageViewPetPicture4.getDrawable() != null && image4UriPath != null){
                    uploadImage(image4UriPath, 4, false);
                    image4UriPath = null;
                }
                if(imageViewPetPicture5.getDrawable() != null && image5UriPath != null){
                    uploadImage(image5UriPath, 5, false);
                    image5UriPath = null;
                }
                if(imageViewPetPicture6.getDrawable() != null && image6UriPath != null){
                    uploadImage(image6UriPath, 6, false);
                    image6UriPath = null;
                }
                Intent intent = new Intent (view.getContext(), PetProfile.class);
                intent.putExtra("petID", petID);
                view.getContext().startActivity(intent);
                break;
            case R.id.fabPic1Delete:
                petProfilePicture.setImageDrawable(null);
                fabAddPic1.setVisibility(View.VISIBLE);
                fabPic1Delete.setVisibility(View.GONE);
                dbPicturesReference.orderByChild("profileID").equalTo(petID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds : snapshot.getChildren()) {
                            Picture pic = ds.getValue(Picture.class);
                            int order = pic.getOrder();
                            if (order == 0) {
                                deleteReference = dbPicturesReference;
                                deleteReference.child(ds.getKey()).removeValue();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                break;
            case R.id.fabPic2Delete:
                imageViewPetPicture2.setImageDrawable(null);
                fabAddPic2.setVisibility(View.VISIBLE);
                fabPic2Delete.setVisibility(View.GONE);
                dbPicturesReference.orderByChild("profileID").equalTo(petID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds : snapshot.getChildren()) {
                            Picture pic = ds.getValue(Picture.class);
                            int order = pic.getOrder();
                            if (order == 2) {
                                deleteReference = dbPicturesReference;
                                deleteReference.child(ds.getKey()).removeValue();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                break;
            case R.id.fabPic3Delete:
                imageViewPetPicture3.setImageDrawable(null);
                fabAddPic3.setVisibility(View.VISIBLE);
                fabPic3Delete.setVisibility(View.GONE);
                dbPicturesReference.orderByChild("profileID").equalTo(petID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds : snapshot.getChildren()) {
                            Picture pic = ds.getValue(Picture.class);
                            int order = pic.getOrder();
                            if (order == 3) {
                                deleteReference = dbPicturesReference;
                                deleteReference.child(ds.getKey()).removeValue();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                break;
            case R.id.fabPic4Delete:
                imageViewPetPicture4.setImageDrawable(null);
                fabAddPic4.setVisibility(View.VISIBLE);
                fabPic4Delete.setVisibility(View.GONE);
                dbPicturesReference.orderByChild("profileID").equalTo(petID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds : snapshot.getChildren()) {
                            Picture pic = ds.getValue(Picture.class);
                            int order = pic.getOrder();
                            if (order == 4) {
                                deleteReference = dbPicturesReference;
                                deleteReference.child(ds.getKey()).removeValue();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                break;
            case R.id.fabPic5Delete:
                imageViewPetPicture5.setImageDrawable(null);
                fabAddPic5.setVisibility(View.VISIBLE);
                fabPic5Delete.setVisibility(View.GONE);
                dbPicturesReference.orderByChild("profileID").equalTo(petID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds : snapshot.getChildren()) {
                            Picture pic = ds.getValue(Picture.class);
                            int order = pic.getOrder();
                            if (order == 5) {
                                deleteReference = dbPicturesReference;
                                deleteReference.child(ds.getKey()).removeValue();
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                break;
            case R.id.fabPic6Delete:
                imageViewPetPicture6.setImageDrawable(null);
                fabAddPic6.setVisibility(View.VISIBLE);
                fabPic6Delete.setVisibility(View.GONE);
                dbPicturesReference.orderByChild("profileID").equalTo(petID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds : snapshot.getChildren()) {
                            Picture pic = ds.getValue(Picture.class);
                            int order = pic.getOrder();
                            if (order == 6) {
                                deleteReference = dbPicturesReference;
                                deleteReference.child(ds.getKey()).removeValue();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                break;
            case R.id.fabDeletePet:
                new AlertDialog.Builder(EditPet.this).setTitle("Delete Pet").setMessage("Are you sure you want to delete this pet from your account? This action cannot be reversed").setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dbReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.hasChildren()){
                                    deleteReference = dbReference;
                                    deleteReference.child(petID).removeValue();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        dbPicturesReference.orderByChild("profileID").equalTo(petID).addValueEventListener(new ValueEventListener() {
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
                        matchReference.orderByChild("petID").equalTo(petID).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for(DataSnapshot ds : snapshot.getChildren()){
                                        deleteReference = matchReference;
                                        deleteReference.child(ds.getKey()).removeValue();
                                    }
                                }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        swipeReference.orderByChild("petID").equalTo(petID).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for(DataSnapshot ds : snapshot.getChildren()){
                                    deleteReference = swipeReference;
                                    deleteReference.child(ds.getKey()).removeValue();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        Toast.makeText(EditPet.this, "This pet has been deleted successfully", Toast.LENGTH_LONG).show();
                        finish();
                    }
                }).setNegativeButton(android.R.string.no, null).setIcon(android.R.drawable.ic_dialog_alert).show();
                break;
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
                    if(pic1Changed == true){
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                            petProfilePicture.setImageBitmap(bitmap);
                            image1UriPath = selectedImageUri;
                            pic1Changed = false;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                   else if(pic2Changed == true) {
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                            imageViewPetPicture2.setImageBitmap(bitmap);
                            image2UriPath = selectedImageUri;
                            pic2Changed = false;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    else if(pic3Changed == true) {
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                            imageViewPetPicture3.setImageBitmap(bitmap);
                            image3UriPath = selectedImageUri;
                            pic3Changed = false;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    else if(pic4Changed == true) {
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                            imageViewPetPicture4.setImageBitmap(bitmap);
                            image4UriPath = selectedImageUri;
                            pic4Changed = false;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    else if(pic5Changed == true) {
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                            imageViewPetPicture5.setImageBitmap(bitmap);
                            image5UriPath = selectedImageUri;
                            pic5Changed = false;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    else if(pic6Changed == true) {
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                            imageViewPetPicture6.setImageBitmap(bitmap);
                            image6UriPath = selectedImageUri;
                            pic6Changed = false;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    private void uploadImage(Uri imageUri, int order, boolean profilePicture) {
        if (imageUri != null) {

            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading Image...");
            progressDialog.show();

            StorageReference ref = storageReference.child("images/" + userID + "/" + UUID.randomUUID().toString());
            ref.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(
                                        UploadTask.TaskSnapshot taskSnapshot) {
                                    Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(@NonNull Uri uri) {
                                            String name = petName.getText().toString().trim();
                                            petsReference = FirebaseDatabase.getInstance().getReference().child("Pets").child(petID);
                                            petsReference.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        Pet pet = snapshot.getValue(Pet.class);
                                                        if (pet.getName().equals(name)) {
                                                            if(profilePicture == true) {
                                                                Picture picture = new Picture(userID, uri.toString(), "Pet", order, true, snapshot.getKey());
                                                                FirebaseDatabase.getInstance().getReference("Pictures").push().setValue(picture);
                                                            }
                                                            else{
                                                                Picture picture = new Picture(userID, uri.toString(), "Pet", order, false, snapshot.getKey());
                                                                FirebaseDatabase.getInstance().getReference("Pictures").push().setValue(picture);
                                                            }
                                                        }
                                                        petsReference.removeEventListener(this);
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                        }
                                    });
                                    progressDialog.dismiss();
                                }
                            })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();Toast.makeText(EditPet.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(
                            new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                    progressDialog.setMessage("Uploaded " + (int) progress + "%"); }
                            });
        }
    }
}