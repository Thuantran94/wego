package com.example.thuantran.wego.Model.Trip;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.thuantran.wego.DataAccess.Constant;
import com.example.thuantran.wego.Interface.Trip.Rela;
import com.example.thuantran.wego.R;
import com.example.thuantran.wego.Object.Relation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Objects;


public class ModelRelation {

    private Rela.Presenter callback;
    public ModelRelation(Rela.Presenter callback){
        this.callback = callback;
    }


    public void handleGetListSelectedDriver(Context context, String trID){

        new Thread(new Runnable() {
            @Override
            public void run() {

                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                Query myData = mDatabase.child(Constant.mRELATION).orderByChild("idPaTrip").equalTo(trID);

                myData.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ArrayList<Relation> relations = new ArrayList<>();
                        for (DataSnapshot ds: dataSnapshot.getChildren()){

                            Relation relation = ds.getValue(Relation.class);
                            Objects.requireNonNull(relation).setiD(ds.getKey());
                            relations.add(relation);

                        }
                        callback.onGetListSelectedDriverSuccess(relations);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        callback.onGetListSelectedDriverFail(context.getString(R.string.errorconnect));
                    }
                });

            }
        }).start();
    }


}
