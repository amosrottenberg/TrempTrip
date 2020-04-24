package com.example.student.trempme;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * this class creates the user object
 */
public class User {
    @NonNull private String userId;
    private String fullName;
    private String phoneNumber;
    private String email;
    private String password;
    private String groupId;
    private List<String> myTrempsIds;
    private List<String> myTripsIds;

    public User(String userId, String groupId, String email, String password, String fullName,String phoneNumber,List<String> myTrempsIds,List<String> myTripsIds)
    {
        this.fullName=fullName;
        this.email=email;
        this.password=password;
        this.groupId=groupId;
        this.myTrempsIds=new ArrayList<>();
        this.myTripsIds=new ArrayList<>();
        this.userId=userId;
        this.phoneNumber=phoneNumber;
    }
    public User(){

    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public List<String> getMyTrempsIds() {
        return myTrempsIds;
    }

    public void setMyTrempsIds(List<String> myTrempsIds) {
        this.myTrempsIds = myTrempsIds;
    }

    public List<String> getMyTripsIds() {
        return myTripsIds;
    }

    public void setMyTripsIds(List<String> myTripsIds) {
        this.myTripsIds = myTripsIds;
    }
}
