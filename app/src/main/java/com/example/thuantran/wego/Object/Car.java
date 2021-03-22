package com.example.thuantran.wego.Object;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Car  implements Parcelable {

    public Car(){

    }

    public Car(String typeCar, String nameCar, String colorCar, String yearCar, String photoCar) {
        this.typeCar = typeCar;
        this.nameCar = nameCar;
        this.colorCar = colorCar;
        this.yearCar = yearCar;
        this.photoCar = photoCar;
    }

    @SerializedName("TypeCar")
    @Expose
    private String typeCar;
    @SerializedName("NameCar")
    @Expose
    private String nameCar;
    @SerializedName("ColorCar")
    @Expose
    private String colorCar;
    @SerializedName("YearCar")
    @Expose
    private String yearCar;

    @SerializedName("PhotoCar")
    @Expose
    private String photoCar;


    protected Car(Parcel in) {
        typeCar = in.readString();
        nameCar = in.readString();
        colorCar = in.readString();
        yearCar = in.readString();
        photoCar = in.readString();
    }

    public static final Creator<Car> CREATOR = new Creator<Car>() {
        @Override
        public Car createFromParcel(Parcel in) {
            return new Car(in);
        }

        @Override
        public Car[] newArray(int size) {
            return new Car[size];
        }
    };

public String getTypeCar() {
return typeCar;
}

public void setTypeCar(String typeCar) {
this.typeCar = typeCar;
}

public String getNameCar() { return nameCar; }

public void setNameCar(String nameCar) {
this.nameCar = nameCar;
}

public String getColorCar() {
return colorCar;
}

public void setColorCar(String colorCar) {
this.colorCar = colorCar;
}

public String getYearCar() {
return yearCar;
}

public void setYearCar(String yearCar) {
this.yearCar = yearCar;
}

public String getPhotoCar() {
        return photoCar;
    }

public void setPhotoCar(String PhotoCar) {
        this.photoCar = PhotoCar;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(typeCar);
        dest.writeString(nameCar);
        dest.writeString(colorCar);
        dest.writeString(yearCar);
        dest.writeString(photoCar);
    }
}