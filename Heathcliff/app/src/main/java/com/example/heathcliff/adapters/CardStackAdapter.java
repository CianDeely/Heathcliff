package com.example.heathcliff.adapters;

import android.content.Context;
import android.location.Location;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.heathcliff.R;
import com.example.heathcliff.UI.Adopt;
import com.example.heathcliff.UI.PetProfile;
import com.example.heathcliff.model.Pet;
import com.example.heathcliff.model.Picture;
import com.example.heathcliff.model.UserLocation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CardStackAdapter extends RecyclerView.Adapter<CardStackAdapter.ViewHolder>{

    private List<String> petIDs;
    private List<Pet> pets;
    Context activityContext;
    ViewPager vp;
    DatabaseReference picturesReference = FirebaseDatabase.getInstance().getReference("Pictures");
    DatabaseReference locationsReference = FirebaseDatabase.getInstance().getReference("UserLocations");
    Double userLat;
    Double userLong;

    public CardStackAdapter(List<Pet> pets, List<String> petIDs, ViewPager vp, Context activityContext, Double userLat, Double userLong) {
        this.pets = pets;
        this.petIDs = petIDs;
        this.vp = vp;
        this.activityContext = activityContext;
        this.userLat = userLat;
        this.userLong = userLong;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(pets.get(position), petIDs.get(position));
    }

    @Override
    public int getItemCount() {
        return pets.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name, type, breed, petDistance;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.item_image);
            name = itemView.findViewById(R.id.item_name);
            type = itemView.findViewById(R.id.item_type);
            breed = itemView.findViewById(R.id.item_breed);
            petDistance = itemView.findViewById(R.id.item_distance);
        }

         void setData(Pet pet, String id) {
            name.setText(pet.getName() + ", " + pet.getAge());
            type.setText(String.valueOf(pet.getType()));
            breed.setText(pet.getBreed());

            picturesReference.orderByChild("profileID").equalTo(id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.hasChildren()){
                        for(DataSnapshot ds : snapshot.getChildren()){
                            Picture pic = ds.getValue(Picture.class);
                            if(pic.isProfilePicture()){
                                //images.add(picture);
                                Picasso.get().load(pic.getAccessToken()).into(image);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            locationsReference.orderByChild("userID").equalTo(pet.getUserID()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot ds : snapshot.getChildren()){
                        UserLocation petLocal = ds.getValue(UserLocation.class);
                        Double petLat, petLong;
                        petLat = petLocal.getLatitude();
                        petLong = petLocal.getLongitude();
                        Location petsLocation = new Location("PetLocation");
                        petsLocation.setLatitude(petLat);
                        petsLocation.setLongitude(petLong);
                        Location userLocation = new Location("UserLocation");
                        userLocation.setLatitude(userLat);
                        userLocation.setLongitude(userLong);
                        double distance = petsLocation.distanceTo(userLocation);
                        distance = distance/1000;
                        int finalDistance = (int) distance;
                        petDistance.setText(finalDistance+"KM Away");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
    }

    public List<Pet> getPets() {
        return pets;
    }

    public void setPets(List<Pet> pets) {
        this.pets = pets;
    }
}
