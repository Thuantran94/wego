package com.example.thuantran.wego.Presenter.User;

import android.content.Context;

import com.example.thuantran.wego.Interface.User.Driver;
import com.example.thuantran.wego.Model.User.ModelDriver;
import com.example.thuantran.wego.Object.DriverTrip;
import com.example.thuantran.wego.Object.PassengerTrip;
import com.example.thuantran.wego.Object.User;
import com.firebase.geofire.GeoQuery;
import com.google.firebase.database.ValueEventListener;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.ArrayList;

public class PresenterDriver implements Driver.Presenter {
    private  Driver.View callback;
    private ModelDriver modelDriver;

    public PresenterDriver(Driver.View callback){
        this.callback =callback;
    }



    public void receiveHandleGetReceivedTrip( User user){
        modelDriver = new ModelDriver(this);
        modelDriver.handleGetReceivedTrip(user);

    }

    public void receivedHandleMultiDriverTrip(Context context, String userID){
        modelDriver = new ModelDriver(this);
        modelDriver.handleGetMultiDriverTrip(context,userID);

    }


    public void receivedHandleGetMultiPaTrip( String userID ,ArrayList<String[]> receivedList, LatLng currentPoint, float radius){
        modelDriver = new ModelDriver(this);
        modelDriver.handleGetMultiPaTrip(userID, receivedList,currentPoint,radius);

    }







    @Override
    public void onGetReceivedListSuccess(ArrayList<String[]> arrayList) {
        callback.onGetReceivedListSuccess(arrayList);

    }

    @Override
    public void onGetDetRefundSuccess(int refund) {
        callback.onGetDetRefundSuccess(refund);
    }


    @Override
    public void onGetMultiTripPaSuccess(ArrayList<PassengerTrip> arrayList) {
        callback.onGetMultiTripPaSuccess(arrayList);

    }

    @Override
    public void onGetMultiTripDriverSuccess(ArrayList<DriverTrip> arrayList) {
        callback.onGetMultiTripDriverSuccess(arrayList);

    }

    @Override
    public void onGetMultiTripDriverFail(String err) {
        callback.onGetMultiTripDriverFail(err);

    }

}
