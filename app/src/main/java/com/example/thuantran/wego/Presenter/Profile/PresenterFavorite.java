package com.example.thuantran.wego.Presenter.Profile;



import com.example.thuantran.wego.Interface.Profile.FavoriteAddress;
import com.example.thuantran.wego.Model.Profile.ModelFavorite;
import com.example.thuantran.wego.Object.Favorite;

import java.util.ArrayList;

public class PresenterFavorite implements FavoriteAddress.Presenter {

    private ModelFavorite modelFavorite;
    private FavoriteAddress.View callback;




    public PresenterFavorite(FavoriteAddress.View callback){
        this.callback = callback;
    }


    public void receivedHandleGetAllFavoriteAddress(String userID){

        modelFavorite = new ModelFavorite(this);
        modelFavorite.getAllFavoriteAddress(userID);

    }



    @Override
    public void onGetAllFavoriteSuccess(ArrayList<Favorite> arrayList) {
        callback.onGetAllFavoriteSuccess(arrayList);
    }


}
