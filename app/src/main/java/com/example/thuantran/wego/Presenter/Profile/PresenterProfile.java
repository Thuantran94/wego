package com.example.thuantran.wego.Presenter.Profile;

import android.content.Context;

import com.example.thuantran.wego.Interface.Profile.Profile;
import com.example.thuantran.wego.Model.Profile.ModelProfile;
import com.example.thuantran.wego.Object.Car;
import com.example.thuantran.wego.Object.User;

import java.util.ArrayList;

public class PresenterProfile implements Profile.Presenter {

    private ModelProfile modelProfile;
    private Profile.View callback;

    public PresenterProfile(Profile.View callback){
        this.callback = callback;
    }


    public void receivedHandleGetProfile(Context context, String userID ){
        modelProfile = new ModelProfile(this);
        modelProfile.handleGetProfile(context,userID);
    }

    public void receivedHandleGetProfile(Context context, ArrayList<String> selectedDriverIDList ){
        modelProfile = new ModelProfile(this);
        modelProfile.handleGetProfile(context,selectedDriverIDList);
    }


    public void receivedHandleGetTBCar(Context context, String userID ){
        modelProfile = new ModelProfile(this);
        modelProfile.handleGetTBCar(context,userID);
    }

    public void receivedHandleGetTBCar(Context context, ArrayList<String> userIDList ){
        modelProfile = new ModelProfile(this);
        modelProfile.handleGetTBCar(context,userIDList);
    }



    @Override
    public void onGetProfileSuccess(User user) {
        callback.onGetProfileSuccess(user);

    }

    @Override
    public void onGetProfileSuccess(ArrayList<User> users,ArrayList<Car> cars) {
        callback.onGetProfileSuccess(users,cars);

    }


    @Override
    public void onGetProfileFail(String err) {
        callback.onGetProfileFail(err);

    }


    @Override
    public void onGetTBCarSuccess(Car object) {
        callback.onGetTBCarSuccess(object);
    }

    @Override
    public void onGetTBCarSuccess(ArrayList<Car> cars) {
        callback.onGetTBCarSuccess(cars);

    }

    @Override
    public void onGetTBCarFail(String err) {
        callback.onGetTBCarFail(err);
    }



}
