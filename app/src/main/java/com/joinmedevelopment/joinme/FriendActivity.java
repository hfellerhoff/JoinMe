package com.joinmedevelopment.joinme;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.data.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class FriendActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.list_location_reports);

        final HashMap<String, Friend> friends = new HashMap<>();

        String path = FirebaseAuth.getInstance().getUid() +"/friends";

        final DatabaseReference databaseUsers = FirebaseDatabase.getInstance().getReference("users");
        final DatabaseReference databaseFriends = databaseUsers.child(path);

        databaseFriends.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                friends.clear();

                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Friend friend = snapshot.getValue(Friend.class);
                    friends.put(friend.getId(), friend);
                }

                FriendAdapter adapter = new FriendAdapter(friends);
                recyclerView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final Button buttonAddFriend = findViewById(R.id.buttonAddFriend);
        final EditText editTextFriendEmail = findViewById(R.id.editTextFriendEmail);

        buttonAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { databaseUsers.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        boolean emailFound = false;

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String editTextEntry = editTextFriendEmail.getText().toString();

                            UserInformation potentialFriend = snapshot.getValue(UserInformation.class);
                            HashMap<String, Friend> userFriends = potentialFriend.getFriends();

                            String currentUserID = FirebaseAuth.getInstance().getUid();

                            if (userFriends.containsKey(currentUserID)) {
                                Toast.makeText(getBaseContext(), "ERROR: Friend already added", Toast.LENGTH_SHORT).show();
                                emailFound = true;
                                break;
                            }
                            else if (editTextEntry.equals(potentialFriend.getEmail())) {
                                potentialFriend.addFriend(currentUserID, false);
                                editTextFriendEmail.setText("");
                                emailFound = true;
                                Toast.makeText(getBaseContext(), "Friend successfully added", Toast.LENGTH_SHORT).show();
                                break;
                            }
                        }

                        if (!emailFound) {
                            Toast.makeText(getBaseContext(), "ERROR: Email not found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });}
        });
    }
}
