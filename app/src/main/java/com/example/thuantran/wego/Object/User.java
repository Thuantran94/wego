package com.example.thuantran.wego.Object;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User implements Parcelable {

    public User(){

    }

    public User(String userID ,String avatar, String name, String gender, String phone, String email, String points,
                String review,String nreview,String lat, String lng, String status,
                String ntriptotal,String invitecode, String invitedby, String onCreated) {

        this.userID  = userID;
        this.name    = name;
        this.gender  = gender;
        this.phone   = phone;
        this.email   = email;
        this.points  = points;
        this.review  = review;
        this.nreview = nreview;
        this.avatar  = avatar;
        this.lat     = lat;
        this.lng     = lng;
        this.status     = status;
        this.ntriptotal = ntriptotal;

        this.invitecode    = invitecode;
        this.invitedby    = invitedby;
        this.onCreated = onCreated;
    }


    public User( String phone, String name, String avatar) {

        this.name = name;
        this.phone = phone;
        this.avatar = avatar;
    }



    @SerializedName("userID")
    @Expose
    private String userID;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("pass")
    @Expose
    private String pass;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("points")
    @Expose
    private String points;
    @SerializedName("review")
    @Expose
    private String review;
    @SerializedName("nreview")
    @Expose
    private String nreview;
    @SerializedName("avatar")
    @Expose
    private String avatar;
    @SerializedName("lat")
    @Expose
    private String lat;
    @SerializedName("lng")
    @Expose
    private String lng;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("model")
    @Expose
    private String model;
    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("ntriptotal")
    @Expose
    private String ntriptotal;
    @SerializedName("invitedby")
    @Expose
    private String invitedby;
    @SerializedName("invitecode")
    @Expose
    private String invitecode;
    @SerializedName("onCreated")
    @Expose
    private String onCreated;


    private int ntriplimitPa;


    protected User(Parcel in) {
        userID = in.readString();
        name = in.readString();
        gender = in.readString();
        phone = in.readString();
        pass = in.readString();
        email = in.readString();
        points = in.readString();
        review = in.readString();
        nreview = in.readString();
        avatar = in.readString();
        lat    = in.readString();
        lng    = in.readString();
        status = in.readString();
        model  = in.readString();
        token = in.readString();
        ntriptotal = in.readString();
        ntriplimitPa = in.readInt();
        invitecode = in.readString();
        invitedby = in.readString();
        onCreated = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getPoints() {
        return points;
    }
    public void setPoints(String points) {
        this.points = points;
    }

    public String getReview() {
        return review;
    }
    public void setReview(String review) {
        this.review = review;
    }

    public String getNReview() {
        return nreview;
    }
    public void setNReview(String nreview) {
        this.nreview = nreview;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat; }

    public String getLng() {
        return lng;
    }
    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public String getModel() { return model; }
    public void setModel(String model) {
        this.model = model;
    }

    public String getInvitecode() {
        return invitecode;
    }
    public void setInvitecode(String invitecode) {
        this.invitecode = invitecode;
    }

    public String getInvitedby() {
        return invitedby;
    }
    public void setInvitedby(String invitedby) {
        this.invitedby = invitedby;
    }

    public String getNtriptotal() {
        return ntriptotal;
    }
    public void setNtriptotal(String n) {
        this.ntriptotal = n;
    }

    public int getNtriplimitPa() {
        return ntriplimitPa;
    }
    public void setNtriplimitPa(int n) {
        this.ntriplimitPa = n;
    }

    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }


    public String getOnCreated() {
        return onCreated;
    }

    public void setOnCreated(String onCreated) {
        this.onCreated = onCreated;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userID);
        dest.writeString(name);
        dest.writeString(gender);
        dest.writeString(phone);
        dest.writeString(pass);
        dest.writeString(email);
        dest.writeString(points);
        dest.writeString(review);
        dest.writeString(nreview);
        dest.writeString(avatar);
        dest.writeString(lat);
        dest.writeString(lng);
        dest.writeString(status);
        dest.writeString(model);
        dest.writeString(token);
        dest.writeString(ntriptotal);
        dest.writeInt(ntriplimitPa);
        dest.writeString(invitecode);
        dest.writeString(invitedby);
        dest.writeString(onCreated);
    }
}

