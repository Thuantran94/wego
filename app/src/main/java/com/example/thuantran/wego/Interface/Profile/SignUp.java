package com.example.thuantran.wego.Interface.Profile;


import com.example.thuantran.wego.Object.User;

public interface SignUp {


    interface Presenter{
        void onCodeSendSuccess(String verificationId);
        void onCodeSendFail(String err);

        void onVerifyCodeSuccess(String str);
        void onVerifyCodeFail(String err);

        void onCreateProfileSuccess(User user);
        void onCreateProfileFail(String err);

    }

    interface View{
        void onCodeSendSuccess(String verificationId);
        void onCodeSendFail(String err);

        void onVerifyCodeSuccess(String str);
        void onVerifyCodeFail(String err);

        void onCreateProfileSuccess(User user);
        void onCreateProfileFail(String err);


    }
}
