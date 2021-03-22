package com.example.thuantran.wego.Model.User;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.thuantran.wego.DataAccess.Constant;
import com.example.thuantran.wego.Interface.User.Passenger;
import com.example.thuantran.wego.Object.DriverTrip;
import com.example.thuantran.wego.Object.PassengerTrip;
import com.example.thuantran.wego.R;
import com.example.thuantran.wego.Tools.Helper;
import com.example.thuantran.wego.View.Driver.DrTripActivity;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.github.florent37.singledateandtimepicker.SingleDateAndTimePicker;
import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ModelPassenger {


    private Passenger.Presenter callback;
    public ModelPassenger(Passenger.Presenter callback){
        this.callback = callback;
    }

    private static String TAG = "ModelPassenger";

    @SuppressLint("DefaultLocale")
    public void handleFindDriver( String userID){


        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        Query mData      = mDatabase.child(Constant.TRIP_PASSENGER).orderByChild("userID").equalTo(userID).limitToLast(1);
        mData.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                PassengerTrip trip = dataSnapshot.getValue(PassengerTrip.class);


                if (trip !=null){
                    // Đã có người chấp nhận
                    trip.setID(dataSnapshot.getKey());
                    if (Integer.valueOf(trip.getNMessenger())>0){
                        callback.onFindDriverSuccess(trip);
                    }
                }

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    public void handleGetMultiDrTrip(LatLng currentPoint, float radius){


        List<String> trIDs     = new ArrayList<>();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        GeoFire geoFire   = new GeoFire(mDatabase.child(Constant.GEOFIRE_LOCATION_TRIP_DRIVER));
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(currentPoint.getLatitude(),currentPoint.getLongitude()),radius);


        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {

                // Giới hạn số lượng chuyến đi lấy về
                if (trIDs.size() < 10){
                    trIDs.add(key);
                    getDataTrip(trIDs);
                }

            }

            @Override
            public void onKeyExited(String key) {

                trIDs.remove(key);


            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                if (trIDs.size()==0){
                    getDataTrip(trIDs);
                }

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });



    }



    private void getDataTrip( List<String> trIDs ){

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        List<DriverTrip> trips = new ArrayList<>();

        if (trIDs.size() == 0){
            callback.onGetMultiDrTripSuccess(trips);
        }else{
            for (String key:trIDs){

                Query mData = mDatabase.child(Constant.TRIP_DRIVER).orderByKey().equalTo(key);

                //
                mData.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot ds) {


                        new TaskGetDataTrip(key,trIDs,trips).execute(ds);


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });



            }

        }


    }


    private class TaskGetDataTrip extends AsyncTask<DataSnapshot,Void,Void> {


        private String key;
        private List<String>  trIDs;
        private List<DriverTrip> Drtrips;


        TaskGetDataTrip(String key, List<String> trIDs,List<DriverTrip> Drtrips ){
            this.key = key;
            this.trIDs = trIDs;
            this.Drtrips = Drtrips;
        }


        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(Void aVoid) {

            if (trIDs.size()== Drtrips.size()){
                callback.onGetMultiDrTripSuccess(Drtrips);

            }

        }

        @Override
        protected Void doInBackground(DataSnapshot... dataSnapshots) {

            DriverTrip trip = dataSnapshots[0].child(key).getValue(DriverTrip.class);
            if (trip != null){
                trip.setID(key);
                Drtrips.add(trip);



            }

            return null;
        }
    }



}
