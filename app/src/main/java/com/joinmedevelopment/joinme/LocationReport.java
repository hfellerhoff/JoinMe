package com.joinmedevelopment.joinme;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LocationReport {

    private String reportID;
    private String name;
    private String location;
    private String userID;
    private long timeCreated;

    //constructors
    public LocationReport() {

    }

    public LocationReport(String reportID, String name, String location) {
        this(FirebaseAuth.getInstance().getUid(), reportID, name, location);
    }

    public LocationReport(String userID, String reportID, String name, String location) {
        this.userID = userID;
        this.reportID = reportID;
        this.name = name;
        this.location = location;
        timeCreated = System.currentTimeMillis();

        addReport();
    }

    //accessor methods for locations

    public String getUserID() {
        return userID;
    }

    public String getReportID() {
        return reportID;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public long getTimeCreated() {
        return timeCreated;
    }

    //creates location report in FireBase
    private void addReport() {
        DatabaseReference databaseReports = FirebaseDatabase.getInstance().getReference("reports");
        databaseReports.child(userID).setValue(this);

        DatabaseReference databaseUsers = FirebaseDatabase.getInstance().getReference("users");
        databaseUsers.child(userID).child("reportSubmitted").setValue(true);
        databaseUsers.child(userID).child("reportID").setValue(reportID);
    }

    @Override
    public String toString() {
        return "Report ID: " + reportID;
    }


}