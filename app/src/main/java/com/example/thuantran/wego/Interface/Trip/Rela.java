package com.example.thuantran.wego.Interface.Trip;

import com.example.thuantran.wego.Object.Relation;

import java.util.ArrayList;

public interface Rela {

    interface Presenter{
        void onGetListSelectedDriverSuccess(ArrayList<Relation> selectedDriver);
        void onGetListSelectedDriverFail(String err);



    }
    interface View{
        void onGetListSelectedDriverSuccess(ArrayList<Relation> selectedDriver);
        void onGetListSelectedDriverFail(String err);


    }

}
