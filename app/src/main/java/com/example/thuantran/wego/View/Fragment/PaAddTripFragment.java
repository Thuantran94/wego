package com.example.thuantran.wego.View.Fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.thuantran.wego.DataAccess.AccessFireBase;
import com.example.thuantran.wego.DataAccess.Constant;
import com.example.thuantran.wego.DataAccess.DeleteBookNowTripService;
import com.example.thuantran.wego.DataAccess.IAccessFireBase;
import com.example.thuantran.wego.Interface.Map.Map;
import com.example.thuantran.wego.Interface.Profile.FavoriteAddress;
import com.example.thuantran.wego.Interface.Tools.Fragment2Activity;
import com.example.thuantran.wego.Interface.User.Passenger;
import com.example.thuantran.wego.Object.DriverTrip;
import com.example.thuantran.wego.Object.Favorite;
import com.example.thuantran.wego.Object.PassengerTrip;
import com.example.thuantran.wego.Presenter.Map.PresenterMap;
import com.example.thuantran.wego.Presenter.Profile.PresenterFavorite;
import com.example.thuantran.wego.Presenter.User.PresenterPassenger;
import com.example.thuantran.wego.R;
import com.example.thuantran.wego.Tools.Calcul;
import com.example.thuantran.wego.Tools.CustomDialog;
import com.example.thuantran.wego.Tools.Helper;
import com.example.thuantran.wego.Object.User;
import com.example.thuantran.wego.Tools.iHelper;
import com.example.thuantran.wego.View.Main.AccountActivity;
import com.example.thuantran.wego.View.Passenger.PaConfirmActivity;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
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
import java.util.Objects;

import retrofit2.Response;

