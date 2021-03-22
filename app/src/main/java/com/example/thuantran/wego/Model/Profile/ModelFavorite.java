package com.example.thuantran.wego.Model.Profile;

import androidx.annotation.NonNull;

import com.example.thuantran.wego.DataAccess.Constant;
import com.example.thuantran.wego.Interface.Profile.FavoriteAddress;
import com.example.thuantran.wego.Object.Favorite;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;


public class ModelFavorite {

    private FavoriteAddress.Presenter callback;

    public ModelFavorite(FavoriteAddress.Presenter callback){
        this.callback = callback;
    }


    
    public void getAllFavoriteAddress( String userID){

        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<Favorite> arrayList = new ArrayList<>();
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                DatabaseReference myData    = mDatabase.child(Constant.USER).child(userID).child(Constant.ADDRESS);

                myData.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.getValue() !=null){
                            for(DataSnapshot ds: dataSnapshot.getChildren()){
                                Favorite favorite = ds.getValue(Favorite.class);
                                Objects.requireNonNull(favorite).setName(ds.getKey());
                                arrayList.add(favorite);
                            }
                            callback.onGetAllFavoriteSuccess(arrayList);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        }).start();

    }

}
