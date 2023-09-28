package com.example.heathcliff.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.heathcliff.R;
import com.example.heathcliff.model.Shelter;
import com.example.heathcliff.model.ShelterRegistrationRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ViewShelterRequest extends AppCompatActivity implements View.OnClickListener {
    private TextView textViewShelterName, textViewShelterAddress, textViewRequestSubmittedBy;
    private Button buttonDownloadDocumentation;
    private DatabaseReference shelterRegistrationRequestReference;
    private DatabaseReference shelterReference;
    private FloatingActionButton fabDenyShelterRequest, fabApproveShelterRequest;
    String  requestID, adopterName;
    ShelterRegistrationRequest request;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_shelter_request);
        shelterRegistrationRequestReference = FirebaseDatabase.getInstance().getReference("ShelterRegistrationRequest");
        shelterReference = FirebaseDatabase.getInstance().getReference("Shelter");
        textViewShelterName = findViewById(R.id.textViewShelterName);
        textViewShelterAddress = findViewById(R.id.textViewShelterAddress);
        textViewRequestSubmittedBy = findViewById(R.id.textViewRequestSubmittedBy);
        buttonDownloadDocumentation = findViewById(R.id.buttonDownloadDocumentation);
        fabApproveShelterRequest = findViewById(R.id.fabApproveShelterRequest);
        fabDenyShelterRequest = findViewById(R.id.fabDenyShelterRequest);
        buttonDownloadDocumentation.setOnClickListener(this);
        fabApproveShelterRequest.setOnClickListener(this);
        fabDenyShelterRequest.setOnClickListener(this);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            requestID = extras.getString("requestID");
            adopterName = extras.getString("adopterName");
        }
        shelterRegistrationRequestReference.child(requestID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()){
                    request = snapshot.getValue(ShelterRegistrationRequest.class);
                    textViewShelterName.setText(request.getShelterName());
                    textViewShelterAddress.setText(request.getShelterAddress());
                    textViewRequestSubmittedBy.setText(adopterName);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonDownloadDocumentation:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                browserIntent.setDataAndType(Uri.parse(request.getDocumentAccessToken()), "application/pdf");
                startActivity(browserIntent);
                break;
            case R.id.fabApproveShelterRequest:
                new AlertDialog.Builder(ViewShelterRequest.this).setTitle("Approve Shelter").setMessage("Are you sure you want to approve this shelter request?").setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        request.setApproved(true);
                        shelterRegistrationRequestReference.child(requestID).setValue(request);
                        Shelter shelter = new Shelter();
                        shelter.setShelterName(request.getShelterName());
                        shelter.setShelterAddress(request.getShelterAddress());
                        shelter.setUserID(request.getUserID());
                        shelterReference.push().setValue(shelter);
                        startActivity(new Intent(ViewShelterRequest.this, ShelterRequests.class));
                    }
                }).setNegativeButton(android.R.string.no, null).setIcon(android.R.drawable.ic_dialog_alert).show();
                break;
            case R.id.fabDenyShelterRequest:
                new AlertDialog.Builder(ViewShelterRequest.this).setTitle("Deny Shelter").setMessage("Are you sure you want to deny this shelter request?").setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        shelterRegistrationRequestReference.child(requestID).removeValue();
                        startActivity(new Intent(ViewShelterRequest.this, ShelterRequests.class));
                    }
                }).setNegativeButton(android.R.string.no, null).setIcon(android.R.drawable.ic_dialog_alert).show();
                break;
        }
    }
}