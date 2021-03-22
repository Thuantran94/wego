package com.example.thuantran.wego.DataAccess;

import com.example.thuantran.wego.Object.User;

public interface ReadDataFromFireBaseListener {


    void onGetUserSuccess(User user);
    void onGetUserFailed(String err);



}
