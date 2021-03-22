package com.example.thuantran.wego.View.Driver;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationAdapter;
import com.example.thuantran.wego.Adapter.TripAdapter;
import com.example.thuantran.wego.DataAccess.AccessFireBase;
import com.example.thuantran.wego.DataAccess.Constant;
import com.example.thuantran.wego.DataAccess.IAccessFireBase;
import com.example.thuantran.wego.Interface.Profile.Profile;
import com.example.thuantran.wego.Presenter.Profile.PresenterProfile;
import com.example.thuantran.wego.Tools.CustomDialog;
import com.example.thuantran.wego.View.Fragment.CodeFragment;
import com.example.thuantran.wego.View.Fragment.SMSFragment;
import com.example.thuantran.wego.Interface.Profile.Review;
import com.example.thuantran.wego.Interface.Tools.Fragment2Activity;
import com.example.thuantran.wego.Interface.Tools.ReceivedSMSListener;
import com.example.thuantran.wego.Interface.User.Driver;
import com.example.thuantran.wego.Object.Car;
import com.example.thuantran.wego.Object.DriverTrip;
import com.example.thuantran.wego.Object.PassengerTrip;
import com.example.thuantran.wego.Object.ReviewContext;
import com.example.thuantran.wego.Presenter.Profile.PresenterReview;
import com.example.thuantran.wego.Presenter.User.PresenterDriver;
import com.example.thuantran.wego.Tools.Helper;
import com.example.thuantran.wego.View.Main.AccountActivity;
import com.example.thuantran.wego.View.Main.MainActivity;
import com.example.thuantran.wego.View.Main.ShowReviewActivity;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.thuantran.wego.View.Fragment.AboutUsFragment;
import com.example.thuantran.wego.View.Fragment.AvailableTripFragment;
import com.example.thuantran.wego.Object.User;
import com.example.thuantran.wego.R;
import com.google.firebase.auth.FirebaseAuth;
import com.jaygoo.widget.OnRangeChangedListener;
import com.jaygoo.widget.RangeSeekBar;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;



public class MainDriver extends AppCompatActivity implements Fragment2Activity, ReceivedSMSListener,Driver.View, Review.View, Profile.View {



    private ProgressDialog dialog;
    private TextView twheaderName, twheaderPhone, points;
    private TextView ratingBar;
    private TextView nreview;
    private ImageView imheaderAvatar;
    private NavigationView mNavigationView;
    private AHBottomNavigation bottomNavigationView;
    private FragmentManager fragmentManager;
    private Fragment fragment;
    private Bundle bundle;
    private Menu mfilter;


    private static final int REQUEST_CODE_CHANGE_INFO = 123;
    private static final int REQUEST_CODE_DRSHOWTRIP  = 456;
    private static final String TAG = "MainDriver";
    private User user;
    private Car car;
    private String userID;
    private LatLng getCurrentPoint;
    private PresenterDriver presenterDriver;
    private PresenterProfile presenterProfile;
    private ArrayList<PassengerTrip> arrayListTrip;

    private boolean isClickFilter = false;



