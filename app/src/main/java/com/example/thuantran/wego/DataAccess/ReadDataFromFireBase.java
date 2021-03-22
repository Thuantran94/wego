package com.example.thuantran.wego.DataAccess;

import com.example.thuantran.wego.Object.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

public class ReadDataFromFireBase {

    private ReadDataFromFireBaseListener callback;

    public ReadDataFromFireBase(ReadDataFromFireBaseListener callback){
        this.callback = callback;
    }




    public void getUserFromFireBase(String userID){
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference myData    = mDatabase.child("USER").child(userID).child("PROFILE");

        myData.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);
                callback.onGetUserSuccess(user);

            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {
                callback.onGetUserFailed("err");
            }

        });


    }




}
