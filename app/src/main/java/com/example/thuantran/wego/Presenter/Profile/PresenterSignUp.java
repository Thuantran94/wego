package com.example.thuantran.wego.Presenter.Profile;


import android.app.Activity;
import android.content.Context;

import com.example.thuantran.wego.Interface.Profile.SignUp;
import com.example.thuantran.wego.Model.Profile.ModelSignUp;
import com.example.thuantran.wego.Object.User;

public class PresenterSignUp implements SignUp.Presenter {

    private ModelSignUp modelSignUp;
    private SignUp.View callback;


    public PresenterSignUp(SignUp.View callback){
        this.callback = callback;
    }



    public void receivedHandleSendCode(Activity activity, String phone){
        modelSignUp = new ModelSignUp(this);
        modelSignUp.sendCode(activity, phone);

    }

    public void receivedHandleVerifyCode(Activity activity, String code, String phoneVireficationId){
        modelSignUp = new ModelSignUp(this);
        modelSignUp.VerifyCode(activity, code, phoneVireficationId);

    }


    public void receivedHandleCreateProfile(Context context, String id,String name, String gender, String phone, String email){

        modelSignUp = new ModelSignUp(this);
        modelSignUp.handleCreateProfile(context, id, name, gender, phone, email);

    }





    @Override
    public void onCodeSendSuccess(String verificationId) {
        callback.onCodeSendSuccess(verificationId);
    }

    @Override
    public void onCodeSendFail(String err) {
        callback.onCodeSendFail(err);
    }

    @Override
    public void onVerifyCodeSuccess(String str) {
        callback.onVerifyCodeSuccess(str);

    }

    @Override
    public void onVerifyCodeFail(String err) {
        callback.onVerifyCodeFail(err);

    }

    @Override
    public void onCreateProfileSuccess(User user) {
        callback.onCreateProfileSuccess(user);
    }

    @Override
    public void onCreateProfileFail(String err) {
        callback.onCreateProfileFail(err);
    }

}
