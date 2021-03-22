package com.example.thuantran.wego.Interface.Map;

import com.mapbox.api.directions.v5.models.DirectionsResponse;

public interface Map {

    interface Presenter{
        void onFindRouteSuccess(retrofit2.Response<DirectionsResponse> response);
        void onFindRouteFail(String err);

        void onFindAddressSuccess(String address);
        void onFindAddressFail(String err);

    }

    interface View{

        void onFindRouteSuccess(retrofit2.Response<DirectionsResponse> response);
        void onFindRouteFail(String err);

        void onFindAddressSuccess(String address);
        void onFindAddressFail(String err);


    }
}
