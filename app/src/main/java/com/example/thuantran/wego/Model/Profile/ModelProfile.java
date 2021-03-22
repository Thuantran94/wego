package com.example.thuantran.wego.Model.Profile;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.thuantran.wego.DataAccess.Constant;
import com.example.thuantran.wego.Interface.Profile.Profile;
import com.example.thuantran.wego.R;
import com.example.thuantran.wego.Object.Car;
import com.example.thuantran.wego.Object.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class ModelProfile {
    private Profile.Presenter callback;
    private int REQUEST_CODE = 123;
    private static final String TAG = "ModeProfile";



    public ModelProfile( Profile.Presenter callback){
        this.callback = callback;
    }


    public void handleGetProfile(Context context, String userID){

        new Thread(() -> {
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            Query mData     = mDatabase.child(Constant.USER).child(userID).child(Constant.PROFILE);
            mData.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    callback.onGetProfileSuccess(user);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    callback.onGetProfileFail(context.getString(R.string.errorconnect));
                }
            });
        }).start();


    }


    public void handleGetProfile(Context context, ArrayList<String> selectedDriverIDList){

        new Thread(new Runnable() {
            @Override
            public void run() {
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                ArrayList<User> users       = new ArrayList<>();
                ArrayList<Car> cars         = new ArrayList<>();
                for (String key:selectedDriverIDList) {

                    Query mData = mDatabase.child(Constant.USER).orderByKey().equalTo(key);

                    mData.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                            User user = dataSnapshot.child(key).child(Constant.PROFILE).getValue(User.class);
                            Car   car = dataSnapshot.child(key).child(Constant.MCAR).getValue(Car.class);
                            users.add(user);
                            cars.add(car);

                            if (selectedDriverIDList.size()== users.size()){
                                callback.onGetProfileSuccess(users,cars); }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            callback.onGetProfileFail(context.getString(R.string.errorconnect));
                        }
                    });

                }


            }
        }).start();

    }



    public void handleGetTBCar(Context context, String userID){
        new Thread(() -> {
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            DatabaseReference myData    = mDatabase.child(Constant.USER).child(userID).child(Constant.MCAR);
            myData.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Car car = dataSnapshot.getValue(Car.class);
                    callback.onGetTBCarSuccess(car);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    callback.onGetTBCarFail(context.getString(R.string.errorconnect));
                }
            });
        }).start();


    }



    public void handleGetTBCar(Context context, ArrayList<String> selectedDriverIDList){
       new Thread(new Runnable() {
           @Override
           public void run() {




               DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
               ArrayList<Car> cars = new ArrayList<>();
               for (String key:selectedDriverIDList) {

                   Query mData = mDatabase.child(Constant.USER).orderByKey().equalTo(key);

                   mData.addListenerForSingleValueEvent(new ValueEventListener() {
                       @Override
                       public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                           Car car = dataSnapshot.child(Constant.MCAR).getValue(Car.class);
                           cars.add(car);

                           if (selectedDriverIDList.size() == cars.size()) {
                               callback.onGetTBCarSuccess(cars);
                           }

                       }

                       @Override
                       public void onCancelled(@NonNull DatabaseError databaseError) {
                           callback.onGetTBCarFail(context.getString(R.string.errorconnect));
                       }
                   });
               }

           }
       }).start();
    }


}
