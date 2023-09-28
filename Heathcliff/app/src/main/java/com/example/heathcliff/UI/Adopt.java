package com.example.heathcliff.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DiffUtil;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.example.heathcliff.R;
import com.example.heathcliff.adapters.CardStackAdapter;
import com.example.heathcliff.adapters.CardStackCallback;
import com.example.heathcliff.adapters.ViewPagerAdapter;
import com.example.heathcliff.model.Chat;
import com.example.heathcliff.model.Pet;
import com.example.heathcliff.model.Preference;
import com.example.heathcliff.model.Swipe;
import com.example.heathcliff.model.UserLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.StackFrom;
import com.yuyakaido.android.cardstackview.SwipeableMethod;

import java.util.ArrayList;
import java.util.List;

public class Adopt extends AppCompatActivity implements View.OnClickListener {
    FloatingActionButton fabPreferences;
    private CardStackLayoutManager manager;
    private CardStackAdapter adapter;
    private static final String TAG = "Adopt";
    private DatabaseReference petsReference;
    private DatabaseReference prefsPetsReference;
    private DatabaseReference profilesReference;
    Preference userPref = new Preference();
    FusedLocationProviderClient mFusedLocationClient;
    int PERMISSION_ID = 44;
    DatabaseReference locationsReference;
    Boolean distance;
    Boolean type;
    Boolean breed;
    Boolean coat;
    Boolean age;
    List<Pet> petsList = new ArrayList<>();
    List<String> petsIDs = new ArrayList<>();
    private DatabaseReference preferencesReference;
    ViewPager mViewPager;
    String userID;
    FirebaseUser user;
    String breedPref;
    String coatPref;
    String distancePref;
    String agePref;
    String typePref;
    Double userLat, userLong;
    String profileID;
    private DatabaseReference swipesReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adopt);
        userLat = 0.0;
        userLong = 0.0;
        swipesReference = FirebaseDatabase.getInstance().getReference("Swipes");
        locationsReference = FirebaseDatabase.getInstance().getReference("UserLocations");
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();
        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();
        mViewPager = (ViewPager)findViewById(R.id.viewPagerMain);
        petsReference = FirebaseDatabase.getInstance().getReference("Pets");
        prefsPetsReference = FirebaseDatabase.getInstance().getReference("Pets");
        profilesReference = FirebaseDatabase.getInstance().getReference("Adopters");
        breedPref = "";
        coatPref = "";
        distancePref = "";
        agePref = "";
        typePref = "";
        preferencesReference = FirebaseDatabase.getInstance().getReference("Preferences");
        fabPreferences = findViewById(R.id.fabPreferences);
        fabPreferences.setOnClickListener(this);
        CardStackView cardStackView = findViewById(R.id.card_stack_view);
        profilesReference.orderByChild("userID").equalTo(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()){
                    profileID = ds.getKey();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        manager = new CardStackLayoutManager(this, new CardStackListener() {
            @Override
            public void onCardDragging(Direction direction, float ratio) {
                Log.d(TAG, "onCardDragging: d= " + direction.name() + " ratio= " + ratio);
            }

            @Override
            public void onCardSwiped(Direction direction) {
                Log.d(TAG, "onCardSwiped: p= " + manager.getTopPosition() + " d= " +direction.name());
                if(direction == Direction.Right){
                    swipesReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Swipe rightSwipe = new Swipe();
                            rightSwipe.setPetID(petsIDs.get(manager.getTopPosition()-1));
                            rightSwipe.setAdopterID(profileID);
                            rightSwipe.setAdopterUserID(userID);
                            rightSwipe.setLike(1);
                            rightSwipe.setUserID(petsList.get(manager.getTopPosition()-1).getUserID());
                            swipesReference.push().setValue(rightSwipe);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                else if(direction == Direction.Top){
                }
                else if(direction == Direction.Left){
                    swipesReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Swipe leftSwipe = new Swipe();
                            leftSwipe.setAdopterID(profileID);
                            leftSwipe.setAdopterUserID(userID);
                            leftSwipe.setLike(0);
                            leftSwipe.setPetID(petsIDs.get(manager.getTopPosition()-1));
                            leftSwipe.setUserID(petsList.get(manager.getTopPosition()-1).getUserID());
                            swipesReference.push().setValue(leftSwipe);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
                else if(direction == Direction.Bottom){
                }

                if(manager.getTopPosition() == adapter.getItemCount() - 5){
                    paginate();
                }
            }

            @Override
            public void onCardRewound() {
            Log.d(TAG, "onCardRewound: p= " +manager.getTopPosition());
            }

            @Override
            public void onCardCanceled() {
                Log.d(TAG, "onCardCancelled: p= " +manager.getTopPosition());

            }

            @Override
            public void onCardAppeared(View view, int position) {
                TextView tv = view.findViewById(R.id.item_name);
                Log.d(TAG, "onCardAppeared: " + position + ", name: " + tv.getText());
            }

            @Override
            public void onCardDisappeared(View view, int position) {
                TextView tv = view.findViewById(R.id.item_name);
                Log.d(TAG, "onCardDisappeared: " + position + ", name: " + tv.getText());
            }
        });
        manager.setStackFrom(StackFrom.None);
        manager.setVisibleCount(3);
        manager.setTranslationInterval(8.0f);
        manager.setScaleInterval(0.95f);
        manager.setSwipeThreshold(0.3f);
        manager.setMaxDegree(20.0f);
        manager.setDirections(Direction.HORIZONTAL);
        manager.setCanScrollHorizontal(true);
        manager.setSwipeableMethod(SwipeableMethod.Manual);
        manager.setOverlayInterpolator(new LinearInterpolator());

        preferencesReference.orderByChild("userID").equalTo(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    userPref = ds.getValue(Preference.class);
                    typePref = userPref.getType();
                    breedPref = userPref.getBreed();
                    coatPref = userPref.getCoat();
                    distancePref = String.valueOf(userPref.getMaxDistance());
                    agePref = String.valueOf(userPref.getMaxAge());
                    preferencesReference.removeEventListener(this);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        petsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.hasChildren()) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            Pet pet = (ds.getValue(Pet.class));
                            type = true;
                            breed = true;
                            coat = true;
                            distance = true;
                            age = true;
                                    if(Integer.parseInt(agePref)<pet.getAge()){
                                        age = false;
                                    }
                                    if(!typePref.equals("")){
                                        if(!typePref.equals(pet.getType()) && !typePref.equals("Any Type")){
                                            type = false;
                                        }
                                        if(!breedPref.equals("")){
                                            if(!breedPref.equals(pet.getBreed()) && !breedPref.equals("Any Breed")){
                                                breed = false;
                                            }
                                        }
                                        if(!coatPref.equals("")) {
                                            if (!coatPref.equals(pet.getCoat()) && !coatPref.equals("Any Coat")) {
                                                coat = false;
                                            }
                                        }
                                        if(type != false && breed != false && coat != false && age != false && distance != false && !pet.getUserID().equals(userID)) {
                                            petsList.add(pet);
                                            petsIDs.add(ds.getKey());
                                        }
                                    } else{
                                        if(age != false && distance != false && !pet.getUserID().equals(userID)){
                                            petsList.add(pet);
                                            petsIDs.add(ds.getKey());
                                        }
                                    }
                        }
                    }
                    adapter = new CardStackAdapter(petsList, petsIDs, mViewPager, Adopt.this, userLat, userLong);
                    cardStackView.setAdapter(adapter);
                }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        cardStackView.setLayoutManager(manager);
        cardStackView.setItemAnimator(new DefaultItemAnimator());
        BottomNavigationView topNavBar = (BottomNavigationView) findViewById(R.id.topNavBar);
        Menu menu = topNavBar.getMenu();
        MenuItem menuItem = menu.getItem(2);
        menuItem.setChecked(true);
        topNavBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.ic_home:
                        startActivity(new Intent(Adopt.this, LandingPage.class));
                        overridePendingTransition(0,0);
                        break;
                    case R.id.ic_adoption_requests:
                        startActivity(new Intent(Adopt.this, AdoptionRequests.class));
                        overridePendingTransition(0,0);
                        break;
                    case R.id.ic_adopt:
                        startActivity(new Intent(Adopt.this, Adopt.class));
                        overridePendingTransition(0,0);
                        break;
                    case R.id.ic_matches:
                        startActivity(new Intent(Adopt.this, Matches.class));
                        overridePendingTransition(0,0);
                        break;
                    case R.id.ic_pets:
                        startActivity(new Intent(Adopt.this, Pets.class));
                        overridePendingTransition(0,0);
                        break;
                }
                return false;
            }
        });
    }

    private void paginate() {
        List<Pet> oldList = adapter.getPets();
        List<Pet> newList = new ArrayList<>(addList());
        CardStackCallback callback = new CardStackCallback(oldList, newList);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);
        adapter.setPets(newList);
        result.dispatchUpdatesTo(adapter);
    }

    private List<Pet> addList() {
        List<Pet> petsList = new ArrayList<>();
        petsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChildren()){
                    for(DataSnapshot ds: snapshot.getChildren()){
                        Pet pet = (ds.getValue(Pet.class));
                        petsList.add(pet);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return petsList;

    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.fabPreferences:
                startActivity(new Intent(this, Preferences.class));
                break;
        }
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
                            userLat = location.getLatitude();
                            userLong = location.getLongitude();
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
        com.google.android.gms.location.LocationRequest mLocationRequest = new com.google.android.gms.location.LocationRequest();
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
            userLat = mLastLocation.getLatitude();
            userLong = mLastLocation.getLongitude();
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
}