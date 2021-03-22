package com.example.thuantran.wego.Model.Map;


import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.example.thuantran.wego.Interface.Map.Map;
import com.example.thuantran.wego.R;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.geometry.LatLng;

import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;

import org.jetbrains.annotations.NotNull;


import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;

public class ModelMap {
    private static final String TAG = "ModelMap";

    private Map.Presenter callback;


    public ModelMap(Map.Presenter callback){
        this.callback =callback;
    }


    @SuppressWarnings( {"MissingPermission"})
    public void handleGetRoute(Context context, LatLng origin, LatLng destination){

        Point originPoint      = Point.fromLngLat(origin.getLongitude(),origin.getLatitude());
        Point destinationPoint = Point.fromLngLat(destination.getLongitude(),destination.getLatitude());

        new  Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // get Route
                    if (Mapbox.getAccessToken() != null) {
                        NavigationRoute.builder(context)
                                .accessToken(Mapbox.getAccessToken())
                                .origin(originPoint)
                                .destination(destinationPoint)
                                .build()
                                .getRoute(new Callback<DirectionsResponse>() {
                                    @Override
                                    public void onResponse(@NotNull Call<DirectionsResponse> call, @NotNull retrofit2.Response<DirectionsResponse> response) {
                                        if (response.body() == null) {
                                            callback.onFindRouteFail( context.getString(R.string.routefail));
                                            return;
                                        } else if (response.body().routes().size() < 1) {

                                            callback.onFindRouteFail( context.getString(R.string.routefail));
                                            return;
                                        }

                                        callback.onFindRouteSuccess(response);
                                    }

                                    @Override
                                    public void onFailure(@NotNull Call<DirectionsResponse> call, @NotNull Throwable throwable) {
                                        callback.onFindRouteFail( context.getString(R.string.errorconnect));
                                    }
                                });
                    }
                }catch (NullPointerException ignored){

                }
            }
        }).start();

    }




    public void handleGetAddress(Context context,LatLng latLgn){


        String rs = "";

        double lat = latLgn.getLatitude();
        double lng = latLgn.getLongitude();


        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = null;
        try {

            addresses = geocoder.getFromLocation(lat,lng,1);

        } catch (Exception e) {
            e.printStackTrace();

        }

        if (addresses !=null){


            try {
                rs = addresses.get(0).getAddressLine(0);
            }catch (IndexOutOfBoundsException ignored){

            }

            callback.onFindAddressSuccess(rs);
        }else{
            callback.onFindAddressFail(context.getString(R.string.errorconnect));
        }


    }

    




}
