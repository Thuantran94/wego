package com.example.thuantran.wego.View.Driver;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.thuantran.wego.DataAccess.AccessFireBase;
import com.example.thuantran.wego.DataAccess.Constant;
import com.example.thuantran.wego.DataAccess.IAccessFireBase;
import com.example.thuantran.wego.Interface.Trip.Trip;
import com.example.thuantran.wego.Object.PassengerTrip;
import com.example.thuantran.wego.Presenter.Trip.PresenterTrip;
import com.example.thuantran.wego.Tools.Calcul;
import com.example.thuantran.wego.Tools.CustomDialog;
import com.example.thuantran.wego.View.Main.AccountActivity;
import com.example.thuantran.wego.View.Main.ShowReviewActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.thuantran.wego.Interface.Map.Map;
import com.example.thuantran.wego.Interface.Profile.Profile;
import com.example.thuantran.wego.Object.User;
import com.example.thuantran.wego.Tools.Helper;
import com.example.thuantran.wego.Presenter.Map.PresenterMap;
import com.example.thuantran.wego.Presenter.Profile.PresenterProfile;
import com.example.thuantran.wego.R;
import com.example.thuantran.wego.Object.Car;
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
import com.squareup.picasso.Picasso;


import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class DrConfirmActivity extends AppCompatActivity implements OnMapReadyCallback, Map.View, Profile.View, Trip.View,MapView.OnDidFinishLoadingMapListener{

    private ProgressDialog dialogsend;
    private MapView  mapView;
    private ImageView pavatar, gender;
    private ImageView imtime1, imtime2, imprix1, imprix2;
    private RadioGroup btdAccept;
    private TextView pten, pTimeMetting, pCost, distancePa, nperson, nreview, typecarrq;
    private TextView pdanhgia, depart, destination;
    private TextView distancepickup, sam;
    private View viewSheet;
    private Button btdAcceptSheet;
    private TextView twPrix, twTime;
    private String new_cost, timeupdated ;

    private int HEIGTH_SCREEN, WIDTH_SCREEN;
    private DecimalFormat decimalFormat = new DecimalFormat("#,##0");
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    private boolean isClickAccepted = false;

    private int ntime1, ntime2, nprix1, nprix2, maxselect;

    private int cost_svf;
    private User user;
    private String trID;
    private String userID;
    private String stt;


    private String pdate, ptime, pcost;
    private String ftime,fcost;

    private MapboxMap map;
    private DirectionsRoute currentRoute;
    private List<DirectionsRoute> list_routes;
    private NavigationMapRoute navigationMapRoute;

    private PresenterMap presenterMap;
    private PresenterTrip presenterTrip;
    private PassengerTrip passengerTrip;

    private LatLng originPoint, destinationPoint;




    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        map = mapboxMap;
        map.setStyle(Style.MAPBOX_STREETS, style -> { });
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
        if(originPoint !=null && destinationPoint !=null){

            int height = (HEIGTH_SCREEN*6)/100;
            int width  = (WIDTH_SCREEN*8)/100;


            map.setMinZoomPreference(10);
            LatLngBounds bounds = new LatLngBounds.Builder()
                    .include(Constant.BOUND_CORNER_NW)
                    .include(Constant.BOUND_CORNER_SE)
                    .build();
            map.setLatLngBoundsForCameraTarget(bounds);

            map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds,width));


            Bitmap b0 = BitmapFactory.decodeResource(getResources(), R.drawable.icon_car);
            Bitmap smallMarker0 = Bitmap.createScaledBitmap(b0, width, height, false);
            map.addMarker(new MarkerOptions()
                    .position(new LatLng(Double.valueOf(user.getLat()),Double.valueOf(user.getLng())))
                    .icon(IconFactory.getInstance(this).fromBitmap(smallMarker0)));

            Bitmap b1 = BitmapFactory.decodeResource(getResources(), R.drawable.depart_cell_icon_new);
            Bitmap smallMarker1 = Bitmap.createScaledBitmap(b1, width, height, false);
            map.addMarker(new MarkerOptions()
                    .position(originPoint)
                    .icon(IconFactory.getInstance(this).fromBitmap(smallMarker1)));

            Bitmap b2 = BitmapFactory.decodeResource(getResources(), R.drawable.arrive_cell_icon_new);
            Bitmap smallMarker2 = Bitmap.createScaledBitmap(b2, width, height, false);
            map.addMarker(new MarkerOptions()
                    .position(destinationPoint)
                    .icon(IconFactory.getInstance(this).fromBitmap(smallMarker2)));


            presenterMap = new PresenterMap(DrConfirmActivity.this);
            presenterMap.receivedHandleGetRoute(DrConfirmActivity.this,originPoint,destinationPoint);
        }
    }


    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Mapbox.getInstance(this, getString(R.string.mapbox_api_key));
        setContentView(R.layout.activity_main_driver_confirm);
        mapToLayout();
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);



        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        HEIGTH_SCREEN = displayMetrics.heightPixels;
        WIDTH_SCREEN  = displayMetrics.widthPixels;



        list_routes = new ArrayList<>();
        Bundle b = getIntent().getBundleExtra("bundle");
        if (b != null) {
            user          = b.getParcelable("user");
            passengerTrip = b.getParcelable("trip");

            userID  = user.getUserID();
            trID    = passengerTrip.getID();
            stt     = passengerTrip.getStt();
        }




        if (stt.equals("received")){ pCost.setEnabled(false); }

        originPoint         = Helper.fromStringToLatLng(passengerTrip.getDepart());
        destinationPoint    = Helper.fromStringToLatLng(passengerTrip.getDestination());

        depart.setText(Helper.fromStringLatLngToFullAddress(this, passengerTrip.getDepart()));
        destination.setText(Helper.fromStringLatLngToFullAddress(this, passengerTrip.getDestination()));


        pdate      = passengerTrip.getDate();
        ptime      = passengerTrip.getTime();
        pcost      = passengerTrip.getCost();
        ftime      = ptime;
        fcost      = pcost;

        nperson.setText(passengerTrip.getNPerson()+ getString(R.string.person));
        pCost.setText(passengerTrip.getCost() + getString(R.string.VND));

        distancePa.setText(passengerTrip.getDuration() + getString(R.string.minute) + " (" + passengerTrip.getDistance() + " km)") ;

        if (passengerTrip.getIDdr().equals("-1")){
            pTimeMetting.setTextColor(getResources().getColor(R.color.red));
            pTimeMetting.setText(getString(R.string.urgent));
        } else{
            pTimeMetting.setTextColor(getResources().getColor(R.color.colorOrange));
            pTimeMetting.setText(passengerTrip.getDate() + getString(R.string.attime) + passengerTrip.getTime());
        }


        if (passengerTrip.getTypeRequest().equals("bike")){
            typecarrq.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_bike_white_16dp,0,0,0);
            typecarrq.setText(getString(R.string.Bike));
        }
        else{
            typecarrq.setText(getString(R.string.Taxi));
            typecarrq.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_directions_car_white_16dp,0,0,0);
        }


        PresenterProfile presenterProfile = new PresenterProfile(this);
        presenterProfile.receivedHandleGetProfile(this, passengerTrip.getUserID());



        // free option
        pCost.setOnClickListener(v -> {
            try {
                showDiagSheet();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });


        btdAccept.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId){

                case R.id.close:
                    Intent intent1 = new Intent();
                    intent1.putExtra("user", user);
                    setResult(RESULT_OK, intent1);
                    finish();
                    break;

                case R.id.accept:
                    if (!stt.equals("received")){
                        if (!isClickAccepted){
                            isClickAccepted = true;
                            Accept();

                        }else{ finish(); }

                    }else{
                        Helper.displayErrorMessage(DrConfirmActivity.this,getString(R.string.dang_cho1));
                        btdAccept.clearCheck();
                    }
                    break;
            }
        });


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

        AccessFireBase.updatePoints(userID, user.getPoints(), new IAccessFireBase.iUpdatePoint() {
            @Override
            public void onSuccess() {
                AccessFireBase.updateTotalTrip(userID,user.getNtriptotal());
            }

            @Override
            public void onFailed() {
                Helper.displayErrorMessage(DrConfirmActivity.this,getString(R.string.errorconnect));

            }
        });

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





    private void Accept(){

        int currentPoints = Integer.valueOf(user.getPoints());
        String c = fcost.replace(",","");
        c = c.replace(".","");
        c = c.replaceAll("\\s","");
        double cost1 = Double.valueOf(c);

        float svp   = Calcul.svp(Float.valueOf(user.getReview()),Integer.valueOf(user.getNReview()));
        cost_svf    = Calcul.svf(cost1,svp);

        CustomDialog diag = new CustomDialog(this, R.drawable.ic_question,2);

        diag.setTitle(getString(R.string.xacnhanyeucau));
        diag.setMessage(getString(R.string.ph_d_ch_vu)+ cost_svf +getString(R.string.points));
        diag.setCancelable(false);
        diag.setConfirmText(getString(R.string.Yes));
        diag.setConfirmClickListener(sweetAlertDialog -> {
            diag.dismiss();

            if(currentPoints < cost_svf){

                CustomDialog pDialog = new CustomDialog(this, R.drawable.ic_warning,2);
                pDialog.setTitle(getString(R.string.sorry));
                pDialog.setMessage(getString(R.string.dtkdu));
                pDialog.setCancelable(false);
                pDialog.setConfirmText(getString(R.string.wallet));
                pDialog.setConfirmClickListener(customDialog -> {
                    pDialog.dismiss();
                    Bundle bundle1 = new Bundle();
                    bundle1.putParcelable("user",user);
                    bundle1.putInt("index",2);
                    Intent intent1 = new Intent(DrConfirmActivity.this, AccountActivity.class);
                    intent1.putExtra("bundle", bundle1);
                    startActivity(intent1);
                });
                pDialog.setCancelText(getString(R.string.Close));
                pDialog.setCancelClickListener(customDialog -> pDialog.dismiss());
                pDialog.show();

            }else{


                dialogsend = new ProgressDialog(this);
                dialogsend.setMessage(getString(R.string.checkingtrip));
                dialogsend.setCancelable(false);
                dialogsend.show();

                presenterTrip = new PresenterTrip(this);
                presenterTrip.receivedHandleGetOneTrip(this, passengerTrip.getID());



            }

            isClickAccepted = false;

        });
        diag.setCancelText(getString(R.string.No));
        diag.setCancelClickListener(sweetAlertDialog -> {
            diag.dismiss();
            isClickAccepted = false;
        });
        diag.show();


    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onGetTBCarSuccess(Car car) {

    }

    @Override
    public void onGetTBCarSuccess(ArrayList<Car> object) {

    }


    @Override
    public void onGetTBCarFail(String err) {
    }

    @Override
    public void onGetOneTripSuccess(PassengerTrip passengerTrip) {

        if (passengerTrip.getStt().equals("pending")){

            dialogsend.setMessage(this.getString(R.string.sendrequest));

            AccessFireBase.updateMessenger(trID, new IAccessFireBase.iUpdateMessenger() {
                @Override
                public void onSuccess() {
                    AccessFireBase.addRelation(trID, userID, pdate, ftime, fcost, passengerTrip.getIDdr(), new IAccessFireBase.iAddRelation() {
                        @Override
                        public void onSuccess() {
                            dialogsend.cancel();

                            int pts   = Integer.valueOf(user.getPoints()) - cost_svf;
                            int ntrip = Integer.valueOf(user.getNtriptotal());
                            user.setPoints(String.valueOf(pts));
                            user.setNtriptotal(String.valueOf(ntrip+1));
                            Helper.displayErrorMessage(DrConfirmActivity.this,getString(R.string.daguiyc2));

                            Intent intent1 = new Intent();
                            intent1.putExtra("user", user);
                            setResult(RESULT_OK, intent1);
                            finish();

                        }

                        @Override
                        public void onFailed() {
                            Helper.displayErrorMessage(DrConfirmActivity.this,getString(R.string.errorconnect));
                            dialogsend.dismiss();
                        }
                    });




                }

                @Override
                public void onFailed() {
                    Helper.displayErrorMessage(DrConfirmActivity.this,getString(R.string.errorconnect));
                    dialogsend.dismiss();
                }
            });





        }else{

            Helper.displayErrorMessage(this,getString(R.string.da_co_nguoi_nhan1));
            dialogsend.dismiss();
        }


    }

    @Override
    public void onGetTBPaTripFail(String err) {

        Helper.displayErrorMessage(this,getString(R.string.da_co_nguoi_nhan1));
        dialogsend.dismiss();
    }


    @Override
    public void onFindRouteSuccess(retrofit2.Response<DirectionsResponse> response) {


        try{
            if (response.body() != null) {
                if(currentRoute == null){
                    this.currentRoute = response.body().routes().get(0);
                    list_routes.add(currentRoute);
                    LatLng CurrentPoint = new LatLng(Double.valueOf(user.getLat()),Double.valueOf(user.getLng()));
                    presenterMap.receivedHandleGetRoute(DrConfirmActivity.this,CurrentPoint,originPoint);
                    setCameraLocation(CurrentPoint);

                }else{
                    DirectionsRoute myRoute = response.body().routes().get(0);
                    double dpickup = response.body().routes().get(0).distance() / 1000;
                    sam.setText(getString(R.string.b_m_v_sds));
                    distancepickup.setText(new DecimalFormat("##.#").format(dpickup)  + " km");
                    list_routes.add(myRoute);
                    // Draw the route on the map
                    if (navigationMapRoute != null) {
                        navigationMapRoute.updateRouteVisibilityTo(false);
                    } else {
                        navigationMapRoute = new NavigationMapRoute(null, mapView, map, R.style.Route);
                    }
                    navigationMapRoute.addRoutes(list_routes);


                }
            }
        }catch (NullPointerException ignored){

        }
    }

    @Override
    public void onFindRouteFail(String err) {
        Helper.displayErrorMessage(this,getString(R.string.Checkinternet));
    }

    @Override
    public void onFindAddressSuccess(String address) {

    }

    @Override
    public void onFindAddressFail(String err) {

    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onGetProfileSuccess(User user) {


        String pname = user.getName();
        float pareview = Float.valueOf(user.getReview());
        pten.setText(pname);

        pdanhgia.setText(String.valueOf(pareview));
        nreview.setText(user.getNReview());
        Picasso.get().load(user.getAvatar())
                .resize(250,250)
                .centerCrop()
                .into(pavatar);

        if (user.getGender().equals("nam")){ gender.setImageResource(R.drawable.ic_masculine);}
        else { gender.setImageResource(R.drawable.ic_female); }


        nreview.setOnClickListener(v -> {
            Bundle bundle2 = new Bundle();
            bundle2.putParcelable("user",user);
            Intent intent2 = new Intent(DrConfirmActivity.this, ShowReviewActivity.class);
            intent2.putExtra("bundle", bundle2);
            startActivity(intent2); });

        pavatar.setOnClickListener(v -> {
            Bundle bundle2 = new Bundle();
            bundle2.putParcelable("user",user);
            Intent intent2 = new Intent(DrConfirmActivity.this, ShowReviewActivity.class);
            intent2.putExtra("bundle", bundle2);
            startActivity(intent2); });

    }

    @Override
    public void onGetProfileSuccess(ArrayList<User> user,ArrayList<Car> car) {

    }


    @Override
    public void onGetProfileFail(String err) {
    }



    @Override
    public void onGetMultiTripSuccess(ArrayList<PassengerTrip> passengerTripArrayList) {

    }


    @Override
    public void haveDriverSelected(PassengerTrip trip) {

    }

    @Override
    public void finalDriverSelected(PassengerTrip trip) {

    }


    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showDiagSheet() throws ParseException {

        maxselect = Constant.DR_MAXSELECT;
        nprix1    = 1;
        nprix2    = 1;
        ntime1    = 1;
        ntime2    = 1;

        imprix1.setVisibility(View.VISIBLE);
        imprix2.setVisibility(View.VISIBLE);
        imtime1.setVisibility(View.VISIBLE);
        imtime2.setVisibility(View.VISIBLE);


        if(viewSheet.getParent() != null) {
            ((ViewGroup)viewSheet.getParent()).removeView(viewSheet); // <- fix
        }

        final BottomSheetDialog dialog = new BottomSheetDialog(DrConfirmActivity.this);
        dialog.setContentView(viewSheet);
        dialog.show();


        pcost               = pcost.replaceAll("\\s","");
        String current_prix = pcost.replace(",","");


        double first_cost = Double.valueOf(current_prix);

        new_cost = decimalFormat.format(first_cost);
        new_cost = new_cost.replace(".",",");
        new_cost = new_cost.replaceAll("\\s",",");
        twPrix.setText( new_cost + getString(R.string.VND));


        if (passengerTrip.getIDdr().equals("-1")){
            imtime1.setVisibility(View.INVISIBLE);
            imtime2.setVisibility(View.INVISIBLE);
            maxselect++; }


        imprix1.setOnClickListener(v -> {

            if (nprix1<maxselect){
                double c = first_cost - (first_cost*nprix1*Constant.DR_PERCENT)/100;
                new_cost   = decimalFormat.format(c);
                new_cost   = new_cost.replace(".",",");
                new_cost   = new_cost.replaceAll("\\s",",");

                twPrix.setText(new_cost + getString(R.string.VND));
                pCost.setText(new_cost + getString(R.string.VND));

                nprix1++;
                nprix2--;
                imprix2.setVisibility(View.VISIBLE);
            }else{
                imprix1.setVisibility(View.INVISIBLE);
            }

        });


        imprix2.setOnClickListener(v -> {

            if (nprix2<maxselect){
                double c = first_cost + (first_cost*nprix2*Constant.DR_PERCENT)/100;

                new_cost   = decimalFormat.format(c);
                new_cost   = new_cost.replace(".",",");
                new_cost   = new_cost.replaceAll("\\s",",");

                twPrix.setText(new_cost + getString(R.string.VND));
                pCost.setText(new_cost + getString(R.string.VND));

                nprix2++;
                nprix1--;
                imprix1.setVisibility(View.VISIBLE);
            }else{
                imprix2.setVisibility(View.INVISIBLE);
            }
        });


        timeupdated = ptime;

        Date current_time = timeFormat.parse(ptime);
        long first_time = current_time.getTime();
        twTime.setText(ptime);
        imtime1.setOnClickListener(v -> {

            if (ntime1<maxselect){
                long t     = first_time - ntime1*10*60*1000;

                long Hours   =  t / (60 * 60* 1000) % 24;
                long Minutes =  t / (60 * 1000) % 60;

                timeupdated  = String.format("%02d", Hours )+":"+String.format("%02d", Minutes);
                twTime.setText(timeupdated);
                pTimeMetting.setText( pdate + getString(R.string.attime)+ timeupdated);


                ntime1++;
                ntime2--;
                imtime2.setVisibility(View.VISIBLE);
            }else{
                imtime1.setVisibility(View.INVISIBLE);
            }

        });


        imtime2.setOnClickListener(v -> {

            if (ntime2<maxselect){
                long t     = first_time + ntime2*10*60*1000;


                long Hours   =  t / (60 * 60* 1000) % 24;
                long Minutes =  t / (60 * 1000) % 60;

                timeupdated  = String.format("%02d", Hours )+":"+String.format("%02d", Minutes);
                twTime.setText(timeupdated);
                pTimeMetting.setText( pdate + getString(R.string.attime)+ timeupdated);


                ntime2++;
                ntime1--;
                imtime1.setVisibility(View.VISIBLE);
            }else{
                imtime2.setVisibility(View.INVISIBLE);
            }

        });

        btdAcceptSheet.setOnClickListener(v -> {
            dialog.cancel();

            fcost  = new_cost;
            ftime  = timeupdated;
        });
    }


    private void setCameraLocation(LatLng CurrentPoint) {
        CameraPosition position = new CameraPosition.Builder()
                .target(CurrentPoint)
                .zoom(13)
                .tilt(30)
                .build();

        map.animateCamera(CameraUpdateFactory.newCameraPosition(position), 5000);
    }


    @SuppressLint("InflateParams")
    private void mapToLayout(){
        btdAccept       = findViewById(R.id.btdAccept);

        pten            = findViewById(R.id.pten);
        pdanhgia        = findViewById(R.id.npdanhgia);
        nreview         = findViewById(R.id.ndanhgia);
        pTimeMetting    = findViewById(R.id.pTimeMetting);
        pCost           = findViewById(R.id.pprix);
        nperson         = findViewById(R.id.nperson);
        distancePa      = findViewById(R.id.distancePa);
        sam             = findViewById(R.id.sam);
        distancepickup  = findViewById(R.id.distancepickup);
        depart          = findViewById(R.id.depart);
        destination     = findViewById(R.id.destination);
        pavatar         = findViewById(R.id.pavatar);
        gender          = findViewById(R.id.gender);
        mapView         = findViewById(R.id.myMap1);
        typecarrq       = findViewById(R.id.typecarrq);

        viewSheet       = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_driver_confirm, null);
        twPrix          = viewSheet.findViewById(R.id.twPrix);
        twTime          = viewSheet.findViewById(R.id.twTime);

        btdAcceptSheet  = viewSheet.findViewById(R.id.btdAcceptSheet);


        imtime1 =  viewSheet.findViewById(R.id.imtime1);
        imtime2 =  viewSheet.findViewById(R.id.imtime2);
        imprix1 =  viewSheet.findViewById(R.id.imprix1);
        imprix2 =  viewSheet.findViewById(R.id.imprix2);




    }


}
