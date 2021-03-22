package com.example.thuantran.wego.DataAccess;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.thuantran.wego.Object.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;


public class DeleteBookNowTripService extends IntentService {
    public DeleteBookNowTripService() {
        super("DeleteBookNowTripService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        if (intent != null) {
            User   user    = intent.getParcelableExtra("user");
            String trID    = intent.getStringExtra("trID");
            boolean refund = intent.getBooleanExtra("refund",false);

            if (user !=null && trID !=null){

                mDatabase.child(Constant.GEOFIRE_LOCATION_TRIP_PASSENGER).child(trID).removeValue();
                mDatabase.child(Constant.TRIP_PASSENGER).child(trID).removeValue();
                mDatabase.child(Constant.TRIP_PASSENGER_BOOKNOW).child(trID).removeValue();

                Map<String, Object> map = new HashMap<>();
                map.put("points",user.getPoints());
                map.put("ntriptotal",user.getNtriptotal());
                mDatabase.child(Constant.USER).child(user.getUserID()).child(Constant.PROFILE).updateChildren(map);

            }

            if (refund){

                mDatabase.child(Constant.mRELATION).orderByChild("idPaTrip").equalTo(trID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds: dataSnapshot.getChildren()) {
                            String key = ds.getKey();

                            if (key != null) {
                                Map<String, Object> map = new HashMap<>();
                                map.put("date","01/01/2000");
                                mDatabase.child(Constant.mRELATION).child(key).updateChildren(map);
                            }

                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

        }


    }
}



                /*
                AccessFireBase.removeTripWithOutRefund(trID, new IAccessFireBase.iRemoveTripWithOutRefund() {
                    @Override
                    public void onSuccess() {
                        Log.d("aadadadadadada","removeTripWithOutRefund onSuccess");
                        AccessFireBase.removeBookNow(trID, new IAccessFireBase.iRemoveBookNow() {
                            @Override
                            public void onSuccess() {
                                Log.d("aadadadadadada","removeBookNow onSuccess");
                                AccessFireBase.updateTotalTrip(user.getUserID(),user.getNtriptotal());
                            }

                            @Override
                            public void onFailed() { }
                        });

                    }
                    @Override
                    public void onFailed() { }
                });

                */