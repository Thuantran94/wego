package com.example.thuantran.wego.Presenter.Profile;

import android.content.Context;

import com.example.thuantran.wego.Interface.Profile.SMS;
import com.example.thuantran.wego.Model.Profile.ModelSMS;
import com.example.thuantran.wego.Object.PassengerTrip;
import com.example.thuantran.wego.Object.SMSContext;

import java.util.ArrayList;

public class PresenterSMS implements SMS.Presenter {

    private ModelSMS modelSMS;
    private SMS.View callback;


    public PresenterSMS (SMS.View callback){
        this.callback = callback;
    }


    public void receivedGetSMS(Context context,String trID){
        modelSMS = new ModelSMS(this);
        modelSMS.handleGetSMS(context,trID);

    }

    public void receivedGetLastSMS(Context context,String userID){
        modelSMS = new ModelSMS(this);
        modelSMS.handleGetLastSMS(context,userID);

    }

    public void receivedGetTrip(String trID){
        modelSMS = new ModelSMS(this);
        modelSMS.handleGetGetTrip(trID);

    }

    @Override
    public void onGetSmsSuccess(ArrayList<SMSContext> arraySMS) {
        callback.onGetSmsSuccess(arraySMS);
    }

    @Override
    public void onGetSmsFail(String err) {
        callback.onGetSmsFail(err);
    }

    @Override
    public void onGetLastSmsSuccess(ArrayList<SMSContext> arraySMS) {
        callback.onGetLastSmsSuccess(arraySMS);
    }

    @Override
    public void onGetLastSmsFail(String err) {
        callback.onGetLastSmsFail(err);
    }

    @Override
    public void onGetTripSuccess(PassengerTrip passengerTrip) {
        callback.onGetTripSuccess(passengerTrip);
    }

    @Override
    public void onGetTripFail(String err) {
        callback.onGetTripFail(err);
    }


}
