package com.example.thuantran.wego.Interface.Profile;

public interface Image {

    interface Presenter{
        void onUpdateImageSuccess(String reponse);
        void onUpdateImageFail(String err);

    }

    interface View{
        void onUpdateImageSuccess(String reponse);
        void onUpdateImageFail(String err);
    }
}
