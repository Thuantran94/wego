package com.example.thuantran.wego.Presenter.Trip;

import android.content.Context;

import com.example.thuantran.wego.Interface.Trip.Trip;
import com.example.thuantran.wego.Model.Trip.ModelTrip;
import com.example.thuantran.wego.Object.PassengerTrip;

import java.util.ArrayList;

public class PresenterTrip implements Trip.Presenter {

    private ModelTrip modelTrip;
    private Trip.View callback;
    public  PresenterTrip( Trip.View callback){
        this.callback = callback;
    }

    public void receivedHandleGetMultiTrip(Context context, String userID){
        modelTrip = new ModelTrip(this);
        modelTrip.handleGetMultiTrip(context, userID);

    }

    public void receivedHandleGetOneTrip(Context context, String trID){
        modelTrip = new ModelTrip(this);
        modelTrip.handleGetOneTrip(context, trID);

    }




    public void receivedHandleSelectTrip(ArrayList<PassengerTrip> passengerTripArrayList, int position){
        modelTrip = new ModelTrip(this);
        modelTrip.handleSelectTrip(passengerTripArrayList,position);

    }




    @Override
    public void onGetMultiTripSuccess(ArrayList<PassengerTrip> passengerTripArrayList) {
        callback.onGetMultiTripSuccess(passengerTripArrayList);

    }

    @Override
    public void onGetOneTripSuccess(PassengerTrip passengerTrip) {
        callback.onGetOneTripSuccess(passengerTrip);
    }


    @Override
    public void onGetTBPaTripFail(String err) {
        callback.onGetTBPaTripFail(err);
    }


    @Override
    public void haveDriverSelected(PassengerTrip passengerTrip) {
        callback.haveDriverSelected(passengerTrip);
    }

    @Override
    public void finalDriverSelected(PassengerTrip passengerTrip) {
        callback.finalDriverSelected(passengerTrip);
    }
}
