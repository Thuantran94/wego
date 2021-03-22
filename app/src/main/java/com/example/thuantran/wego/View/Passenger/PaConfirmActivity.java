package com.example.thuantran.wego.View.Passenger;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.thuantran.wego.Adapter.RecyclerItemClickListener;
import com.example.thuantran.wego.DataAccess.AccessFireBase;
import com.example.thuantran.wego.DataAccess.DeleteBookNowTripService;
import com.example.thuantran.wego.DataAccess.IAccessFireBase;
import com.example.thuantran.wego.Interface.Profile.Profile;
import com.example.thuantran.wego.Interface.Trip.Rela;
import com.example.thuantran.wego.Object.PassengerTrip;
import com.example.thuantran.wego.Object.Relation;
import com.example.thuantran.wego.Object.User;
import com.example.thuantran.wego.Object.UserSelected;
import com.example.thuantran.wego.Tools.Calcul;
import com.example.thuantran.wego.Tools.CustomDialog;
import com.example.thuantran.wego.Tools.Helper;
import com.example.thuantran.wego.Presenter.Profile.PresenterProfile;
import com.example.thuantran.wego.Presenter.Trip.PresenterRelation;
import com.example.thuantran.wego.R;
import com.example.thuantran.wego.Adapter.UserAdapter;
import com.example.thuantran.wego.Object.Car;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;


public class PaConfirmActivity extends AppCompatActivity implements Rela.View, Profile.View {

    private ProgressDialog dialog, dialogconfirm;

    private TextView twDate, twPersons, twCost, twDepart, twDestination;
    private RecyclerView recyclerView;
    private ImageView Back, Myavatar;


    private User user;
    private String trID;
    private PassengerTrip passengerTrip;

