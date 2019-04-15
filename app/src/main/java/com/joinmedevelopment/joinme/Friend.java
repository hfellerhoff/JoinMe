package com.joinmedevelopment.joinme;

import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.PopupWindow;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class Friend {
    private String id;
    private String name;
    private String idWhoAdded;
    private boolean isFriend;

    public Friend() {

    }

    public Friend(String id, boolean isFriend) {
        this(id, isFriend, FirebaseAuth.getInstance().getUid());
    }

    public Friend(String id, boolean isFriend, final String idToAddTo) {
        this(id,"Placeholder Name", isFriend, idToAddTo);

        retrieveName(idToAddTo);
    }

    public Friend(String id, String name, boolean isFriend, final String idToAddTo) {
        final String currentUserID = FirebaseAuth.getInstance().getUid();

        this.id = id;
        this.name = name;
        this.isFriend = isFriend;
        idWhoAdded = currentUserID;

        final String tempID = id;

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                GenericTypeIndicator<HashMap<String, Friend>> typeIndicator = new GenericTypeIndicator<HashMap<String, Friend>>() {};
                HashMap<String, Friend> friendsMap = dataSnapshot.child(idToAddTo).child("friends").getValue(typeIndicator);

                if (friendsMap == null) {
                    friendsMap = new HashMap<String, Friend>();
                }

                friendsMap.put(tempID, getFriend());
                databaseReference.child(idToAddTo).child("friends").setValue(friendsMap);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public String getId() {
        return id;
    }

    public boolean isFriend() {
        return isFriend;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void retrieveName(final String idToAddTo) {
        final String tempID = id;

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserInformation userInformation = dataSnapshot.child(tempID).getValue(UserInformation.class);
                name = userInformation.getName();

                GenericTypeIndicator<HashMap<String, Friend>> typeIndicator = new GenericTypeIndicator<HashMap<String, Friend>>() {};
                HashMap<String, Friend> friendsMap = dataSnapshot.child(idToAddTo).child("friends").getValue(typeIndicator);

                if (friendsMap == null) {
                    friendsMap = new HashMap<String, Friend>();
                }

                friendsMap.put(tempID, getFriend());
                databaseReference.child(idToAddTo).child("friends").setValue(friendsMap);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void setFriend(boolean friend) {
        isFriend = friend;
    }

    @Override
    public String toString() {
        return id + ", " + name + ", " + isFriend;
    }

    private Friend getFriend() {
        return this;
    }

    public String getIdWhoAdded() {return idWhoAdded;}
}
