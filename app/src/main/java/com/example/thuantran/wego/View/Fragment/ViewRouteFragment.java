package com.example.thuantran.wego.View.Fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.thuantran.wego.DataAccess.Constant;
import com.example.thuantran.wego.Interface.Map.Map;
import com.example.thuantran.wego.Presenter.Map.PresenterMap;
import com.example.thuantran.wego.R;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ViewRouteFragment extends Fragment implements OnMapReadyCallback, Map.View, MapView.OnDidFinishLoadingMapListener {

    private MapView mapView;
    private MapboxMap map;
    private int HEIGTH_SCREEN, WIDTH_SCREEN;
    private DirectionsRoute currentRoute;
    private List<DirectionsRoute> list_routes;
    private NavigationMapRoute navigationMapRoute;
    private LatLng originPoint, destinationPoint, currentPoint;

    private PresenterMap presenterMap;


    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        map = mapboxMap;
        mapboxMap.setStyle(Style.MAPBOX_STREETS, style -> { });
        mapView.addOnDidFinishLoadingMapListener(this);

        map.setMinZoomPreference(10);
        LatLngBounds bounds = new LatLngBounds.Builder()
                .include(Constant.BOUND_CORNER_NW)
                .include(Constant.BOUND_CORNER_SE)
                .build();
        map.setLatLngBoundsForCameraTarget(bounds);
    }
    @Override
    public void onDidFinishLoadingMap() {
        if(originPoint !=null && destinationPoint !=null && currentPoint != null) {



            int height = (HEIGTH_SCREEN*6)/100;
            int width  = (WIDTH_SCREEN*8)/100;

            Bitmap b0 = BitmapFactory.decodeResource(getResources(), R.drawable.icon_car);
            Bitmap smallMarker0 = Bitmap.createScaledBitmap(b0, width, height, false);
            map.addMarker(new MarkerOptions()
                    .position(currentPoint)
                    .icon(IconFactory.getInstance(Objects.requireNonNull(getActivity())).fromBitmap(smallMarker0)));

            Bitmap b1 = BitmapFactory.decodeResource(getResources(), R.drawable.depart_cell_icon_new);
            Bitmap smallMarker1 = Bitmap.createScaledBitmap(b1, width, height, false);
            map.addMarker(new MarkerOptions()
                    .position(originPoint)
                    .icon(IconFactory.getInstance(Objects.requireNonNull(getActivity())).fromBitmap(smallMarker1)));

            Bitmap b2 = BitmapFactory.decodeResource(getResources(), R.drawable.arrive_cell_icon_new);
            Bitmap smallMarker2 = Bitmap.createScaledBitmap(b2, width, height, false);
            map.addMarker(new MarkerOptions()
                    .position(destinationPoint)
                    .icon(IconFactory.getInstance(getActivity()).fromBitmap(smallMarker2)));

            setCameraLocation(currentPoint);
            presenterMap.receivedHandleGetRoute(getActivity(), originPoint, destinationPoint);




        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Mapbox.getInstance(Objects.requireNonNull(getActivity()), getString(R.string.mapbox_api_key));
        View view = inflater.inflate(R.layout.layout_view_route,container,false);
        mapView   = view.findViewById(R.id.myMap12);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);


        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        HEIGTH_SCREEN = displayMetrics.heightPixels;
        WIDTH_SCREEN  = displayMetrics.widthPixels;

        presenterMap = new PresenterMap(ViewRouteFragment.this);

        Bundle bundle = getArguments();
        if (bundle != null) {
            list_routes = new ArrayList<>();

            originPoint      = bundle.getParcelable("originPoint");
            destinationPoint = bundle.getParcelable("destinationPoint");
            currentPoint     = bundle.getParcelable("currentPoint");
        }




        return view;
    }



    private void setCameraLocation(LatLng CurrentPoint) {
        CameraPosition position = new CameraPosition.Builder()
                .target(CurrentPoint)
                .zoom(13)
                .tilt(30)
                .build();

        map.animateCamera(CameraUpdateFactory.newCameraPosition(position), 5000);
    }



    @Override
    public void onFindRouteSuccess(retrofit2.Response<DirectionsResponse> response) {
        try {
            if (response.body() != null) {

                if(currentRoute == null){
                    this.currentRoute = response.body().routes().get(0);
                    list_routes.add(currentRoute);

                    presenterMap.receivedHandleGetRoute(getActivity(),currentPoint,originPoint);


                }else{
                    DirectionsRoute myRoute = response.body().routes().get(0);
                    list_routes.add(myRoute);
                    // Draw the route on the map
                    if (navigationMapRoute != null) {
                        navigationMapRoute.updateRouteVisibilityTo(true);
                    }
                    else {

                        navigationMapRoute = new NavigationMapRoute(null, mapView, map, R.style.Route);

                    }
                    if (navigationMapRoute != null) {


                        navigationMapRoute.addRoutes(list_routes);



                    }



                }
            }
        }catch (NullPointerException ignored){

        }

    }

    @Override
    public void onFindRouteFail(String err) {

    }

    @Override
    public void onFindAddressSuccess(String address) {

    }

    @Override
    public void onFindAddressFail(String err) {

    }


    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onSaveInstanceState(@NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }




}
