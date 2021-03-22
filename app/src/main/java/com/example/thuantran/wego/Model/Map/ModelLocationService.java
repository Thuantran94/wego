package com.example.thuantran.wego.Model.Map;

import android.app.Activity;
import android.content.IntentSender;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;


public class ModelLocationService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{


    boolean rs = false;

    public void EnableGPSAutoMatically(Activity activity){

        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(activity.getApplicationContext())
                .addApi(LocationServices.API).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();

        googleApiClient.connect();
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        // **************************
        builder.setAlwaysShow(true); // this is the key ingredient
        // **************************

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi
                .checkLocationSettings(googleApiClient, builder.build());

        result.setResultCallback(result1 -> {
            final Status status = result1.getStatus();
            final LocationSettingsStates state = result1
                    .getLocationSettingsStates();
            switch (status.getStatusCode()) {
                case LocationSettingsStatusCodes.SUCCESS:
                    //toast("Success");
                    // All location settings are satisfied. The client can
                    // initialize location
                    // requests here.
                    rs = true;
                    break;
                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                    //toast("GPS is not on");
                    // Location settings are not satisfied. But could be
                    // fixed by showing the user
                    // a dialog.
                    try {
                        // Show the dialog by calling
                        // startResolutionForResult(),
                        // and check the result in onActivityResult().
                        status.startResolutionForResult(activity, 1000);

                    } catch (IntentSender.SendIntentException e) {
                        // Ignore the error.
                    }
                    break;
                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                    //toast("Setting change not allowed");
                    // Location settings are not satisfied. However, we have
                    // no way to fix the
                    // settings so we won't show the dialog.
                    break;
            }
        });

    }

    public boolean isEnable(){
        return rs;
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        //toast("Suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //toast("Failed");
    }


    private void toast(String message) {
        try {
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        } catch (Exception ex) {
        }
    }





}
