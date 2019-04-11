package com.joinmedevelopment.joinme;

import android.support.annotation.NonNull;

import com.firebase.ui.auth.data.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class UserInformation {

    private FirebaseUser user;
    private DatabaseReference databaseUsers;

    private String userID;
    private String name;
    private String email;
    private boolean reportSubmitted;
    private String reportID;

    private HashMap<String, Friend> friends;

    public UserInformation() {
        this(false, null);
    }

    public UserInformation(UserInformation userInformation) {
        this(userInformation.reportSubmitted, userInformation.reportID);
    }

    public UserInformation(boolean reportSubmitted, String reportID) {
        user = FirebaseAuth.getInstance().getCurrentUser();
        databaseUsers = FirebaseDatabase.getInstance().getReference("users");

        this.userID = user.getUid();
        this.name = user.getDisplayName();
        this.email = user.getEmail();
        this.friends = new HashMap<String, Friend>();
        this.reportSubmitted = reportSubmitted;
        this.reportID = reportID;
    }



    public String getUserID() {
        return userID;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public HashMap<String, Friend> getFriends() {
        return friends;
    }

    public boolean isReportSubmitted() {
        return reportSubmitted;
    }

    public String getReportID() {
        return reportID;
    }

    public void createReport(String reportID) {
        this.reportSubmitted = true;
        this.reportID = reportID;

        updateUserInformation();
    }

    public void deleteReport() {
        this.reportSubmitted = false;
        this.reportID = null;

        updateUserInformation();
    }

    public void addFriend(String friendID, boolean isFriend) {
        friends.put(friendID, new Friend(friendID, isFriend, userID));
        updateUserInformation();
    }

    public void updateUserInformation() {
        databaseUsers.child(userID).setValue(this);
    }

    @Override
    public String toString() {
        return name + ", " + email;
    }
}
