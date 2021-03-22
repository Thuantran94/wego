package com.example.thuantran.wego.DataAccess;

public interface IAccessFireBase {

    interface iAddTripPassenger{
        void onSuccess(String trID);
        void onFailed();
    }
    interface iAddTripDriver{
        void onSuccess();
        void onFailed();
    }

    interface iAddRelation{
        void onSuccess();
        void onFailed();
    }

    interface iAddReview{
        void onSuccess();
        void onFailed();
    }

    interface iAddReport{
        void onSuccess();
        void onFailed();
    }

    interface iAddSms{
        void onSuccess();
        void onFailed();
    }


    interface iUpdateLocation{
        void onSuccess();
        void onFailed();
    }


    interface iUpdatePoint{
        void onSuccess();
        void onFailed();
    }

    interface iUpdateProfile{
        void onSuccess();
        void onFailed();
    }

    interface iUpdateProfileCar{
        void onSuccess();
        void onFailed();
    }

    interface iUpdateAvatar{
        void onSuccess();
        void onFailed();
    }

    interface iUpdateMessenger{
        void onSuccess();
        void onFailed();
    }

    interface iUpdateReview{
        void onSuccess();
        void onFailed();
    }

    interface iUpdateRelationStt{
        void onSuccess();
        void onFailed();
    }

    interface iUpdateReceivedTrip{
        void onSuccess();
        void onFailed();
    }

    interface iUpdateLastSms{
        void onSuccess();
        void onFailed();
    }
    interface iUpdateStatusSeen{
        void onSuccess();
        void onFailed();
    }


    interface iCheckPromoCode{
        void onSuccess();
        void onFailed();
    }


    interface iGetLocationSuccess{
        void onSuccess();
        void onFailed(String err);
    }


    interface iAddFeedBack{
        void onSuccess();
        void onFailed();
    }

    interface iRemoveTripWithOutRefund{
        void onSuccess();
        void onFailed();
    }

    interface iRemoveTripWithRefund{
        void onSuccess();
        void onFailed();
    }

    interface iRemoveTripBookNow{
        void onSuccess();
        void onFailed();
    }
}
