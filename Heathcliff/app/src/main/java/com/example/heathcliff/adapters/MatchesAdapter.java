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
import com.example.heathcliff.model.Adopter;
import com.example.heathcliff.model.Match;
import com.example.heathcliff.model.Pet;
import com.example.heathcliff.model.Picture;
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

public class MatchesAdapter extends RecyclerView.Adapter<MatchesAdapter.ViewHolder> {
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    List<Match> matchesList;
    List<Picture> pictureList;
    List<Picture> petPictures;
    List<User> usersList;
    Context context;
    String adopterID;
    private DatabaseReference adoptersReference = FirebaseDatabase.getInstance().getReference("Adopters");
    private FirebaseUser loggedInUser = FirebaseAuth.getInstance().getCurrentUser();

    public MatchesAdapter(Context context, List<Match> matchesList, List<Picture> pictureList, List<User> usersList, List<Picture> petPictures){
        this.pictureList = pictureList;
        this.matchesList = matchesList;
        this.context = context;
        this.usersList = usersList;
        this.petPictures = petPictures;
    }


    @NonNull
    @Override
    public MatchesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.match_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MatchesAdapter.ViewHolder holder, final int position) {
        Match match = matchesList.get(holder.getAdapterPosition());
        User user = usersList.get(holder.getAdapterPosition());
        String otherUserID;
        if(loggedInUser.getUid().equals(match.getAdopterUserID())){
            otherUserID = match.getUserID();
            holder.cv.getBackground().setTint(Color.WHITE);
        }
        else{
            otherUserID = match.getAdopterUserID();
        }
        holder.textMatch.setText(user.getFirstName());
        try {
            Picture picture = pictureList.get(holder.getAdapterPosition());
            Picasso.get().load(picture.getAccessToken()).into(holder.imgMatch);
        } catch(Exception exc){
            holder.imgMatch.setImageResource(R.drawable.pawprint);
        }
        try{
                    Picture picture = petPictures.get(holder.getAdapterPosition());
                    Picasso.get().load(picture.getAccessToken()).into(holder.imgPetMatch);
        }
        catch(Exception exc){
            holder.imgPetMatch.setImageResource(R.drawable.pawprint);
        }
        adoptersReference.orderByChild("userID").equalTo(otherUserID).addValueEventListener(new ValueEventListener() {
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
        holder.imgPetMatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (view.getContext(), PetProfile.class);
                intent.putExtra("petID", match.getPetID());
                view.getContext().startActivity(intent);
            }
        });
        holder.imgMatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (view.getContext(), UserProfile.class);
                intent.putExtra("userID", otherUserID);
                intent.putExtra("adopterID", adopterID);
                view.getContext().startActivity(intent);
            }
        });
        holder.cv.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                               Intent intent = new Intent (view.getContext(), DirectMessage.class);
                               intent.putExtra("otherUserID", otherUserID);
                                view.getContext().startActivity(intent);
                            }
                    });
            }


    @Override
    public int getItemCount() {
        return matchesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgMatch;
        ImageView imgPetMatch;
        TextView textMatch;
        CardView cv;
        public ViewHolder(View itemView) {
            super(itemView);
            imgPetMatch = (ImageView) itemView.findViewById(R.id.imgPet_matches);
            imgMatch = (ImageView) itemView.findViewById(R.id.imgUser_matches);
            textMatch = (TextView) itemView.findViewById(R.id.textUser_Matches);
            cv = (CardView) itemView.findViewById(R.id.matches_cv);
        }
    }
}
