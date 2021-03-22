package com.example.thuantran.wego.Presenter.Map;

import android.content.Context;

import com.example.thuantran.wego.Interface.Map.Map;
import com.example.thuantran.wego.Model.Map.ModelMap;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.mapboxsdk.geometry.LatLng;

import retrofit2.Response;

public class PresenterMap implements Map.Presenter {

    private ModelMap modelMap;
    private Map.View callback;

    public PresenterMap(Map.View callback){
        this.callback =callback;
    }

    public void receivedHandleGetRoute(Context context, LatLng origin, LatLng destination){

        modelMap = new ModelMap(this);
        modelMap.handleGetRoute(context, origin, destination);


    }

    public void receivedHandleGetAddress(Context context,LatLng latLng){
        modelMap = new ModelMap(this);
        modelMap.handleGetAddress(context, latLng);

    }




    @Override
    public void onFindRouteSuccess(Response<DirectionsResponse> response) {
        callback.onFindRouteSuccess(response);

    }

    @Override
    public void onFindRouteFail(String err) {
        callback.onFindRouteFail(err);

    }

    @Override
    public void onFindAddressSuccess(String address) {
        callback.onFindAddressSuccess(address);
    }

    @Override
    public void onFindAddressFail(String err) {
        callback.onFindAddressFail(err);

    }

}
