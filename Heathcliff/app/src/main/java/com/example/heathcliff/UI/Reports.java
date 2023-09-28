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
import com.example.heathcliff.adapters.ReportsAdapter;
import com.example.heathcliff.model.Adopter;
import com.example.heathcliff.model.Match;
import com.example.heathcliff.model.Picture;
import com.example.heathcliff.model.Report;
import com.example.heathcliff.model.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.Repo;

import java.util.ArrayList;

public class Reports extends AppCompatActivity {

    RecyclerView recyclerView;
    ReportsAdapter reportsAdapter;
    ArrayList<Picture> pictures;
    ArrayList<Picture> reportedUserPictures;
    ArrayList<User> users;
    ArrayList<User> reportedUsers;
    ArrayList<Adopter> adopters;
    ArrayList<Adopter> reportedAdopters;
    ArrayList<Report> reports;
    ArrayList<String> reportIDs;
    private FirebaseUser user;
    private DatabaseReference reportReference;
    private DatabaseReference picturesReference;
    private DatabaseReference usersReference;
    private DatabaseReference adoptersReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);
        user = FirebaseAuth.getInstance().getCurrentUser();
        reportedUserPictures = new ArrayList<Picture>();
        reportReference = FirebaseDatabase.getInstance().getReference("Report");
        picturesReference = FirebaseDatabase.getInstance().getReference("Pictures");
        usersReference = FirebaseDatabase.getInstance().getReference("Users");
        adoptersReference = FirebaseDatabase.getInstance().getReference("Adopters");
        reports = new ArrayList<Report>();
        users = new ArrayList<User>();
        reportedUsers = new ArrayList<User>();
        adopters = new ArrayList<Adopter>();
        reportedAdopters = new ArrayList<Adopter>();
        pictures = new ArrayList<Picture>();
        reportIDs = new ArrayList<String>();
        reportReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()){
                    Report report = ds.getValue(Report.class);
                    Boolean resolved = report.getResolved();
                    if(resolved == false){
                        reports.add(report);
                        reportIDs.add(ds.getKey());
                        picturesReference.orderByChild("userID").equalTo(report.getReporterID()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds : snapshot.getChildren()) {
                                    Picture picture = ds.getValue(Picture.class);
                                        if (picture.getUserID().equals(report.getReporterID())) {
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
                        picturesReference.orderByChild("userID").equalTo(report.getReportedID()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds : snapshot.getChildren()) {
                                    Picture picture = ds.getValue(Picture.class);
                                        if (picture.getUserID().equals(report.getReportedID())) {
                                            if (picture.getType().equals("User")) {
                                                if (picture.isProfilePicture() == true) {
                                                    reportedUserPictures.add(picture);
                                                }
                                            }
                                        }
                                    }
                                }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
                    }
                    usersReference.child(report.getReporterID()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.hasChildren()){
                                users.add(snapshot.getValue(User.class));
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    usersReference.child(report.getReportedID()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.hasChildren()){
                                reportedUsers.add(snapshot.getValue(User.class));
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    adoptersReference.orderByChild("userID").equalTo(report.getReporterID()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds : snapshot.getChildren()){
                                if(ds.getValue(Adopter.class).getUserID().equals(report.getReporterID())){
                                    adopters.add(ds.getValue(Adopter.class));
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    adoptersReference.orderByChild("userID").equalTo(report.getReportedID()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds : snapshot.getChildren()){
                                if(ds.getValue(Adopter.class).getUserID().equals(report.getReportedID())){
                                    reportedAdopters.add(ds.getValue(Adopter.class));
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Handler handler1 = new Handler();
        handler1.postDelayed(new Runnable() {
            public void run() {
        recyclerView = (RecyclerView)findViewById(R.id.reportsRecyclerView);
        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        reportsAdapter = new ReportsAdapter(Reports.this, reports, pictures, users, reportedUserPictures, adopters, reportedAdopters, reportedUsers, reportIDs);
        recyclerView.setAdapter(reportsAdapter);
            }
        }, 1000);
        BottomNavigationView topNavBar = (BottomNavigationView) findViewById(R.id.topNavBar);
        Menu menu = topNavBar.getMenu();
        MenuItem menuItem = menu.getItem(2);
        menuItem.setChecked(true);
        topNavBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.ic_home:
                        startActivity(new Intent(Reports.this, LandingPage.class));
                        overridePendingTransition(0, 0);
                        break;
                    case R.id.ic_shelter_requests:
                        startActivity(new Intent(Reports.this, ShelterRequests.class));
                        overridePendingTransition(0, 0);
                        break;
                    case R.id.ic_reports:
                        startActivity(new Intent(Reports.this, Reports.class));
                        overridePendingTransition(0, 0);
                        break;
                    case R.id.ic_suspend:
                        startActivity(new Intent(Reports.this, Suspend.class));
                        overridePendingTransition(0, 0);
                        break;
                }
                return false;
            }
        });
    }
}