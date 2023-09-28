package com.example.heathcliff.UI;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.heathcliff.R;
import com.example.heathcliff.adapters.ViewPagerAdapter;
import com.example.heathcliff.model.Pet;
import com.example.heathcliff.model.Picture;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class PetProfile extends AppCompatActivity implements View.OnClickListener {
    String petID;
    private FirebaseUser user;
    private DatabaseReference reference;
    private DatabaseReference petsReference;
    DatabaseReference dbPicturesReference;
    ViewPager mViewPager;
    ViewPagerAdapter mViewPagerAdapter;
    private TextView petName, petType, petBreed, petCoat, petDescription;
    FloatingActionButton fabEditPetDetails;
    ImageView petTypeImage, petCoatImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_profile);
        petDescription = findViewById(R.id.textViewUserDescription);
        petCoatImage = findViewById(R.id.imageViewPetCoat);
        petCoat = findViewById(R.id.textViewPetCoat);
        petTypeImage = findViewById(R.id.imageViewShelterVerify);
        petName = findViewById(R.id.textViewPetName);
        petType = findViewById(R.id.textViewShelterVerify);
        petBreed = findViewById(R.id.textViewPetBreed);
        mViewPager = (ViewPager)findViewById(R.id.viewPagerMain);
        user = FirebaseAuth.getInstance().getCurrentUser();
        fabEditPetDetails = (FloatingActionButton) findViewById(R.id.fabEditPetDetails);
        fabEditPetDetails.setOnClickListener(this);
        reference = FirebaseDatabase.getInstance().getReference("Users");
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            petID = extras.getString("petID");
        } else{
            startActivity(new Intent(PetProfile.this, SplashScreenActivity.class));
        }
        dbPicturesReference = FirebaseDatabase.getInstance().getReference().child("Pictures");
        petsReference = FirebaseDatabase.getInstance().getReference().child("Pets").child(petID);
        petsReference.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Pet pet = snapshot.getValue(Pet.class);
               Boolean isNull = Objects.isNull(pet);
                    if(isNull.equals(true)){
                        startActivity(new Intent(PetProfile.this, Pets.class));
                    }
                    if(pet.getUserID().equals(user.getUid())){
                        fabEditPetDetails.setVisibility(View.VISIBLE);
                    }
                    petName.setText(pet.getName() +", " +pet.getAge());
                    petType.setText(pet.getType());
                    petBreed.setText(pet.getBreed());
                    petDescription.setText(pet.getDescription());
                    if(pet.getType().equals("Dog")){
                        petCoat.setText(pet.getCoat());
                        petTypeImage.setImageDrawable(ContextCompat.getDrawable(PetProfile.this, R.drawable.dog));
                    }
                    else if(pet.getType().equals("Cat")){
                        petCoat.setText(pet.getCoat());
                    petTypeImage.setImageDrawable(ContextCompat.getDrawable(PetProfile.this, R.drawable.cat));
                     }
                    else if(pet.getType().equals("Reptile")){
                        petCoat.setVisibility(View.GONE);
                        petCoatImage.setVisibility(View.GONE);
                        petTypeImage.setImageDrawable(ContextCompat.getDrawable(PetProfile.this, R.drawable.snake));
                    }
                    else if(pet.getType().equals("Bird")){
                        petCoat.setVisibility(View.GONE);
                        petCoatImage.setVisibility(View.GONE);
                        petTypeImage.setImageDrawable(ContextCompat.getDrawable(PetProfile.this, R.drawable.bird));
                    }
                dbPicturesReference.orderByChild("profileID").equalTo(petID).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                ArrayList<Picture> images = new ArrayList<>();
                                for (DataSnapshot ds : snapshot.getChildren()){
                                    Picture picture = ds.getValue(Picture.class);
                                    images.add(picture);
                                }
                                mViewPagerAdapter = new ViewPagerAdapter(PetProfile.this, images);
                                mViewPager.setAdapter(mViewPagerAdapter);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        BottomNavigationView topNavBar = (BottomNavigationView) findViewById(R.id.topNavBar);
        Menu menu = topNavBar.getMenu();
        MenuItem menuItem = menu.getItem(4);
        menuItem.setChecked(true);
        topNavBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.ic_home:
                        startActivity(new Intent(PetProfile.this, LandingPage.class));
                        overridePendingTransition(0, 0);
                        break;
                    case R.id.ic_adoption_requests:
                        startActivity(new Intent(PetProfile.this, AdoptionRequests.class));
                        overridePendingTransition(0, 0);
                        break;
                    case R.id.ic_adopt:
                        startActivity(new Intent(PetProfile.this, Adopt.class));
                        overridePendingTransition(0, 0);
                        break;
                    case R.id.ic_matches:
                        startActivity(new Intent(PetProfile.this, Matches.class));
                        overridePendingTransition(0, 0);
                        break;
                    case R.id.ic_pets:
                        startActivity(new Intent(PetProfile.this, Pets.class));
                        overridePendingTransition(0, 0);
                        break;
                }
                return false;
            }
        });


    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case (R.id.fabEditPetDetails):
                Intent intent = new Intent (view.getContext(), EditPet.class);
                intent.putExtra("petID", petID);
                view.getContext().startActivity(intent);
                break;
        }
    }
}