package com.example.thuantran.wego.Interface.User;

import com.example.thuantran.wego.Object.DriverTrip;
import com.example.thuantran.wego.Object.PassengerTrip;

import java.util.ArrayList;
import java.util.List;

public interface Passenger {

    interface Presenter{


        void onFindDriverSuccess(PassengerTrip trip);
        void onGetMultiDrTripSuccess(  List<DriverTrip> driverTrips);

    }

    interface View{


        void onFindDriverSuccess(PassengerTrip trip);
        void onGetMultiDrTripSuccess(  List<DriverTrip> driverTrips);


    }
}
