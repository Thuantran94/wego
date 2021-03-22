package com.example.thuantran.wego.Interface.Profile;


import com.example.thuantran.wego.Object.ReviewContext;

import java.util.ArrayList;

public interface Review {

    interface Presenter{
        void onGetReviewSuccess(ArrayList<ReviewContext> reviewContexts);
        void onGetReportSuccess(ArrayList<ReviewContext> reviewContexts);

        void onGetReviewFailed(String err);
        void onGetReportFailed(String err);

    }

    interface View{
        void onGetReviewSuccess(ArrayList<ReviewContext> reviewContexts);
        void onGetReportSuccess(ArrayList<ReviewContext> reviewContexts);


        void onGetReviewFailed(String err);
        void onGetReportFailed(String err);

    }

}
