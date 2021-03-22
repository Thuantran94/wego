package com.example.thuantran.wego.Interface.User;

import com.example.thuantran.wego.Object.DriverTrip;
import com.example.thuantran.wego.Object.PassengerTrip;
import com.firebase.geofire.GeoQuery;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public interface Driver {

    interface Presenter{

        void onGetReceivedListSuccess(ArrayList<String[]> arrayList);
        void onGetDetRefundSuccess(int refund);


        void onGetMultiTripPaSuccess(ArrayList<PassengerTrip> arrayList);

        void onGetMultiTripDriverSuccess(ArrayList<DriverTrip> arrayList);
        void onGetMultiTripDriverFail(String err);

    }

    interface View{

        void onGetReceivedListSuccess(ArrayList<String[]> arrayList);
        void onGetDetRefundSuccess(int refund);


        void onGetMultiTripPaSuccess(ArrayList<PassengerTrip> arrayList);

        void onGetMultiTripDriverSuccess(ArrayList<DriverTrip> arrayList);
        void onGetMultiTripDriverFail(String err);

    }
}
