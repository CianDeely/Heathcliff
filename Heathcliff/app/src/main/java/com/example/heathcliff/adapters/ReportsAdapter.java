package com.example.heathcliff.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.heathcliff.R;
import com.example.heathcliff.UI.DirectMessage;
import com.example.heathcliff.UI.PetProfile;
import com.example.heathcliff.UI.UserProfile;
import com.example.heathcliff.UI.ViewReport;
import com.example.heathcliff.model.Adopter;
import com.example.heathcliff.model.Match;
import com.example.heathcliff.model.Picture;
import com.example.heathcliff.model.Report;
import com.example.heathcliff.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ReportsAdapter extends RecyclerView.Adapter<ReportsAdapter.ViewHolder> {
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    List<Report> reportsList;
    List<Picture> pictureList;
    List<Picture> reportedUserPictures;
    List<User> reportedUsersList;
    List<Adopter> adoptersList;
    List<Adopter> reportedAdoptersList;
    List<User> usersList;
    List<String> reportIDs;
    Context context;
    String adopterID;
    String reportedAdopterID;
    private DatabaseReference adoptersReference = FirebaseDatabase.getInstance().getReference("Adopters");

    public ReportsAdapter(Context context, List<Report> reportsList, List<Picture> pictureList, List<User> usersList, List<Picture> reportedUserPictures, List<Adopter> adoptersList, List<Adopter> reportedAdoptersList, List<User> reportedUsersList, List<String> reportIDs){
        this.pictureList = pictureList;
        this.reportsList = reportsList;
        this.context = context;
        this.usersList = usersList;
        this.reportedUserPictures = reportedUserPictures;
        this.adoptersList = adoptersList;
        this.reportedUsersList = reportedUsersList;
        this.reportedAdoptersList = reportedAdoptersList;
        this.reportIDs = reportIDs;
    }


    @NonNull
    @Override
    public ReportsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.report_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ReportsAdapter.ViewHolder holder, final int position) {
        Report report = reportsList.get(holder.getAdapterPosition());
        User user = usersList.get(holder.getAdapterPosition());
        User reportedUser = reportedUsersList.get(holder.getAdapterPosition());
        holder.cv.getBackground().setTint(Color.WHITE);
        holder.textReport.setText(user.getFirstName() + " reported " + reportedUser.getFirstName());
        try {
            Picture picture = pictureList.get(holder.getAdapterPosition());
            Picasso.get().load(picture.getAccessToken()).into(holder.imgReporterUserPic);
        } catch(Exception exc){
            holder.imgReporterUserPic.setImageResource(R.drawable.baseline_person_black_48);
        }
        try{
                    Picture picture = reportedUserPictures.get(holder.getAdapterPosition());
                    Picasso.get().load(picture.getAccessToken()).into(holder.imgReportedUserPic);
        }
        catch(Exception exc){
            holder.imgReportedUserPic.setImageResource(R.drawable.baseline_person_black_48);
        }
        adoptersReference.orderByChild("userID").equalTo(report.getReporterID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChildren()){
                    for(DataSnapshot ds : snapshot.getChildren()){
                        adopterID = ds.getKey();
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
                if(snapshot.hasChildren()){
                    for(DataSnapshot ds : snapshot.getChildren()){
                        reportedAdopterID = ds.getKey();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.imgReportedUserPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (view.getContext(), UserProfile.class);
                intent.putExtra("userID", report.getReportedID());
                intent.putExtra("adopterID", reportedAdopterID);
                view.getContext().startActivity(intent);
            }
        });
        holder.imgReporterUserPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (view.getContext(), UserProfile.class);
                intent.putExtra("userID", report.getReporterID());
                intent.putExtra("adopterID", adopterID);
                view.getContext().startActivity(intent);
            }
        });
        holder.cv.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                               Intent intent = new Intent (view.getContext(), ViewReport.class);
                               intent.putExtra("reportID", reportIDs.get(holder.getAdapterPosition()));
                               intent.putExtra("reportedName", reportedUser.getFirstName() + " " + reportedUser.getLastName());
                               intent.putExtra("reporterName", user.getFirstName() + " " + user.getLastName());
                               view.getContext().startActivity(intent);
                            }
                    });
            }


    @Override
    public int getItemCount() {
        return reportsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgReportedUserPic;
        ImageView imgReporterUserPic;
        TextView textReport;
        CardView cv;
        public ViewHolder(View itemView) {
            super(itemView);
            imgReportedUserPic = (ImageView) itemView.findViewById(R.id.imgReportedUser_Reports);
            imgReporterUserPic = (ImageView) itemView.findViewById(R.id.imgUser_Reports);
            textReport = (TextView) itemView.findViewById(R.id.textUser_Reports);
            cv = (CardView) itemView.findViewById(R.id.reports_cv);
        }
    }
}
