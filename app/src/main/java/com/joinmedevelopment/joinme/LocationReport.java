package com.joinmedevelopment.joinme;

import com.google.firebase.auth.FirebaseUser;

public class LocationReport {

    private String id;
    private String name;
    private String location;

    private long timeCreated;

    public LocationReport() {

    }

    public LocationReport(String id, String name, String location) {
        this.id = id;
        this.name = name;
        this.location = location;
        timeCreated = System.currentTimeMillis();
    }

    public String getID() {
        return id;
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
}