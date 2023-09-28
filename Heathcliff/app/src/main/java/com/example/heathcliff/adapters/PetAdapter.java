package com.example.heathcliff.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.heathcliff.R;
import com.example.heathcliff.UI.PetProfile;
import com.example.heathcliff.model.Pet;
import com.example.heathcliff.model.Picture;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PetAdapter extends RecyclerView.Adapter<PetAdapter.ViewHolder>{
    List<Pet> petList;
    List<Picture> pictureList;
    List<String> petIDs;
    Context context;
    private DatabaseReference petsReference = FirebaseDatabase.getInstance().getReference("Pets");
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    public PetAdapter(Context context, List<Pet> petList, List<Picture> pictureList, List<String> petIDs){
        this.pictureList = pictureList;
        this.petList = petList;
        this.context = context;
        this.petIDs = petIDs;
    }


    @NonNull
    @Override
    public PetAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pet_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PetAdapter.ViewHolder holder, final int position) {
        Pet pet = petList.get(holder.getAdapterPosition());
        String petName = pet.getName();
        holder.textPet.setText(pet.getName());
        try {
            for(Picture pic : pictureList){
                if(pic.getProfileID().equals(petIDs.get(holder.getAdapterPosition()))){
                    Picasso.get().load(pic.getAccessToken()).into(holder.imgPet);
                }
            }
        } catch(Exception exc){
            holder.imgPet.setImageResource(R.drawable.pawprint);
        }
        holder.cv.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                petsReference.orderByChild("userID").equalTo(user.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()){
                            Pet pet = ds.getValue(Pet.class);
                            if(pet.getName().equals(petName)){
                                Intent intent = new Intent (view.getContext(), PetProfile.class);
                                intent.putExtra("petID", ds.getKey());
                                view.getContext().startActivity(intent);
                            }
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
        return petList.size();
    }


public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPet;
        TextView textPet;
        CardView cv;
    public ViewHolder(View itemView) {
        super(itemView);
        imgPet = (ImageView) itemView.findViewById(R.id.imgPet);
        textPet = (TextView) itemView.findViewById(R.id.textPet);
        cv = (CardView) itemView.findViewById(R.id.cv);
    }
}
}
