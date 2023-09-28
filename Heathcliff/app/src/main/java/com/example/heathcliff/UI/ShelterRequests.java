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
import com.example.heathcliff.adapters.ReportsAdapter;
import com.example.heathcliff.adapters.ShelterRequestsAdapter;
import com.example.heathcliff.model.Adopter;
import com.example.heathcliff.model.Picture;
import com.example.heathcliff.model.Report;
import com.example.heathcliff.model.ShelterRegistrationRequest;
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

public class ShelterRequests extends AppCompatActivity {
    BottomNavigationView topNavBar;
    RecyclerView recyclerView;
    ShelterRequestsAdapter shelterRequestsAdapter;
    ArrayList<Picture> pictures;
    ArrayList<User> users;
    ArrayList<Adopter> adopters;
    ArrayList<ShelterRegistrationRequest> shelterRequests;
    ArrayList<String> requestIDs;
    private FirebaseUser user;
    private DatabaseReference usersReference;
    private DatabaseReference picturesReference;
    private DatabaseReference adoptersReference;
    private DatabaseReference shelterRequestsReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shelter_requests);
        user = FirebaseAuth.getInstance().getCurrentUser();
        shelterRequestsReference = FirebaseDatabase.getInstance().getReference("ShelterRegistrationRequest");
        picturesReference = FirebaseDatabase.getInstance().getReference("Pictures");
        usersReference = FirebaseDatabase.getInstance().getReference("Users");
        adoptersReference = FirebaseDatabase.getInstance().getReference("Adopters");
        users = new ArrayList<User>();
        pictures = new ArrayList<Picture>();
        adopters = new ArrayList<Adopter>();
        shelterRequests = new ArrayList<ShelterRegistrationRequest>();
        requestIDs = new ArrayList<String>();
        shelterRequestsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ShelterRegistrationRequest request = ds.getValue(ShelterRegistrationRequest.class);
                    Boolean approved = request.isApproved();
                    if (approved == false) {
                        shelterRequests.add(request);
                        requestIDs.add(ds.getKey());
                        picturesReference.orderByChild("userID").equalTo(request.getUserID()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds : snapshot.getChildren()) {
                                    Picture picture = ds.getValue(Picture.class);
                                    if (picture.getUserID().equals(request.getUserID())) {
                                        if (picture.getType().equals("User")) {
                                            if (picture.isProfilePicture() == true) {
                                                pictures.add(picture);
                                            }
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        usersReference.child(request.getUserID()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.hasChildren()) {
                                    users.add(snapshot.getValue(User.class));
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        adoptersReference.orderByChild("userID").equalTo(request.getUserID()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds : snapshot.getChildren()) {
                                    if (ds.getValue(Adopter.class).getUserID().equals(request.getUserID())) {
                                        adopters.add(ds.getValue(Adopter.class));
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Handler handler1 = new Handler();
        handler1.postDelayed(new Runnable() {
            public void run() {
                recyclerView = (RecyclerView)findViewById(R.id.shelterRequestsRecyclerView);
                LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
                llm.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(llm);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                shelterRequestsAdapter = new ShelterRequestsAdapter(ShelterRequests.this, shelterRequests, pictures, users, requestIDs, adopters);
                recyclerView.setAdapter(shelterRequestsAdapter);
            }
        }, 1000);
        BottomNavigationView topNavBar = (BottomNavigationView) findViewById(R.id.topNavBar);
        Menu menu = topNavBar.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);
        topNavBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.ic_home:
                        startActivity(new Intent(ShelterRequests.this, LandingPage.class));
                        overridePendingTransition(0, 0);

                        break;
                    case R.id.ic_shelter_requests:
                        startActivity(new Intent(ShelterRequests.this, ShelterRequests.class));
                        overridePendingTransition(0, 0);
                        break;
                    case R.id.ic_reports:
                        startActivity(new Intent(ShelterRequests.this, Reports.class));
                        overridePendingTransition(0, 0);
                        break;
                    case R.id.ic_suspend:
                        startActivity(new Intent(ShelterRequests.this, Suspend.class));
                        overridePendingTransition(0, 0);
                        break;
                }
                return false;
            }
        });
    }
}