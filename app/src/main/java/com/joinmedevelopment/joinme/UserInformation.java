package com.joinmedevelopment.joinme;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class UserInformation {

    private FirebaseUser user;
    private DatabaseReference databaseUsers;

    private String userID;
    private String name;
    private String email;
    private ArrayList<String> friendIDList;
    private boolean reportSubmitted;
    private String reportID;

    public UserInformation() {
        this(false, null);
    }

    public UserInformation(boolean reportSubmitted, String reportID) {
        user = FirebaseAuth.getInstance().getCurrentUser();
        databaseUsers = FirebaseDatabase.getInstance().getReference("users");

        this.userID = user.getUid();
        this.name = user.getDisplayName();
        this.email = user.getEmail();
        this.friendIDList = new ArrayList<String>();
        this.reportSubmitted = reportSubmitted;
        this.reportID = reportID;

        updateUserInformation();
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

    public ArrayList<String> getFriendIDList() {
        return friendIDList;
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

    public void updateUserInformation() {
        databaseUsers.child(userID).setValue(this);
    }
}
