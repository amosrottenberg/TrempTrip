package com.example.student.trempme;

/**
 * this class extends Tremp object
 */
public class TrempListObject extends Tremp {
    private String fromName;
    private String toName;
    private String userName;
    private String userPhoneNumber;

    public TrempListObject(Tremp tremp,String fromName,String toName, String userName,String userPhoneNumber){
        super(tremp.getTrempId(),tremp.getFromId(),tremp.getToId(),tremp.getDepartureTime(),tremp.getNumOfAvailableSits(),tremp.getUserId() );
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
