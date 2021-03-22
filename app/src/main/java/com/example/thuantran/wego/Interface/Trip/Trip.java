package com.example.thuantran.wego.Interface.Trip;

import com.example.thuantran.wego.Object.PassengerTrip;

import java.util.ArrayList;

public interface Trip {

    interface Presenter{
        void onGetMultiTripSuccess(  ArrayList<PassengerTrip> passengerTripArrayList);
        void onGetOneTripSuccess(  PassengerTrip passengerTrip);
        void onGetTBPaTripFail(String err);




        void haveDriverSelected(PassengerTrip trip);
        void finalDriverSelected(PassengerTrip trip);
    }

    interface View{

        void onGetMultiTripSuccess(ArrayList<PassengerTrip> passengerTripArrayList);
        void onGetOneTripSuccess(  PassengerTrip passengerTrip);
        void onGetTBPaTripFail(String err);


        void haveDriverSelected(PassengerTrip trip);
        void finalDriverSelected(PassengerTrip trip);
    }
}
