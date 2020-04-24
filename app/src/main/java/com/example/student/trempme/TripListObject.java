package com.example.student.trempme;


/**
 * this class extends Trip object
 */
public class TripListObject extends Trip {
    private String fromName;
    private String toName;
    private String userName;
    private String userPhoneNumber;

    public TripListObject(Trip trip, String fromName,String toName, String userName, String userPhoneNumber){
        super(trip.getTripId(),trip.getFromId(),trip.getToId(),trip.getDepartureTime(),trip.getNumOfAvailableSits(),trip.getUserId(),trip.getTremps());
        this.fromName=fromName;
        this.toName=toName;
        this.userName=userName;
        this.userPhoneNumber=userPhoneNumber;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getToName() {
        return toName;
    }

    public void setToName(String toName) {
        this.toName = toName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhoneNumber() {
        return userPhoneNumber;
    }

    public void setUserPhoneNumber(String userPhoneNumber) {
        this.userPhoneNumber = userPhoneNumber;
    }
}
