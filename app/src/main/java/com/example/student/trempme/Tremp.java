package com.example.student.trempme;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.GeoDataApi;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * this class creates the trenp object
 */
public class Tremp {

    @NonNull private String trempId;
    private String fromId;
    private String toId;
    private long departureTime;
    private int numOfAvailableSits;
    private String userId;
    private boolean notificationSent;

    public Tremp( String trempId,String fromId,String toId,long departureTime, int numOfAvailableSits,String userId ){
        this.fromId=fromId;
        this.toId=toId;
        this.numOfAvailableSits=numOfAvailableSits;
        this.departureTime=departureTime;
        this.userId=userId;
        this.trempId=trempId;
        this.notificationSent=false;
    }

    public Tremp(){

    }

    public String getTrempId() {
        return trempId;
    }

    public void setTrempId(String trempId) {
        this.trempId = trempId;
    }

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public String getToId() {
        return toId;
    }

    public void setToId(String toId) {
        this.toId = toId;
    }

    public long getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(long departureTime) {
        this.departureTime = departureTime;
    }

    public int getNumOfAvailableSits() {
        return numOfAvailableSits;
    }

    public void setNumOfAvailableSits(int numOfAvailableSits) {
        this.numOfAvailableSits = numOfAvailableSits;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isNotificationSent() {
        return notificationSent;
    }

    public void setNotificationSent(boolean notificationSent) {
        this.notificationSent = notificationSent;
    }


}
