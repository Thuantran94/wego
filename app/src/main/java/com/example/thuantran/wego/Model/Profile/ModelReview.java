package com.example.thuantran.wego.Model.Profile;


import com.example.thuantran.wego.DataAccess.Constant;
import com.example.thuantran.wego.Interface.Profile.Review;
import com.example.thuantran.wego.Object.ReviewContext;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ModelReview {
    private Review.Presenter callback;
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    public ModelReview(Review.Presenter callback){
        this.callback = callback;
    }


    public void getAllReview(String userID){

        new Thread(() -> {
            Query myData = mDatabase.child(Constant.USER).child(userID).child(Constant.REVIEW);
            ArrayList<ReviewContext> reviewContexts = new ArrayList<>();

            myData.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                    reviewContexts.clear();
                    for (DataSnapshot ds:dataSnapshot.getChildren()){

                        ReviewContext review = ds.getValue(ReviewContext.class);

                        reviewContexts.add(review);

                    }

                    callback.onGetReviewSuccess(reviewContexts);

                }

                @Override
                public void onCancelled(@NotNull DatabaseError databaseError) {
                    callback.onGetReportFailed("err");
                }
            });
        });

    }


    public void getAllReport(String userID){

        new Thread(() -> {
            Query myData = mDatabase.child(Constant.USER).child(userID).child(Constant.REPORT);
            ArrayList<ReviewContext> reportContexts = new ArrayList<>();

            myData.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                    reportContexts.clear();
                    for (DataSnapshot ds:dataSnapshot.getChildren()){

                        ReviewContext review = ds.getValue(ReviewContext.class);

                        reportContexts.add(review);

                    }

                    callback.onGetReportSuccess(reportContexts);

                }

                @Override
                public void onCancelled(@NotNull DatabaseError databaseError) {
                    callback.onGetReportFailed("err");
                }
            });
        });

    }

}
