package com.example.heathcliff.adapters;

import android.content.Context;
import android.content.Intent;
import com.example.heathcliff.model.Message;
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

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder>{
    List<Message> messageList;
    List<Picture> pictureList;
    Context context;
    String chatID;
    private DatabaseReference messagesReference = FirebaseDatabase.getInstance().getReference("Pets");

    public MessageAdapter(Context context, List<Message> messageList, List<Picture> pictureList, String chatID){
        this.context = context;
        this.messageList = messageList;
        this.pictureList = pictureList;
        this.chatID = chatID;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {
        Message message = messageList.get(holder.getAdapterPosition());
        holder.textMessage.setText(message.getMessage());
        for(Picture pic : pictureList){
            if(pic.getUserID().equals(message.getSenderID())){
                try {
                    Picasso.get().load(pic.getAccessToken()).into(holder.imgUser);
                } catch(Exception exc){
                    holder.imgUser.setImageResource(R.drawable.pawprint);
                }
            }
        }

        holder.cv.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
            }
        });
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgUser;
        TextView textMessage;
        CardView cv;
        public ViewHolder(View itemView) {
            super(itemView);
            imgUser = (ImageView) itemView.findViewById(R.id.imgUser_message);
            textMessage = (TextView) itemView.findViewById(R.id.textMessage);
            cv = (CardView) itemView.findViewById(R.id.cv_message);
        }
    }
}
