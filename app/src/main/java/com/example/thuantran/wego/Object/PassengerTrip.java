package com.example.thuantran.wego.Object;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PassengerTrip implements Parcelable {

    @SerializedName("ID")
    @Expose
    private String iD;
    @SerializedName("userID")
    @Expose
    private String userID;
    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("avatar")
    @Expose
    private String avatar;

    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("time")
    @Expose
    private String time;
    @SerializedName("depart")
    @Expose
    private String depart;
    @SerializedName("destination")
    @Expose
    private String destination;
    @SerializedName("nperson")
    @Expose
    private String nperson;
    @SerializedName("distance")
    @Expose
    private String distance;
    @SerializedName("duration")
    @Expose
    private String duration;
    @SerializedName("cost")
    @Expose
    private String cost;
    @SerializedName("nMessenger")
    @Expose
    private String nmessenger;
    @SerializedName("Stt")
    @Expose
    private String stt;
    @SerializedName("iddr")
    @Expose
    private String iddr;

    @SerializedName("onCreated")
    @Expose
    private String onCreated;

    @SerializedName("typeRequest")
    @Expose
    private String typeRequest;

    @SerializedName("reviewPa2Dr")
    @Expose
    private String reviewPa2Dr;

    @SerializedName("reviewDr2Pa")
    @Expose
    private String reviewDr2Pa;

    public PassengerTrip(){

    }

    public PassengerTrip(String name, String avatar, String userID, String date, String time, String depart, String destination,
                         String typeRequest , String nPerson, String distance, String duration, String cost,
                         String nMessenger, String stt, String iDdr, String reviewPa2Dr, String reviewDr2Pa, String onCreated) {
        this.name = name;
        this.avatar = avatar;
        this.userID = userID;
        this.date = date;
        this.time = time;
        this.depart = depart;
        this.destination = destination;
        this.nperson = nPerson;
        this.distance = distance;
        this.duration = duration;
        this.cost = cost;
        this.nmessenger = nMessenger;
        this.stt = stt;
        this.iddr = iDdr;
        this.onCreated = onCreated;
        this.typeRequest = typeRequest;
        this.reviewPa2Dr = reviewPa2Dr;
        this.reviewDr2Pa = reviewDr2Pa;

    }



    public static final Creator<PassengerTrip> CREATOR = new Creator<PassengerTrip>() {
        @Override
        public PassengerTrip createFromParcel(Parcel in) {
            return new PassengerTrip(in);
        }

        @Override
        public PassengerTrip[] newArray(int size) {
            return new PassengerTrip[size];
        }
    };

    public String getID() {
        return iD;
    }

    public void setID(String iD) {
        this.iD = iD;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getUserID() {
        return userID;
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

    public String getNPerson() {
        return nperson;
    }

    public void setNPerson(String nPerson) {
        this.nperson = nPerson;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getNMessenger() {
        return nmessenger;
    }

    public void setNMessenger(String nMessenger) {
        this.nmessenger = nMessenger;
    }


    public String getOnCreated() {
        return onCreated;
    }

    public void setOnCreated(String onCreated) {
        this.onCreated = onCreated;
    }


    public String getIDdr() {
        return iddr;
    }

    public void setIDdr(String iDdr) {
        this.iddr = iDdr;
    }

    public String getStt() {
        return stt;
    }

    public void setStt(String stt) {
        this.stt = stt;
    }

    public String getTypeRequest() {
        return typeRequest;
    }

    public void setTypeRequest(String stt) {
        this.typeRequest = stt;
    }


    public String getReviewPa2Dr() {
        return reviewPa2Dr;
    }

    public void setReviewPa2Dr(String reviewPa2Dr) {
        this.reviewPa2Dr = reviewPa2Dr;
    }

    public String getReviewDr2Pa() {
        return reviewDr2Pa;
    }

    public void setReviewDr2Pa(String reviewDr2Pa) {
        this.reviewDr2Pa = reviewDr2Pa;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    protected PassengerTrip(Parcel in) {
        iD = in.readString();
        name = in.readString();
        avatar = in.readString();
        userID = in.readString();
        date = in.readString();
        time = in.readString();
        depart = in.readString();
        destination = in.readString();
        nperson = in.readString();
        distance = in.readString();
        duration = in.readString();
        cost = in.readString();
        nmessenger = in.readString();
        iddr = in.readString();
        onCreated = in.readString();
        stt = in.readString();
        typeRequest = in.readString();
        reviewDr2Pa = in.readString();
        reviewPa2Dr = in.readString();
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(iD);
        dest.writeString(name);
        dest.writeString(avatar);
        dest.writeString(userID);
        dest.writeString(date);
        dest.writeString(time);
        dest.writeString(depart);
        dest.writeString(destination);
        dest.writeString(nperson);
        dest.writeString(distance);
        dest.writeString(duration);
        dest.writeString(cost);
        dest.writeString(nmessenger);
        dest.writeString(iddr);
        dest.writeString(onCreated);
        dest.writeString(stt);
        dest.writeString(typeRequest);
        dest.writeString(reviewDr2Pa);
        dest.writeString(reviewPa2Dr);
    }
}