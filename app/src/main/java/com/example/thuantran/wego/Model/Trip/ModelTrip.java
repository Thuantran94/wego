package com.example.thuantran.wego.Model.Trip;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.thuantran.wego.DataAccess.Constant;
import com.example.thuantran.wego.Interface.Trip.Trip;
import com.example.thuantran.wego.Object.PassengerTrip;
import com.example.thuantran.wego.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ModelTrip {

    private Trip.Presenter callback;

    private static  final String TAG = "ModelTrip";



    public ModelTrip( Trip.Presenter callback){
        this.callback = callback;
    }

    public void handleGetMultiTrip(Context context, String userID){
        ArrayList<PassengerTrip> trips = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {

                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                Query mData     = mDatabase.child(Constant.TRIP_PASSENGER).orderByChild("userID").equalTo(userID);

                mData.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                      trips.clear();
                        for(DataSnapshot ds: dataSnapshot.getChildren()){

                            PassengerTrip trip = ds.getValue(PassengerTrip.class);
                            trip.setID(ds.getKey());
                            trips.add(trip);
                        }
                        callback.onGetMultiTripSuccess(trips);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        callback.onGetTBPaTripFail(context.getString(R.string.notrip));
                    }
                });
            }
        }).start();

    }

    public void handleGetOneTrip(Context context, String trID){

        new Thread(() -> {

            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            Query mData     = mDatabase.child(Constant.TRIP_PASSENGER).child(trID);

            mData.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                        PassengerTrip trip = dataSnapshot.getValue(PassengerTrip.class);

                        if (trip == null){

                            callback.onGetTBPaTripFail(context.getString(R.string.notrip));

                        }else {
                            trip.setID(dataSnapshot.getKey());
                            callback.onGetOneTripSuccess(trip);
                        }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    callback.onGetTBPaTripFail(context.getString(R.string.notrip));
                }
            });
        }).start();

    }



    public void handleSelectTrip(ArrayList<PassengerTrip> passengerTripArrayList, int position){

        PassengerTrip trip = passengerTripArrayList.get(position);

        if (!trip.getNMessenger().equals("0")) { // truong hop co tai xe chon chuyen di cua ban nMessenger > 0
            if( !trip.getIDdr().equals("0") ){ // da co nguoi chap nhan

                callback.finalDriverSelected(trip);

            }else{ // chon tai xe
                callback.haveDriverSelected(trip);
            }
        }
    }







}
