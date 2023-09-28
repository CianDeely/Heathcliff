package com.example.heathcliff.UI;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.heathcliff.R;
import com.example.heathcliff.model.Preference;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Preferences extends AppCompatActivity implements AdapterView.OnItemSelectedListener, SeekBar.OnSeekBarChangeListener, View.OnClickListener {

    Spinner spinnerPreferencesType;
    Spinner breedSpinner, coatSpinner;
    SeekBar distanceSeek, ageSeek;
    TextView maxDistanceLabel, maxAgeLabel;
    FloatingActionButton updatePreferencesButton;
    String userID;
    FirebaseUser user;
    private DatabaseReference preferencesReference;
    Preference preference;
    String type;
    Boolean changed = false;
    private DatabaseReference deleteRef;
    String breed;
    String coat;
    String distance;
    String age;
    Boolean firstTime;
    int firstTimeThingy = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);
        firstTime = true;
        breed = "";
        coat = "";
        distance = "";
        age = "";
        preference = new Preference();
        maxDistanceLabel = findViewById(R.id.textViewPreferencesDistanceLabel);
        maxAgeLabel = findViewById(R.id.textViewPreferencesAgeLabel);
        spinnerPreferencesType = (Spinner) findViewById(R.id.spinnerPreferencesType);
        breedSpinner = (Spinner) findViewById(R.id.spinnerPreferencesBreed);
        coatSpinner = (Spinner) findViewById(R.id.spinnerPreferencesCoat);
        spinnerPreferencesType.setOnItemSelectedListener(this);
        distanceSeek = findViewById(R.id.distanceSeekBarPreferences);
        ageSeek = findViewById(R.id.ageSeekBarPreferences);
        distanceSeek.setOnSeekBarChangeListener(this);
        ageSeek.setOnSeekBarChangeListener(this);
        updatePreferencesButton = findViewById(R.id.fabUpdatePreferences);
        updatePreferencesButton.setOnClickListener(this);
        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();
        type = "";
        preferencesReference = FirebaseDatabase.getInstance().getReference("Preferences");
        preferencesReference.orderByChild("userID").equalTo(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Preference pref = ds.getValue(Preference.class);
                    type = pref.getType();
                    breed = pref.getBreed();
                     coat = pref.getCoat();
                     distance = String.valueOf(pref.getMaxDistance());
                     age = String.valueOf(pref.getMaxAge());
                    preferencesReference.removeEventListener(this);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if(firstTimeThingy>1) {
             type = spinnerPreferencesType.getSelectedItem().toString();
        }
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(Preferences.this, R.array.dog_breeds_array_preferences, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> coatAdapter = ArrayAdapter.createFromResource(Preferences.this, R.array.dog_coats_array_preferences, android.R.layout.simple_spinner_item);

        if(firstTimeThingy<=1) {
         if (type.equals("")) {
        type = "Any Type";
        }
        }

        if(type.equals("Any Type")){
            breedSpinner.setEnabled(false);
            breedSpinner.setClickable(false);
            coatSpinner.setEnabled(false);
            coatSpinner.setClickable(false);
        }
        else if(type.equals("Dog")) {
            adapter = ArrayAdapter.createFromResource(Preferences.this, R.array.dog_breeds_array_preferences, android.R.layout.simple_spinner_item);
            coatAdapter = ArrayAdapter.createFromResource(Preferences.this, R.array.dog_coats_array_preferences, android.R.layout.simple_spinner_item);
            breedSpinner.setEnabled(true);
            breedSpinner.setClickable(true);
            coatSpinner.setEnabled(true);
            coatSpinner.setClickable(true);
        }
        else if(type.equals("Cat")){
            adapter = ArrayAdapter.createFromResource(Preferences.this, R.array.cat_breeds_array_preferences, android.R.layout.simple_spinner_item);
            coatAdapter = ArrayAdapter.createFromResource(Preferences.this, R.array.cat_coats_array_preferences, android.R.layout.simple_spinner_item);
            breedSpinner.setEnabled(true);
            breedSpinner.setClickable(true);
            coatSpinner.setEnabled(true);
            coatSpinner.setClickable(true);
        }
        else if(type.equals("Bird")){

            adapter = ArrayAdapter.createFromResource(Preferences.this, R.array.bird_breeds_array_preferences, android.R.layout.simple_spinner_item);
            coatAdapter = ArrayAdapter.createFromResource(Preferences.this, R.array.bird_coats_array, android.R.layout.simple_spinner_item);
            breedSpinner.setEnabled(true);
            breedSpinner.setClickable(true);
            coatSpinner.setEnabled(true);
            coatSpinner.setClickable(true);
        }
        else if(type.equals("Rodent")){

            adapter = ArrayAdapter.createFromResource(Preferences.this, R.array.rodent_breeds_array_preferences, android.R.layout.simple_spinner_item);
            coatAdapter = ArrayAdapter.createFromResource(Preferences.this, R.array.rodent_coats_array, android.R.layout.simple_spinner_item);
            breedSpinner.setEnabled(true);
            breedSpinner.setClickable(true);
            coatSpinner.setEnabled(true);
            coatSpinner.setClickable(true);
        }
        else if(type.equals("Reptile")){

            adapter = ArrayAdapter.createFromResource(Preferences.this, R.array.reptiles_breeds_array_preferences, android.R.layout.simple_spinner_item);
            coatAdapter = ArrayAdapter.createFromResource(Preferences.this, R.array.reptile_coats_array, android.R.layout.simple_spinner_item);
            breedSpinner.setEnabled(true);
            breedSpinner.setClickable(true);
            coatSpinner.setEnabled(true);
            coatSpinner.setClickable(true);
        }
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        breedSpinner.setAdapter(adapter);
        coatAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        coatSpinner.setAdapter(coatAdapter);
        if (breed != null && firstTimeThingy<=1) {
            int spinnerPosition = adapter.getPosition(breed);
            breedSpinner.setSelection(spinnerPosition);
        }
        if (type != null && firstTimeThingy<=1) {
            spinnerPreferencesType.setSelection(getIndex(spinnerPreferencesType, type));
        }
        if (coat != null && firstTimeThingy<=1) {

            int spinnerPosition = coatAdapter.getPosition(coat);

            coatSpinner.setSelection(spinnerPosition);
        }
        if(distance != null && distance != "" && firstTimeThingy<=1){
            distanceSeek.setProgress(Integer.parseInt(distance));
        }
        if(age != null && age != "" && firstTimeThingy<=1){
            ageSeek.setProgress(Integer.parseInt(age));
        }
        firstTimeThingy++;
    }
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
        switch(seekBar.getId()) {
            case R.id.distanceSeekBarPreferences:
            int val = (progress * (seekBar.getWidth() - 2 * seekBar.getThumbOffset())) / seekBar.getMax();
                maxDistanceLabel.setText("" + progress + "KM");
                maxDistanceLabel.setX(seekBar.getX() + val + seekBar.getThumbOffset() / 2);
            break;
            case R.id.ageSeekBarPreferences:
                int val2 = (progress * (seekBar.getWidth() - 2 * seekBar.getThumbOffset())) / seekBar.getMax();
                maxAgeLabel.setText("" + progress +" Years");
                maxAgeLabel.setX(seekBar.getX() + val2 + seekBar.getThumbOffset() / 2);
                break;
        }
    }
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.fabUpdatePreferences:
                String selectedType = spinnerPreferencesType.getSelectedItem().toString();
                String selectedBreed = breedSpinner.getSelectedItem().toString();
                String selectedCoat = coatSpinner.getSelectedItem().toString();
                String distance = maxDistanceLabel.getText().toString();
                distance = distance.replace("KM", "").trim();
                int selectedMaxDistance = 0;
                int selectedMaxAge = 0;
                if(!TextUtils.isDigitsOnly(distance)){
                    maxDistanceLabel.setError("Please set a valid maximum distance");
                    maxDistanceLabel.requestFocus();
                }else {
                    selectedMaxDistance = Integer.parseInt(distance);
                }
                String age = maxAgeLabel.getText().toString();
                age = age.replace("Years", "").trim();
                if(!TextUtils.isDigitsOnly(age)){
                    maxAgeLabel.setError("Please set a valid maximum age");
                    maxAgeLabel.requestFocus();
                }else{
                    selectedMaxAge = Integer.parseInt(age);
                }
                preference.setUserID(userID);
                preference.setBreed(selectedBreed);
                preference.setCoat(selectedCoat);
                preference.setMaxAge(selectedMaxAge);
                preference.setMaxDistance(selectedMaxDistance);
                preference.setType(selectedType);
                changePreferences();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                            Intent intent = new Intent (view.getContext(), Adopt.class);
                            view.getContext().startActivity(intent);
                            finish();
                        }
                }, 1000);
                break;
        }
    }
    public void changePreferences(){
        preferencesReference.orderByChild("userID").equalTo(userID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
               if(changed.equals(false)) {
                   deleteRef = FirebaseDatabase.getInstance().getReference("Preferences").child(snapshot.getKey());
                   deleteRef.removeValue();
               }
                changed = true;
                FirebaseDatabase.getInstance().getReference("Preferences").push().setValue(preference);
                preferencesReference.removeEventListener(this);
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
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                if(changed!=true){
                    FirebaseDatabase.getInstance().getReference("Preferences").push().setValue(preference);
                }            }
        }, 1000);



    }
    private int getIndex(Spinner spinner, String myString){
        int index = 0;
        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).equals(myString)){
                index = i;
            }
        }
        return index;
    }
}