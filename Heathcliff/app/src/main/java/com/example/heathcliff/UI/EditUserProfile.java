package com.example.heathcliff.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
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
import com.example.heathcliff.model.Adopter;
import com.example.heathcliff.model.Picture;
import com.example.heathcliff.model.User;
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

public class EditUserProfile extends AppCompatActivity implements View.OnClickListener {

    ImageView userProfilePicture, imageViewUserPicture2, imageViewUserPicture3, imageViewUserPicture4, imageViewUserPicture5, imageViewUserPicture6;
    FloatingActionButton fabPic1Delete, fabPic2Delete, fabPic3Delete, fabPic4Delete, fabPic5Delete, fabPic6Delete, fabAddPic1, fabAddPic2, fabAddPic3, fabAddPic4, fabAddPic5, fabAddPic6;
    String userID, adopterID;
    private final int PICK_IMAGE_REQUEST = 22;
    StorageReference storageReference;
    FirebaseStorage storage;
    FirebaseUser user;
    Uri image1UriPath, image2UriPath, image3UriPath, image4UriPath, image5UriPath, image6UriPath;
    DatabaseReference deleteReference;
    Spinner genderSpinner;
    EditText userFirstName, userLastName, userDescription, userOccupation;
    private DatabaseReference adoptersReference, userReference;
    FloatingActionButton editUserDetails;
    Uri selectedImageUri;
    private DatabaseReference dbPicturesReference;
    Boolean pic1Changed, pic2Changed, pic3Changed, pic4Changed, pic5Changed, pic6Changed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_profile);
        userProfilePicture = findViewById(R.id.imageViewUserProfilePicture);
        imageViewUserPicture2 = findViewById(R.id.imageViewUserPicture2);
        imageViewUserPicture3 = findViewById(R.id.imageViewUserPicture3);
        imageViewUserPicture4 = findViewById(R.id.imageViewUserPicture4);
        imageViewUserPicture5 = findViewById(R.id.imageViewUserPicture5);
        imageViewUserPicture6 = findViewById(R.id.imageViewUserPicture6);
        pic1Changed = false;
        pic2Changed = false;
        pic3Changed = false;
        pic4Changed = false;
        pic5Changed = false;
        pic6Changed = false;
        genderSpinner = findViewById(R.id.spinnerGender);
        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();
        userFirstName = findViewById(R.id.editTextShelterName);
        userLastName = findViewById(R.id.editTextUserLastName);
        userDescription = findViewById(R.id.editTextShelterAddress);
        userOccupation = findViewById(R.id.editTextUserOccupation);
        editUserDetails = findViewById(R.id.buttonEditProfile);
        editUserDetails.setOnClickListener(this);
        fabPic2Delete = findViewById(R.id.fabPic2Delete);
        fabPic3Delete = findViewById(R.id.fabPic3Delete);
        fabPic4Delete = findViewById(R.id.fabPic4Delete);
        fabPic5Delete = findViewById(R.id.fabPic5Delete);
        fabPic6Delete = findViewById(R.id.fabPic6Delete);
        fabPic1Delete = findViewById(R.id.fabPic1Delete);
        fabPic1Delete.setOnClickListener(this);
        fabPic2Delete.setOnClickListener(this);
        fabPic3Delete.setOnClickListener(this);
        fabPic4Delete.setOnClickListener(this);
        fabPic5Delete.setOnClickListener(this);
        fabPic6Delete.setOnClickListener(this);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
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
        userReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
        dbPicturesReference = FirebaseDatabase.getInstance().getReference().child("Pictures");
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            adopterID = extras.getString("adopterID");
        }
        adoptersReference = FirebaseDatabase.getInstance().getReference().child("Adopters").child(adopterID);
        adoptersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Adopter adopter = snapshot.getValue(Adopter.class);
                userDescription.setText(adopter.getAboutMe());
                userOccupation.setText(adopter.getOccupation());
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(EditUserProfile.this, R.array.genders_array, android.R.layout.simple_spinner_item);
                int spinnerPosition = adapter.getPosition(adopter.getGender());
                genderSpinner.setSelection(spinnerPosition);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                userFirstName.setText(user.getFirstName());
                userLastName.setText(user.getLastName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        dbPicturesReference.orderByChild("profileID").equalTo(adopterID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Picture> images = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Picture picture = ds.getValue(Picture.class);
                    images.add(picture);
                }

                for (Picture pic : images) {
                    if (pic.isProfilePicture() == true) {
                        Picasso.get().load(pic.getAccessToken()).into(userProfilePicture);
                        fabPic1Delete.setVisibility(View.VISIBLE);
                        fabAddPic1.setVisibility(View.GONE);
                    }
                    if (pic.getOrder() == 2) {
                        Picasso.get().load(pic.getAccessToken()).into(imageViewUserPicture2);
                        fabPic2Delete.setVisibility(View.VISIBLE);
                        fabAddPic2.setVisibility(View.GONE);
                    }
                    if (pic.getOrder() == 3) {
                        Picasso.get().load(pic.getAccessToken()).into(imageViewUserPicture3);
                        fabPic3Delete.setVisibility(View.VISIBLE);
                        fabAddPic3.setVisibility(View.GONE);
                    }
                    if (pic.getOrder() == 4) {
                        Picasso.get().load(pic.getAccessToken()).into(imageViewUserPicture4);
                        fabPic4Delete.setVisibility(View.VISIBLE);
                        fabAddPic4.setVisibility(View.GONE);
                    }
                    if (pic.getOrder() == 5) {
                        Picasso.get().load(pic.getAccessToken()).into(imageViewUserPicture5);
                        fabPic5Delete.setVisibility(View.VISIBLE);
                        fabAddPic5.setVisibility(View.GONE);
                    }
                    if (pic.getOrder() == 6) {
                        Picasso.get().load(pic.getAccessToken()).into(imageViewUserPicture6);
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
                String newFirstName = userFirstName.getText().toString().trim();
                String newLastName = userLastName.getText().toString().trim();
                String newOccupation = userOccupation.getText().toString().trim();
                String newDescription = userDescription.getText().toString().trim();
                String gender = genderSpinner.getSelectedItem().toString();
                adoptersReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Adopter adopter = snapshot.getValue(Adopter.class);
                        adopter.setAboutMe(newDescription);
                        adopter.setGender(gender);
                        adopter.setOccupation(newOccupation);
                        adoptersReference.setValue(adopter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
                userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        user.setFirstName(newFirstName);
                        user.setLastName(newLastName);
                        userReference.setValue(user);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                dbPicturesReference.orderByChild("profileID").equalTo(adopterID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            Picture pic = ds.getValue(Picture.class);

                            switch (pic.getOrder()) {
                                case 0:
                                    if (image1UriPath != null) {
                                        deleteReference = dbPicturesReference;
                                        deleteReference.child(ds.getKey()).removeValue();
                                    }
                                    break;
                                case 2:
                                    if (image2UriPath != null) {
                                        deleteReference = dbPicturesReference;
                                        deleteReference.child(ds.getKey()).removeValue();
                                    }
                                    break;
                                case 3:
                                    if (image3UriPath != null) {
                                        deleteReference = dbPicturesReference;
                                        deleteReference.child(ds.getKey()).removeValue();
                                    }
                                    break;
                                case 4:
                                    if (image4UriPath != null) {
                                        deleteReference = dbPicturesReference;
                                        deleteReference.child(ds.getKey()).removeValue();
                                    }
                                    break;
                                case 5:
                                    if (image5UriPath != null) {
                                        deleteReference = dbPicturesReference;
                                        deleteReference.child(ds.getKey()).removeValue();
                                    }
                                    break;
                                case 6:
                                    if (image6UriPath != null) {
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
                if (userProfilePicture.getDrawable() != null && image1UriPath != null) {
                    uploadImage(image1UriPath, 0, true);
                    image1UriPath = null;
                }
                if (imageViewUserPicture2.getDrawable() != null && image2UriPath != null) {
                    uploadImage(image2UriPath, 2, false);
                    image2UriPath = null;
                }
                if (imageViewUserPicture3.getDrawable() != null && image3UriPath != null) {
                    uploadImage(image3UriPath, 3, false);
                    image3UriPath = null;
                }
                if (imageViewUserPicture4.getDrawable() != null && image4UriPath != null) {
                    uploadImage(image4UriPath, 4, false);
                    image4UriPath = null;
                }
                if (imageViewUserPicture5.getDrawable() != null && image5UriPath != null) {
                    uploadImage(image5UriPath, 5, false);
                    image5UriPath = null;
                }
                if (imageViewUserPicture6.getDrawable() != null && image6UriPath != null) {
                    uploadImage(image6UriPath, 6, false);
                    image6UriPath = null;
                }
                Intent intent = new Intent(view.getContext(), LandingPage.class);
                view.getContext().startActivity(intent);
                break;
            case R.id.fabPic1Delete:
                userProfilePicture.setImageDrawable(null);
                fabAddPic1.setVisibility(View.VISIBLE);
                fabPic1Delete.setVisibility(View.GONE);
                dbPicturesReference.orderByChild("profileID").equalTo(adopterID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
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
                imageViewUserPicture2.setImageDrawable(null);
                fabAddPic2.setVisibility(View.VISIBLE);
                fabPic2Delete.setVisibility(View.GONE);
                dbPicturesReference.orderByChild("profileID").equalTo(adopterID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
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
                imageViewUserPicture3.setImageDrawable(null);
                fabAddPic3.setVisibility(View.VISIBLE);
                fabPic3Delete.setVisibility(View.GONE);
                dbPicturesReference.orderByChild("profileID").equalTo(adopterID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
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
                imageViewUserPicture4.setImageDrawable(null);
                fabAddPic4.setVisibility(View.VISIBLE);
                fabPic4Delete.setVisibility(View.GONE);
                dbPicturesReference.orderByChild("profileID").equalTo(adopterID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
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
                imageViewUserPicture5.setImageDrawable(null);
                fabAddPic5.setVisibility(View.VISIBLE);
                fabPic5Delete.setVisibility(View.GONE);
                dbPicturesReference.orderByChild("profileID").equalTo(adopterID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
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
                imageViewUserPicture6.setImageDrawable(null);
                fabAddPic6.setVisibility(View.VISIBLE);
                fabPic6Delete.setVisibility(View.GONE);
                dbPicturesReference.orderByChild("profileID").equalTo(adopterID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
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
                            userProfilePicture.setImageBitmap(bitmap);
                            image1UriPath = selectedImageUri;
                            pic1Changed = false;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    else if(pic2Changed == true) {
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                            imageViewUserPicture2.setImageBitmap(bitmap);
                            image2UriPath = selectedImageUri;
                            pic2Changed = false;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    else if(pic3Changed == true) {
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                            imageViewUserPicture3.setImageBitmap(bitmap);
                            image3UriPath = selectedImageUri;
                            pic3Changed = false;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    else if(pic4Changed == true) {
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                            imageViewUserPicture4.setImageBitmap(bitmap);
                            image4UriPath = selectedImageUri;
                            pic4Changed = false;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    else if(pic5Changed == true) {
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                            imageViewUserPicture5.setImageBitmap(bitmap);
                            image5UriPath = selectedImageUri;
                            pic5Changed = false;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    else if(pic6Changed == true) {
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                            imageViewUserPicture6.setImageBitmap(bitmap);
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
            ref.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(
                                        UploadTask.TaskSnapshot taskSnapshot) { Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(@NonNull Uri uri) {
                                            if(profilePicture == true) {
                                                Picture picture = new Picture(userID, uri.toString(), "User", order, true, adopterID);
                                                FirebaseDatabase.getInstance().getReference("Pictures").push().setValue(picture);
                                            }
                                            else{
                                                Picture picture = new Picture(userID, uri.toString(), "User", order, false, adopterID);
                                                FirebaseDatabase.getInstance().getReference("Pictures").push().setValue(picture);
                                            }
                                        }
                                    });
                                    progressDialog.dismiss();
                                }
                            })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(EditUserProfile.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
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