package com.example.thuantran.wego.Interface.Profile;

import com.example.thuantran.wego.Object.Favorite;

import java.util.ArrayList;


public interface FavoriteAddress {

    interface Presenter{
        void onGetAllFavoriteSuccess(ArrayList<Favorite> arrayList);

    }

    interface View{

        void onGetAllFavoriteSuccess(ArrayList<Favorite> arrayList);

    }

}
