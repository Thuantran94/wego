package com.example.thuantran.wego.Model.Profile;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.thuantran.wego.DataAccess.Constant;
import com.example.thuantran.wego.Interface.Profile.SMS;
import com.example.thuantran.wego.Object.PassengerTrip;
import com.example.thuantran.wego.Object.SMSContext;
import com.example.thuantran.wego.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class ModelSMS {

    private SMS.Presenter callback;
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    public ModelSMS (SMS.Presenter callback){ this.callback = callback;}



    public void handleGetSMS(Context context,String trID){

        new Thread(new Runnable() {
            @Override
            public void run() {

                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                Query mData     = mDatabase.child(Constant.SMS).child(trID).orderByKey();


                mData.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        ArrayList<SMSContext> arraySMS = new ArrayList<>();

                        for(DataSnapshot ds: dataSnapshot.getChildren()){

                            SMSContext sms = ds.getValue(SMSContext.class);
                            arraySMS.add(sms);

                        }
                        callback.onGetSmsSuccess(arraySMS);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        callback.onGetSmsFail(context.getString(R.string.errorconnect));
                    }
                });
            }
        }).start();

    }


    public void handleGetLastSMS(Context context,String userID){

       new Thread(new Runnable() {
           @Override
           public void run() {

               Query mData     = mDatabase.child(Constant.USER).child(userID).child(Constant.LAST_SMS).orderByKey();


               mData.addValueEventListener(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                       ArrayList<SMSContext> arraySMS = new ArrayList<>();


                       for(DataSnapshot ds: dataSnapshot.getChildren()){


                           SMSContext sms = ds.getValue(SMSContext.class);
                           arraySMS.add(sms);
                       }
                       callback.onGetLastSmsSuccess(arraySMS);

                   }

                   @Override
                   public void onCancelled(@NonNull DatabaseError databaseError) {
                       callback.onGetLastSmsFail(context.getString(R.string.errorconnect));
                   }
               });


           }
       }).start();

    }



    public void handleGetGetTrip(String trID){

        new Thread(new Runnable() {
            @Override
            public void run() {

                Query mData      = mDatabase.child(Constant.TRIP_PASSENGER).child(trID);

                mData.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        PassengerTrip trip = dataSnapshot.getValue(PassengerTrip.class);

                        if (trip == null){

                            callback.onGetTripFail("Tin nhắn đã bị xoá.");

                        }else {
                            trip.setID(dataSnapshot.getKey());
                            callback.onGetTripSuccess(trip);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });

    }



}
