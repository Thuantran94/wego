package com.example.thuantran.wego.Object;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Relation  implements Parcelable  {


    @SerializedName("ID")
    @Expose
    private String iD;
    @SerializedName("IDPaTrip")
    @Expose
    private String idPaTrip;
    @SerializedName("IDdr")
    @Expose
    private String idDr;
    @SerializedName("Date")
    @Expose
    private String date;
    @SerializedName("Time")
    @Expose
    private String time;
    @SerializedName("Cost")
    @Expose
    private String cost;
    @SerializedName("Stt")
    @Expose
    private String stt;

    public Relation(){

    }




    protected Relation(Parcel in) {
        iD       = in.readString();
        idPaTrip = in.readString();
        idDr     = in.readString();
        date = in.readString();
        time = in.readString();
        cost = in.readString();
        stt = in.readString();
    }

    public static final Creator<Relation> CREATOR = new Creator<Relation>() {
        @Override
        public Relation createFromParcel(Parcel in) {
            return new Relation(in);
        }

        @Override
        public Relation[] newArray(int size) {
            return new Relation[size];
        }
    };

    public String getiD() {
        return iD;
    }

    public void setiD(String iD) {
        this.iD = iD;
    }

    public String getIDPaTrip() {
        return idPaTrip;
    }

    public void setIDPaTrip(String IDPaTrip) {
        this.idPaTrip = IDPaTrip;
    }

    public String getIDdr() {
        return idDr;
    }

    public void setIDdr(String IDdr) {
        this.idDr = IDdr;
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

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getStt() {
        return stt;
    }

    public void setStt(String stt) {
        this.stt = stt;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(iD);
        dest.writeString(idPaTrip);
        dest.writeString(idDr);
        dest.writeString(date);
        dest.writeString(time);
        dest.writeString(cost);
        dest.writeString(stt);
    }
}
