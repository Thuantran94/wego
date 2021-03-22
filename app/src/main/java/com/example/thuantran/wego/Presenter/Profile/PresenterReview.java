package com.example.thuantran.wego.Presenter.Profile;

import com.example.thuantran.wego.Interface.Profile.Review;
import com.example.thuantran.wego.Model.Profile.ModelReview;
import com.example.thuantran.wego.Object.ReviewContext;

import java.util.ArrayList;

public class PresenterReview implements Review.Presenter {

    private Review.View callback;
    private ModelReview modelReview;


    public PresenterReview(Review.View callback){
        this.callback = callback;
    }

    public void receivedHandleGetAllReview(String userID){

        modelReview = new ModelReview(this);
        modelReview.getAllReview(userID);

    }

    public void receivedHandleGetAllReport(String userID){

        modelReview = new ModelReview(this);
        modelReview.getAllReport(userID);

    }



    @Override
    public void onGetReviewSuccess(ArrayList<ReviewContext> reviewContexts) {
            callback.onGetReviewSuccess(reviewContexts);
    }

    @Override
    public void onGetReportSuccess(ArrayList<ReviewContext> reviewContexts) {
        callback.onGetReportSuccess(reviewContexts);
    }

    @Override
    public void onGetReviewFailed(String err) {
        callback.onGetReviewFailed(err);
    }

    @Override
    public void onGetReportFailed(String err) {
        callback.onGetReportFailed(err);

    }
}
