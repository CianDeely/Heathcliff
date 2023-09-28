package com.example.heathcliff.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.heathcliff.R;
import com.example.heathcliff.adapters.ViewPagerAdapter;
import com.example.heathcliff.model.Adopter;
import com.example.heathcliff.model.Chat;
import com.example.heathcliff.model.Match;
import com.example.heathcliff.model.Picture;
import com.example.heathcliff.model.Report;
import com.example.heathcliff.model.Shelter;
import com.example.heathcliff.model.Swipe;
import com.example.heathcliff.model.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class UserProfile extends AppCompatActivity implements View.OnClickListener {

    String adopterID;
    private FirebaseUser user;
    private DatabaseReference reference;
    private DatabaseReference adopterReference, userReference;
    private DatabaseReference swipesReference;
    private DatabaseReference matchReference;
    private DatabaseReference deleteRef;
    private DatabaseReference chatRef;
    private DatabaseReference reportReference;
    private DatabaseReference shelterReference;
    DatabaseReference deleteReference;
    DatabaseReference dbPicturesReference;
    ViewPager mViewPager;
    ViewPagerAdapter mViewPagerAdapter;
    private TextView userName, userGender, userOccupation, userDescription, verifyShelter;
    private ImageView verifiedShelter;
    FloatingActionButton fabEditUserDetails, fabSwipeLeftUserProfile, fabSwipeRightUserProfile, fabUnmatchUserProfile, fabOpenChatUserProfile, fabReportUserProfile;
    Date dob;
    Spinner spinnerReportReason;
    EditText editTextReportMessage;
    Swipe matchSwipe;
    Match currentMatch;
    String userID;
    String alternateUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        userName = findViewById(R.id.textViewUserName);
        matchSwipe = new Swipe();
        verifiedShelter = findViewById(R.id.imageViewShelterVerify);
        verifyShelter = findViewById(R.id.textViewShelterVerify);
        userID = FirebaseAuth.getInstance().getUid();
        userGender = findViewById(R.id.textViewUserGender);
        userOccupation = findViewById(R.id.textViewUserOccupation);
        userDescription = findViewById(R.id.textViewUserDescription);
        fabEditUserDetails = findViewById(R.id.fabEditUserDetails);
        fabSwipeLeftUserProfile = findViewById(R.id.fabSwipeLeftUserProfile);
        fabSwipeRightUserProfile = findViewById(R.id.fabSwipeRightUserProfile);
        fabOpenChatUserProfile = findViewById(R.id.fabOpenChatUserProfile);
        fabUnmatchUserProfile = findViewById(R.id.fabUnmatchUserProfile);
        fabReportUserProfile = findViewById(R.id.fabReportUserProfile);
        fabReportUserProfile.setOnClickListener(this);
        fabSwipeRightUserProfile.setOnClickListener(this);
        fabSwipeLeftUserProfile.setOnClickListener(this);
        fabOpenChatUserProfile.setOnClickListener(this);
        fabUnmatchUserProfile.setOnClickListener(this);
        swipesReference = FirebaseDatabase.getInstance().getReference("Swipes");
        reportReference = FirebaseDatabase.getInstance().getReference("Report");
        chatRef = FirebaseDatabase.getInstance().getReference("Chat");
        shelterReference = FirebaseDatabase.getInstance().getReference("Shelter");
        fabEditUserDetails.setOnClickListener(this);
        mViewPager = (ViewPager)findViewById(R.id.viewPagerMain);
        dbPicturesReference = FirebaseDatabase.getInstance().getReference().child("Pictures");
        user = FirebaseAuth.getInstance().getCurrentUser();
        dob  = new Date();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        matchReference = FirebaseDatabase.getInstance().getReference("Match");
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            adopterID = extras.getString("adopterID");
        }
        adopterReference = FirebaseDatabase.getInstance().getReference().child("Adopters").child(adopterID);
        adopterReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChildren()){
                    Adopter adopter = snapshot.getValue(Adopter.class);
                    alternateUserID = adopter.getUserID();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Handler handler1 = new Handler();
        handler1.postDelayed(new Runnable() {
            public void run() {
        shelterReference.orderByChild("userID").equalTo(alternateUserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()){
                    Shelter shelter = ds.getValue(Shelter.class);
                    if(shelter.getUserID().equals(alternateUserID)){
                    verifiedShelter.setVisibility(View.VISIBLE);
                    verifyShelter.setVisibility(View.VISIBLE);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });         }
        }, 1000);
        swipesReference.orderByChild("adopterID").equalTo(adopterID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()){
                    Swipe swipe = ds.getValue(Swipe.class);
                    if(swipe.getAdopterID().equals(adopterID) && swipe.getUserID().equals(user.getUid())){
                        matchSwipe = swipe;
                        deleteRef = FirebaseDatabase.getInstance().getReference().child("Swipes").child(ds.getKey());
                        fabSwipeLeftUserProfile.setVisibility(View.VISIBLE);
                        fabSwipeRightUserProfile.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        matchReference.orderByChild("adopterID").equalTo(adopterID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()){
                    Match match = ds.getValue(Match.class);
                    if(match.getAdopterID().equals(adopterID) && match.getUserID().equals(user.getUid())){
                        currentMatch = match;
                        deleteRef = FirebaseDatabase.getInstance().getReference().child("Match").child(ds.getKey());
                        fabOpenChatUserProfile.setVisibility(View.VISIBLE);
                        fabUnmatchUserProfile.setVisibility(View.VISIBLE);
                        fabReportUserProfile.setVisibility(View.VISIBLE);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        adopterReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Adopter adopter = snapshot.getValue(Adopter.class);
                dob = snapshot.child("dob").getValue(Date.class);
                if(adopter.getUserID().equals(user.getUid())){
                    fabEditUserDetails.setVisibility(View.VISIBLE);
                }
                userReference = FirebaseDatabase.getInstance().getReference().child("Users").child(adopter.getUserID());
                userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
                        Date now = new Date();
                        long diff = now.getTime() - dob.getTime();
                        TimeUnit time = TimeUnit.DAYS;
                        long difference = time.convert(diff, TimeUnit.MILLISECONDS)/365;
                        userName.setText(user.getFirstName() + " " + String.valueOf(difference));
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                userGender.setText(adopter.getGender());
                userOccupation.setText(adopter.getOccupation());
                userDescription.setText(adopter.getAboutMe());
                dbPicturesReference.orderByChild("profileID").equalTo(adopterID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<Picture> images = new ArrayList<>();
                        for (DataSnapshot ds : snapshot.getChildren()){
                            Picture picture = ds.getValue(Picture.class);
                            images.add(picture);
                        }
                        mViewPagerAdapter = new ViewPagerAdapter(UserProfile.this, images);
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
        topNavBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.ic_home:
                        startActivity(new Intent(UserProfile.this, LandingPage.class));
                        overridePendingTransition(0, 0);

                        break;
                    case R.id.ic_adoption_requests:
                        startActivity(new Intent(UserProfile.this, AdoptionRequests.class));
                        overridePendingTransition(0, 0);
                        break;
                    case R.id.ic_adopt:
                        startActivity(new Intent(UserProfile.this, Adopt.class));
                        overridePendingTransition(0, 0);
                        break;
                    case R.id.ic_matches:
                        startActivity(new Intent(UserProfile.this, Matches.class));
                        overridePendingTransition(0, 0);
                        break;
                    case R.id.ic_pets:
                        startActivity(new Intent(UserProfile.this, Pets.class));
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
            case (R.id.fabEditUserDetails):
                Intent intent = new Intent (view.getContext(), EditUserProfile.class);
                intent.putExtra("adopterID", adopterID);
                intent.putExtra("userID", user.getUid());
                view.getContext().startActivity(intent);
                break;
            case(R.id.fabSwipeRightUserProfile):
                Match matchAccept = new Match();
                matchAccept.setAdopterID(matchSwipe.getAdopterID());
                matchAccept.setPetID(matchSwipe.getPetID());
                matchAccept.setUserID(matchSwipe.getUserID());
                matchAccept.setAdopterUserID(matchSwipe.getAdopterUserID());
                matchAccept.setResponse(1);
                matchReference.push().setValue(matchAccept);
                deleteRef.removeValue();
                Chat chat = new Chat();
                chat.setUserID(matchAccept.getUserID());
                chat.setOtherUserID(matchAccept.getAdopterUserID());
                chatRef.push().setValue(chat);
                Intent intent1 = new Intent(view.getContext(), AdoptionRequests.class);
                view.getContext().startActivity(intent1);
                break;
            case(R.id.fabSwipeLeftUserProfile):
                Match matchReject = new Match();
                matchReject.setAdopterID(matchSwipe.getAdopterID());
                matchReject.setPetID(matchSwipe.getPetID());
                matchReject.setUserID(matchSwipe.getUserID());
                matchReject.setAdopterUserID(matchSwipe.getAdopterUserID());
                matchReject.setResponse(0);
                matchReference.push().setValue(matchReject);
                deleteRef.removeValue();
                Intent intent2 = new Intent(view.getContext(), AdoptionRequests.class);
                view.getContext().startActivity(intent2);
                break;
            case(R.id.fabOpenChatUserProfile):
                Intent intent3 = new Intent(view.getContext(), DirectMessage.class);
                if(user.getUid().equals(currentMatch.getAdopterUserID())){
                    intent3.putExtra("otherUserID", currentMatch.getUserID());
                }
                else{
                    intent3.putExtra("otherUserID", currentMatch.getAdopterUserID());
                }
                String otherUserID = currentMatch.getAdopterUserID();
                view.getContext().startActivity(intent3);
                break;
            case(R.id.fabUnmatchUserProfile):
                new AlertDialog.Builder(UserProfile.this).setTitle("Unmatch With Adopter").setMessage("Are you sure you want to unmatch? This action cannot be reversed").setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        chatRef.orderByChild("userID").equalTo(user.getUid()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.hasChildren()){
                                    for(DataSnapshot ds : snapshot.getChildren()){
                                        Chat chat = ds.getValue(Chat.class);
                                        if(chat.getOtherUserID().trim().equals(user.getUid().trim()) && chat.getUserID().trim().equals(alternateUserID.trim())){
                                            deleteReference = chatRef;
                                            deleteReference.child(ds.getKey()).removeValue();
                                        } else if(chat.getUserID().trim().equals(user.getUid().trim()) && chat.getOtherUserID().trim().equals(alternateUserID.trim())){
                                            deleteReference = FirebaseDatabase.getInstance().getReference("Chat");
                                            deleteReference.child(ds.getKey()).removeValue();
                                        }
                                    }

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        matchReference.orderByChild("userID").equalTo(user.getUid()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.hasChildren()){
                                    for(DataSnapshot ds : snapshot.getChildren()){
                                        Match match = ds.getValue(Match.class);
                                        if(match.getAdopterUserID() == currentMatch.getAdopterUserID() && match.getUserID() == match.getUserID()){
                                            deleteReference = matchReference;
                                            deleteReference.child(ds.getKey()).removeValue();
                                            Intent intent4 = new Intent(view.getContext(), Matches.class);
                                            view.getContext().startActivity(intent4);
                                        }
                                    }

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }).setNegativeButton(android.R.string.no, null).setIcon(android.R.drawable.ic_dialog_alert).show();

                break;
            case R.id.fabReportUserProfile:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(UserProfile.this);
                final View reportDialog = getLayoutInflater().inflate(R.layout.report_dialog, null);
                alertDialogBuilder.setView(reportDialog);
                alertDialogBuilder.setCancelable(true).setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Date reportDate = new Date();
                        spinnerReportReason = reportDialog.findViewById(R.id.spinnerReportReason);
                        editTextReportMessage = reportDialog.findViewById(R.id.editTextSuspensionReason);
                        String reportReason = spinnerReportReason.getSelectedItem().toString();
                        String reportMessage = editTextReportMessage.getText().toString();
                        Report report = new Report(userID, alternateUserID, reportReason, reportMessage, false, reportDate);
                        reportReference.push().setValue(report);
                    }
                }).setNegativeButton(android.R.string.no, null).setIcon(android.R.drawable.ic_dialog_alert).show();
                break;
        }
    }
}