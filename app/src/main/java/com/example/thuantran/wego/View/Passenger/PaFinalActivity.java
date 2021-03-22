package com.example.thuantran.wego.View.Passenger;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

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
import android.widget.Toast;

import com.example.thuantran.wego.DataAccess.AccessFireBase;
import com.example.thuantran.wego.DataAccess.IAccessFireBase;
import com.example.thuantran.wego.View.Fragment.ChatFragment;
import com.example.thuantran.wego.View.Fragment.ViewRouteFragment;
import com.example.thuantran.wego.Interface.Profile.Profile;
import com.example.thuantran.wego.Interface.Trip.Rela;
import com.example.thuantran.wego.Object.PassengerTrip;
import com.example.thuantran.wego.Object.Relation;
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


public class PaFinalActivity extends AppCompatActivity implements Rela.View, Profile.View{


    private ImageView davatar, photocar,gender;
    private TextView Drname, Drphone, Drnamecar, Drtime, Drcost, nreview, distancePa;
    private TextView Drreview, depart, destination;
    private Button  finished, report;
    private RadioGroup stSMSpa;
    private RadioButton btsend;
    private FragmentManager fragmentManager;
    private Fragment fragment;
    private Bundle bundle;


    private boolean flag = true;
    private LatLng originPoint, destinationPoint,CurrentPoint;
    private PassengerTrip passengerTrip;
    private User user;


