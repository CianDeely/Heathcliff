package com.example.heathcliff.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;

import com.example.heathcliff.R;
import com.example.heathcliff.adapters.PetAdapter;
import com.example.heathcliff.adapters.RequestsAdapter;
import com.example.heathcliff.model.Adopter;
import com.example.heathcliff.model.Pet;
import com.example.heathcliff.model.Picture;
import com.example.heathcliff.model.Swipe;
import com.example.heathcliff.model.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdoptionRequests extends AppCompatActivity {

    RecyclerView recyclerView;
    RequestsAdapter requestAdapter;
    ArrayList<Pet> pets;
    ArrayList<Swipe> swipes;
    ArrayList<User> users;
    ArrayList<Picture> pictures;
    ArrayList<Adopter> adopters;
    ArrayList<Picture> petPictures;
    ArrayList<String> petIDs;
    private FirebaseUser user;
    private DatabaseReference reference;
    private DatabaseReference petsReference;
    private DatabaseReference picturesReference;
    private DatabaseReference swipesReference;
    private DatabaseReference usersReference;
    private DatabaseReference adoptersReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adoption_requests);
        BottomNavigationView topNavBar = (BottomNavigationView) findViewById(R.id.topNavBar);
        Menu menu = topNavBar.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);
        user = FirebaseAuth.getInstance().getCurrentUser();
        swipesReference = FirebaseDatabase.getInstance().getReference("Swipes");
        petsReference = FirebaseDatabase.getInstance().getReference("Pets");
        reference = FirebaseDatabase.getInstance().getReference("Users");
        picturesReference = FirebaseDatabase.getInstance().getReference("Pictures");
        usersReference = FirebaseDatabase.getInstance().getReference("Users");
        adoptersReference = FirebaseDatabase.getInstance().getReference("Adopters");
        pets = new ArrayList<Pet>();
        users = new ArrayList<User>();
        adopters = new ArrayList<Adopter>();
        swipes = new ArrayList<Swipe>();
        pictures = new ArrayList<Picture>();
        petPictures = new ArrayList<Picture>();
        petIDs = new ArrayList<String>();
        swipesReference.orderByChild("userID").equalTo(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()){
                    Swipe swipe = ds.getValue(Swipe.class);
                    if(swipe.getLike()==1) {
                        swipes.add(swipe);
                    }
                }
                for(Swipe s : swipes) {
                    picturesReference.orderByChild("profileID").equalTo(s.getPetID()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                Picture picture = ds.getValue(Picture.class);
                                if (picture.getType().equals("Pet")) {
                                    if (picture.isProfilePicture() == true) {
                                        if(picture.getProfileID().equals(s.getPetID())) {
                                            petPictures.add(picture);
                                            petIDs.add(picture.getProfileID());
                                            petsReference.child(picture.getProfileID()).addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    Pet pet = snapshot.getValue(Pet.class);
                                                    pets.add(pet);
                                                }
                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                        }
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    adoptersReference.child(s.getAdopterID()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Adopter adopter = snapshot.getValue(Adopter.class);
                                adopters.add(adopter);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        for(Adopter a : adopters) {
                            usersReference.child(a.getUserID()).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    User user = snapshot.getValue(User.class);
                                    users.add(user);
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                        for(Adopter a : adopters) {
                            picturesReference.orderByChild("userID").equalTo(a.getUserID()).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds : snapshot.getChildren()) {
                                        Picture picture = ds.getValue(Picture.class);
                                        if (picture.getType().equals("User")) {
                                            if (picture.isProfilePicture() == true) {
                                                pictures.add(picture);
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }
                }, 500);

                Handler handler1 = new Handler();
                handler1.postDelayed(new Runnable() {
                    public void run() {
                        recyclerView = (RecyclerView)findViewById(R.id.requestsRecyclerView);
                        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
                        llm.setOrientation(LinearLayoutManager.VERTICAL);
                        recyclerView.setLayoutManager(llm);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        requestAdapter = new RequestsAdapter(AdoptionRequests.this, pets, users, adopters, pictures, petPictures, petIDs);
                        recyclerView.setAdapter(requestAdapter);
                    }
                }, 1000);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        topNavBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.ic_home:
                        startActivity(new Intent(AdoptionRequests.this, LandingPage.class));
                        overridePendingTransition(0,0);
                        break;
                    case R.id.ic_adoption_requests:
                        startActivity(new Intent(AdoptionRequests.this, AdoptionRequests.class));
                        overridePendingTransition(0,0);
                        break;
                    case R.id.ic_adopt:
                        startActivity(new Intent(AdoptionRequests.this, Adopt.class));
                        overridePendingTransition(0,0);
                        break;
                    case R.id.ic_matches:
                        startActivity(new Intent(AdoptionRequests.this, Matches.class));
                        overridePendingTransition(0,0);
                        break;
                    case R.id.ic_pets:
                        startActivity(new Intent(AdoptionRequests.this, Pets.class));
                        overridePendingTransition(0,0);
                        break;
                }
                return false;
            }
        });
    }
}