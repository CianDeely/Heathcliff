package com.example.heathcliff.UI;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.heathcliff.R;
import com.example.heathcliff.model.Report;
import com.example.heathcliff.model.Suspension;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class ViewReport extends AppCompatActivity implements View.OnClickListener {

    private TextView textViewReportDate, textViewReportReason, textViewReportMessage, textViewReporterName, textViewReportedName;
    private DatabaseReference reportReference;
    private DatabaseReference suspensionReference;
    private FloatingActionButton fabMarkReportAsSolved, fabDeleteReport, fabSuspendUserReport;
    String reportID;
    String reportedName, reporterName;
    Date reportDate = new Date();
    Report report;
    String userID;
    private DatabaseReference usersReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_report);
        usersReference = FirebaseDatabase.getInstance().getReference("Users");
        userID = FirebaseAuth.getInstance().getUid();
        suspensionReference = FirebaseDatabase.getInstance().getReference("Suspension");
        textViewReportDate = findViewById(R.id.textViewShelterName);
        textViewReportReason = findViewById(R.id.textViewShelterAddress);
        textViewReportMessage = findViewById(R.id.textViewRequestSubmittedBy);
        textViewReporterName = findViewById(R.id.textViewReporterName);
        textViewReportedName = findViewById(R.id.textViewReportedName);
        fabMarkReportAsSolved = findViewById(R.id.fabMarkReportAsSolved);
        fabDeleteReport = findViewById(R.id.fabDeleteReport);
        fabSuspendUserReport = findViewById(R.id.fabSuspendUserReport);
        fabMarkReportAsSolved.setOnClickListener(this);
        fabDeleteReport.setOnClickListener(this);
        fabSuspendUserReport.setOnClickListener(this);
        reportReference = FirebaseDatabase.getInstance().getReference("Report");
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            reportID = extras.getString("reportID");
            reportedName = extras.getString("reportedName");
            reporterName = extras.getString("reporterName");
        }
        reportReference.child(reportID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()){
                    report = snapshot.getValue(Report.class);
                    reportDate = report.getReportDate();
                    textViewReportMessage.setText(report.getReportMessage());
                    textViewReportReason.setText(report.getReason());
                    textViewReportDate.setText(reportDate.toString());
                    textViewReportedName.setText("ID: " + report.getReportedID() + " Name:" + reportedName);
                    textViewReporterName.setText("ID: " + report.getReporterID() + " Name:" + reporterName);

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.fabMarkReportAsSolved:
                new AlertDialog.Builder(ViewReport.this).setTitle("Mark as Resolved").setMessage("Are you sure you want to mark this report as resolved?").setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        report.setResolved(true);
                        reportReference.child(reportID).setValue(report);
                        startActivity(new Intent(ViewReport.this, Reports.class));
                    }
                    }).setNegativeButton(android.R.string.no, null).setIcon(android.R.drawable.ic_dialog_alert).show();
                break;
            case R.id.fabDeleteReport:
                new AlertDialog.Builder(ViewReport.this).setTitle("Delete Report").setMessage("Are you sure you want to delete this report? this action is irreversible").setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        reportReference.child(reportID).removeValue();
                        startActivity(new Intent(ViewReport.this, Reports.class));
                    }
                }).setNegativeButton(android.R.string.no, null).setIcon(android.R.drawable.ic_dialog_alert).show();
                break;
            case R.id.fabSuspendUserReport:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ViewReport.this);
                final View suspendDialog = getLayoutInflater().inflate(R.layout.suspend_dialog, null);
                alertDialogBuilder.setView(suspendDialog);
                alertDialogBuilder.setCancelable(true).setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    public void onClick(DialogInterface dialog, int id) {
                        Date endDate = new Date();
                        LocalDateTime localDateTime = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                        Spinner spinnerSupendReason = suspendDialog.findViewById(R.id.spinnerSuspensionReason);
                        Spinner spinnerSuspensionLength = suspendDialog.findViewById(R.id.spinnerSuspensionLength);
                        EditText editTextSuspendMessage = suspendDialog.findViewById(R.id.editTextSuspensionReason);
                        String suspensionReason = spinnerSupendReason.getSelectedItem().toString();
                        String suspensionMessage = editTextSuspendMessage.getText().toString();
                        String suspensionLength = spinnerSuspensionLength.getSelectedItem().toString();
                        if(suspensionLength.equals("Permanently")){
                            endDate = new Date(endDate.getTime() + TimeUnit.DAYS.toMillis(100000));
                            Toast.makeText(ViewReport.this, reportedName + " suspended permanently successfully", Toast.LENGTH_LONG).show();
                        }
                        else{
                        int suspendDays = Integer.parseInt(suspensionLength.replace("Days", "").trim());
                            endDate = new Date(endDate.getTime() + TimeUnit.DAYS.toMillis(suspendDays));
                            Toast.makeText(ViewReport.this, reportedName + " suspended successfully for " + suspensionLength, Toast.LENGTH_LONG).show();
                        }
                        Suspension suspension = new Suspension(report.getReportedID(), userID, suspensionReason, suspensionMessage, suspensionLength, endDate);
                        suspensionReference.push().setValue(suspension);
                    }
                }).setNegativeButton(android.R.string.no, null).setIcon(android.R.drawable.ic_dialog_alert).show();
                break;

        }

        }
}