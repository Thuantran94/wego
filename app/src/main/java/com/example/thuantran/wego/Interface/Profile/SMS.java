package com.example.thuantran.wego.Interface.Profile;

import com.example.thuantran.wego.Object.PassengerTrip;
import com.example.thuantran.wego.Object.SMSContext;

import java.util.ArrayList;

public interface SMS {

    interface Presenter{
        void onGetSmsSuccess(ArrayList<SMSContext> arraySMS);
        void onGetSmsFail(String err);
        void onGetLastSmsSuccess(ArrayList<SMSContext> arraySMS);
        void onGetLastSmsFail(String err);
        void onGetTripSuccess(PassengerTrip passengerTrip);
        void onGetTripFail(String err);
    }

    interface View{

        void onGetSmsSuccess(ArrayList<SMSContext> arraySMS);
        void onGetSmsFail(String err);
        void onGetLastSmsSuccess(ArrayList<SMSContext> arraySMS);
        void onGetLastSmsFail(String err);
        void onGetTripSuccess(PassengerTrip passengerTrip);
        void onGetTripFail(String err);
    }
}