    @SuppressLint("RtlHardcoded")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main_driver);
        mapToLayout();




        Bundle b = getIntent().getBundleExtra("bundle");
        if (b != null) {
            user     = b.getParcelable("user");
            if (user !=null){

                initialUserInformation();

                userID = user.getUserID();
                fragmentManager = getSupportFragmentManager();
                fragment = new AvailableTripFragment();
                fragmentManager.beginTransaction().add(R.id.fragmentDr,fragment,"one").commit();



                fragment = new SMSFragment();
                bundle   = new Bundle();
                bundle.putParcelable("user",user);
                fragment.setArguments(bundle);
                fragmentManager.beginTransaction().add(R.id.fragmentDr,fragment,"sms").commit();

                if(fragmentManager.executePendingTransactions()){
                    fragmentManager.beginTransaction().hide(Objects.requireNonNull(fragmentManager.findFragmentByTag("sms"))).commit(); }


                // Thay đổi ảnh đại diện và các thông tin cá nhân
                imheaderAvatar.setOnClickListener(v -> {
                    Bundle bundle1 = new Bundle();
                    bundle1.putParcelable("user",user);
                    bundle1.putInt("index",0);
                    Intent intent1 = new Intent(MainDriver.this, AccountActivity.class);
                    intent1.putExtra("bundle", bundle1);
                    startActivityForResult(intent1,REQUEST_CODE_CHANGE_INFO); });


                nreview.setOnClickListener(v -> {
                    Bundle bundle2 = new Bundle();
                    bundle2.putParcelable("user",user);
                    Intent intent2 = new Intent(MainDriver.this, ShowReviewActivity.class);
                    intent2.putExtra("bundle", bundle2);
                    startActivity(intent2);

                });


                mNavigationView.setItemIconTintList(null);
                mNavigationView.setNavigationItemSelectedListener(item -> {
                    mNavigation(item);
                    return true; });

                bottomNavigationView.setOnTabSelectedListener((position, wasSelected) -> {
                    bottomNavigation(position);
                    return true; });


                dialog = new ProgressDialog(this);
                dialog.setCancelable(false);
                dialog.setTitle(getString(R.string.loading));
                dialog.show();

                AccessFireBase.updateStatus(userID,"online");

                presenterProfile = new PresenterProfile(this);
                presenterProfile.receivedHandleGetTBCar(this,userID);



                // lấy review của người dùng hiện tại
                PresenterReview presenterReview = new PresenterReview(this);
                presenterReview.receivedHandleGetAllReview(userID);
                presenterReview.receivedHandleGetAllReport(userID);



                presenterDriver = new PresenterDriver(this);
                presenterDriver.receiveHandleGetReceivedTrip(user);




            }

        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mfilter = menu;

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_driver_top, menu);



        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();


        if (id == R.id.action_filter) {


            if (!isClickFilter){
                openDialog();
                isClickFilter = true; }

            return true;
        }


        return super.onOptionsItemSelected(item);
    }



    @SuppressLint("SetTextI18n")
    @Override
    public void getUser(User user) {
        this.user = user;
        points.setText(user.getPoints());
    }

    @Override
    public void getCar(Car car) {

    }

    @Override
    public void getSMS(String sms) {

        bottomNavigationView.setNotification("new", 1);
    }





    @Override
    public void onGetReceivedListSuccess(ArrayList<String[]> receivedList) {


        Log.d("hahahahahahaha",TAG+ " Đang tìm các hành khách xung quanh...");
        presenterDriver.receivedHandleGetMultiPaTrip(userID, receivedList,getCurrentPoint, Constant.radius);

    }



    @SuppressLint("SetTextI18n")
    @Override
    public void onGetDetRefundSuccess(int refund) {


        Log.d("hahahahahahaha",TAG+ " Đã hoàn trả DT = "+ refund);
        Helper.displayDiagWarning(this,getString(R.string.het_han),getString(R.string.refunddt) + refund+getString(R.string.points));


        String pts  = String.valueOf(Integer.valueOf(user.getPoints())+refund);
        points.setText(pts);
        user.setPoints(pts);


        int ntrip = Integer.valueOf(user.getNtriptotal());
        user.setNtriptotal(String.valueOf(ntrip-1));


    }


    @Override
    public void onGetMultiTripPaSuccess(ArrayList<PassengerTrip> listPassengerTrip) {

        if (dialog != null){dialog.cancel();}


        Log.d("hahahahahahaha",TAG+ " Đã tìm thấy tổng cộng "+ listPassengerTrip.size() +" hành khách xung quanh bạn.");

        Collections.reverse(listPassengerTrip);
        this.arrayListTrip = listPassengerTrip;
        if (fragmentManager.findFragmentByTag("one") != null) { sendData2AvailableFragment(listPassengerTrip); }


    }


    @Override
    public void onGetMultiTripDriverSuccess(ArrayList<DriverTrip> arrayList) {

    }

    @Override
    public void onGetMultiTripDriverFail(String err) {

    }




    @SuppressLint("SetTextI18n")
    private void initialUserInformation(){

        if (user.getName().length() > 25){ twheaderName.setTextSize(13); }
        twheaderName.setText(user.getName());
        twheaderPhone.setText(user.getPhone());
        Picasso.get().load(user.getAvatar())
                .resize(250,250)
                .centerCrop()
                .into(imheaderAvatar);
        ratingBar.setText(user.getReview());
        nreview.setText(user.getNReview());
        points.setText(user.getPoints());


        getCurrentPoint = new LatLng(Double.valueOf(user.getLat()),Double.valueOf(user.getLng()));

    }


    private void sendData2Event(){
        CodeFragment fragment = (CodeFragment) getSupportFragmentManager().findFragmentByTag("event");
        if (fragment != null) {
            fragment.sendData(user);
        }
    }


    private void sendData2SmsFragment(){
        SMSFragment fragment_sms = (SMSFragment) getSupportFragmentManager().findFragmentByTag("sms");
        if (fragment_sms != null) {
            fragment_sms.sendData(user,"fragmentDr");
        }
    }
    private void sendData2AvailableFragment(ArrayList<PassengerTrip> listPassengerTrip){
        AvailableTripFragment fragment = (AvailableTripFragment) getSupportFragmentManager().findFragmentByTag("one");
        if (fragment != null) {

            fragment.sendData(user, listPassengerTrip);
        }
    }
    private void sendUser2AvailableFragment(){
        AvailableTripFragment fragment = (AvailableTripFragment) getSupportFragmentManager().findFragmentByTag("one");
        if (fragment != null) {
            fragment.sendData(user);
        }
    }







    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE_CHANGE_INFO && resultCode == Activity.RESULT_OK){
            if (data != null) {
                if( data.getParcelableExtra("user") !=null ){
                    this.user = data.getParcelableExtra("user");

                    if (user != null) {
                        twheaderName.setText(user.getName());
                        Picasso.get().load(user.getAvatar())
                                .resize(250,250)
                                .centerCrop()
                                .into(imheaderAvatar);

                        // khi người dùng thay đồi thông tin, thì chỉ cập nhật thông tin user vào fragment đã tồn tại, không tạo mới bằng replace.
                        sendData2SmsFragment();
                        sendUser2AvailableFragment();
                    }

                }

            }

        }

        if(data != null && requestCode == REQUEST_CODE_DRSHOWTRIP && resultCode == Activity.RESULT_OK){
            if( data.getParcelableExtra("user") !=null ){

                user = data.getParcelableExtra("user");
                if (user != null) {
                    points.setText(user.getPoints());
                }

            }
        }

    }





    private void mapToLayout() {
        mNavigationView      =  findViewById(R.id.nav_view);
        bottomNavigationView =  findViewById(R.id.nav_view1dr);
        Toolbar toolbar = findViewById(R.id.toolbardr);
        DrawerLayout drawer = findViewById(R.id.drawer_layoutdr);
        View headerView      =  mNavigationView.getHeaderView(0);
        twheaderName         =  headerView.findViewById(R.id.tvheadername);
        twheaderPhone        =  headerView.findViewById(R.id.twheaderPhone);
        imheaderAvatar       =  headerView.findViewById(R.id.imheaderavatar);
        ratingBar            =  headerView.findViewById(R.id.npdanhgia);
        nreview              =  headerView.findViewById(R.id.ndanhgia);
        points               =  headerView.findViewById(R.id.points);



        AHBottomNavigationAdapter navigationAdapter = new AHBottomNavigationAdapter(this, R.menu.menu_driver_bottom);
        navigationAdapter.setupWithBottomNavigation(bottomNavigationView, null);
        bottomNavigationView.setAccentColor(Color.parseColor("#eb7b00"));


        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


    }
    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onPause() {
        super.onPause();
        AccessFireBase.updatePoints(userID, user.getPoints(), new IAccessFireBase.iUpdatePoint() {
            @Override
            public void onSuccess() {
                AccessFireBase.updateTotalTrip(userID,user.getNtriptotal());
            }

            @Override
            public void onFailed() {
                Helper.displayErrorMessage(MainDriver.this,getString(R.string.errorconnect));
            }
        });

    }


    private void bottomNavigation(int position){
        switch (position) {
            case 0:
                this.setTitle(getResources().getString(R.string.Nearme));
                mfilter.getItem(0).setVisible(true);
                fragmentManager.beginTransaction().show(Objects.requireNonNull(fragmentManager.findFragmentByTag("one"))).commit();

                if(fragmentManager.findFragmentByTag("chat")!=null)
                    fragmentManager.beginTransaction().hide(Objects.requireNonNull(fragmentManager.findFragmentByTag("chat"))).commit();
                if(fragmentManager.findFragmentByTag("info")!=null)
                    fragmentManager.beginTransaction().hide(Objects.requireNonNull(fragmentManager.findFragmentByTag("info"))).commit();
                if(fragmentManager.findFragmentByTag("event")!=null)
                    fragmentManager.beginTransaction().hide(Objects.requireNonNull(fragmentManager.findFragmentByTag("event"))).commit();
                if(fragmentManager.findFragmentByTag("sms")!=null)
                    fragmentManager.beginTransaction().hide(Objects.requireNonNull(fragmentManager.findFragmentByTag("sms"))).commit();

                break;
            case 1:
                this.setTitle(getResources().getString(R.string.SMS));
                mfilter.getItem(0).setVisible(false);
                bottomNavigationView.setNotification("", 1);
                if(fragmentManager.findFragmentByTag("chat")!=null)
                    fragmentManager.beginTransaction().hide(Objects.requireNonNull(fragmentManager.findFragmentByTag("chat"))).commit();
                if(fragmentManager.findFragmentByTag("one")!=null)
                    fragmentManager.beginTransaction().hide(Objects.requireNonNull(fragmentManager.findFragmentByTag("one"))).commit();
                if(fragmentManager.findFragmentByTag("info")!=null)
                    fragmentManager.beginTransaction().hide(Objects.requireNonNull(fragmentManager.findFragmentByTag("info"))).commit();
                if(fragmentManager.findFragmentByTag("event")!=null)
                    fragmentManager.beginTransaction().hide(Objects.requireNonNull(fragmentManager.findFragmentByTag("event"))).commit();

                if(fragmentManager.findFragmentByTag("sms")!=null){

                    sendData2SmsFragment();
                    fragmentManager.beginTransaction().show(Objects.requireNonNull(fragmentManager.findFragmentByTag("sms"))).commit(); }
                else {

                    fragment = new SMSFragment();
                    bundle   = new Bundle();
                    bundle.putParcelable("user",user);
                    bundle.putString("keyfragment","fragmentDr");
                    fragment.setArguments(bundle);
                    fragmentManager.beginTransaction().add(R.id.fragmentDr,fragment,"sms").commit();
                }


                break;

            case 2:

                PassengerTrip currentTrip = Helper.getCurrentTrip(arrayListTrip);
                if (currentTrip==null){
                    Helper.displayErrorMessage(this,getResources().getString(R.string.nocurrenttrip));
                }else {
                    Helper.drawNavigationRoute(this,currentTrip);
                }



                break;
        }

    }
    private void mNavigation(MenuItem item){
        switch (item.getItemId()) {

            case R.id.menuNearby:
                setTitle(getResources().getString(R.string.nearme));
                mfilter.getItem(0).setVisible(true);

                bottomNavigationView.restoreBottomNavigation();
                fragmentManager.beginTransaction().show(Objects.requireNonNull(fragmentManager.findFragmentByTag("one"))).commit();
                if(fragmentManager.findFragmentByTag("chat")!=null)
                    fragmentManager.beginTransaction().hide(Objects.requireNonNull(fragmentManager.findFragmentByTag("chat"))).commit();
                if(fragmentManager.findFragmentByTag("info")!=null)
                    fragmentManager.beginTransaction().hide(Objects.requireNonNull(fragmentManager.findFragmentByTag("info"))).commit();
                if(fragmentManager.findFragmentByTag("event")!=null)
                    fragmentManager.beginTransaction().hide(Objects.requireNonNull(fragmentManager.findFragmentByTag("event"))).commit();
                if(fragmentManager.findFragmentByTag("sms")!=null)
                    fragmentManager.beginTransaction().hide(Objects.requireNonNull(fragmentManager.findFragmentByTag("sms"))).commit();

                break;

            case R.id.menuTrips:
                Bundle b1 = new Bundle();
                b1.putParcelable("user",user);
                Intent intent1 = new Intent(MainDriver.this, DrTripActivity.class);
                intent1.putExtra("bundle", b1);
                startActivity(intent1);
                break;

            case R.id.da_nhan:
                Bundle b2 = new Bundle();
                b2.putParcelable("user",user);
                b2.putParcelableArrayList("arrayListTrip",arrayListTrip);
                Intent intent2 = new Intent(MainDriver.this, DrShowTripActivity.class);
                intent2.putExtra("bundle",b2);

                startActivityForResult(intent2,REQUEST_CODE_DRSHOWTRIP);
                break;

            case R.id.menuBonus:
                bottomNavigationView.hideBottomNavigation();
                mfilter.getItem(0).setVisible(false);
                setTitle(getResources().getString(R.string.khuy_n_m_i));
                if(fragmentManager.findFragmentByTag("event") !=null){
                    sendData2Event();
                    fragmentManager.beginTransaction().show(Objects.requireNonNull(fragmentManager.findFragmentByTag("event"))).commit();
                }else{
                    fragment = new CodeFragment();
                    fragment = new CodeFragment();
                    bundle   = new Bundle();
                    bundle.putParcelable("user", user);
                    fragment.setArguments(bundle);
                    fragmentManager.beginTransaction().add(R.id.fragmentDr, fragment,"event").commit();
                }
                if(fragmentManager.findFragmentByTag("chat")!=null)
                    fragmentManager.beginTransaction().hide(Objects.requireNonNull(fragmentManager.findFragmentByTag("chat"))).commit();
                if(fragmentManager.findFragmentByTag("one")!=null)
                    fragmentManager.beginTransaction().hide(Objects.requireNonNull(fragmentManager.findFragmentByTag("one"))).commit();
                if(fragmentManager.findFragmentByTag("info")!=null)
                    fragmentManager.beginTransaction().hide(Objects.requireNonNull(fragmentManager.findFragmentByTag("info"))).commit();
                if(fragmentManager.findFragmentByTag("sms")!=null)
                    fragmentManager.beginTransaction().hide(Objects.requireNonNull(fragmentManager.findFragmentByTag("sms"))).commit();

                break;


            case R.id.menuHd:
                bottomNavigationView.hideBottomNavigation();
                mfilter.getItem(0).setVisible(false);
                setTitle(getResources().getString(R.string.Infor));
                if(fragmentManager.findFragmentByTag("info") !=null){
                    fragmentManager.beginTransaction().show(Objects.requireNonNull(fragmentManager.findFragmentByTag("info"))).commit();
                }else{
                    fragment = new AboutUsFragment();
                    fragmentManager.beginTransaction().add(R.id.fragmentDr, fragment,"info").commit();
                }
                if(fragmentManager.findFragmentByTag("chat")!=null)
                    fragmentManager.beginTransaction().hide(Objects.requireNonNull(fragmentManager.findFragmentByTag("chat"))).commit();
                if(fragmentManager.findFragmentByTag("sms")!=null)
                    fragmentManager.beginTransaction().hide(Objects.requireNonNull(fragmentManager.findFragmentByTag("sms"))).commit();
                if(fragmentManager.findFragmentByTag("one")!=null)
                    fragmentManager.beginTransaction().hide(Objects.requireNonNull(fragmentManager.findFragmentByTag("one"))).commit();
                if(fragmentManager.findFragmentByTag("event")!=null)
                    fragmentManager.beginTransaction().hide(Objects.requireNonNull(fragmentManager.findFragmentByTag("event"))).commit();
                break;
            case R.id.menuShare:
                Helper.shareText(this, user.getInvitecode());break;

            case R.id.menuReview:
                Helper.reviewApp(this, user);break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layoutdr);
        drawer.closeDrawer(GravityCompat.START);

    }


    @SuppressLint("SimpleDateFormat")
    private void openDialog() {

        LayoutInflater inflater = LayoutInflater.from(this);
        View subView = inflater.inflate(R.layout.layout_filter, null);


        final TextView kcmin    = subView.findViewById(R.id.kc1);
        final TextView kcmax    = subView.findViewById(R.id.kc2);
        final RangeSeekBar sbkc = subView.findViewById(R.id.sbkc);
        final RangeSeekBar skbt = subView.findViewById(R.id.sbti);
        final TextView time1    = subView.findViewById(R.id.time1);
        final TextView time2    = subView.findViewById(R.id.time2);
        final CheckBox cb2      = subView.findViewById(R.id.cb2);
        final CheckBox cb3      = subView.findViewById(R.id.cb3);
        final CheckBox cb4      = subView.findViewById(R.id.cb4);
        final CheckBox cb5      = subView.findViewById(R.id.cb5);
        final CheckBox cb6      = subView.findViewById(R.id.cb6);
        final CheckBox cb7      = subView.findViewById(R.id.cb7);
        final CheckBox cb8      = subView.findViewById(R.id.cb8);
        final CheckBox hide_trip= subView.findViewById(R.id.hide_trip);



        sbkc.setValue(0,3);
        sbkc.getLeftSeekBar().setThumbDrawableId(R.drawable.ic_marker_red);
        sbkc.getRightSeekBar().setThumbDrawableId(R.drawable.ic_marker_green);
        sbkc.setOnRangeChangedListener(new OnRangeChangedListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {

                kcmin.setText(Math.round(leftValue) + "");
                kcmax.setText(Math.round(rightValue) + "");

            }
            @Override
            public void onStartTrackingTouch(RangeSeekBar view,  boolean isLeft) {

            }

            @Override
            public void onStopTrackingTouch(RangeSeekBar view,  boolean isLeft) {

            }
        });



        skbt.setValue(0,24);
        skbt.getLeftSeekBar().setThumbDrawableId(R.drawable.ic_marker_red);
        skbt.getRightSeekBar().setThumbDrawableId(R.drawable.ic_marker_green);
        skbt.setOnRangeChangedListener(new OnRangeChangedListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {
                if (leftValue <= rightValue){
                    time1.setText(Math.round(leftValue)+"H");
                    time2.setText(Math.round(rightValue)+"H");

                }else {
                    time1.setText(Math.round(rightValue)+"H");
                }


            }

            @Override
            public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {

            }

            @Override
            public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft) {

            }
        });




        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(subView);
        builder.setTitle(getString(R.string.adbl));
        builder.setPositiveButton(getResources().getString(R.string.Yes), (dialog, which) -> {

            // get new layout_passenger_trip by filter
            ArrayList<PassengerTrip> newArrayListTrip   = new ArrayList<>();

            double minDistance = Double.valueOf(kcmin.getText().toString());
            double maxDistance = Double.valueOf(kcmax.getText().toString());
            int minTime        = Integer.valueOf(time1.getText().toString().replace("H",""));
            int maxTime        = Integer.valueOf(time2.getText().toString().replace("H",""));

            Date userDate      = null;
            int hour;
            int dayOfWeek;
            for(int i = 0; i <arrayListTrip.size();i++){

                PassengerTrip trip = arrayListTrip.get(i);

                // Chuyến đi đã được nhận
                if(hide_trip.isChecked() && (trip.getStt().equals("accepted") || trip.getStt().equals("acceptedbyother"))){continue;}

                LatLng userPoint = Helper.fromStringToLatLng(trip.getDepart());
                String[] time = trip.getTime().split(":");
                hour = Integer.valueOf(time[0]);

                try {
                    userDate = new SimpleDateFormat("dd/MM/yyyy").parse(trip.getDate());
                } catch (ParseException e) { e.printStackTrace(); }


                if (userDate != null) {
                    Calendar c = Calendar.getInstance();
                    c.setTime(userDate);
                    dayOfWeek = c.get(Calendar.DAY_OF_WEEK);

                    // Áp dụng bộ lọc theo khoảng cách
                    double distance = userPoint.distanceTo(new LatLng(Double.valueOf(user.getLat()),Double.valueOf(user.getLng())));
                    if (distance > minDistance*1000 && distance < maxDistance*1000){

                        // Lọc theo thời gian và các ngày trong tuần
                        if (hour > minTime && hour < maxTime){

                            if ((dayOfWeek != 2 || cb2.isChecked())
                                    && (dayOfWeek != 3 || cb3.isChecked())
                                    && (dayOfWeek != 4 || cb4.isChecked())
                                    && (dayOfWeek != 5 || cb5.isChecked())
                                    && (dayOfWeek != 6 || cb6.isChecked())
                                    && (dayOfWeek != 7 || cb7.isChecked())
                                    && (dayOfWeek != 1 || cb8.isChecked())) {
                                newArrayListTrip.add(trip);
                            }

                        }
                    }
                }


            }

            sendData2AvailableFragment(newArrayListTrip);

            if (newArrayListTrip.size() == 0){
                Helper.displayMessenger(this,getString(R.string.notrip)); }
            else {
                Helper.displayMessenger(this,getString(R.string.found) + newArrayListTrip.size() + getString(R.string.trips)); }

            isClickFilter = false;


        });

        builder.setNegativeButton(getString(R.string.No), (dialog, which) -> {
            isClickFilter = false;
        });

        builder.show();
    }

    @Override
    public void onGetReviewSuccess(ArrayList<ReviewContext> reviews) {

        float rate;
        if (reviews.size() > 0){
            float sum = 0;
            for (int i= 0;i<reviews.size();i++){
                sum = sum + Float.valueOf(reviews.get(i).getRate());
            }
            rate = sum/reviews.size();
        }
        else{
            rate = 0;
        }

        String mrate = String.valueOf(rate);
        user.setNReview(String.valueOf(reviews.size()));
        user.setReview(mrate);
        AccessFireBase.updateReview(userID,mrate,reviews.size());

    }

    @Override
    public void onGetReportSuccess(ArrayList<ReviewContext> reports) {

        if ( reports.size() > 10 && Integer.valueOf(user.getNReview())<100){

            CustomDialog dialog = new CustomDialog(this,R.drawable.ic_error, 1);
            dialog.setCancelable(false);
            dialog.setTitle(getString(R.string.sorry));
            dialog.setMessage(getString(R.string.tkbk));
            dialog.setCancelText(getString(R.string.ok));
            dialog.setCancelClickListener(dialog1 -> {
                dialog.cancel();
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                mAuth.signOut();
                AccessFireBase.updateStatus(userID,"offline");
                Intent intent5 = new Intent(MainDriver.this, MainActivity.class);
                startActivity(intent5);
            });
            dialog.show();
        }



    }

    @Override
    public void onGetReviewFailed(String err) {
        dialog.dismiss();
        Helper.displayErrorMessage(MainDriver.this,getString(R.string.errorconnect));
    }

    @Override
    public void onGetReportFailed(String err) {
        dialog.dismiss();
        Helper.displayErrorMessage(MainDriver.this,getString(R.string.errorconnect));
    }

    @Override
    public void onGetProfileSuccess(User user) {

    }

    @Override
    public void onGetProfileSuccess(ArrayList<User> user, ArrayList<Car> car) {

    }

    @Override
    public void onGetProfileFail(String err) {

    }

    @Override
    public void onGetTBCarSuccess(Car car) {
        this.car = car;
        if ( car.getNameCar().equals("")){
            dialog.dismiss();

            openDiagAddCar(); }
        else {
            dialog.setTitle(getString(R.string.dangtimkiemchuyendi)); }


    }

    @Override
    public void onGetTBCarSuccess(ArrayList<Car> object) {

    }

    @Override
    public void onGetTBCarFail(String err) {

    }



    private void openDiagAddCar(){


        CustomDialog dialog = new CustomDialog(this,R.drawable.ic_warning, 1);
        dialog.setCancelable(false);
        dialog.setTitle(getString(R.string.addinforcar0));
        dialog.setMessage(getString(R.string.themttxe));
        dialog.setCancelText(getString(R.string.addinforcar));
        dialog.setCancelClickListener(dialog1 -> {
            Bundle bundle1 = new Bundle();
            bundle1.putParcelable("user",user);
            bundle1.putParcelable("car",car);
            bundle1.putInt("index",1);
            Intent intent1 = new Intent(MainDriver.this, AccountActivity.class);
            intent1.putExtra("bundle", bundle1);
            startActivityForResult(intent1,REQUEST_CODE_CHANGE_INFO);
            dialog.dismiss();
        });
        dialog.show();

    }


}

