package com.example.heathcliff.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.heathcliff.R;
import com.example.heathcliff.adapters.PetAdapter;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class Pets extends AppCompatActivity implements View.OnClickListener {

    RecyclerView recyclerView;
    PetAdapter petAdapter;
    ArrayList<Pet> pets;
    ArrayList<Picture> pictures;
    ArrayList<String> petIDs;
    private FirebaseUser user;
    private DatabaseReference reference;
    private DatabaseReference petsReference;
    private DatabaseReference picturesReference;
    private FloatingActionButton addPets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pets);
        addPets = (FloatingActionButton) findViewById(R.id.fabAddPet);
        addPets.setOnClickListener(this);
        user = FirebaseAuth.getInstance().getCurrentUser();
        petsReference = FirebaseDatabase.getInstance().getReference("Pets");
        reference = FirebaseDatabase.getInstance().getReference("Users");
        picturesReference = FirebaseDatabase.getInstance().getReference("Pictures");
        pets = new ArrayList<Pet>();
        petIDs = new ArrayList<String>();
        pictures = new ArrayList<Picture>();
        petsReference.orderByChild("userID").equalTo(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()){
                    Pet pet = ds.getValue(Pet.class);
                    pets.add(pet);
                    petIDs.add(ds.getKey());
                }
                picturesReference.orderByChild("userID").equalTo(user.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot ds : snapshot.getChildren()){
                                Picture picture = ds.getValue(Picture.class);
                                if(picture.getType().equals("Pet")){
                                    if(picture.isProfilePicture() == true){
                                        pictures.add(picture);
                                    }
                                }
                            }
                        recyclerView = (RecyclerView)findViewById(R.id.petsRecyclerView);
                        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
                        llm.setOrientation(LinearLayoutManager.VERTICAL);
                        recyclerView.setLayoutManager(llm);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        petAdapter = new PetAdapter(Pets.this, pets, pictures, petIDs);
                        recyclerView.setAdapter(petAdapter);

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
                        startActivity(new Intent(Pets.this, LandingPage.class));
                        overridePendingTransition(0,0);
                        break;
                    case R.id.ic_adoption_requests:
                        startActivity(new Intent(Pets.this, AdoptionRequests.class));
                        overridePendingTransition(0,0);
                        break;
                    case R.id.ic_adopt:
                        startActivity(new Intent(Pets.this, Adopt.class));
                        overridePendingTransition(0,0);
                        break;
                    case R.id.ic_matches:
                        startActivity(new Intent(Pets.this, Matches.class));
                        overridePendingTransition(0,0);
                        break;
                    case R.id.ic_pets:
                        startActivity(new Intent(Pets.this, Pets.class));
                        overridePendingTransition(0,0);
                        break;
                }
                return false;
            }
        });
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fabAddPet:
                startActivity(new Intent(this, AddPet.class));
                break;
        }
    }
}