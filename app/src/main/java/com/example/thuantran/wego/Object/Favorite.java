package com.example.thuantran.wego.Object;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Favorite implements Parcelable {

    public Favorite(){

    }

    public Favorite( double lat,double lng) {
        this.lat = lat;
        this.lng = lng;

    }

    public Favorite(String name, double lat,double lng) {
        this.name = name;
        this.lat = lat;
        this.lng = lng;

    }



    @SerializedName("Name")
    @Expose
    private String name;

    @SerializedName("Lat")
    @Expose
    private double lat;
    @SerializedName("Lng")
    @Expose
    private double lng;

    protected Favorite(Parcel in) {
        name = in.readString();
        lat = in.readDouble();
        lng = in.readDouble();

    }

    public static final Creator<Favorite> CREATOR = new Creator<Favorite>() {
        @Override
        public Favorite createFromParcel(Parcel in) {
            return new Favorite(in);
        }

        @Override
        public Favorite[] newArray(int size) {
            return new Favorite[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeDouble(lat);
        dest.writeDouble(lng);

    }
}

