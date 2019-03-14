package com.joinmedevelopment.joinme;

public class LocationReport {

    private String id;
    private String name;
    private String location;

    public LocationReport() {

    }

    public LocationReport(String id, String name, String location) {
        this.id = id;
        this.name = name;
        this.location = location;
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
}
