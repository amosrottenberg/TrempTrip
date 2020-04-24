package com.example.student.trempme;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * this class creates the group object
 */
public class Group {
    @NonNull private String groupId;
    private String groupName;
    private List<User> users;
    private List<Tremp> tremps;
    private List<Trip> trips;

    public Group(String groupId, String groupName){
        this.groupId=groupId;
        this.groupName=groupName;
        this.users=new ArrayList<>();
        this.tremps=new ArrayList<>();
        this.trips=new ArrayList<>();
    }

    public Group(){}

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<Tremp> getTremps() {
        return tremps;
    }

    public void setTremps(List<Tremp> tremps) {
        this.tremps = tremps;
    }

    public List<Trip> getTrips() {
        return trips;
    }

    public void setTrips(List<Trip> trips) {
        this.trips = trips;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
