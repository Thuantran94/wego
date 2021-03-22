package com.example.thuantran.wego.Presenter.Trip;

import android.content.Context;

import com.example.thuantran.wego.Interface.Trip.Rela;
import com.example.thuantran.wego.Model.Trip.ModelRelation;
import com.example.thuantran.wego.Object.Relation;

import java.util.ArrayList;

public class PresenterRelation implements Rela.Presenter {

    private ModelRelation modelRelation;
    private Rela.View callback;

    public  PresenterRelation(Rela.View callback){
        this.callback =callback;
    }




    public void receivedHandleGetListSelectedDriver(Context context, String trID){
        modelRelation = new ModelRelation(this);
        modelRelation.handleGetListSelectedDriver(context,trID);
    }



    @Override
    public void onGetListSelectedDriverSuccess(ArrayList<Relation> selectedDriver) {
        callback.onGetListSelectedDriverSuccess(selectedDriver);
    }

    @Override
    public void onGetListSelectedDriverFail(String err) {
        callback.onGetListSelectedDriverFail(err);

    }


}
