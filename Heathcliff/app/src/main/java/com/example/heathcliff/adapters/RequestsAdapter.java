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
import com.example.heathcliff.model.Adopter;
import com.example.heathcliff.model.Pet;
import com.example.heathcliff.model.Picture;
import com.example.heathcliff.model.Swipe;
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

public class RequestsAdapter extends RecyclerView.Adapter<RequestsAdapter.ViewHolder>{
    List<Pet> petsList;
    List<User> usersList;
    List<Picture> pictureList;
    List<Adopter> adoptersList;
    List<Picture> petPictures;
    List<String> petIDs;
    Context context;
    private DatabaseReference adoptersReference = FirebaseDatabase.getInstance().getReference("Adopters");
    private DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("Users");

    public RequestsAdapter(Context context, List<Pet> petsList, List<User> usersList, List<Adopter> adoptersList, List<Picture> pictureList, List<Picture> petPictures, List<String> petIDs){
        this.pictureList = pictureList;
        this.petsList = petsList;
        this.usersList = usersList;
        this.context = context;
        this.adoptersList = adoptersList;
        this.petPictures = petPictures;
        this.petIDs = petIDs;
    }

    @NonNull
    @Override
    public RequestsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.swipe_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RequestsAdapter.ViewHolder holder, final int position) {
        Pet pet = petsList.get(holder.getAdapterPosition());
        User user = usersList.get(holder.getAdapterPosition());
        holder.textPet.setText(user.getFirstName() + " liked " + pet.getName());
        try {
            for(Picture pic : petPictures) {
                if(pic.getProfileID().equals(petIDs.get(holder.getAdapterPosition()))) {
                    Picasso.get().load(pic.getAccessToken()).into(holder.imgPet);
                }
            }
        } catch(Exception exc){
            holder.imgPet.setImageResource(R.drawable.pawprint);
        }
        try {
            userReference.orderByChild("email").equalTo(user.getEmail()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot ds : snapshot.getChildren()) {
                        for (Picture pic : pictureList) {
                            if (pic.getUserID().equals(ds.getKey())) {
                                Picasso.get().load(pic.getAccessToken()).into(holder.imgUser);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });{
            }

        } catch(Exception exc){
            holder.imgUser.setImageResource(R.drawable.pawprint);
        }
        holder.imgPet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (view.getContext(), PetProfile.class);
                intent.putExtra("petID", petIDs.get(holder.getAdapterPosition()));
                view.getContext().startActivity(intent);
            }
        });
        holder.cv.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                userReference.orderByChild("email").equalTo(user.getEmail()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds : snapshot.getChildren()){
                            String userID = ds.getKey();
                            adoptersReference.orderByChild("userID").equalTo(userID).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds : snapshot.getChildren()){
                                        Adopter adopter = ds.getValue(Adopter.class);
                                            Intent intent = new Intent (view.getContext(), UserProfile.class);
                                            intent.putExtra("adopterID", ds.getKey());
                                            view.getContext().startActivity(intent);
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

            }
        });
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPet;
        ImageView imgUser;
        TextView textPet;
        CardView cv;
        public ViewHolder(View itemView) {
            super(itemView);
            imgUser = (ImageView) itemView.findViewById(R.id.imgUser_requests);
            imgPet = (ImageView) itemView.findViewById(R.id.imgPet_requests);
            textPet = (TextView) itemView.findViewById(R.id.textPet_requests);
            cv = (CardView) itemView.findViewById(R.id.requests_cv);
        }
    }

}