    private boolean isReviewed =  false;
    private boolean isPassTrip =  false;
    private boolean isOpenDiag =  false;
    private boolean isOpenRepo =  false;



    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main_passenger_final);
        mapToLayout();


        Bundle b = getIntent().getBundleExtra("bundle");
        if (b != null) {
            user          = b.getParcelable("user");
            passengerTrip = b.getParcelable("trip");

        }else{ return; }

        CurrentPoint      = new LatLng(Double.valueOf(user.getLat()),Double.valueOf(user.getLng()));
        originPoint       = Helper.fromStringToLatLng(passengerTrip.getDepart());
        destinationPoint  = Helper.fromStringToLatLng(passengerTrip.getDestination());


        //Kiểm tra chuyến đi này đã được đánh giá hay chưa
        if(passengerTrip.getStt().equals("completed")) {

            finished.setVisibility(View.VISIBLE);
            report.setVisibility(View.VISIBLE);
            isPassTrip = true;

            if (!passengerTrip.getReviewPa2Dr().equals("")){
                isReviewed = true;
                finished.setVisibility(View.INVISIBLE);
                report.setText(getString(R.string.da_xac_nhan));
                report.setBackground(getResources().getDrawable(R.drawable.button_green_background));
            }

        }else{
            report.setVisibility(View.INVISIBLE);
            finished.setVisibility(View.INVISIBLE);
        }



        fragmentManager = getSupportFragmentManager();
        if(fragmentManager.findFragmentByTag("map")!=null){

            fragmentManager.beginTransaction().show(Objects.requireNonNull(fragmentManager.findFragmentByTag("map"))).commit();
        }else{
            fragment = new ViewRouteFragment();
            bundle   = new Bundle();
            bundle.putParcelable("originPoint",originPoint);
            bundle.putParcelable("destinationPoint",destinationPoint);
            bundle.putParcelable("currentPoint",CurrentPoint);
            fragment.setArguments(bundle);
            fragmentManager.beginTransaction().add(R.id.contentSMS,fragment,"map").commit();
        }

        if(fragmentManager.findFragmentByTag("layout_sms_sent")!=null){
            fragmentManager.beginTransaction().hide(Objects.requireNonNull(fragmentManager.findFragmentByTag("layout_sms_sent"))).commit(); }



        PresenterProfile presenterProfile = new PresenterProfile(this);
        presenterProfile.receivedHandleGetProfile(this, passengerTrip.getIDdr());
        presenterProfile.receivedHandleGetTBCar(this, passengerTrip.getIDdr());

        depart.setText(Helper.fromStringLatLngToFullAddress(this, passengerTrip.getDepart()));
        destination.setText(Helper.fromStringLatLngToFullAddress(this, passengerTrip.getDestination()));
        Drtime.setText(passengerTrip.getDate() +getString(R.string.attime)+ passengerTrip.getTime() );
        Drcost.setText(passengerTrip.getCost()+getString(R.string.VND));
        distancePa.setText(passengerTrip.getDuration() + getString(R.string.minute) + " (" + passengerTrip.getDistance() + " km)") ;
        if (passengerTrip.getTypeRequest().equals("taxi")){
            Drnamecar.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_directions_car_white_16dp,0,0,0); }



        finished.setOnClickListener(view -> {

            if (!isOpenDiag){
                if (!isReviewed){ openDialog(); }
            }
            isOpenDiag = true;

        });


        report.setOnClickListener(view -> {

            if (!isOpenRepo){
                if (!isReviewed){ openDialogReport(); }
            }
            isOpenRepo = true;

        });



        stSMSpa.setOnCheckedChangeListener((group, checkedId) -> {

            View radioButton = stSMSpa.findViewById(checkedId);
            int index = stSMSpa.indexOfChild(radioButton);


            switch (index){

                case 0:
                    Intent intent = new Intent();
                    intent.putExtra("passengerTrip", passengerTrip);
                    intent.putExtra("user",user);
                    setResult(Activity.RESULT_OK,intent);
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
                            bundle.putParcelable("trip", passengerTrip);
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

                    stSMSpa.clearCheck();
                    break;

            }
        });


    }


    @SuppressLint("SimpleDateFormat")
    private void openDialog() {

        LayoutInflater inflater = LayoutInflater.from(this);
        View subView = inflater.inflate(R.layout.layout_finished_trip, null);

        EditText  edSMS     = subView.findViewById(R.id.message);
        RatingBar ratingBar = subView.findViewById(R.id.rating);
        ImageView image     = subView.findViewById(R.id.image_rate);

        Picasso.get().load(passengerTrip.getAvatar())
                .resize(250,250)
                .centerCrop()
                .into(image);


        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(this));
        builder.setView(subView);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", (dialogInterface, i) -> {

            float rate       = ratingBar.getRating();
            String sms       = edSMS.getText().toString().trim();
            String timestamp = Helper.getTimeStamp();



            AccessFireBase.addReview(passengerTrip.getIDdr(), passengerTrip.getID(), user.getName(), user.getAvatar(), sms, rate, timestamp,
                    new IAccessFireBase.iAddReview() {
                        @Override
                        public void onSuccess() {
                            AccessFireBase.updateReviewTrip(passengerTrip.getID(), "Pa", new IAccessFireBase.iUpdateReview() {
                                @Override
                                public void onSuccess() {
                                    passengerTrip.setReviewPa2Dr("reviewed");
                                    finished.setText(getString(R.string.da_xac_nhan));
                                    finished.setBackground(getResources().getDrawable(R.drawable.button_green_background));
                                    isReviewed = true;
                                }

                                @Override
                                public void onFailed() {

                                }
                            });

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

        Picasso.get().load(passengerTrip.getAvatar())
                .resize(250,250)
                .centerCrop()
                .into(image);


        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(this));
        builder.setView(subView);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", (dialogInterface, i) -> {

            String sms = edSMS.getText().toString().trim();

            if (sms.isEmpty()){

                if (delay.isChecked())
                    sms = getString(R.string.tr_h_n_m_kh_ng_b_o_tr_c);

                if (destroy.isChecked())
                    sms = getString(R.string.t_ng_hu_chuy_n_i_v_o_ph_t_cu_i);
            }

            if (!sms.isEmpty()){
                String timestamp = Helper.getTimeStamp();

                AccessFireBase.addReport(passengerTrip.getIDdr(), passengerTrip.getID(), user.getName(), user.getAvatar(), sms, timestamp,
                        new IAccessFireBase.iAddReport() {
                            @Override
                            public void onSuccess() {
                                AccessFireBase.updateReviewTrip(passengerTrip.getID(), "Pa", new IAccessFireBase.iUpdateReview() {
                                    @Override
                                    public void onSuccess() {
                                        passengerTrip.setReviewPa2Dr("reported");
                                        report.setText(getString(R.string.da_report));
                                        report.setBackground(getResources().getDrawable(R.drawable.button_green_background));
                                        isReviewed = true;


                                        String cost   = passengerTrip.getCost().replace(",","");
                                        float svp     = Calcul.svp(Float.valueOf(user.getReview()),Integer.valueOf(user.getNReview()));
                                        int p         = Calcul.svf(Double.valueOf(cost),svp);
                                        int pts       = Integer.valueOf(user.getPoints())+p;
                                        user.setPoints(String.valueOf(pts));

                                        Helper.displayDiagSuccess(PaFinalActivity.this,getString(R.string.daguiyc1),getString(R.string.refunddt) + p + getString(R.string.points) );

                                    }

                                    @Override
                                    public void onFailed() {

                                    }
                                });
                            }

                            @Override
                            public void onFailed() {

                            }
                        });


            }else{
                isOpenRepo = false;
                Helper.displayErrorMessage(PaFinalActivity.this,getString(R.string.tooshor1));
            }




        });
        builder.setNegativeButton(getString(R.string.Cancel1), (dialogInterface, i) -> {
            isOpenRepo = false;
        });
        builder.show();
    }




    @Override
    public void onGetListSelectedDriverSuccess(ArrayList<Relation> ids) {

    }

    @Override
    public void onGetListSelectedDriverFail(String err) {

    }


    @Override
    public void onGetProfileSuccess(User user) {

        Drname.setText(user.getName());
        Drphone.setText(user.getPhone());
        Drreview.setText(user.getReview());
        nreview.setText(user.getNReview());
        Picasso.get().load(user.getAvatar())
                .resize(250,250)
                .centerCrop()
                .into(davatar);

        if (user.getGender().equals("nam")){
            gender.setImageResource(R.drawable.ic_masculine);}
        else {
            gender.setImageResource(R.drawable.ic_female); }




        nreview.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putParcelable("user",user);
            Intent intent2 = new Intent(PaFinalActivity.this, ShowReviewActivity.class);
            intent2.putExtra("bundle", bundle);
            startActivity(intent2); });

        davatar.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putParcelable("user",user);
            Intent intent2 = new Intent(PaFinalActivity.this, ShowReviewActivity.class);
            intent2.putExtra("bundle", bundle);
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


        Picasso.get().load(object.getPhotoCar()).into(photocar);
        Drnamecar.setText(object.getNameCar());

    }

    @Override
    public void onGetTBCarSuccess(ArrayList<Car> object) {

    }

    @Override
    public void onGetTBCarFail(String err) {
        Toast.makeText(this,  err, Toast.LENGTH_SHORT).show();

    }

    private void mapToLayout(){
        davatar   = findViewById(R.id.davatar);
        gender    = findViewById(R.id.gender);
        photocar  = findViewById(R.id.photocar);
        Drname    = findViewById(R.id.dten);
        Drphone   = findViewById(R.id.twphone);
        Drnamecar = findViewById(R.id.twxe);
        Drreview  = findViewById(R.id.npdanhgia);
        nreview   = findViewById(R.id.ndanhgia);
        Drtime    = findViewById(R.id.dinfor);
        Drcost    = findViewById(R.id.dprix);
        stSMSpa   = findViewById(R.id.stSMSpa);
        btsend    = findViewById(R.id.sendsms);
        finished  = findViewById(R.id.finished);
        report    = findViewById(R.id.report);
        distancePa   = findViewById(R.id.distancePa);
        depart       = findViewById(R.id.depart);
        destination  = findViewById(R.id.destination);

    }



}

