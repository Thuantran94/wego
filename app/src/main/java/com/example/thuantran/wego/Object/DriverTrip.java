package com.example.thuantran.wego.Object;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DriverTrip {

    public DriverTrip(){

    }

    public DriverTrip(String userID,String avatar, String date, String time, String depart, String destination,
                      String nSeat,String requestCar,String repeat, String onCreated) {
        this.userID = userID;
        this.avatar = avatar;
        this.date = date;
        this.time = time;
        this.depart = depart;
        this.destination = destination;
        this.nSeat = nSeat;
        this.repeat = repeat;
        this.onCreated = onCreated;
        this.requestCar = requestCar;
    }

    @SerializedName("ID")
    @Expose
    private String iD;
    @SerializedName("UserID")
    @Expose
    private String userID;
    @SerializedName("Avatar")
    @Expose
    private String avatar;
    @SerializedName("Date")
    @Expose
    private String date;
    @SerializedName("Time")
    @Expose
    private String time;
    @SerializedName("Depart")
    @Expose
    private String depart;
    @SerializedName("Destination")
    @Expose
    private String destination;

    @SerializedName("nSeat")
    @Expose
    private String nSeat;
    @SerializedName("RequestCar")
    @Expose
    private String requestCar;
    @SerializedName("Repeat")
    @Expose
    private String repeat;
    @SerializedName("Oncreated")
    @Expose
    private String onCreated;


    public String getID() {
        return iD;
    }

    public void setID(String iD) {
        this.iD = iD;
    }

    public String getUserID() {
        return userID;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDepart() {
        return depart;
    }

    public void setDepart(String depart) {
        this.depart = depart;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }


    public String getSeat() {
        return nSeat;
    }

    public void setSeat(String nSeat) {
        this.nSeat = nSeat;
    }

    public String getRequestCar() {
        return requestCar;
    }

    public void setRequestCar(String requestCar) {
        this.requestCar = requestCar;
    }

    public String getRepeat() {
        return repeat;
    }

    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }


    public String getOnCreated() {
        return onCreated;
    }

    public void setOnCreated(String onCreated) {
        this.onCreated = onCreated;
    }

}