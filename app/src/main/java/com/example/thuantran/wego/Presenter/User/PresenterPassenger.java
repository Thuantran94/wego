package com.example.thuantran.wego.Presenter.User;

import android.content.Context;

import com.example.thuantran.wego.Interface.User.Passenger;
import com.example.thuantran.wego.Model.User.ModelPassenger;
import com.example.thuantran.wego.Object.DriverTrip;
import com.example.thuantran.wego.Object.PassengerTrip;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.ArrayList;
import java.util.List;


public class PresenterPassenger implements Passenger.Presenter {

    private ModelPassenger modelPassenger;
    private Passenger.View callback;

    public  PresenterPassenger( Passenger.View callback){
        this.callback = callback;
    }




    public void receivedHandleFindDriver(String userID){
        modelPassenger = new ModelPassenger(this);
        modelPassenger.handleFindDriver(userID);
    }


    public void receivedHandleGetMultiDrTrip(LatLng currentPoint, float radius){
        modelPassenger = new ModelPassenger(this);
        modelPassenger.handleGetMultiDrTrip(currentPoint,radius);

    }

    @Override
    public void onFindDriverSuccess(PassengerTrip trip) {
        callback.onFindDriverSuccess(trip);

    }


    @Override
    public void onGetMultiDrTripSuccess(List<DriverTrip> driverTrips) {
        callback.onGetMultiDrTripSuccess(driverTrips);
    }



}
