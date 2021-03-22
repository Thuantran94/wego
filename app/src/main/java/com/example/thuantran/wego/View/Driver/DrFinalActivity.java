package com.example.thuantran.wego.View.Driver;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.thuantran.wego.DataAccess.AccessFireBase;
import com.example.thuantran.wego.DataAccess.IAccessFireBase;
import com.example.thuantran.wego.Presenter.Trip.PresenterTrip;
import com.example.thuantran.wego.View.Fragment.ChatFragment;
import com.example.thuantran.wego.View.Fragment.ViewRouteFragment;
import com.example.thuantran.wego.Interface.Profile.Profile;
import com.example.thuantran.wego.Interface.Trip.Trip;
import com.example.thuantran.wego.Object.PassengerTrip;
import com.example.thuantran.wego.Object.User;
import com.example.thuantran.wego.Tools.Calcul;
import com.example.thuantran.wego.Tools.Helper;
import com.example.thuantran.wego.Presenter.Profile.PresenterProfile;
import com.example.thuantran.wego.R;
import com.example.thuantran.wego.Object.Car;
import com.example.thuantran.wego.View.Main.ShowReviewActivity;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;


public class DrFinalActivity extends AppCompatActivity implements Trip.View, Profile.View {


    private ImageView pavatar, gender;
    private TextView pten, pphone, pTimeMetting, pCost,distancePa,nperson, nreview, typecarrq;
    private TextView pdanhgia, depart, destination;

    private FragmentManager fragmentManager;
    private Fragment fragment;
    private Bundle bundle;

    private User user, userPA;
    private PassengerTrip trip;
    private LatLng originPoint, destinationPoint, CurrentPoint;
    private Button finished,report;
    private RadioGroup btSMSdr;
    private RadioButton btsend;

    private boolean flag = true;

    private boolean isReviewed =  false;
    private boolean isPassTrip =  false;
    private boolean isOpenDiag =  false;
    private boolean isOpenRepo =  false;

    private boolean isRemovedByPassenger = false;


