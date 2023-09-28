package com.example.heathcliff.UI;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.heathcliff.R;
import com.example.heathcliff.model.Adopter;
import com.example.heathcliff.model.Picture;
import com.example.heathcliff.model.Preference;
import com.example.heathcliff.model.UserLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
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
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class CreateProfile extends AppCompatActivity {

    Button selectProfilePicture;
    ImageView previewProfilePicture;
    Spinner genderSpinner;
    EditText editTextDOB, editTextOccupation, editTextAboutMe;
    Button createProfile;
    Calendar myCalendar = Calendar.getInstance();
    FirebaseUser user;
    int SELECT_PICTURE = 200;
    FusedLocationProviderClient mFusedLocationClient;
    double userLatitude;
    double userLongitude;
    int PERMISSION_ID = 44;
    FirebaseDatabase database;
    DatabaseReference locationReference;
    String locationID;
    String myFormat = "dd/MM/yyyy";
    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
    Uri selectedImageUri;
    FirebaseStorage storage;
    StorageReference storageReference;
    private final int PICK_IMAGE_REQUEST = 22;
    String userID;
    String accessToken;
    Date userDOB;
    FirebaseDatabase newDatabaseReference;
    private DatabaseReference preferencesReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);
        genderSpinner = findViewById(R.id.spinnerGender);
        editTextDOB = findViewById(R.id.editTextDOB);
        editTextOccupation = findViewById(R.id.editTextOccupation);
        editTextAboutMe = findViewById(R.id.editTextAboutMe);
        createProfile = findViewById(R.id.buttonCreateProfile);
        preferencesReference = FirebaseDatabase.getInstance().getReference("Preferences");
        selectProfilePicture = findViewById(R.id.buttonSelectProfilePicture);
        previewProfilePicture = findViewById(R.id.imageProfilePicturePreview);
        user = FirebaseAuth.getInstance().getCurrentUser();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        locationReference = database.getReference("UserLocations");
        newDatabaseReference = database;
        locationReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
            UserLocation usersLocation = snapshot.getValue(UserLocation.class);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        editTextDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(CreateProfile.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        createProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.imageHeathcliffBannerCreateProfile:
                        startActivity(new Intent(CreateProfile.this, MainActivity.class));
                        break;
                    case R.id.buttonCreateProfile:
                            createNewProfile();
                        break;
                }
            }
        });
        selectProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageChooser();
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location == null) {
                            requestNewLocationData();
                        } else {
                            userLatitude = location.getLatitude();
                            userLongitude = location.getLongitude() ;
                        }
                    }
                });
            } else {
                Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            userLatitude = mLastLocation.getLatitude();
            userLongitude = mLastLocation.getLongitude();
        }
    };

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    public void
    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
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
                        previewProfilePicture.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void createNewProfile() {
        String occupation = editTextOccupation.getText().toString().trim();
        String aboutMe = editTextAboutMe.getText().toString().trim();
        String dobString = editTextDOB.getText().toString().trim();
        Drawable profilePicture;
        String profileImageURI = previewProfilePicture.toString();
        String gender = genderSpinner.getSelectedItem().toString();
        userID = user.getUid();
        userDOB = new Date();

        if (dobString.isEmpty()) {
            editTextOccupation.setError("Please enter a valid date of birth");
            editTextOccupation.requestFocus();
            return;
        }
        if (!profileImageURI.isEmpty()) {
            profilePicture = previewProfilePicture.getDrawable();
        }
        Timestamp dateOfBirth = null;
        try {
             userDOB = sdf.parse(dobString);
            dateOfBirth = new Timestamp(userDOB.getTime());
        } catch (ParseException e) {
            Toast.makeText(CreateProfile.this, "Failed to parse date time to timestamp " + dobString, Toast.LENGTH_LONG).show();
        }
        UserLocation location = new UserLocation(userLatitude, userLongitude, "Initial", userID);
        FirebaseDatabase.getInstance().getReference("UserLocations").push().setValue(location).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                } else {
                }
            }
        });
        locationReference.orderByChild("userID").equalTo(userID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String
                    previousChildName) {
                locationID = snapshot.getKey();
                Adopter adopter = new Adopter(userDOB, gender, aboutMe, occupation, locationID, userID);

                FirebaseDatabase.getInstance().getReference("Adopters").push().setValue(adopter).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            uploadImage();
                            Preference preference = new Preference();
                            preference.setUserID(userID);
                            preference.setBreed("Any Breed");
                            preference.setCoat("Any Coat");
                            preference.setMaxAge(30);
                            preference.setMaxDistance(250);
                            preference.setType("Any Type");
                            FirebaseDatabase.getInstance().getReference("Preferences").push().setValue(preference);
                            Toast.makeText(CreateProfile.this, "Profile created successfully", Toast.LENGTH_LONG).show();

                        } else {
                            Toast.makeText(CreateProfile.this, "Failed to create profile, please try again", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                locationReference.removeEventListener(this);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String
                    previousChildName) {
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String
                    previousChildName) {
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }
    private void updateLabel() {
        editTextDOB.setText(sdf.format(myCalendar.getTime()));
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

                                            accessToken = uri.toString();
                                            FirebaseDatabase.getInstance().getReference("Adopters").orderByChild("userID").equalTo(userID).addChildEventListener(new ChildEventListener() {

                                                @Override
                                                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                                    Picture picture = new Picture(userID, accessToken, "User", 0, true, snapshot.getKey() );
                                                    FirebaseDatabase.getInstance().getReference("Pictures").push().setValue(picture);
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

                                        }
                                    });
                                    progressDialog.dismiss();
                                    }
                            })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                            progressDialog.dismiss();
                            Toast.makeText(CreateProfile.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(
                            new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot)
                                {
                                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                    progressDialog.setMessage("Uploaded " + (int)progress + "%");
                                }
                            });
        }
    }



                        }