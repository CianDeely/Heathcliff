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
import com.example.heathcliff.adapters.MatchesAdapter;
import com.example.heathcliff.adapters.PetAdapter;
import com.example.heathcliff.model.Adopter;
import com.example.heathcliff.model.Match;
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

public class Matches extends AppCompatActivity {

    RecyclerView recyclerView;
    MatchesAdapter matchesAdapter;
    ArrayList<Picture> pictures;
    ArrayList<User> users;
    ArrayList<Adopter> adopters;
    ArrayList<Match> matches;
    String userID;
    ArrayList<Picture> petPictures;
    private FirebaseUser user;
    private DatabaseReference matchesReference;
    private DatabaseReference picturesReference;
    private DatabaseReference usersReference;
    private DatabaseReference adoptersReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matches);
        BottomNavigationView topNavBar = (BottomNavigationView) findViewById(R.id.topNavBar);
        Menu menu = topNavBar.getMenu();
        MenuItem menuItem = menu.getItem(3);
        menuItem.setChecked(true);
        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();
        petPictures = new ArrayList<Picture>();
        matchesReference = FirebaseDatabase.getInstance().getReference("Match");
        picturesReference = FirebaseDatabase.getInstance().getReference("Pictures");
        usersReference = FirebaseDatabase.getInstance().getReference("Users");
        adoptersReference = FirebaseDatabase.getInstance().getReference("Adopters");
        matches = new ArrayList<Match>();
        users = new ArrayList<User>();
        adopters = new ArrayList<Adopter>();
        pictures = new ArrayList<Picture>();
        matchesReference.orderByChild("userID").equalTo(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()){
                    Boolean newMatch = true;
                    Match match = ds.getValue(Match.class);
                    for(Match addedMatch : matches) {
                        if(match.equals(addedMatch)){
                            newMatch = false;
                        }
                    }
                    if(newMatch.equals(true)){
                        matches.add(match);
                    }
                }


            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        matchesReference.orderByChild("adopterUserID").equalTo(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()){
                    Boolean newMatch = true;
                    Match match = ds.getValue(Match.class);
                    for(Match addedMatch : matches) {
                        if(match.equals(addedMatch)){
                            newMatch = false;
                        }
                    }
                    if(newMatch.equals(true)){
                        matches.add(match);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                for(Match m : matches) {
                    usersReference.child(m.getUserID()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            User user = snapshot.getValue(User.class);
                            Boolean newUser = true;
                            for(User u : users){
                                if(u.equals(user)){
                                    newUser = false;
                                }
                            }
                            String snapshotID = snapshot.getKey();
                            if(!snapshotID.equals(userID) && newUser.equals(true)){
                                users.add(user);
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    usersReference.child(m.getAdopterUserID()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            User user = snapshot.getValue(User.class);
                            Boolean newUser = true;
                            for(User u : users){
                                if(u.equals(user)){
                                    newUser = false;
                                }
                            }
                            String snapshotID = snapshot.getKey();
                            if(!snapshotID.equals(userID) && newUser.equals(true)){
                                users.add(user);
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    picturesReference.orderByChild("userID").equalTo(m.getAdopterUserID()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                Picture picture = ds.getValue(Picture.class);
                                Boolean newPic = true;
                                for (Picture pic : pictures) {
                                    if (pic.equals(picture))
                                        newPic = false;
                                }
                                if (!picture.getUserID().equals(userID)) {
                                    if (picture.getType().equals("User")) {
                                        if (picture.isProfilePicture() == true) {
                                            if(newPic.equals(true)) {
                                                pictures.add(picture);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    picturesReference.orderByChild("userID").equalTo(m.getUserID()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                Picture picture = ds.getValue(Picture.class);
                                Boolean newPic = true;
                                for (Picture pic : pictures) {
                                    if (pic.equals(picture))
                                        newPic = false;
                                }
                                if (!picture.getUserID().equals(userID)) {
                                    if (picture.getType().equals("User")) {
                                        if (picture.isProfilePicture() == true) {
                                            if(newPic.equals(true)) {
                                                pictures.add(picture);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                        picturesReference.orderByChild("profileID").equalTo(m.getPetID()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                for (DataSnapshot ds : snapshot.getChildren()) {
                                    Picture picture = ds.getValue(Picture.class);
                                    if (picture.getType().equals("Pet")) {
                                        if (picture.isProfilePicture() == true) {
                                            petPictures.add(picture);
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
                recyclerView = (RecyclerView)findViewById(R.id.matchesRecyclerView);
                LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
                llm.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(llm);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                matchesAdapter = new MatchesAdapter(Matches.this, matches, pictures, users, petPictures);
                recyclerView.setAdapter(matchesAdapter);
            }
        }, 1000);

        topNavBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.ic_home:
                        startActivity(new Intent(Matches.this, LandingPage.class));
                        overridePendingTransition(0,0);
                        break;
                    case R.id.ic_adoption_requests:
                        startActivity(new Intent(Matches.this, AdoptionRequests.class));
                        overridePendingTransition(0,0);
                        break;
                    case R.id.ic_adopt:
                        startActivity(new Intent(Matches.this, Adopt.class));
                        overridePendingTransition(0,0);
                        break;
                    case R.id.ic_matches:
                        startActivity(new Intent(Matches.this, Matches.class));
                        overridePendingTransition(0,0);
                        break;
                    case R.id.ic_pets:
                        startActivity(new Intent(Matches.this, Pets.class));
                        overridePendingTransition(0,0);
                        break;
                }
                return false;
            }
        });
    }
}