public class PaAddTripFragment extends Fragment implements OnMapReadyCallback,  View.OnLongClickListener,
        View.OnClickListener,Passenger.View, Map.View, FavoriteAddress.View, MapboxMap.OnMapClickListener {

    private DecimalFormat decimalFormat = new DecimalFormat("#,##0");
    private static String TAG = "PaAddTripFragment";

    private AutoCompleteTextView autocompletetw;
    private ProgressDialog dialog1;
    private ProgressDialog dialogwait, dialogfindroute;
    private MapView mapView;
    private MapboxMap map;
    private DirectionsRoute currentRoute;
    private NavigationMapRoute navigationMapRoute;
    private FrameLayout frameLayout;
    private ImageView  picker;
    private TextView  twPrix, twresulttime, twresultdura;
    private SeekBar sbPrix;
    private FloatingActionMenu fabmenu;
    private FloatingActionButton fab1,fab2,fab3,fab4;
    private Animation up2down, down2up;
    private Button departPoint, arrivePoint, validate;
    private TextView  twTB, twPrixXN;
    private CustomDialog diagNoFoundTrip;
    private boolean stopTimer;
    private Handler handler;

    private RadioGroup  rdbvehicle,rdbPerson,rdbtime,rdbaccept;
    private RadioButton rdbike,rdcar, person1, rdbooknow, rdbooklater;





    private boolean isClickedAccept;
    private boolean isFoundDRiver;
    private boolean isFoundAddress;
    private boolean isChooseDepart;

    private static int REQUEST_CODE_DRIVER_ACCEPT =123;
    private int HEIGTH_SCREEN, WIDTH_SCREEN;


    private User user;
    private String userID;
    private String trID;
    private float svp;
    private String depart, destination, date,time, cost;
    private String DrInit;
    private int costXN;
    private int nPerson;
    private double dura, dist;
    private String distance, duration;
    private String typeRequest;
    private LatLng homeAddress, workAddress, favoriteAddress;
    private LatLng DepartPoint, DestinationPoint, CurrentPoint, MarkedPoint;
    private Marker marker1, marker2;
    private Helper helper;
    private PresenterPassenger presenterPassenger;
    private PresenterMap presenterMap;
    private PresenterFavorite presenterFavorite;

    private Fragment2Activity mcallback;





    public void sendData(User user ) {
        this.user = user;
    }

    public void getUser(User user) {
        // Get user from fragment Profile!
        this.user = user;
    }


    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        mcallback = (Fragment2Activity) getActivity();
    }


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
            public void onMoveBegin(@NotNull MoveGestureDetector detector) {

            }
            @Override
            public void onMove(@NotNull MoveGestureDetector detector) {

            }
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



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Mapbox.getInstance(Objects.requireNonNull(getActivity()), getString(R.string.mapbox_api_key));
        Objects.requireNonNull(getActivity()).setTitle(getResources().getString(R.string.app_name));
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        View view = inflater.inflate(R.layout.activity_main_passenger_add_trip, container, false);
        mapToLayout(view);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        HEIGTH_SCREEN = displayMetrics.heightPixels;
        WIDTH_SCREEN  = displayMetrics.widthPixels;

        up2down = AnimationUtils.loadAnimation(getActivity(), R.anim.up2down);
        down2up = AnimationUtils.loadAnimation(getActivity(), R.anim.down2up);





        Bundle bundle = getArguments();
        if(bundle != null) {
            user = bundle.getParcelable("user");
            if (user != null) {
                userID = user.getUserID();


                helper              = new Helper();
                presenterMap        = new PresenterMap(this);
                presenterPassenger  = new PresenterPassenger(this);
                presenterFavorite   = new PresenterFavorite(this);



                Log.d("hahahahahahaha",TAG+ " Đang tìm các tài xế xung quanh...");
                CurrentPoint = new LatLng(Double.valueOf(user.getLat()),Double.valueOf(user.getLng()));

                presenterPassenger.receivedHandleGetMultiDrTrip(CurrentPoint,1);
                presenterFavorite.receivedHandleGetAllFavoriteAddress(userID);



                fab1.setOnClickListener(this);
                fab2.setOnClickListener(this);
                fab3.setOnClickListener(this);
                fab4.setOnClickListener(this);
                validate.setOnClickListener(this);
                fab2.setOnLongClickListener(this);
                fab3.setOnLongClickListener(this);
                fab4.setOnLongClickListener(this);
                autocompletetw.setOnClickListener(this);


                rdbvehicle.setOnCheckedChangeListener((group, checkedId) -> {

                    View radioButton = rdbvehicle.findViewById(checkedId);
                    int index = rdbvehicle.indexOfChild(radioButton);

                    switch (index){
                        case 0:

                            typeRequest = "bike";
                            break;

                        case 1:

                            typeRequest = "taxi";
                            break;
                    }

                    CalculCost(dist, nPerson, typeRequest,svp);

                });


                rdbPerson.setOnCheckedChangeListener((group, checkedId) -> {

                    View radioButton = rdbPerson.findViewById(checkedId);
                    int index = rdbPerson.indexOfChild(radioButton);

                    nPerson = index+1;

                    if(nPerson>1){
                        typeRequest = "taxi";
                        rdcar.setChecked(true);
                    }
                    else{
                        typeRequest = "bike";
                        rdbike.setChecked(true);
                    }
                    CalculCost(dist, nPerson, typeRequest,svp); });


                rdbtime.setOnCheckedChangeListener((group, checkedId) -> {

                    View radioButton = rdbtime.findViewById(checkedId);
                    int index = rdbtime.indexOfChild(radioButton);

                    switch (index) {
                        case 0:

                            svp = Calcul.svp(Float.valueOf(user.getReview()),Integer.valueOf(user.getNReview()));

                            isFoundDRiver = false;
                            DrInit = "-1";
                            SelectTimeBookNow();

                            break;
                        case 1:



                            svp = 0;   // free service fee

                            DrInit = "0";
                            SelectTimeBookLater();
                            break;
                    }
                    CalculCost(dist, nPerson, typeRequest,svp);

                });


                rdbaccept.setOnCheckedChangeListener((group, checkedId) -> {

                    View radioButton = rdbaccept.findViewById(checkedId);
                    int index = rdbaccept.indexOfChild(radioButton);

                    switch (index){
                        case 0:
                            initialSheetBox();
                            break;

                        case 1:


                            if (nPerson>1 && typeRequest.equals("bike")){
                                Helper.displayDiagWarning(getActivity(),getString(R.string.thaotackhople),getString(R.string.warning1));

                                rdbaccept.clearCheck();
                                return; }

                            if ( DrInit.equals("0") && user.getNtriplimitPa() >= 5){
                                Helper.displayDiagWarning(getActivity(),getResources().getString(R.string.err),getResources().getString(R.string.limited));
                                rdbaccept.clearCheck();
                                return;
                            }

                            if (DrInit.equals("0") && Helper.isIncorrectBookingTime(date,time)){
                                Helper.displayDiagWarning(getActivity(),getResources().getString(R.string.timefail),getResources().getString(R.string.timefail1));
                                rdbaccept.clearCheck();
                                return;}



                            if(Integer.valueOf(user.getPoints()) < costXN){

                                CustomDialog dialog = new CustomDialog(getActivity(),R.drawable.ic_warning, 2);
                                dialog.setCancelable(false);
                                dialog.setTitle(getString(R.string.sorry));
                                dialog.setMessage(getString(R.string.dtkdu));
                                dialog.setCancelText(getString(R.string.Close));
                                dialog.setCancelClickListener(dialog1 -> {
                                    dialog.dismiss();
                                    rdbaccept.clearCheck(); });
                                dialog.setConfirmText(getString(R.string.wallet));
                                dialog.setConfirmClickListener(dialog2 -> {
                                    dialog.dismiss();
                                    Bundle bundle1 = new Bundle();
                                    bundle1.putParcelable("user",user);
                                    bundle1.putInt("index",2);
                                    Intent intent1 = new Intent(getActivity(), AccountActivity.class);
                                    intent1.putExtra("bundle", bundle1);
                                    startActivity(intent1);
                                });
                                dialog.show();

                            }
                            else {

                                if (!isClickedAccept){
                                    isClickedAccept = true;

                                    dialogwait = new ProgressDialog(getActivity());
                                    dialogwait.setMessage(this.getString(R.string.sendrequest));
                                    dialogwait.setCancelable(false);
                                    dialogwait.show();



                                    String onCreated = Helper.getTimeStamp();
                                    AccessFireBase.addTrip(user.getName(),user.getAvatar(),userID, date, time, depart, destination, nPerson, duration, distance, cost, typeRequest,DrInit,onCreated,
                                            new IAccessFireBase.iAddTripPassenger() {
                                                @Override
                                                public void onSuccess(String id) {

                                                    trID = id;

                                                    dialogwait.dismiss();
                                                    rdbaccept.clearCheck();
                                                    int totalPoints = Integer.valueOf(user.getPoints()) - costXN;
                                                    user.setPoints(String.valueOf(totalPoints));
                                                    int ntrip = Integer.valueOf(user.getNtriptotal());
                                                    user.setNtriptotal(String.valueOf(ntrip+1));
                                                    mcallback.getUser(user);

                                                    if (DrInit.equals("-1")){
                                                        findDriver();}
                                                    else{
                                                        Helper.displayDiagSuccess(getActivity(),getResources().getString(R.string.addtripsuccess),getResources().getString(R.string.addtripsuccess1));
                                                        initialSheetBox();}
                                                }

                                                @Override
                                                public void onFailed() {
                                                    rdbaccept.clearCheck();
                                                    dialogwait.dismiss();
                                                    Helper.displayErrorMessage(getActivity(),getResources().getString(R.string.errorconnect));
                                                }
                                            });
                                }

                            }

                            break;
                    }

                });



            }
        }



        return view;
    }




    private void InitialAutoComplete(){

        autocompletetw.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_SEARCH
                    || i == EditorInfo.IME_ACTION_DONE
                    || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                    || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {


                String searchString = autocompletetw.getText().toString();
                Geocoder geocoder = new Geocoder(getActivity());
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

                        Helper.displayErrorMessage(getActivity(),getString(R.string.limitedadd));
                        return false;
                    }

                    MarkedPoint = new LatLng(address.getLatitude(),address.getLongitude());


                    updateMakerPoint(MarkedPoint);
                    setCameraLocation(MarkedPoint,2000);
                }
            }
            return true;
        });

    }


    private void initialSheetBox() {

        stopTimer       = false;
        isClickedAccept = false;
        isFoundDRiver   = false;
        isChooseDepart  = false;
        isFoundAddress  = true;
        handler         = null;

        typeRequest = "bike";
        nPerson     = 1;
        SelectTimeBookNow();


        frameLayout.setVisibility(View.INVISIBLE);
        autocompletetw.setVisibility(View.VISIBLE);
        fabmenu.setVisibility(View.VISIBLE);
        validate.setVisibility(View.VISIBLE);
        picker.setVisibility(View.VISIBLE);


        validate.setText(R.string.Adddepart);
        autocompletetw.setText(R.string.Currentlocation);


        picker.setImageResource(R.drawable.depart_cell_icon_new);


        rdbike.setChecked(true);
        person1.setChecked(true);
        rdbooknow.setChecked(true);
        rdbaccept.clearCheck();



        if (CurrentPoint != null) {
            DepartPoint   = CurrentPoint;
            setCameraLocation(CurrentPoint,2000);
        }
        if (marker1 != null) { marker1.remove();}
        if (marker2 != null) { marker2.remove();}
        if (currentRoute != null) {
            navigationMapRoute.updateRouteVisibilityTo(false);
            currentRoute = null;}
    }







    private void initialLocation() {

        MarkedPoint      = CurrentPoint;
        DepartPoint      = CurrentPoint;
        setCameraLocation(CurrentPoint,5000);

    }

    private void setCameraLocation(LatLng CurrentPoint, int duration) {

        if (map != null){
            CameraPosition position = new CameraPosition.Builder()
                    .target(CurrentPoint)
                    .zoom(15)
                    .build();
            map.animateCamera(CameraUpdateFactory.newCameraPosition(position),duration);
        }

    }

    private void updateMakerPoint(LatLng point){
        MarkedPoint = point;

        if (picker.getVisibility() == View.VISIBLE) {

            presenterMap.receivedHandleGetAddress(getActivity(),MarkedPoint);

            if (departPoint.getText().toString().isEmpty()) {

                DepartPoint = MarkedPoint;

            } else { DestinationPoint = MarkedPoint; }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.autocompletetw:
                autocompletetw.selectAll();
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
                            isChooseDepart = true;
                            isFoundAddress = false;

                            Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.depart_cell_icon_new);
                            Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
                            marker1 = map.addMarker(new MarkerOptions()
                                    .position(DepartPoint)
                                    .icon(IconFactory.getInstance(Objects.requireNonNull(getActivity())).fromBitmap(smallMarker)));
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
                                    .icon(IconFactory.getInstance(Objects.requireNonNull(getActivity())).fromBitmap(smallMarker)));
                            picker.setVisibility(View.INVISIBLE);



                            dialogfindroute = new ProgressDialog(getActivity());
                            dialogfindroute.setMessage(this.getString(R.string.findroute));
                            dialogfindroute.setCancelable(false);
                            dialogfindroute.show();
                            presenterMap.receivedHandleGetRoute(getActivity(), DepartPoint, DestinationPoint);
                        }
                    }
                }

                break;

            case R.id.fab1:
                updateMakerPoint(CurrentPoint);
                setCameraLocation(CurrentPoint,2000);
                fabmenu.close(true);
                break;

            case R.id.fab2:
                if (homeAddress==null) {
                    Helper.displayDiag(getActivity(),getResources().getString(R.string.unlocated),getResources().getString(R.string.nhan_giu)); }
                else{
                    updateMakerPoint(homeAddress);
                    setCameraLocation(homeAddress,2000);}
                fabmenu.close(true);
                break;

            case R.id.fab3:
                if (workAddress==null) {
                    Helper.displayDiag(getActivity(),getResources().getString(R.string.unlocated),getResources().getString(R.string.nhan_giu)); }
                else {
                    updateMakerPoint(workAddress);
                    setCameraLocation(workAddress,2000);}
                fabmenu.close(true);
                break;

            case R.id.fab4:
                if (favoriteAddress==null) {
                    Helper.displayDiag(getActivity(),getResources().getString(R.string.unlocated),getResources().getString(R.string.nhan_giu)); }

                else {
                    updateMakerPoint(favoriteAddress);
                    setCameraLocation(favoriteAddress,2000);}
                fabmenu.close(true);
                break;

        }

    }

    private void findDriver(){

        // Tìm layout_driver_info
        presenterPassenger.receivedHandleFindDriver(userID);
        frameLayout.setVisibility(View.INVISIBLE);


        dialog1 = new ProgressDialog(getActivity());
        dialog1.setCancelable(false);
        dialog1.setTitle(getActivity().getString(R.string.findDr));
        dialog1.show();


        handler = new Handler();
        handler.post(new Runnable() {
            int seconds = 10;
            public void run() {
                seconds--;
                dialog1.setMessage(getString(R.string.pleasewaitabout)+seconds+"s");
                if (seconds == 0) {
                    stopTimer = true;
                    dialog1.dismiss();


                    diagNoFoundTrip = new CustomDialog(getActivity(),R.drawable.ic_question, 2);
                    diagNoFoundTrip.setCancelable(false);
                    diagNoFoundTrip.setTitle(getString(R.string.no_dr_found));
                    diagNoFoundTrip.setMessage(getString(R.string.no_dr_found1));
                    diagNoFoundTrip.setCancelText(getString(R.string.No));
                    diagNoFoundTrip.setCancelClickListener(dialog1 -> {
                        if(!isFoundDRiver){ removeBookNowTrip(); } });
                    diagNoFoundTrip.setConfirmText(getString(R.string.Yes));
                    diagNoFoundTrip.setConfirmClickListener(dialog2 -> {
                        dialog2.dismiss();
                        if(!isFoundDRiver){
                            stopTimer = false;
                            findDriver(); }
                    });
                    diagNoFoundTrip.show();

                }
                if(!stopTimer) { handler.postDelayed(this, 1000); }
            }
        });


    }

    private void removeBookNowTrip(){

        diagNoFoundTrip.dismiss();
        int p = Integer.valueOf(user.getPoints()) + costXN;
        int ntrip = Integer.valueOf(user.getNtriptotal());
        user.setPoints(String.valueOf(p));
        user.setNtriptotal(String.valueOf(ntrip-1));
        mcallback.getUser(user);
        initialSheetBox();

        Intent intent = new Intent(Intent.ACTION_SYNC, null, getActivity(), DeleteBookNowTripService.class);
        intent.putExtra("user",user);
        intent.putExtra("trID",trID);
        intent.putExtra("refund",false);
        Objects.requireNonNull(getActivity()).startService(intent);



    }


    private void SelectTimeBookLater(){
        helper.handleShowDatePicker(getActivity(), new iHelper.pickTime() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(String[] mtime) {
                date = mtime[0];
                time = mtime[1];

                rdbooknow.setText(getString(R.string.booknow));
                rdbooklater.setText(String.format("%s - %s", mtime[0], mtime[1]));

            }

            @Override
            public void onFailed() {

            }
        });

    }

    private void SelectTimeBookNow(){
        helper.callSystemTime();
        String[] mtime = helper.getTime();
        date = mtime[0];
        time = mtime[1];

        rdbooklater.setText(getString(R.string.booklater));


    }



    @SuppressLint("SetTextI18n")
    private void showTB (int costXN, int totalPoints,float svp){
        if (costXN > totalPoints){
            twTB.setTextColor(Color.RED);
            twTB.setText(getString(R.string.youneed)+ costXN +getString(R.string.valico));
        }else{
            twTB.setTextColor(getResources().getColor(R.color.colorSecondaryText));
            twTB.setText(getString(R.string.svfee) + new DecimalFormat("##.##").format(svp) + "%.");
        }
    }



    @Override
    public void onGetMultiDrTripSuccess(List<DriverTrip> driverTrips) {


        Log.d("hahahahahahaha",TAG+ " Đã tìm thấy tổng cộng "+ driverTrips.size() +" tài xế xung quanh bạn.");

        if (map != null){

            int height = (HEIGTH_SCREEN*5)/100;
            int width  = (WIDTH_SCREEN*8)/100;
            Bitmap b0  = BitmapFactory.decodeResource(getResources(), R.drawable.icon_car);
            Bitmap smallMarker0 = Bitmap.createScaledBitmap(b0, width, height, false);

            for (int m = 0; m < driverTrips.size(); m++){

                DriverTrip dr = driverTrips.get(m);
                LatLng     p  = Helper.fromStringToLatLng(dr.getDepart());

                map.addMarker(new MarkerOptions()
                        .position(p)
                        .icon(IconFactory.getInstance(Objects.requireNonNull(getActivity())).fromBitmap(smallMarker0)));

            }

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
                Helper.displayErrorMessage(getActivity(),getString(R.string.addedhome));
                break;
            case R.id.fab3:
                workAddress     = MarkedPoint;
                AccessFireBase.updateWorkAddress(userID,lat,lng);
                Helper.displayErrorMessage(getActivity(),getString(R.string.addedwork));
                break;
            case R.id.fab4:
                favoriteAddress = MarkedPoint;
                AccessFireBase.updateFavoriteAddress(userID,lat,lng);
                Helper.displayErrorMessage(getActivity(),getString(R.string.addedfavorite));
                break;
        }
        return true;
    }



    @SuppressLint("SetTextI18n")
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


        duration = new DecimalFormat("##").format(dura);
        distance = new DecimalFormat("##.#").format(dist) ;

        isFoundAddress = true;
        frameLayout.startAnimation(up2down);
        frameLayout.setVisibility(View.VISIBLE);

        autocompletetw.setVisibility(View.INVISIBLE);
        fabmenu.setVisibility(View.INVISIBLE);
        twresulttime.setText(duration + getString(R.string.minute));
        twresultdura.setText(" (" + distance + " km)");

        svp = Calcul.svp(Float.valueOf(user.getReview()),Integer.valueOf(user.getNReview()));
        CalculCost(dist, 1, "bike",svp);

        // Draw the route on the map
        if (navigationMapRoute != null) {
            navigationMapRoute.updateRouteVisibilityTo(true); }
        else {
            navigationMapRoute = new NavigationMapRoute(null, mapView, map, R.style.Route); }

        navigationMapRoute.addRoute(currentRoute);


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


    @Override
    public void onFindRouteFail(String err) {
        dialogfindroute.dismiss();
        Helper.displayErrorMessage(getActivity(),err);
        initialSheetBox();
    }

    @Override
    public void onFindAddressSuccess(String address) {

        double absLat = Math.abs(CurrentPoint.getLatitude() - MarkedPoint.getLatitude());
        double absLng = Math.abs(CurrentPoint.getLongitude() - MarkedPoint.getLongitude());
        double err    = 0.00001;

        if (absLat > err && absLng > err) { autocompletetw.setText(address); }
        else { autocompletetw.setText(R.string.Currentlocation); }

        isFoundAddress = true;

    }

    @Override
    public void onFindAddressFail(String err) {
        Helper.displayErrorMessage(getActivity(),err);
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
    public void onFindDriverSuccess(PassengerTrip trip) {

        dialog1.dismiss();

        if (diagNoFoundTrip !=null){
            diagNoFoundTrip.cancel();
        }

        if (!isFoundDRiver && handler !=null){
            isFoundDRiver = true;
            handler.removeCallbacksAndMessages(null);

            initialSheetBox();

            Bundle bundle = new Bundle();
            bundle.putParcelable("user",user);
            bundle.putParcelable("trip",trip);
            Intent intent = new Intent(getActivity(), PaConfirmActivity.class);
            intent.putExtra("bundle",bundle);
            startActivityForResult(intent,REQUEST_CODE_DRIVER_ACCEPT);
        }




    }





    @SuppressLint("SetTextI18n")
    private void CalculCost(double distance, int nperson, String typeRequest, float svp){

        if (distance>0){

            double first_cost = Calcul.phi_chuyen_di(distance,nperson,typeRequest);
            double min_cost   = Calcul.phi_thap_nhat(first_cost);
            double max_cost   = Calcul.phi_cao_nhat(first_cost);

            int myprocess = (int) (max_cost/2);

            sbPrix.setMax((int) max_cost);
            sbPrix.setProgress((int) (max_cost/2));

            cost   = decimalFormat.format(first_cost);
            cost   = cost.replace(".",",");
            cost   = cost.replaceAll("\\s",",");
            costXN = Calcul.svf(first_cost,svp);
            twPrix.setText(cost + getString(R.string.VND));
            twPrixXN.setText(costXN +getString(R.string.points));
            showTB(costXN,Integer.valueOf(user.getPoints()),svp);

            sbPrix.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    double c;
                    if(progress<myprocess){
                        c    = min_cost +  (progress * (first_cost - min_cost)) / max_cost;
                    }else{
                        c    = first_cost + (progress * (max_cost - first_cost)) / max_cost;
                    }
                    cost   = decimalFormat.format(c);
                    cost   = cost.replace(".",",");
                    cost   = cost.replaceAll("\\s",",");
                    costXN = Calcul.svf(c,svp);
                    twPrix.setText(cost + getString(R.string.VND));
                    twPrixXN.setText(costXN + getString(R.string.points));
                    showTB(costXN,Integer.valueOf(user.getPoints()),svp);

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });



        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && data != null && requestCode == REQUEST_CODE_DRIVER_ACCEPT) {

            user = data.getParcelableExtra("user");
            mcallback.getUser(user);


        }
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

        if(!isFoundDRiver && handler !=null){

            handler.removeCallbacksAndMessages(null);

            int p = Integer.valueOf(user.getPoints())+ costXN;
            int ntrip = Integer.valueOf(user.getNtriptotal());
            user.setNtriptotal(String.valueOf(ntrip-1));
            user.setPoints(String.valueOf(p));
            mcallback.getUser(user);


            Intent intent = new Intent(Intent.ACTION_SYNC, null, getActivity(), DeleteBookNowTripService.class);
            intent.putExtra("user",user);
            intent.putExtra("trID",trID);
            Objects.requireNonNull(getActivity()).startService(intent);

        }

        if (diagNoFoundTrip !=null){
            diagNoFoundTrip.cancel(); }

        if (dialog1 !=null){
            dialog1.cancel(); }


        initialSheetBox();

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
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }






    private void mapToLayout(View view) {
        departPoint  = view.findViewById(R.id.btPointA);
        arrivePoint  = view.findViewById(R.id.btPointB);
        frameLayout  = view.findViewById(R.id.frame1);
        twresulttime = view.findViewById(R.id.twresult1);
        twresultdura = view.findViewById(R.id.twresult2);
        mapView      = view.findViewById(R.id.mapview);
        picker       = view.findViewById(R.id.maker);
        sbPrix       = view.findViewById(R.id.sbPrix);
        twPrix       = view.findViewById(R.id.twPrix);
        rdbtime      = view.findViewById(R.id.rdbtime);
        rdbooknow    = view.findViewById(R.id.booknow);
        rdbooklater  = view.findViewById(R.id.booklater);
        rdbvehicle   = view.findViewById(R.id.rbtCar);
        rdbike       = view.findViewById(R.id.bike);
        rdcar        = view.findViewById(R.id.taxi);
        rdbaccept    = view.findViewById(R.id.btValidate);
        rdbPerson    = view.findViewById(R.id.rbtPersonp);
        person1      = view.findViewById(R.id.person1);
        validate     = view.findViewById(R.id.validate);
        twPrixXN     = view.findViewById(R.id.twPrixXN);
        twTB         = view.findViewById(R.id.twTB);
        fabmenu      = view.findViewById(R.id.fabmenu);
        fab1         = view.findViewById(R.id.fab1);
        fab2         = view.findViewById(R.id.fab2);
        fab3         = view.findViewById(R.id.fab3);
        fab4         = view.findViewById(R.id.fab4);
        autocompletetw = view.findViewById(R.id.autocompletetw);




    }



}
