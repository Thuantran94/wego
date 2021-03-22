package com.example.thuantran.wego.View.Driver;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import androidx.annotation.NonNull;

import com.example.thuantran.wego.DataAccess.AccessFireBase;
import com.example.thuantran.wego.DataAccess.Constant;
import com.example.thuantran.wego.DataAccess.IAccessFireBase;
import com.example.thuantran.wego.Interface.Profile.FavoriteAddress;
import com.example.thuantran.wego.Object.Favorite;
import com.example.thuantran.wego.Presenter.Profile.PresenterFavorite;
import com.example.thuantran.wego.Tools.Helper;
import com.example.thuantran.wego.Tools.iHelper;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import androidx.appcompat.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.thuantran.wego.Interface.Map.Map;
import com.example.thuantran.wego.Object.User;
import com.example.thuantran.wego.Presenter.Map.PresenterMap;
import com.example.thuantran.wego.R;
import com.mapbox.android.gestures.MoveGestureDetector;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
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

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Response;


public class DrAddTripActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener,
        OnMapReadyCallback,MapboxMap.OnMapClickListener,
        FavoriteAddress.View, Map.View {

    private ProgressDialog dialogwait, dialogfindroute;
    private MapboxMap map;
    private MapView mapView;
    private DirectionsRoute currentRoute;
    private NavigationMapRoute navigationMapRoute;
    private FrameLayout frameLayout;
    private FloatingActionMenu fabmenu;
    private FloatingActionButton fab1,fab2,fab3,fab4;
    private AutoCompleteTextView autocompletetw;
    private Marker marker1, marker2;
    private Animation up2down, down2up;
    private Button departPoint, arrivePoint,Back, validate;
    private ImageView picker;
    private TextView  twresulttime, twresultdura;
    private RadioGroup btSeat,btCar, btPick, btAccept;
    private RadioButton rdbike, rdcar, seat1, rdbooknow, rdbooklater;
    private CheckBox cbRepeat;
    private int HEIGTH_SCREEN, WIDTH_SCREEN;


    private static final String TAG = "Main_add_proposer";



    private User user;
    private String userID;
    private String depart, destination, date,time;
    private double dura, dist;
    private String distance, duration;
    private LatLng homeAddress, workAddress, favoriteAddress;
    private LatLng DepartPoint, DestinationPoint, CurrentPoint, MarkedPoint;

    private int limit;

    private int nSeat ;
    private String typeRequest ;
    private int repeat ;

    private boolean isClickedAccept;
    private boolean isFoundAddress;
    private boolean isChooseDepart;


    private Helper helper;
    private PresenterMap presenterMap;
    private PresenterFavorite presenterFavorite;

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        map = mapboxMap;
        map.setStyle(Style.MAPBOX_STREETS, style -> { });
        map.addOnMapClickListener(this);

        map.setMinZoomPreference(10);
        LatLngBounds bounds = new LatLngBounds.Builder()
                .include(Constant.BOUND_CORNER_NW)
                .include(Constant.BOUND_CORNER_SE)
                .build();
        map.setLatLngBoundsForCameraTarget(bounds);

        map.addOnMoveListener(new MapboxMap.OnMoveListener() {
            @Override
            public void onMoveBegin(@NotNull MoveGestureDetector detector) { }
            @Override
            public void onMove(@NotNull MoveGestureDetector detector) { }
            @Override
            public void onMoveEnd(@NotNull MoveGestureDetector detector) {

                MarkedPoint = map.getCameraPosition().target;
                updateMakerPoint(MarkedPoint);
            }
        });

        if (CurrentPoint != null){
            initialSheetBox();
            initialLocation();
            InitialAutoComplete();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Mapbox.getInstance(this, getString(R.string.mapbox_api_key));
        setContentView(R.layout.activity_main_driver_add_trip);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        HEIGTH_SCREEN = displayMetrics.heightPixels;
        WIDTH_SCREEN  = displayMetrics.widthPixels;

        mapToLayout();
        up2down = AnimationUtils.loadAnimation(this, R.anim.up2down);
        down2up = AnimationUtils.loadAnimation(this, R.anim.down2up);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        Bundle b = getIntent().getBundleExtra("bundle");
        if (b != null) {
            user        = b.getParcelable("user");
            limit       = b.getInt("limit");
            if (user != null) {
                userID = user.getUserID();

                helper              = new Helper();
                presenterMap        = new PresenterMap(this);
                presenterFavorite   = new PresenterFavorite(this);
                presenterFavorite.receivedHandleGetAllFavoriteAddress(userID);

                CurrentPoint = new LatLng(Double.valueOf(user.getLat()),Double.valueOf(user.getLng()));

            }
        }



        fab1.setOnClickListener(this);
        fab2.setOnClickListener(this);
        fab3.setOnClickListener(this);
        fab4.setOnClickListener(this);
        validate.setOnClickListener(this);
        autocompletetw.setOnClickListener(this);
        fab2.setOnLongClickListener(this);
        fab3.setOnLongClickListener(this);
        fab4.setOnLongClickListener(this);
        Back.setOnClickListener(this);



        btCar.setOnCheckedChangeListener((group, checkedId) -> {

            View radioButton = btCar.findViewById(checkedId);
            int index = btCar.indexOfChild(radioButton);

            switch (index){
                case 0:
                    typeRequest = "bike";
                    break;

                case 1:
                    typeRequest = "taxi";
                    break;
            }
        });



        btSeat.setOnCheckedChangeListener((group, checkedId) -> {

            View radioButton = btSeat.findViewById(checkedId);
            int index = btSeat.indexOfChild(radioButton);

            nSeat = index+1;

            if(nSeat>1){
                typeRequest = "taxi";
                rdcar.setChecked(true);
            }
            else{
                typeRequest = "bike";
                rdbike.setChecked(true);
            } });



        btPick.setOnCheckedChangeListener((group, checkedId) -> {

            View radioButton = btPick.findViewById(checkedId);
            int index = btPick.indexOfChild(radioButton);

            switch (index) {
                case 0:

                    AcceptPickNow();

                    break;
                case 1:

                    AcceptPickLater();
                    break;
            }

        });


        btAccept.setOnCheckedChangeListener((group, checkedId) -> {
            View radioButton = btAccept.findViewById(checkedId);
            int index = btAccept.indexOfChild(radioButton);

            switch (index){

                case 0:
                    initialSheetBox();
                    break;

                case 1:


                    if (limit >=5){
                        Helper.displayDiagWarning(DrAddTripActivity.this,getResources().getString(R.string.err),getResources().getString(R.string.limited));
                        btAccept.clearCheck();
                        return;
                    }

                    if (nSeat>1 && typeRequest.equals("bike")){
                        Helper.displayDiagWarning(DrAddTripActivity.this,"",getString(R.string.vuilongchon));
                        btAccept.clearCheck();
                        return;
                    }


                    if (cbRepeat.isChecked()){
                        repeat = 1; }

                    if (!isClickedAccept){
                        isClickedAccept = true;

                        dialogwait = new ProgressDialog(DrAddTripActivity.this);
                        dialogwait.setMessage(getString(R.string.sendrequest));
                        dialogwait.setCancelable(false);
                        dialogwait.show();

                        String onCreated = Helper.getTimeStamp();
                        AccessFireBase.addTrip(userID, user.getAvatar(), date, time, depart, destination, nSeat, typeRequest, repeat, onCreated,
                                new IAccessFireBase.iAddTripDriver() {
                                    @Override
                                    public void onSuccess() {
                                        dialogwait.dismiss();
                                        btAccept.clearCheck();
                                        Helper.displayDiagSuccess(DrAddTripActivity.this,getResources().getString(R.string.addtripsuccess),"");
                                        initialSheetBox();
                                        limit++;
                                    }

                                    @Override
                                    public void onFailed() {
                                        dialogwait.dismiss();
                                        btAccept.clearCheck();
                                        Helper.displayErrorMessage(DrAddTripActivity.this,getResources().getString(R.string.errorconnect));
                                    }
                                });

                    }



                    break;


            }
        });




    }





    private void InitialAutoComplete(){


        autocompletetw.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_SEARCH
                    || i == EditorInfo.IME_ACTION_DONE
                    || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                    || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {


                String searchString = autocompletetw.getText().toString();
                Geocoder geocoder = new Geocoder(this);
                List<Address> list = new ArrayList<>();
                try {
                    list = geocoder.getFromLocationName(searchString, 1);
                } catch (IOException ignored) {
                }
                if (list.size() > 0) {
                    Address address = list.get(0);


                    if (address.getLatitude()> Constant.BOUND_CORNER_NW.getLatitude()
                            || address.getLatitude()< Constant.BOUND_CORNER_SE.getLatitude()
                            || address.getLongitude()< Constant.BOUND_CORNER_NW.getLongitude()
                            || address.getLongitude()> Constant.BOUND_CORNER_SE.getLongitude()){

                        Helper.displayErrorMessage(this,getString(R.string.limitedadd));
                        return false;
                    }

                    MarkedPoint = new LatLng(address.getLatitude(),address.getLongitude());
                    updateMakerPoint(MarkedPoint);
                    setCameraLocation(MarkedPoint);
                }
            }
            return true;
        });

    }


    private void initialSheetBox() {


        isFoundAddress  = true;
        isClickedAccept = false;
        isChooseDepart  = false;


        repeat = 0;
        typeRequest = "bike";
        nSeat = 1;

        AcceptPickNow();



        frameLayout.setVisibility(View.INVISIBLE);
        validate.setVisibility(View.VISIBLE);
        autocompletetw.setVisibility(View.VISIBLE);
        fabmenu.setVisibility(View.VISIBLE);

        validate.setText(R.string.Adddepart);
        picker.setVisibility(View.VISIBLE);
        picker.setImageResource(R.drawable.depart_cell_icon_new);
        departPoint.setText(null);
        arrivePoint.setText(null);
        autocompletetw.setText(R.string.Currentlocation);



        cbRepeat.setChecked(false);
        rdbike.setChecked(true);
        seat1.setChecked(true);
        rdbooknow.setChecked(true);
        btAccept.clearCheck();


        if (CurrentPoint != null) {
            DepartPoint = CurrentPoint;
            DestinationPoint = CurrentPoint;
            setCameraLocation(CurrentPoint);
        }
        if (marker1 != null) { marker1.remove();}
        if (marker2 != null) { marker2.remove();}
        if (currentRoute != null) {
            navigationMapRoute.updateRouteVisibilityTo(false);
            currentRoute = null;}
    }

    private void setCameraLocation(LatLng CurrentPoint) {
        CameraPosition position = new CameraPosition.Builder()
                .target(CurrentPoint)
                .zoom(15)
                .tilt(30)
                .build();

        map.animateCamera(CameraUpdateFactory.newCameraPosition(position), 5000);
    }

    private void initialLocation() {

        // the first location when user login
        MarkedPoint      = CurrentPoint;
        DepartPoint      = CurrentPoint;
        DestinationPoint = CurrentPoint;
        setCameraLocation(CurrentPoint);
    }

    private void updateMakerPoint(LatLng point){
        MarkedPoint = point;

        if (picker.getVisibility() == View.VISIBLE) {

            presenterMap.receivedHandleGetAddress(this,MarkedPoint);

            if (departPoint.getText().toString().isEmpty()) {

                DepartPoint = MarkedPoint;

            } else { DestinationPoint = MarkedPoint; }
        }
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.autocompletetw:
                autocompletetw.selectAll();
                break;

            case R.id.back1:
                finish();
                break;
            case R.id.validate:

                int height = (HEIGTH_SCREEN*6)/100;
                int width  = (WIDTH_SCREEN*8)/100;

                if (isFoundAddress){
                    if (!isChooseDepart) {

                        if (DepartPoint !=null) {
                            picker.setImageResource(R.drawable.arrive_cell_icon_new);
                            departPoint.setText(autocompletetw.getText());
                            validate.setText(R.string.Adddesti);
                            isFoundAddress = false;
                            isChooseDepart = true;

                            Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.depart_cell_icon_new);
                            Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
                            marker1 = map.addMarker(new MarkerOptions()
                                    .position(DepartPoint)
                                    .icon(IconFactory.getInstance(this).fromBitmap(smallMarker)));
                        }
                    }
                    else {

                        if (DestinationPoint !=null) {
                            validate.setVisibility(View.INVISIBLE);
                            arrivePoint.setText(autocompletetw.getText());
                            isFoundAddress = false;

                            Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.arrive_cell_icon_new);
                            Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
                            marker2 = map.addMarker(new MarkerOptions()
                                    .position(DestinationPoint)
                                    .icon(IconFactory.getInstance(this).fromBitmap(smallMarker)));
                            picker.setVisibility(View.INVISIBLE);


                            dialogfindroute = new ProgressDialog(this);
                            dialogfindroute.setMessage(this.getString(R.string.findroute));
                            dialogfindroute.setCancelable(false);
                            dialogfindroute.show();

                            presenterMap.receivedHandleGetRoute(this, DepartPoint, DestinationPoint);
                        }
                    }
                }

                break;
            case R.id.fab1:
                updateMakerPoint(CurrentPoint);
                setCameraLocation(CurrentPoint);
                fabmenu.close(true);
                break;

            case R.id.fab2:
                if (homeAddress==null) {
                    Helper.displayDiag(this,getResources().getString(R.string.unlocated),getResources().getString(R.string.nhan_giu)); }
                else{
                    updateMakerPoint(homeAddress);
                    setCameraLocation(homeAddress);}
                fabmenu.close(true);
                break;

            case R.id.fab3:
                if (workAddress==null) {
                    Helper.displayDiag(this,getResources().getString(R.string.unlocated),getResources().getString(R.string.nhan_giu)); }
                else {
                    updateMakerPoint(workAddress);
                    setCameraLocation(workAddress);}
                fabmenu.close(true);
                break;

            case R.id.fab4:
                if (favoriteAddress==null) {
                    Helper.displayDiag(this,getResources().getString(R.string.unlocated),getResources().getString(R.string.nhan_giu)); }

                else {
                    updateMakerPoint(favoriteAddress);
                    setCameraLocation(favoriteAddress);}
                fabmenu.close(true);
                break;
        }
    }





    @Override
    public boolean onLongClick(View v) {
        double lat = MarkedPoint.getLatitude();
        double lng = MarkedPoint.getLongitude();
        switch (v.getId()) {
            case R.id.fab2:
                homeAddress     = MarkedPoint;
                AccessFireBase.updateHomeAddress(userID,lat,lng);
                Helper.displayErrorMessage(this,getString(R.string.addedhome));
                break;
            case R.id.fab3:
                workAddress     = MarkedPoint;
                AccessFireBase.updateWorkAddress(userID,lat,lng);
                Helper.displayErrorMessage(this,getString(R.string.addedwork));
                break;
            case R.id.fab4:
                favoriteAddress = MarkedPoint;
                AccessFireBase.updateFavoriteAddress(userID,lat,lng);
                Helper.displayErrorMessage(this,getString(R.string.addedfavorite));
                break;
        }
        return true;
    }




    private void AcceptPickLater(){
        helper.handleShowDatePicker(this, new iHelper.pickTime() {
            @Override
            public void onSuccess(String[] mtime) {
                date = mtime[0];
                time = mtime[1];

                rdbooknow.setText(getString(R.string.booknow));
                rdbooklater.setText(String.format("%s - %s", date, time));
            }

            @Override
            public void onFailed() {

            }
        });

    }

    private void AcceptPickNow(){
        helper.callSystemTime();
        String[] rs = helper.getTime();
        date = rs[0];
        time = rs[1];

        rdbooklater.setText(getString(R.string.booklater1));
        rdbooknow.setText(String.format("%s - %s", date, time));


    }



    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(@NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }






    private void mapToLayout(){
        mapView     = findViewById(R.id.mapview);
        departPoint = findViewById(R.id.ipPointAdr);
        arrivePoint = findViewById(R.id.ipPointBdr);
        validate    = findViewById(R.id.validate);
        btAccept    = findViewById(R.id.btValidate);
        Back        = findViewById(R.id.back1);
        picker      =  findViewById(R.id.maker);
        frameLayout = findViewById(R.id.frame1);
        btPick      = findViewById(R.id.rdbtime);


        btCar       = findViewById(R.id.rbtCar);
        btSeat      = findViewById(R.id.rbtPersonp);
        rdbike      = findViewById(R.id.bike);
        rdcar       = findViewById(R.id.taxi);
        rdbooknow   = findViewById(R.id.booknow);
        rdbooklater = findViewById(R.id.booklater);
        seat1       = findViewById(R.id.person1);


        fabmenu      = findViewById(R.id.fabmenu);
        fab1         = findViewById(R.id.fab1);
        fab2         = findViewById(R.id.fab2);
        fab3         = findViewById(R.id.fab3);
        fab4         = findViewById(R.id.fab4);
        autocompletetw = findViewById(R.id.autocompletetw);
        twresulttime = findViewById(R.id.twresult1);
        twresultdura = findViewById(R.id.twresult2);

        cbRepeat = findViewById(R.id.cbRepeat);



    }




    @Override
    public void onFindRouteSuccess(Response<DirectionsResponse> response) {
        dialogfindroute.dismiss();
        if (response.body() != null){
            depart       = response.body().waypoints().get(0).location().coordinates().toString();
            destination  = response.body().waypoints().get(1).location().coordinates().toString();




            dura         = response.body().routes().get(0).duration() / 60;
            dist         = response.body().routes().get(0).distance() / 1000;
            currentRoute = response.body().routes().get(0);
        }

        duration = new DecimalFormat("##").format(dura) + getString(R.string.minute);
        distance = " (" + new DecimalFormat("##.#").format(dist) + " km)";


        isFoundAddress = true;
        frameLayout.startAnimation(up2down);
        frameLayout.setVisibility(View.VISIBLE);
        autocompletetw.setVisibility(View.INVISIBLE);
        fabmenu.setVisibility(View.INVISIBLE);
        twresulttime.setText(duration);
        twresultdura.setText(distance);


        // Draw the route on the map
        if (navigationMapRoute != null) {
            navigationMapRoute.updateRouteVisibilityTo(true); }
        else {
            navigationMapRoute = new NavigationMapRoute(null, mapView, map, R.style.Route); }
        navigationMapRoute.addRoute(currentRoute);



    }

    @Override
    public void onFindRouteFail(String err) {
        dialogfindroute.dismiss();
        Helper.displayErrorMessage(this,err);
        initialSheetBox();
    }

    @Override
    public void onFindAddressSuccess(String address) {
        double absLat = Math.abs(CurrentPoint.getLatitude()  - MarkedPoint.getLatitude());
        double absLng = Math.abs(CurrentPoint.getLongitude() - MarkedPoint.getLongitude());
        double err    = 0.00001;

        if (absLat > err && absLng > err) { autocompletetw.setText(address);}
        else { autocompletetw.setText(R.string.Currentlocation); }
        isFoundAddress = true;
    }

    @Override
    public void onFindAddressFail(String err) {
        Helper.displayErrorMessage(this,getString(R.string.addressfail));
        isFoundAddress = false;

    }






    @Override
    public void onGetAllFavoriteSuccess(ArrayList<Favorite> arrayList) {
        for (int i=0;i<arrayList.size();i++){
            Favorite address = arrayList.get(i);
            LatLng latLng = new LatLng(address.getLat(),address.getLng());
            if (address.getName().equals("Favorite1")){
                favoriteAddress = latLng; }
            if (address.getName().equals("Home")){
                homeAddress     = latLng; }
            if (address.getName().equals("Work")){
                workAddress     = latLng; }
        }


    }


    @Override
    public boolean onMapClick(@NonNull LatLng point) {

        if(currentRoute !=null){
            if(frameLayout.getVisibility() == View.VISIBLE){
                frameLayout.startAnimation(down2up);
                frameLayout.setVisibility(View.INVISIBLE);
                autocompletetw.setVisibility(View.VISIBLE);
                fabmenu.setVisibility(View.VISIBLE);
            }else{
                frameLayout.startAnimation(up2down);
                frameLayout.setVisibility(View.VISIBLE);
                autocompletetw.setVisibility(View.INVISIBLE);
                fabmenu.setVisibility(View.INVISIBLE);
            }
        }

        return true;
    }



}

