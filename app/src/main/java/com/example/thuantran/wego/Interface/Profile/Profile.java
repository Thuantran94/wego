package com.example.thuantran.wego.Interface.Profile;

import com.example.thuantran.wego.Object.Car;
import com.example.thuantran.wego.Object.User;

import java.util.ArrayList;

public interface Profile {

    interface Presenter{
        void onGetProfileSuccess(User user);
        void onGetProfileSuccess(ArrayList<User> user,ArrayList<Car> car);
        void onGetProfileFail(String err);


        void onGetTBCarSuccess(Car car);
        void onGetTBCarSuccess(ArrayList<Car>  car);
        void onGetTBCarFail(String err);



    }

    interface View{
        void onGetProfileSuccess(User user);
        void onGetProfileSuccess(ArrayList<User> user,ArrayList<Car> car);
        void onGetProfileFail(String err);


        void onGetTBCarSuccess(Car object);
        void onGetTBCarSuccess(ArrayList<Car> object);
        void onGetTBCarFail(String err);



    }



}
