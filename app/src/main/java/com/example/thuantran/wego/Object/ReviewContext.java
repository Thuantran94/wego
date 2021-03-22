package com.example.thuantran.wego.Object;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ReviewContext {



    @SerializedName("Avatar")
    @Expose
    private String avatar;

    @SerializedName("Name")
    @Expose
    private String name;

    @SerializedName("Context")
    @Expose
    private String context;

    @SerializedName("Timestamp")
    @Expose
    private String timestamp;

    @SerializedName("Rate")
    @Expose
    private String rate;

    public ReviewContext(){

    }

    public ReviewContext(String avatar, String name, String context, String timestamp,String rate) {
        this.name = name;
        this.avatar = avatar;
        this.rate = rate;
        this.context = context;
        this.timestamp = timestamp;
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

    public void setAvatar(String userAvatar) {
        this.avatar = userAvatar;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