    private ArrayList<Relation> selectedDriver;
    private ArrayList<UserSelected> driverArrayList;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main_passenger_confirm);
        mapToLayout();


        Bundle b = getIntent().getBundleExtra("bundle");

        if (b != null) {

            user          = b.getParcelable("user");
            passengerTrip = b.getParcelable("trip");


            if (passengerTrip != null) {
                trID     = passengerTrip.getID();
                Picasso.get().load(user.getAvatar()).into(Myavatar);

                twDate.setText(passengerTrip.getDate() + getString(R.string.attime) + passengerTrip.getTime());
                twPersons.setText(passengerTrip.getNPerson() );
                twCost.setText(passengerTrip.getCost() + getString(R.string.VND));
                twDepart.setText(Helper.fromStringLatLngToFullAddress(this, passengerTrip.getDepart()));
                twDestination.setText(Helper.fromStringLatLngToFullAddress(this, passengerTrip.getDestination()));


                dialog = new ProgressDialog(this);
                dialog.setMessage(getString(R.string.loading));
                dialog.setCancelable(false);
                dialog.show();

                PresenterRelation presenterRelation = new PresenterRelation(this);
                presenterRelation.receivedHandleGetListSelectedDriver(this,trID);




            }

        }


        Back.setOnClickListener(v -> {

            if (passengerTrip.getIDdr().equals("-1")){ removeTrip(); }
            else{ finish(); }
        });


    }



    @Override
    protected void onPause() {
        super.onPause();
        if (passengerTrip.getIDdr().equals("-1")){


            String cost = passengerTrip.getCost().replace(",","");
            float svp   = Calcul.svp(Float.valueOf(user.getReview()),Integer.valueOf(user.getNReview()));
            int refund  = Calcul.svf(Double.valueOf(cost),svp);
            int pts     = Integer.valueOf(user.getPoints())+refund;
            int ntrip = Integer.valueOf(user.getNtriptotal());

            user.setPoints(String.valueOf(pts));
            user.setNtriptotal(String.valueOf(ntrip-1));

            Intent intent = new Intent(Intent.ACTION_SYNC, null,this, DeleteBookNowTripService.class);
            intent.putExtra("user",user);
            intent.putExtra("trID",trID);
            intent.putExtra("refund",true);
            startService(intent);

        }

        Intent intent1 = new Intent();
        intent1.putExtra("user",user);
        setResult(Activity.RESULT_OK,intent1);
        finish();



    }


    private void removeTrip(){

        CustomDialog pDialog = new CustomDialog(this, R.drawable.ic_warning,2);

        pDialog.setTitle(getString(R.string.xacnhanhuy));
        pDialog.setConfirmText(getString(R.string.Yes));
        pDialog.setConfirmClickListener(sweetAlertDialog -> {
            pDialog.cancel();
            refundMoney();

        });

        pDialog.setCancelText(getString(R.string.No));
        pDialog.setCancelClickListener(sweetAlertDialog -> pDialog.cancel());
        pDialog.show();
    }

    private void refundMoney(){

        String cost = passengerTrip.getCost().replace(",","");
        float svp   = Calcul.svp(Float.valueOf(user.getReview()),Integer.valueOf(user.getNReview()));
        int refund  = Calcul.svf(Double.valueOf(cost),svp);
        int pts     = Integer.valueOf(user.getPoints())+refund;
        int ntrip   = Integer.valueOf(user.getNtriptotal());

        user.setPoints(String.valueOf(pts));
        user.setNtriptotal(String.valueOf(ntrip-1));

        Intent intent = new Intent(Intent.ACTION_SYNC, null,this, DeleteBookNowTripService.class);
        intent.putExtra("user",user);
        intent.putExtra("trID",trID);
        intent.putExtra("refund",true);
        startService(intent);

        Intent intent1 = new Intent();
        intent1.putExtra("user",user);
        setResult(Activity.RESULT_OK,intent1);
        finish();


    }


    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    public void Accept( int position){

        UserSelected myDriver   = driverArrayList.get(position);


        CustomDialog dialog = new CustomDialog(this,R.drawable.ic_question, 2);
        dialog.setCancelable(false);
        dialog.setTitle(getString(R.string.xacnhanyeucau1));
        dialog.setMessage(getString(R.string.xacnhanyeucautx));
        dialog.setCancelText(getString(R.string.No));
        dialog.setCancelClickListener(dialog1 -> dialog.dismiss());
        dialog.setConfirmText(getString(R.string.Yes));
        dialog.setConfirmClickListener(dialog12 -> {
            dialog.dismiss();

            if (passengerTrip.getIDdr().equals("-1")){

                AccessFireBase.removeBookNow(trID, new IAccessFireBase.iRemoveTripBookNow() {
                    @Override
                    public void onSuccess() { }

                    @Override
                    public void onFailed() { }
                });

            }


            dialogconfirm = new ProgressDialog(PaConfirmActivity.this);
            dialogconfirm.setMessage(getString(R.string.sendrequest));
            dialogconfirm.setCancelable(false);
            dialogconfirm.show();


            passengerTrip.setAvatar(myDriver.getAvatar());
            passengerTrip.setName(myDriver.getName());
            passengerTrip.setTime(myDriver.getTime());
            passengerTrip.setCost(myDriver.getCost());
            passengerTrip.setIDdr(myDriver.getId());
            passengerTrip.setStt("accepted");

            Helper helper = new Helper();
            helper.callSystemTime();
            String[] rs2      = helper.getTime();
            String datesms    = rs2[0];
            String timesms    = rs2[1];

            AccessFireBase.updateReceivedTrip(trID, myDriver.getAvatar(), myDriver.getName(), myDriver.getTime(), myDriver.getCost(), myDriver.getId(),
                    new IAccessFireBase.iUpdateReceivedTrip() {
                        @Override
                        public void onSuccess() {

                            AccessFireBase.updateRelationStt(trID, myDriver.getId(), new IAccessFireBase.iUpdateRelationStt() {
                                @Override
                                public void onSuccess() {

                                    String contextsms = getString(R.string.hello) + myDriver.getName() +
                                            getString(R.string.toidanhanchuyendi) + user.getPhone();

                                    AccessFireBase.addSms(user, contextsms,datesms ,timesms, passengerTrip, new IAccessFireBase.iAddSms() {

                                        @Override
                                        public void onSuccess() {

                                            AccessFireBase.updateLastSms(user.getUserID(), user.getAvatar(), user.getName(), contextsms ,datesms,timesms, passengerTrip,
                                                    new IAccessFireBase.iUpdateLastSms() {
                                                        @Override
                                                        public void onSuccess() {

                                                            dialogconfirm.dismiss();

                                                            Intent intent = new Intent();
                                                            intent.putExtra("passengerTrip", passengerTrip);
                                                            intent.putExtra("user",user);
                                                            setResult(Activity.RESULT_OK,intent);


                                                            Bundle bundle = new Bundle();
                                                            bundle.putParcelable("user",user);
                                                            bundle.putParcelable("trip", passengerTrip);
                                                            Intent intent1 = new Intent(PaConfirmActivity.this, PaFinalActivity.class);
                                                            intent1.putExtra("bundle",bundle);
                                                            startActivity(intent1);
                                                        }

                                                        @Override
                                                        public void onFailed() {
                                                            dialogconfirm.dismiss();
                                                            Helper.displayErrorMessage(PaConfirmActivity.this,getString(R.string.errorconnect));
                                                        }
                                                    });
                                        }

                                        @Override
                                        public void onFailed() {
                                            dialogconfirm.dismiss();
                                            Helper.displayErrorMessage(PaConfirmActivity.this,getString(R.string.errorconnect));
                                        }
                                    });

                                }

                                @Override
                                public void onFailed() {
                                    dialogconfirm.dismiss();
                                    Helper.displayErrorMessage(PaConfirmActivity.this,getString(R.string.errorconnect));
                                }
                            });

                        }

                        @Override
                        public void onFailed() {
                            dialogconfirm.dismiss();
                            Helper.displayErrorMessage(PaConfirmActivity.this,getString(R.string.errorconnect));
                        }
                    });



        });
        dialog.show();


    }



    @Override
    public void onGetListSelectedDriverSuccess(ArrayList<Relation> selectedDriver) {

        this.selectedDriver = selectedDriver;
        ArrayList<String> selectedDriverIDList = new ArrayList<>();
        for (int i=0;i<selectedDriver.size();i++){

            selectedDriverIDList.add(selectedDriver.get(i).getIDdr());
        }

        PresenterProfile presenterProfile = new PresenterProfile(this);
        presenterProfile.receivedHandleGetProfile(this, selectedDriverIDList);

    }

    @Override
    public void onGetListSelectedDriverFail(String err) {
        dialog.dismiss();
        Helper.displayErrorMessage(PaConfirmActivity.this,getString(R.string.errorconnect));
    }



    @Override
    public void onGetProfileSuccess(User user) {

    }

    @Override
    public void onGetProfileSuccess(ArrayList<User> users, ArrayList<Car> cars) {
        dialog.dismiss();
        driverArrayList  = new ArrayList<>();

        if (selectedDriver.size() == cars.size()){

            // lay thong tin cac layout_driver_info da chon chuyen di
            for (int i = 0; i < selectedDriver.size(); i++){

                Relation driver = selectedDriver.get(i);
                String drID     = driver.getIDdr();
                String date     = driver.getDate();
                String time     = driver.getTime();
                String cost     = driver.getCost();

                User   user     = users.get(i);
                String name     = user.getName();
                String phone    = user.getPhone();
                String gender   = user.getGender();
                String avatar   = user.getAvatar();

                float rev       = Float.valueOf(user.getReview());
                int  nrev       = Integer.valueOf(user.getNReview());

                Car    car      = cars.get(i);
                String namecar  = car.getNameCar();
                String photocar = car.getPhotoCar();

                driverArrayList.add(new UserSelected(drID,name,phone,gender,date,time,cost,namecar,rev,nrev,avatar,photocar));
            }

            Collections.reverse(driverArrayList);
            UserAdapter adapter = new UserAdapter(this, driverArrayList);

            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();



            recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this,recyclerView,new RecyclerItemClickListener.OnItemClickListener(){

                @Override
                public void onItemClick(View view, int i) {
                    // chỉ cho phép chọn tài xế khi bạn chưa chọn bất kì tài xế nào
                    if (passengerTrip.getIDdr().length()<5){ Accept(i);}
                }

                @Override
                public void onLongItemClick(View view, int position) {

                }
            }));

        }

    }

    @Override
    public void onGetProfileFail(String err) {
        dialog.dismiss();
        Helper.displayErrorMessage(PaConfirmActivity.this,getString(R.string.errorconnect));
    }

    @Override
    public void onGetTBCarSuccess(Car object) {

    }

    @Override
    public void onGetTBCarSuccess(ArrayList<Car> cars) {

    }

    @Override
    public void onGetTBCarFail(String err) {

    }


    private void mapToLayout(){
        twDate          = findViewById(R.id.date);
        twPersons       = findViewById(R.id.persons);
        twCost          = findViewById(R.id.prix);
        twDepart        = findViewById(R.id.depart);
        twDestination   = findViewById(R.id.destination);
        Back            = findViewById(R.id.backtt12);
        Myavatar        = findViewById(R.id.avatar);
        recyclerView    = findViewById(R.id.lwDrivers);

    }

}
