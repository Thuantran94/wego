package com.example.thuantran.wego.Object;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SMSContext {



    @SerializedName("UserID")
    @Expose
    private String userID;

    @SerializedName("Avatar")
    @Expose
    private String avatar;

    @SerializedName("Name")
    @Expose
    private String name;

    @SerializedName("TrID")
    @Expose
    private String trID;

    @SerializedName("Context")
    @Expose
    private String context;

    @SerializedName("Status")
    @Expose
    private String status;

    @SerializedName("Date")
    @Expose
    private String date;

    @SerializedName("Time")
    @Expose
    private String time;



    public SMSContext(){

    }

    public SMSContext(String userID, String avatar, String name, String trID, String context, String status,String date, String time) {
        this.userID = userID;
        this.avatar = avatar;
        this.name   = name;
        this.trID = trID;
        this.context = context;
        this.date = date;
        this.time = time;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String userAvatar) {
        this.avatar = userAvatar;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTrID() {
        return trID;
    }

    public void setTrID(String trID) {
        this.trID = trID;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
}
