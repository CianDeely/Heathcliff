package com.example.heathcliff.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.heathcliff.R;
import com.example.heathcliff.UI.PetProfile;
import com.example.heathcliff.UI.UserProfile;
import com.example.heathcliff.UI.ViewShelterRequest;
import com.example.heathcliff.model.Adopter;
import com.example.heathcliff.model.Pet;
import com.example.heathcliff.model.Picture;
import com.example.heathcliff.model.ShelterRegistrationRequest;
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

public class ShelterRequestsAdapter extends RecyclerView.Adapter<ShelterRequestsAdapter.ViewHolder>{
    List<ShelterRegistrationRequest> shelterRequestsList;
    List<User> usersList;
    List<Picture> pictureList;
    List<Adopter> adoptersList;
    List<String> requestIDs;
    Context context;
    private DatabaseReference adoptersReference = FirebaseDatabase.getInstance().getReference("Adopters");
    public ShelterRequestsAdapter(Context context, List<ShelterRegistrationRequest> requestsList, List<Picture> picturesList, List<User> usersList, List<String> requestIDs, List<Adopter> adoptersList){
        this.pictureList = picturesList;
        this.shelterRequestsList = requestsList;
        this.usersList = usersList;
        this.context = context;
        this.adoptersList = adoptersList;
        this.requestIDs = requestIDs;
    }

    @NonNull
    @Override
    public ShelterRequestsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shelter_request_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ShelterRequestsAdapter.ViewHolder holder, final int position) {
        ShelterRegistrationRequest request = shelterRequestsList.get(holder.getAdapterPosition());
        User user = usersList.get(holder.getAdapterPosition());
        holder.textShelterRequest.setText(user.getFirstName() + " " + user.getLastName() + " | " + request.getShelterName());
        Boolean hasPic = false;
        try {
            for (Picture pic : pictureList) {
                if (pic.getUserID().equals(request.getUserID())) {
                    Picasso.get().load(pic.getAccessToken()).into(holder.imgUser);
                    hasPic = true;
                }
            }
        } catch (Exception exc) {
            holder.imgUser.setImageResource(R.drawable.baseline_person_black_48);
        }
        if (hasPic.equals(false)) {
            holder.imgUser.setImageResource(R.drawable.baseline_person_black_48);
        }
        holder.imgUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adoptersReference.orderByChild("userID").equalTo(request.getUserID()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            String adopterID = ds.getKey();
                            Intent intent = new Intent(view.getContext(), UserProfile.class);
                            intent.putExtra("userID", request.getUserID());
                            intent.putExtra("adopterID", adopterID);
                            view.getContext().startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });
        holder.cv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ViewShelterRequest.class);
                intent.putExtra("requestID", requestIDs.get(holder.getAdapterPosition()));
                intent.putExtra("adopterName", user.getFirstName() + " " + user.getLastName());
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgUser;
        TextView textShelterRequest;
        CardView cv;
        public ViewHolder(View itemView) {
            super(itemView);
            imgUser = (ImageView) itemView.findViewById(R.id.imgShelterRequest);
            textShelterRequest = (TextView) itemView.findViewById(R.id.textShelterRequest);
            cv = (CardView) itemView.findViewById(R.id.shelter_requests_cv);
        }
    }

}