    private PresenterTrip presenterTrip;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main_driver_final);
        mapToLayout();

        Bundle b = getIntent().getBundleExtra("bundle");
        if (b != null) {
            user     = b.getParcelable("user");
            trip     = b.getParcelable("trip");
        }else{ return; }


        CurrentPoint           = new LatLng(Double.valueOf(user.getLat()),Double.valueOf(user.getLng()));
        originPoint            = Helper.fromStringToLatLng(trip.getDepart());
        destinationPoint       = Helper.fromStringToLatLng(trip.getDestination());


        presenterTrip = new PresenterTrip(this);
        presenterTrip.receivedHandleGetOneTrip(this, trip.getID());

        finished.setClickable(false);
        report.setClickable(false);


        if(trip.getStt().equals("completed")) {
            finished.setVisibility(View.VISIBLE);
            report.setVisibility(View.VISIBLE);
            isPassTrip = true;

            //Chuyến đi này đã được đánh giá
            if (!trip.getReviewDr2Pa().equals("")){
                isReviewed = true;
                finished.setVisibility(View.INVISIBLE);
                report.setText(getString(R.string.da_xac_nhan));
                report.setBackground(getResources().getDrawable(R.drawable.button_green_background));
            }


        }else{
            report.setVisibility(View.INVISIBLE);
            finished.setVisibility(View.INVISIBLE); }



        fragmentManager = getSupportFragmentManager();
        if(fragmentManager.findFragmentByTag("map")!=null){
            fragmentManager.beginTransaction().show(Objects.requireNonNull(fragmentManager.findFragmentByTag("map"))).commit();
        }
        else{

            fragment = new ViewRouteFragment();
            bundle   = new Bundle();
            bundle.putParcelable("originPoint",originPoint);
            bundle.putParcelable("destinationPoint",destinationPoint);
            bundle.putParcelable("currentPoint",CurrentPoint);
            fragment.setArguments(bundle);
            fragmentManager.beginTransaction().add(R.id.contentSMS,fragment,"map").commit();
        }
        if(fragmentManager.findFragmentByTag("layout_sms_sent")!=null)
            fragmentManager.beginTransaction().hide(Objects.requireNonNull(fragmentManager.findFragmentByTag("layout_sms_sent"))).commit();



        PresenterProfile presenterProfile = new PresenterProfile(DrFinalActivity.this);
        presenterProfile.receivedHandleGetProfile(this,trip.getUserID());

        pTimeMetting.setText(trip.getDate() + getString(R.string.attime) + trip.getTime());


        if (trip.getTypeRequest().equals("bike")){
            typecarrq.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_bike_white_16dp,0,0,0);
            typecarrq.setText(getString(R.string.Bike));
        }
        else{
            typecarrq.setText(getString(R.string.Taxi));
            typecarrq.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_directions_car_white_16dp,0,0,0);
        }


        nperson.setText(trip.getNPerson() + getString(R.string.person));
        distancePa.setText(trip.getDuration() + getString(R.string.minute) + " (" + trip.getDistance() + " km)") ;
        pCost.setText(trip.getCost() + getString(R.string.VND));


        depart.setText(Helper.fromStringLatLngToFullAddress(this,trip.getDepart()));
        destination.setText(Helper.fromStringLatLngToFullAddress(this,trip.getDestination()));



        btSMSdr.setOnCheckedChangeListener((group, checkedId) -> {
            View radioButton = btSMSdr.findViewById(checkedId);
            int index = btSMSdr.indexOfChild(radioButton);

            switch (index){
                case 0:
                    Intent intent1 = new Intent();
                    intent1.putExtra("trip", trip);
                    intent1.putExtra("user",user);
                    setResult(Activity.RESULT_OK, intent1);
                    finish();
                    break;

                case 1:
                    if(flag){
                        if(fragmentManager.findFragmentByTag("layout_sms_sent")!=null){
                            fragmentManager.beginTransaction().show(Objects.requireNonNull(fragmentManager.findFragmentByTag("layout_sms_sent"))).commit();
                        }
                        else{
                            fragment = new ChatFragment();
                            bundle   = new Bundle();
                            bundle.putParcelable("user",user);
                            bundle.putParcelable("trip",trip);
                            fragment.setArguments(bundle);
                            fragmentManager.beginTransaction().add(R.id.contentSMS,fragment,"layout_sms_sent").commit();
                        }
                        if(fragmentManager.findFragmentByTag("map")!=null)
                            fragmentManager.beginTransaction().hide(Objects.requireNonNull(fragmentManager.findFragmentByTag("map"))).commit();

                        report.setVisibility(View.INVISIBLE);
                        finished.setVisibility(View.INVISIBLE);
                        btsend.setText(getString(R.string.An));
                        flag = false;
                    }
                    else{

                        if(fragmentManager.findFragmentByTag("map")!=null){
                            fragmentManager.beginTransaction().show(Objects.requireNonNull(fragmentManager.findFragmentByTag("map"))).commit();
                        }
                        else{
                            fragment = new ViewRouteFragment();
                            bundle   = new Bundle();
                            bundle.putParcelable("originPoint",originPoint);
                            bundle.putParcelable("destinationPoint",destinationPoint);
                            bundle.putParcelable("currentPoint",CurrentPoint);
                            fragment.setArguments(bundle);
                            fragmentManager.beginTransaction().add(R.id.contentSMS,fragment,"map").commit();
                        }
                        if(fragmentManager.findFragmentByTag("layout_sms_sent")!=null)
                            fragmentManager.beginTransaction().hide(Objects.requireNonNull(fragmentManager.findFragmentByTag("layout_sms_sent"))).commit();


                        if(isPassTrip){
                            report.setVisibility(View.VISIBLE);
                            finished.setVisibility(View.VISIBLE);}
                        btsend.setText(getString(R.string.Gui_tin_nhan));
                        flag = true;
                    }
                    btSMSdr.clearCheck();
                    break;
            }
        });




        finished.setOnClickListener(view -> {
           if(!isOpenDiag){
               if (!isReviewed){ openDialogReview(); }
           }
           isOpenDiag = true;
        });

        report.setOnClickListener(view -> {
            if(!isOpenRepo){
                if (!isReviewed){ openDialogReport(); }
            }
            isOpenRepo = true;
        });


    }
    @SuppressLint("SimpleDateFormat")
    private void openDialogReview() {

        LayoutInflater inflater = LayoutInflater.from(this);
        View subView = inflater.inflate(R.layout.layout_finished_trip, null);

        EditText  edSMS     = subView.findViewById(R.id.message);
        RatingBar ratingBar = subView.findViewById(R.id.rating);
        ImageView image     = subView.findViewById(R.id.image_rate);

        Picasso.get().load(userPA.getAvatar())
                .resize(250,250)
                .centerCrop()
                .into(image);


        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(this));
        builder.setView(subView);
        builder.setCancelable(false);
        builder.setPositiveButton(getString(R.string.ok), (dialogInterface, i) -> {


            String timestamp = Helper.getTimeStamp();
            float rate = ratingBar.getRating();
            String sms  = edSMS.getText().toString().trim();

            AccessFireBase.addReview(userPA.getUserID(),trip.getID(),user.getName(),user.getAvatar(),sms,rate,timestamp,
                    new IAccessFireBase.iAddReview() {
                        @Override
                        public void onSuccess() {

                            if (!isRemovedByPassenger){

                                AccessFireBase.updateReviewTrip(trip.getID(),"Dr", new IAccessFireBase.iUpdateReview() {
                                    @Override
                                    public void onSuccess() { }

                                    @Override
                                    public void onFailed() { }
                                });
                            }


                            trip.setReviewDr2Pa("reviewed");
                            finished.setText(getString(R.string.da_xac_nhan));
                            finished.setBackground(getResources().getDrawable(R.drawable.button_green_background));
                            isReviewed = true;


                        }

                        @Override
                        public void onFailed() {

                        }
                    });


        });
        builder.setNegativeButton(getString(R.string.Cancel1), (dialogInterface, i) -> {
            isOpenDiag = false;
        });
        builder.show();
    }


    private void openDialogReport() {

        LayoutInflater inflater = LayoutInflater.from(this);
        View subView = inflater.inflate(R.layout.layout_report, null);

        EditText  edSMS     = subView.findViewById(R.id.message);
        RadioButton delay   = subView.findViewById(R.id.delay);
        RadioButton destroy = subView.findViewById(R.id.destroy);

        ImageView image     = subView.findViewById(R.id.image_rate);

        Picasso.get().load(userPA.getAvatar())
                .resize(250,250)
                .centerCrop()
                .into(image);


        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(this));
        builder.setView(subView);
        builder.setCancelable(false);
        builder.setPositiveButton(getString(R.string.ok), (dialogInterface, i) -> {



            String sms  = edSMS.getText().toString().trim();

            if (sms.isEmpty()){

                if (delay.isChecked())
                    sms = getString(R.string.tr_h_n_m_kh_ng_b_o_tr_c);

                if (destroy.isChecked())
                    sms = getString(R.string.t_ng_hu_chuy_n_i_v_o_ph_t_cu_i);
            }

            if (!sms.isEmpty()){
                String timestamp = Helper.getTimeStamp();


                AccessFireBase.addReport(userPA.getUserID(), trip.getID(), user.getName(), user.getAvatar(), sms, timestamp,
                        new IAccessFireBase.iAddReport() {
                            @Override
                            public void onSuccess() {

                                if (!isRemovedByPassenger){
                                    AccessFireBase.updateReviewTrip(trip.getID(), "Dr", new IAccessFireBase.iUpdateReview() {
                                        @Override
                                        public void onSuccess() { }

                                        @Override
                                        public void onFailed() { }
                                    });
                                }

                                trip.setReviewDr2Pa("reported");
                                report.setText(getString(R.string.da_report));
                                report.setBackground(getResources().getDrawable(R.drawable.button_green_background));
                                isReviewed = true;

                                String cost   = trip.getCost().replace(",","");
                                float svp     = Calcul.svp(Float.valueOf(user.getReview()),Integer.valueOf(user.getNReview()));
                                int p         = Calcul.svf(Double.valueOf(cost),svp);
                                int pts       = Integer.valueOf(user.getPoints())+p;
                                user.setPoints(String.valueOf(pts));

                                Helper.displayDiagSuccess(DrFinalActivity.this,getString(R.string.daguiyc1),getString(R.string.refunddt) + p + getString(R.string.points));



                            }

                            @Override
                            public void onFailed() {

                            }
                        });

            }else{
                isOpenRepo = false;
                Helper.displayErrorMessage(DrFinalActivity.this,getString(R.string.tooshor1));
            }




        });
        builder.setNegativeButton(getString(R.string.Cancel1), (dialogInterface, i) -> {
            isOpenRepo = false;
        });
        builder.show();
    }



    private void mapToLayout(){
        pten            = findViewById(R.id.pten);
        pphone          = findViewById(R.id.pphone);
        pdanhgia        = findViewById(R.id.npdanhgia);
        nreview         = findViewById(R.id.ndanhgia);
        typecarrq       = findViewById(R.id.typecarrq);
        pTimeMetting    = findViewById(R.id.pTimeMetting);
        pCost           = findViewById(R.id.pprix);
        nperson         = findViewById(R.id.nperson);
        distancePa      = findViewById(R.id.distancePa);
        pavatar         = findViewById(R.id.pavatar);
        gender          = findViewById(R.id.gender);
        btSMSdr         = findViewById(R.id.btSMSdr);
        btsend          = findViewById(R.id.sendsms);
        finished        = findViewById(R.id.finished);
        report          = findViewById(R.id.report);
        depart          = findViewById(R.id.depart);
        destination     = findViewById(R.id.destination);



    }


    @Override
    public void onGetMultiTripSuccess(ArrayList<PassengerTrip> passengerTripArrayList) {

    }

    @Override
    public void onGetOneTripSuccess(PassengerTrip passengerTrip) {


        finished.setClickable(true);
        report.setClickable(true);
        isRemovedByPassenger = false;

    }


    @Override
    public void onGetTBPaTripFail(String err) {

        finished.setClickable(true);
        report.setClickable(true);

        isRemovedByPassenger = true;

    }


    @Override
    public void haveDriverSelected(PassengerTrip trID) {

    }

    @Override
    public void finalDriverSelected(PassengerTrip trID) {

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onGetProfileSuccess(User userPA) {


        this.userPA = userPA;

        pten.setText(userPA.getName());
        pphone.setText(userPA.getPhone());
        pdanhgia.setText(userPA.getReview());
        nreview.setText(userPA.getNReview());
        Picasso.get().load(userPA.getAvatar())
                .resize(250,250)
                .centerCrop()
                .into(pavatar);



        if (userPA.getGender().equals("nam")){ gender.setImageResource(R.drawable.ic_masculine);}
        else { gender.setImageResource(R.drawable.ic_female); }



        nreview.setOnClickListener(v -> {
            Bundle bundle2 = new Bundle();
            bundle2.putParcelable("user",userPA);
            Intent intent2 = new Intent(DrFinalActivity.this, ShowReviewActivity.class);
            intent2.putExtra("bundle", bundle2);
            startActivity(intent2); });

        pavatar.setOnClickListener(v -> {
            Bundle bundle2 = new Bundle();
            bundle2.putParcelable("user",userPA);
            Intent intent2 = new Intent(DrFinalActivity.this, ShowReviewActivity.class);
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
    public void onGetTBCarSuccess(Car object) {

    }

    @Override
    public void onGetTBCarSuccess(ArrayList<Car> object) {

    }

    @Override
    public void onGetTBCarFail(String err) {

    }



}


/*
    Intent skypeIntent = new Intent(Intent.ACTION_VIEW);
    skypeIntent.setData(Uri.parse("skype:" + skypeId + "?call"));
    context.startActivity(skypeIntent);
 */