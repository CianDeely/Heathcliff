package com.example.heathcliff.UI;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.example.heathcliff.adapters.PetAdapter;
import com.example.heathcliff.model.Message;

import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.heathcliff.R;
import com.example.heathcliff.adapters.MessageAdapter;
import com.example.heathcliff.model.Chat;
import com.example.heathcliff.model.Pet;
import com.example.heathcliff.model.Picture;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ktx.Firebase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class DirectMessage extends AppCompatActivity {

    RecyclerView recyclerView;
    MessageAdapter messageAdapter;
    ArrayList<Message> messages;
    Button sendMessage;
    EditText composeMessage;
    ArrayList<Picture> pictures;
    private FirebaseUser user;
    private DatabaseReference reference;
    private DatabaseReference adoptersReference;
    private DatabaseReference picturesReference;
    private DatabaseReference chatsReference;
    private DatabaseReference messageReference;
    private DatabaseReference newMessageReference;
    private DatabaseReference chatReference;
    String otherUserID;
    String chatID;
    Date messageTimeNow;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direct_message);
        composeMessage = (EditText) findViewById(R.id.editTextComposeMessage);
        sendMessage = findViewById(R.id.btnSendMessage);
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        chatsReference = FirebaseDatabase.getInstance().getReference("Chat");
        picturesReference = FirebaseDatabase.getInstance().getReference("Pictures");
        adoptersReference = FirebaseDatabase.getInstance().getReference("Adopters");
        messages = new ArrayList<Message>();
        sendMessage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                newMessageReference = messageReference;
                Message newMessage = new Message();
                String textMessage = composeMessage.getText().toString().trim();
                if(textMessage.length()>255){
                    composeMessage.requestFocus();
                    composeMessage.setError("Please enter a message that is less than 255 characters");
                }
                else if(textMessage.isEmpty()){
                    composeMessage.requestFocus();
                    composeMessage.setError("Please enter a message");
                }
                else{
                    newMessage.setMessage(composeMessage.getText().toString().trim());
                    newMessage.setSenderID(user.getUid());
                    newMessage.setRecipientID(otherUserID);
                    messageTimeNow = new Date();
                    newMessage.setTimeSent(messageTimeNow);
                    newMessage.setChatID(chatID);
                    newMessageReference.push().setValue(newMessage);
                    recyclerView.setAdapter(new MessageAdapter(DirectMessage.this, messages, pictures, chatID));
                    recyclerView.invalidate();
                    finish();
                    startActivity(getIntent());
                }

            }
        });
        pictures = new ArrayList<Picture>();
        String userID = user.getUid();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            otherUserID = extras.getString("otherUserID");
        }
        chatsReference.orderByChild("userID").equalTo(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Chat currentChat = new Chat();
                for(DataSnapshot ds : snapshot.getChildren()){
                    Chat chat = ds.getValue(Chat.class);
                    if(chat.getOtherUserID().equals(otherUserID)){
                        currentChat = chat;
                        chatID = ds.getKey();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        chatsReference.orderByChild("userID").equalTo(otherUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Chat currentChat = new Chat();
                for(DataSnapshot ds : snapshot.getChildren()){
                    Chat chat = ds.getValue(Chat.class);
                    if(chat.getOtherUserID().equals(userID)){
                        currentChat = chat;
                        chatID = ds.getKey();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        picturesReference.orderByChild("userID").equalTo(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot ds : snapshot.getChildren()){
                    Picture picture = ds.getValue(Picture.class);
                    if(picture.getType().equals("User")){
                        if(picture.isProfilePicture() == true){
                            pictures.add(picture);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        picturesReference.orderByChild("userID").equalTo(otherUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()){
                    Picture picture = ds.getValue(Picture.class);
                    if(picture.getType().equals("User")){
                        if(picture.isProfilePicture() == true){
                            pictures.add(picture);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                chatReference = FirebaseDatabase.getInstance().getReference("Chat").child(chatID);
                messageReference = chatReference.child("Messages");
                messageReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds : snapshot.getChildren()){
                            Message newMessage = new Message();
                               Date timeSent = new Date();
                               timeSent = ds.child("timeSent").getValue(Date.class);
                               String chatID = ds.child("chatID").getValue().toString();
                               String message = ds.child("message").getValue().toString();
                               String recipientID = ds.child("recipientID").getValue().toString();
                               String senderID = ds.child("senderID").getValue().toString();
                               newMessage.setChatID(chatID);
                               newMessage.setMessage(message);
                               newMessage.setRecipientID(recipientID);
                               newMessage.setSenderID(senderID);
                               newMessage.setTimeSent(timeSent);
                            messages.add(newMessage);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                Collections.sort(messages, Collections.reverseOrder());

                         }
        }, 500);

        Handler handler1 = new Handler();
        handler1.postDelayed(new Runnable() {
            public void run() {
                recyclerView = (RecyclerView)findViewById(R.id.directMessageRecyclerView);
                LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
                llm.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(llm);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                messageAdapter = new MessageAdapter(DirectMessage.this, messages, pictures, chatID);
                recyclerView.setAdapter(messageAdapter);             }
        }, 1000);

        BottomNavigationView topNavBar = (BottomNavigationView) findViewById(R.id.topNavBar);
        Menu menu = topNavBar.getMenu();
        MenuItem menuItem = menu.getItem(3);
        menuItem.setChecked(true);
        topNavBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.ic_home:
                        startActivity(new Intent(DirectMessage.this, LandingPage.class));
                        overridePendingTransition(0,0);
                        break;
                    case R.id.ic_adoption_requests:
                        startActivity(new Intent(DirectMessage.this, AdoptionRequests.class));
                        overridePendingTransition(0,0);
                        break;
                    case R.id.ic_adopt:
                        startActivity(new Intent(DirectMessage.this, Adopt.class));
                        overridePendingTransition(0,0);
                        break;
                    case R.id.ic_matches:
                        startActivity(new Intent(DirectMessage.this, Matches.class));
                        overridePendingTransition(0,0);
                        break;
                    case R.id.ic_pets:
                        startActivity(new Intent(DirectMessage.this, Pets.class));
                        overridePendingTransition(0,0);
                        break;
                }
                return false;
            }
        });

    }
}

