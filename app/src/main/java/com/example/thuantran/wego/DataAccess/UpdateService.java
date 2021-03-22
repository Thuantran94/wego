package com.example.thuantran.wego.DataAccess;

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.Nullable;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class UpdateService extends IntentService {


    public UpdateService() {
        super("UpdateService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        if (intent != null) {
            String userID = intent.getStringExtra("userID");
            String status = intent.getStringExtra("status");

            if (userID !=null){
                Map<String, Object> map = new HashMap<>();
                map.put("status",status);
                mDatabase.child(Constant.USER).child(userID).child(Constant.PROFILE).updateChildren(map);
            }
        }
    }
}
