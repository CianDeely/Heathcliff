package com.example.heathcliff.UI;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.heathcliff.R;
import com.example.heathcliff.model.Admin;
import com.example.heathcliff.model.Adopter;
import com.example.heathcliff.model.Picture;
import com.example.heathcliff.model.Suspension;
import com.example.heathcliff.model.User;
import com.example.heathcliff.model.UserLocation;
import com.example.heathcliff.parser.NdefMessageParser;
import com.example.heathcliff.record.ParsedNdefRecord;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class LandingPage extends AppCompatActivity implements View.OnClickListener {
    private Button signOut;
    private FirebaseUser user;
    private DatabaseReference reference;
    private DatabaseReference adoptersReference, adminReference;
    private String userID;
    private ImageView profilePic;
    private boolean isAdmin;
    private String firstName;
    private FloatingActionButton bigAdoptButton, bigProfileButton, bigSettingsButton, bigRegisterShelterButton;
    private FloatingActionButton bigSuspendButton, bigReportsButton;
    private TextView bigAdoptLabel, bigProfileLabel, bigReportsLabel, bigSuspendLabel;
    DatabaseReference suspensionReference;
    List<Picture> pictureList;
    NfcAdapter nfcAdapter;
    PendingIntent pendingIntent;
    FusedLocationProviderClient mFusedLocationClient;
    double userLatitude;
    double userLongitude;
    int PERMISSION_ID = 44;
    FirebaseDatabase database;
    DatabaseReference locationReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        suspensionReference = FirebaseDatabase.getInstance().getReference("Suspension");
        isAdmin = false;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();
        database = FirebaseDatabase.getInstance();
        locationReference = database.getReference("UserLocations");

        if (nfcAdapter == null) {
            Toast.makeText(this, "No NFC", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference();
        profilePic = (ImageView) findViewById(R.id.profilePicture);
        profilePic.setOnClickListener(this);
        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();
        suspensionReference.orderByChild("userID").equalTo(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Date now = new Date();
                for(DataSnapshot ds : snapshot.getChildren()){
                    Suspension suspension = ds.getValue(Suspension.class);
                    if(suspension.getUserID().equals(userID)){
                        if(suspension.getEndDate().after(now)){
                            Toast.makeText(LandingPage.this, "Logging you out as you are suspended until " + suspension.getEndDate().toString() + " for " + suspension.getSuspensionReason(), Toast.LENGTH_LONG).show();
                            FirebaseAuth.getInstance().signOut();
                            startActivity(new Intent(LandingPage.this, MainActivity.class));
                        } else{

                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        adoptersReference = FirebaseDatabase.getInstance().getReference("Adopters");
        adminReference = FirebaseDatabase.getInstance().getReference("Admin");
        reference = FirebaseDatabase.getInstance().getReference("Users");
        bigAdoptButton = findViewById(R.id.adoptBigButton);
        bigProfileButton = findViewById(R.id.bigProfileButton);
        bigSettingsButton = findViewById(R.id.bigSettingsButton);
        bigRegisterShelterButton = findViewById(R.id.fabRegisterShelter);
        bigRegisterShelterButton.setOnClickListener(this);
        bigAdoptButton.setOnClickListener(this);
        bigProfileButton.setOnClickListener(this);
        bigSettingsButton.setOnClickListener(this);
        bigReportsButton = findViewById(R.id.reportBigButton);
        bigSuspendButton = findViewById(R.id.suspendBigButton);
        bigReportsButton.setOnClickListener(this);
        bigSuspendButton.setOnClickListener(this);
        bigAdoptLabel = findViewById(R.id.bigAdoptLabel);
        bigProfileLabel = findViewById(R.id.bigProfileLabel);
        bigReportsLabel = findViewById(R.id.bigReportLabel);
        bigSuspendLabel = findViewById(R.id.bigSuspendLabel);

        adminReference.orderByChild("userID").equalTo(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()){
                    Admin admin = ds.getValue(Admin.class);
                    if(admin.getUserID().equals(userID)){
                        isAdmin = true;
                    }
                }
                if(isAdmin == true){
                    profilePic.setOnClickListener(null);
                    bigAdoptButton.setVisibility(View.GONE);
                    bigProfileButton.setVisibility(View.GONE);
                    bigReportsButton.setVisibility(View.VISIBLE);
                    bigSuspendButton.setVisibility(View.VISIBLE);
                    bigSuspendLabel.setVisibility(View.VISIBLE);
                    bigReportsLabel.setVisibility(View.VISIBLE);
                    bigAdoptLabel.setVisibility(View.GONE);
                    bigProfileLabel.setVisibility(View.GONE);
                    BottomNavigationView topNavBar = (BottomNavigationView) findViewById(R.id.topNavBar);
                    Menu menu = topNavBar.getMenu();
                    menu.clear();
                    topNavBar.inflateMenu(R.menu.admin_top_nav);
                   MenuItem menuItem = menu.getItem(0);
                    menuItem.setChecked(true);
                    topNavBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                            switch (item.getItemId()) {

                                case R.id.ic_home:
                                    startActivity(new Intent(LandingPage.this, LandingPage.class));
                                    overridePendingTransition(0, 0);
                                    break;
                                case R.id.ic_shelter_requests:
                                    startActivity(new Intent(LandingPage.this, ShelterRequests.class));
                                    overridePendingTransition(0, 0);
                                    break;
                                case R.id.ic_reports:
                                    startActivity(new Intent(LandingPage.this, Reports.class));
                                    overridePendingTransition(0, 0);
                                    break;
                                case R.id.ic_suspend:
                                    startActivity(new Intent(LandingPage.this, Suspend.class));
                                    overridePendingTransition(0, 0);
                                    break;
                            }
                            return false;
                        }
                    });
                }
                else{
                    bigAdoptButton.setVisibility(View.VISIBLE);
                    bigProfileButton.setVisibility(View.VISIBLE);
                    bigReportsButton.setVisibility(View.GONE);
                    bigSuspendButton.setVisibility(View.GONE);
                    bigSuspendLabel.setVisibility(View.GONE);
                    bigReportsLabel.setVisibility(View.GONE);
                    bigAdoptLabel.setVisibility(View.VISIBLE);
                    bigProfileLabel.setVisibility(View.VISIBLE);
                    BottomNavigationView topNavBar = (BottomNavigationView) findViewById(R.id.topNavBar);
                    Menu menu = topNavBar.getMenu();
                    MenuItem menuItem = menu.getItem(0);
                    menuItem.setChecked(true);
                    topNavBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.ic_home:
                                    startActivity(new Intent(LandingPage.this, LandingPage.class));
                                    overridePendingTransition(0, 0);
                                    break;
                                case R.id.ic_adoption_requests:
                                    startActivity(new Intent(LandingPage.this, AdoptionRequests.class));
                                    overridePendingTransition(0, 0);
                                    break;
                                case R.id.ic_adopt:
                                    startActivity(new Intent(LandingPage.this, Adopt.class));
                                    overridePendingTransition(0, 0);
                                    break;
                                case R.id.ic_matches:
                                    startActivity(new Intent(LandingPage.this, Matches.class));
                                    overridePendingTransition(0, 0);
                                    break;
                                case R.id.ic_pets:
                                    startActivity(new Intent(LandingPage.this, Pets.class));
                                    overridePendingTransition(0, 0);
                                    break;
                            }
                            return false;
                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        pictureList = new ArrayList<Picture>();
        DatabaseReference getImage = databaseReference.child("Pictures");
        adoptersReference.orderByChild("userID").equalTo(userID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                getImage.orderByChild("profileID").equalTo(snapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            Picture picture = ds.getValue(Picture.class);
                            pictureList.add(picture);
                        }
                        for (Picture pic : pictureList) {
                            if (pic.getType().equals("User")) {
                                if (pic.isProfilePicture() == true) {
                                    Picasso.get().load(pic.getAccessToken()).into(profilePic);
                                }
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
                @Override
                public void onChildChanged (@NonNull DataSnapshot snapshot, @Nullable String
                previousChildName){

                }

                @Override
                public void onChildRemoved (@NonNull DataSnapshot snapshot){

                }

                @Override
                public void onChildMoved (@NonNull DataSnapshot snapshot, @Nullable String
                previousChildName){

                }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
        signOut = (Button) findViewById(R.id.signOutButton);
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(LandingPage.this, MainActivity.class));
            }
        });
        final TextView textViewName = (TextView) findViewById(R.id.textViewName);
        final TextView textViewOccupation = (TextView) findViewById(R.id.textViewOccupation);
        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);
                if (userProfile != null) {
                    firstName = userProfile.getFirstName();
                    adoptersReference.orderByChild("userID").equalTo(userID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
                            Date dob = new Date();
                            String gender = "";
                            String aboutMe = "";
                            String occupation = "";
                            String locationID = "";
                            String userID = "";
                            Date now = new Date();
                            for(DataSnapshot ds : snapshot.getChildren()){
                                dob = ds.child("dob").getValue(Date.class);
                                gender = ds.child("gender").getValue(String.class);
                                aboutMe = ds.child("aboutMe").getValue(String.class);
                                occupation = ds.child("occupation").getValue(String.class);
                                locationID = ds.child("locationID").getValue(String.class);
                                userID = ds.child("userID").getValue(String.class);
                            }
                            long diff = now.getTime() - dob.getTime();
                            TimeUnit time = TimeUnit.DAYS;
                            long difference = time.convert(diff, TimeUnit.MILLISECONDS)/365;
                            textViewName.setText(firstName + ", " + String.valueOf(difference));
                            textViewOccupation.setText(occupation);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LandingPage.this, "Error displaying account details", Toast.LENGTH_LONG).show();
            }
        });
    }
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.adoptBigButton:
                startActivity(new Intent(this, Adopt.class));
                break;
            case R.id.profilePicture:
            case R.id.bigProfileButton:
                adoptersReference.orderByChild("userID").equalTo(userID).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        Intent intent = new Intent (v.getContext(), UserProfile.class);
                        intent.putExtra("adopterID", snapshot.getKey());
                        v.getContext().startActivity(intent);
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
                break;
            case R.id.bigSettingsButton:
                startActivity(new Intent(this, Settings.class));
                break;
            case R.id.reportBigButton:
                startActivity(new Intent(this, Reports.class));
                break;
            case R.id.suspendBigButton:
                startActivity(new Intent(this, Suspend.class));
                break;
            case R.id.fabRegisterShelter:
                startActivity(new Intent(this, RegisterAsShelter.class));
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
                            userLatitude = location.getLatitude();
                            userLongitude = location.getLongitude() ;
                            locationReference.orderByChild("userID").equalTo(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.hasChildren()){
                                        for(DataSnapshot DS : snapshot.getChildren()){
                                            if(DS.getValue(UserLocation.class).getUserID().equals(userID)){
                                                locationReference = FirebaseDatabase.getInstance().getReference().child("UserLocations").child(DS.getKey());
                                                UserLocation newLocation = new UserLocation(userLatitude, userLongitude, "Initial", userID);
                                                locationReference.setValue(newLocation);
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
                });
            } else {
                Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
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

    private String dumpTagData(Tag tag) {
        StringBuilder sb = new StringBuilder();
        byte[] id = tag.getId();
        sb.append("ID (hex): ").append(toHex(id)).append('\n');
        sb.append("ID (reversed hex): ").append(toReversedHex(id)).append('\n');
        sb.append("ID (dec): ").append(toDec(id)).append('\n');
        sb.append("ID (reversed dec): ").append(toReversedDec(id)).append('\n');
        String prefix = "android.nfc.tech.";
        sb.append("Technologies: ");
        for (String tech : tag.getTechList()) {
            sb.append(tech.substring(prefix.length()));
            sb.append(", ");
        }

        sb.delete(sb.length() - 2, sb.length());

        for (String tech : tag.getTechList()) {
            if (tech.equals(MifareClassic.class.getName())) {
                sb.append('\n');
                String type = "Unknown";
                try {
                    MifareClassic mifareTag = MifareClassic.get(tag);

                    switch (mifareTag.getType()) {
                        case MifareClassic.TYPE_CLASSIC:
                            type = "Classic";
                            break;
                        case MifareClassic.TYPE_PLUS:
                            type = "Plus";
                            break;
                        case MifareClassic.TYPE_PRO:
                            type = "Pro";
                            break;
                    }
                    sb.append("Mifare Classic type: ");
                    sb.append(type);
                    sb.append('\n');

                    sb.append("Mifare size: ");
                    sb.append(mifareTag.getSize() + " bytes");
                    sb.append('\n');

                    sb.append("Mifare sectors: ");
                    sb.append(mifareTag.getSectorCount());
                    sb.append('\n');

                    sb.append("Mifare blocks: ");
                    sb.append(mifareTag.getBlockCount());
                } catch (Exception e) {
                    sb.append("Mifare classic error: " + e.getMessage());
                }
            }

            if (tech.equals(MifareUltralight.class.getName())) {
                sb.append('\n');
                MifareUltralight mifareUlTag = MifareUltralight.get(tag);
                String type = "Unknown";
                switch (mifareUlTag.getType()) {
                    case MifareUltralight.TYPE_ULTRALIGHT:
                        type = "Ultralight";
                        break;
                    case MifareUltralight.TYPE_ULTRALIGHT_C:
                        type = "Ultralight C";
                        break;
                }
                sb.append("Mifare Ultralight type: ");
                sb.append(type);
            }
        }

        return sb.toString();
    }

    private String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = bytes.length - 1; i >= 0; --i) {
            int b = bytes[i] & 0xff;
            if (b < 0x10)
                sb.append('0');
            sb.append(Integer.toHexString(b));
            if (i > 0) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    private String toReversedHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; ++i) {
            if (i > 0) {
                sb.append(" ");
            }
            int b = bytes[i] & 0xff;
            if (b < 0x10)
                sb.append('0');
            sb.append(Integer.toHexString(b));
        }
        return sb.toString();
    }

    private long toDec(byte[] bytes) {
        long result = 0;
        long factor = 1;
        for (int i = 0; i < bytes.length; ++i) {
            long value = bytes[i] & 0xffl;
            result += value * factor;
            factor *= 256l;
        }
        return result;
    }

    private long toReversedDec(byte[] bytes) {
        long result = 0;
        long factor = 1;
        for (int i = bytes.length - 1; i >= 0; --i) {
            long value = bytes[i] & 0xffl;
            result += value * factor;
            factor *= 256l;
        }
        return result;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (checkPermissions()) {
            getLastLocation();
        }

        if (nfcAdapter != null) {
            if (!nfcAdapter.isEnabled())
                showWirelessSettings();

            nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
        }
    }

    private void showWirelessSettings() {
        Toast.makeText(this, "You need to enable NFC", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
        startActivity(intent);
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        resolveIntent(intent);
    }

    private void resolveIntent(Intent intent) {
        String action = intent.getAction();

        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage[] msgs;

            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];

                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }

            } else {
                byte[] empty = new byte[0];
                byte[] id = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
                Tag tag = (Tag) intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                byte[] payload = dumpTagData(tag).getBytes();
                NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, id, payload);
                NdefMessage msg = new NdefMessage(new NdefRecord[] {record});
                msgs = new NdefMessage[] {msg};
            }

            displayMsgs(msgs);
        }
    }

    private void displayMsgs(NdefMessage[] msgs) {
        if (msgs == null || msgs.length == 0)
            return;
        StringBuilder builder = new StringBuilder();
        List<ParsedNdefRecord> records = NdefMessageParser.parse(msgs[0]);
        final int size = records.size();

        for (int i = 0; i < size; i++) {
            ParsedNdefRecord record = records.get(i);
            String str = record.str();
            builder.append(str).append("\n");
        }
        Intent intent = new Intent (LandingPage.this, PetProfile.class);
        intent.putExtra("petID", builder.toString().trim());
        startActivity(intent);
    }
    }
