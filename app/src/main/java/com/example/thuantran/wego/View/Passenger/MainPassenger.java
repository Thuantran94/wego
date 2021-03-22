package com.example.thuantran.wego.View.Passenger;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationAdapter;
import com.example.thuantran.wego.DataAccess.AccessFireBase;
import com.example.thuantran.wego.DataAccess.IAccessFireBase;
import com.example.thuantran.wego.Tools.CustomDialog;
import com.example.thuantran.wego.View.Fragment.AboutUsFragment;
import com.example.thuantran.wego.View.Fragment.CodeFragment;
import com.example.thuantran.wego.View.Fragment.PaAddTripFragment;
import com.example.thuantran.wego.View.Fragment.SMSFragment;
import com.example.thuantran.wego.View.Fragment.ShowTripFragment;
import com.example.thuantran.wego.Interface.Profile.Review;
import com.example.thuantran.wego.Interface.Tools.Fragment2Activity;
import com.example.thuantran.wego.Interface.Tools.ReceivedSMSListener;
import com.example.thuantran.wego.Interface.Trip.Trip;
import com.example.thuantran.wego.Object.Car;
import com.example.thuantran.wego.Object.PassengerTrip;
import com.example.thuantran.wego.Object.ReviewContext;
import com.example.thuantran.wego.Object.User;
import com.example.thuantran.wego.Presenter.Profile.PresenterReview;
import com.example.thuantran.wego.Presenter.Trip.PresenterTrip;
import com.example.thuantran.wego.R;
import com.example.thuantran.wego.Tools.Helper;
import com.example.thuantran.wego.View.Main.AccountActivity;
import com.example.thuantran.wego.View.Main.MainActivity;
import com.example.thuantran.wego.View.Main.ShowReviewActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;


public class MainPassenger extends AppCompatActivity implements  Fragment2Activity, ReceivedSMSListener, Trip.View, Review.View{


    private NavigationView mNavigationView;
    private TextView ratingBar;
    private TextView nreview;
    private ImageView imheaderAvatar;
    private TextView twheaderName, twheaderPhone, points;
    private AHBottomNavigation bottomNavigationView;
    private FragmentManager fragmentManager;
    private Fragment fragment;
    private Bundle bundle;

    private static final int REQUEST_CODE_CHANGE_INFO   = 123;
    private static final String TAG = "MainPassenger";
    private User user;
    private String userID;
    private ArrayList<PassengerTrip> comingList, passList, removedList, receivedList;



    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main_passenger);
        mapToLayout();


        Bundle b = getIntent().getBundleExtra("bundle");
        if (b != null) {

            user     = b.getParcelable("user");

            if (user != null){


                userID = user.getUserID();
                initialUserInformation();



                fragmentManager = getSupportFragmentManager();
                fragment = new SMSFragment();
                bundle   = new Bundle();
                bundle.putParcelable("user",user);
                fragment.setArguments(bundle);
                fragmentManager.beginTransaction().add(R.id.fragmentPa,fragment,"sms").commit();


                fragment = new PaAddTripFragment();
                bundle   = new Bundle();
                bundle.putParcelable("user",user);
                fragment.setArguments(bundle);
                fragmentManager.beginTransaction().add(R.id.fragmentPa,fragment,"addtrip").commit();


                if(fragmentManager.executePendingTransactions()){
                    fragmentManager.beginTransaction().hide(Objects.requireNonNull(fragmentManager.findFragmentByTag("sms"))).commitAllowingStateLoss(); }


                // remember last mode
                AccessFireBase.updateStatus(userID,"online");

                // lấy review của người dùng hiện tại
                PresenterReview presenterReview = new PresenterReview(this);
                presenterReview.receivedHandleGetAllReview(userID);
                presenterReview.receivedHandleGetAllReport(userID);


                // Lấy danh sách tất cả chuyến đi của người dùng hiện tại.
                PresenterTrip presenterTrip = new PresenterTrip(this);
                presenterTrip.receivedHandleGetMultiTrip(this, userID);








                imheaderAvatar.setOnClickListener(v -> {
                    Bundle bundle1 = new Bundle();
                    bundle1.putParcelable("user",user);
                    bundle1.putInt("index",0);
                    Intent intent1 = new Intent(MainPassenger.this, AccountActivity.class);
                    intent1.putExtra("bundle", bundle1);
                    startActivityForResult(intent1,REQUEST_CODE_CHANGE_INFO);
                });


                nreview.setOnClickListener(v -> {
                    Bundle bundle2 = new Bundle();
                    bundle2.putParcelable("user",user);
                    Intent intent2 = new Intent(MainPassenger.this, ShowReviewActivity.class);
                    intent2.putExtra("bundle", bundle2);
                    startActivity(intent2);

                });


                mNavigationView.setNavigationItemSelectedListener(item -> {
                    mNavigation(item);
                    return true;
                });
                bottomNavigationView.disableItemAtPosition(1);
                bottomNavigationView.setOnTabSelectedListener((position, wasSelected) -> {
                    bottomNavigation(position);
                    return true;
                });

            }
        }
    }



    @Override
    public void onGetMultiTripSuccess(ArrayList<PassengerTrip> passengerTripArrayList) {


        bottomNavigationView.enableItemAtPosition(1);


        Log.d("hahahahahahaha",TAG+ " Đã lấy tất cả "+passengerTripArrayList.size()+" chuyến đi của hành khách");

        Collections.reverse(passengerTripArrayList);
        getComingEtPass(passengerTripArrayList);

    }

    @Override
    public void onGetOneTripSuccess(PassengerTrip passengerTrip) {

    }


    @SuppressLint("SetTextI18n")
    private void getComingEtPass(ArrayList<PassengerTrip> passengerTripArrayList){

        comingList     = new ArrayList<>();
        passList       = new ArrayList<>();
        removedList    = new ArrayList<>();
        receivedList   = new ArrayList<>();


        for(int i=0; i<passengerTripArrayList.size();i++){

            PassengerTrip passengerTrip = passengerTripArrayList.get(i);

            if(Helper.isExpire(passengerTrip.getDate(), passengerTrip.getTime())){


                // Xóa các chuyến đi đã hết hạn và không có người nhận
                if(passengerTrip.getIDdr().equals("0") ){


                    AccessFireBase.removeTripWithRefund(passengerTrip.getID(), new IAccessFireBase.iRemoveTripWithRefund() {
                        @Override
                        public void onSuccess() {
                            removedList.add(passengerTrip);
                            int ntrip = Integer.valueOf(user.getNtriptotal());
                            user.setNtriptotal(String.valueOf(ntrip-1));

                            // thông báo cho người dùng biết bằng cách set layout_notification
                            bottomNavigationView.setNotification("!", 1);

                        }

                        @Override
                        public void onFailed() { }
                    });



                }else if(!passengerTrip.getIDdr().equals("-1")){ // Chuyến đi booknow
                    //Đưa chuyến đi đã hoàn tất vào passList, set status cho chuyến đi đã hoàn tất = -1
                    AccessFireBase.updateStatusTrip(passengerTrip.getID(),"completed");
                    passengerTrip.setStt("completed");
                    passList.add(passengerTrip);
                }

            }else{

                comingList.add(passengerTrip);

                if(!passengerTrip.getNMessenger().equals("0") && passengerTrip.getIDdr().equals("0")){ // Ai đó đã gửi yêu cầu cho chuyến đi này
                    bottomNavigationView.setNotification("!", 1);
                    receivedList.add(passengerTrip);
                }

            }

        }

        user.setNtriplimitPa(comingList.size());

        if(fragmentManager.findFragmentByTag("comming_list")!=null){
            sendData2ShowTripFragment(comingList,"comming_list");
        }

        if(fragmentManager.findFragmentByTag("pass_list")!=null){
             sendData2ShowTripFragment(passList,"pass_list");
        }

    }

    @Override
    public void onGetTBPaTripFail(String err) { }
    @Override
    public void haveDriverSelected(PassengerTrip trip) { }
    @Override
    public void finalDriverSelected(PassengerTrip trip) { }


    private void sendData2Event(){
        CodeFragment fragment = (CodeFragment) getSupportFragmentManager().findFragmentByTag("event");
        if (fragment != null) {
            fragment.sendData(user);
        }
    }

    private void sendData2AddTrip(){
        PaAddTripFragment fragment = (PaAddTripFragment) getSupportFragmentManager().findFragmentByTag("addtrip");
        if (fragment != null) {
            fragment.sendData(user);
        }
    }

    private void sendData2SmsFragment(){
        SMSFragment fragment_sms = (SMSFragment) getSupportFragmentManager().findFragmentByTag("sms");
        if (fragment_sms != null) {
            fragment_sms.sendData(user,removedList,receivedList,"fragmentPa");
        }
    }

    private void sendData2ShowTripFragment(ArrayList<PassengerTrip> passengerTripArrayList, String tab){
        ShowTripFragment fragment_sms = (ShowTripFragment) getSupportFragmentManager().findFragmentByTag(tab);
        if (fragment_sms != null) {
            fragment_sms.sendData(user,passengerTripArrayList);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && data !=null){

            user = data.getParcelableExtra("user");

            if(requestCode == REQUEST_CODE_CHANGE_INFO){
                twheaderName.setText(user.getName());
                Picasso.get().load(user.getAvatar())
                        .resize(250,250)
                        .centerCrop()
                        .into(imheaderAvatar);


                // khi người dùng thay đồi thông tin, thì chỉ cập nhật thông tin user vào fragment đã tồn tại, không tạo mới bằng replace.
                // thông tin đã thay đổi có thể là ảnh đại diện hoặc tên...
                PaAddTripFragment fragment_addtrip = (PaAddTripFragment) getSupportFragmentManager().findFragmentByTag("addtrip");
                if (fragment_addtrip != null) {
                    fragment_addtrip.getUser(user);
                }

                // gửi thông tin user và giao diện chat
                sendData2SmsFragment();

            }

        }
    }


    // Cập nhật lại user khi có thay đổi
    @SuppressLint("SetTextI18n")
    @Override
    public void getUser(User user) {
        this.user = user;
        points.setText(user.getPoints());
    }

    @Override
    public void getCar(Car car) {

    }


    // Khi có tin nhắn đến, setNotification = new
    @Override
    public void getSMS(String sms) {

        bottomNavigationView.setNotification("new", 1);
    }


    @SuppressLint("SetTextI18n")
    private void initialUserInformation() {

        if (user.getName().length() > 25) { twheaderName.setTextSize(13); }
        twheaderName.setText(user.getName());
        twheaderPhone.setText(user.getPhone());
        points.setText(user.getPoints());
        Picasso.get().load(user.getAvatar())
                .resize(250, 250)
                .centerCrop()
                .into(imheaderAvatar);
        ratingBar.setText(user.getReview());
        nreview.setText(user.getNReview());


    }


    @SuppressLint("InflateParams")
    private void mapToLayout() {
        mNavigationView = findViewById(R.id.nav_view);
        bottomNavigationView =  findViewById(R.id.nav_viewPa);
        View headerView = mNavigationView.getHeaderView(0);
        twheaderName = headerView.findViewById(R.id.tvheadername);
        twheaderPhone = headerView.findViewById(R.id.tvheaderphone);

        imheaderAvatar = headerView.findViewById(R.id.imheaderavatar1);
        ratingBar = headerView.findViewById(R.id.npdanhgia);
        nreview   = headerView.findViewById(R.id.ndanhgia);

        points     = headerView.findViewById(R.id.points);
        Toolbar toolbar = findViewById(R.id.toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        mNavigationView.setItemIconTintList(null);

        AHBottomNavigationAdapter navigationAdapter = new AHBottomNavigationAdapter(this, R.menu.menu_passenger_bottom);
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
                Helper.displayErrorMessage(MainPassenger.this,getString(R.string.errorconnect));

            }
        });

    }



    private void bottomNavigation(int position){
        switch (position) {
            case 0:

                this.setTitle(getResources().getString(R.string.app_name));
                if(fragmentManager.findFragmentByTag("chat")!=null)
                    fragmentManager.beginTransaction().hide(Objects.requireNonNull(fragmentManager.findFragmentByTag("chat"))).commit();
                if(fragmentManager.findFragmentByTag("sms")!=null)
                    fragmentManager.beginTransaction().hide(Objects.requireNonNull(fragmentManager.findFragmentByTag("sms"))).commit();
                if(fragmentManager.findFragmentByTag("info")!=null)
                    fragmentManager.beginTransaction().hide(Objects.requireNonNull(fragmentManager.findFragmentByTag("info"))).commit();
                if(fragmentManager.findFragmentByTag("event")!=null)
                    fragmentManager.beginTransaction().hide(Objects.requireNonNull(fragmentManager.findFragmentByTag("event"))).commit();
                if(fragmentManager.findFragmentByTag("comming_list")!=null)
                    fragmentManager.beginTransaction().hide(Objects.requireNonNull(fragmentManager.findFragmentByTag("comming_list"))).commit();
                if(fragmentManager.findFragmentByTag("pass_list")!=null)
                    fragmentManager.beginTransaction().hide(Objects.requireNonNull(fragmentManager.findFragmentByTag("pass_list"))).commit();
                if(fragmentManager.findFragmentByTag("addtrip")!=null){
                    sendData2AddTrip();
                    fragmentManager.beginTransaction().show(Objects.requireNonNull(fragmentManager.findFragmentByTag("addtrip"))).commit();
                }
                else{
                    fragment = new PaAddTripFragment();
                    bundle   = new Bundle();
                    bundle.putParcelable("user",user);
                    fragment.setArguments(bundle);
                    fragmentManager.beginTransaction().add(R.id.fragmentPa,fragment,"addtrip").commit();
                }
                break;

            case 1:
                this.setTitle(getResources().getString(R.string.SMS));
                bottomNavigationView.setNotification("", 1);
                if(fragmentManager.findFragmentByTag("chat")!=null)
                    fragmentManager.beginTransaction().hide(Objects.requireNonNull(fragmentManager.findFragmentByTag("chat"))).commit();
                if(fragmentManager.findFragmentByTag("addtrip")!=null){
                    fragmentManager.beginTransaction().hide(Objects.requireNonNull(fragmentManager.findFragmentByTag("addtrip"))).commit(); }
                if(fragmentManager.findFragmentByTag("info")!=null)
                    fragmentManager.beginTransaction().hide(Objects.requireNonNull(fragmentManager.findFragmentByTag("info"))).commit();
                if(fragmentManager.findFragmentByTag("event")!=null)
                    fragmentManager.beginTransaction().hide(Objects.requireNonNull(fragmentManager.findFragmentByTag("event"))).commit();
                if(fragmentManager.findFragmentByTag("comming_list")!=null)
                    fragmentManager.beginTransaction().hide(Objects.requireNonNull(fragmentManager.findFragmentByTag("comming_list"))).commit();
                if(fragmentManager.findFragmentByTag("pass_list")!=null)
                    fragmentManager.beginTransaction().hide(Objects.requireNonNull(fragmentManager.findFragmentByTag("pass_list"))).commit();


                if(fragmentManager.findFragmentByTag("sms")!=null){

                    sendData2SmsFragment();
                    fragmentManager.beginTransaction().show(Objects.requireNonNull(fragmentManager.findFragmentByTag("sms"))).commit(); }
                else {
                    fragment = new SMSFragment();
                    bundle   = new Bundle();
                    bundle.putParcelable("user",user);
                    bundle.putParcelableArrayList("removedList",removedList);
                    bundle.putParcelableArrayList("receivedList",receivedList);
                    bundle.putString("keyfragment","fragmentPa");
                    fragment.setArguments(bundle);
                    fragmentManager.beginTransaction().add(R.id.fragmentPa,fragment,"sms").commit();

                }

                break;

            case 2:
                PassengerTrip currentTrip = Helper.getCurrentTrip(passList);
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

            case R.id.menuAddnew:
                bottomNavigationView.restoreBottomNavigation();
                this.setTitle(getResources().getString(R.string.app_name));
                if(fragmentManager.findFragmentByTag("chat")!=null)
                    fragmentManager.beginTransaction().hide(Objects.requireNonNull(fragmentManager.findFragmentByTag("chat"))).commit();
                if(fragmentManager.findFragmentByTag("sms")!=null)
                    fragmentManager.beginTransaction().hide(Objects.requireNonNull(fragmentManager.findFragmentByTag("sms"))).commit();
                if(fragmentManager.findFragmentByTag("info")!=null)
                    fragmentManager.beginTransaction().hide(Objects.requireNonNull(fragmentManager.findFragmentByTag("info"))).commit();
                if(fragmentManager.findFragmentByTag("event")!=null)
                    fragmentManager.beginTransaction().hide(Objects.requireNonNull(fragmentManager.findFragmentByTag("event"))).commit();
                if(fragmentManager.findFragmentByTag("comming_list")!=null)
                    fragmentManager.beginTransaction().hide(Objects.requireNonNull(fragmentManager.findFragmentByTag("comming_list"))).commit();
                if(fragmentManager.findFragmentByTag("pass_list")!=null)
                    fragmentManager.beginTransaction().hide(Objects.requireNonNull(fragmentManager.findFragmentByTag("pass_list"))).commit();

                if(fragmentManager.findFragmentByTag("addtrip")!=null){
                    sendData2AddTrip();
                    fragmentManager.beginTransaction().show(Objects.requireNonNull(fragmentManager.findFragmentByTag("addtrip"))).commit();
                }
                else{
                    fragment = new PaAddTripFragment();
                    bundle   = new Bundle();
                    bundle.putParcelable("user",user);
                    fragment.setArguments(bundle);
                    fragmentManager.beginTransaction().add(R.id.fragmentPa,fragment,"addtrip").commit();
                }
                break;


            case R.id.menuCommingTrips:

                bottomNavigationView.hideBottomNavigation();
                setTitle(getResources().getString(R.string.Chuy_n_i_s_p_t_i));
                if(fragmentManager.findFragmentByTag("chat")!=null)
                    fragmentManager.beginTransaction().hide(Objects.requireNonNull(fragmentManager.findFragmentByTag("chat"))).commit();
                if(fragmentManager.findFragmentByTag("sms")!=null)
                    fragmentManager.beginTransaction().hide(Objects.requireNonNull(fragmentManager.findFragmentByTag("sms"))).commit();
                if(fragmentManager.findFragmentByTag("addtrip")!=null)
                    fragmentManager.beginTransaction().hide(Objects.requireNonNull(fragmentManager.findFragmentByTag("addtrip"))).commit();
                if(fragmentManager.findFragmentByTag("info")!=null)
                    fragmentManager.beginTransaction().hide(Objects.requireNonNull(fragmentManager.findFragmentByTag("info"))).commit();
                if(fragmentManager.findFragmentByTag("event")!=null)
                    fragmentManager.beginTransaction().hide(Objects.requireNonNull(fragmentManager.findFragmentByTag("event"))).commit();
                if(fragmentManager.findFragmentByTag("pass_list")!=null)
                    fragmentManager.beginTransaction().hide(Objects.requireNonNull(fragmentManager.findFragmentByTag("pass_list"))).commit();

                if(fragmentManager.findFragmentByTag("comming_list")!=null){
                    fragmentManager.beginTransaction().show(Objects.requireNonNull(fragmentManager.findFragmentByTag("comming_list"))).commit();
                }else {

                    fragment = new ShowTripFragment();
                    bundle   = new Bundle();
                    bundle.putParcelable("user", user);
                    bundle.putParcelableArrayList("list",comingList);
                    fragment.setArguments(bundle);
                    fragmentManager.beginTransaction().add(R.id.fragmentPa, fragment,"comming_list").commit(); }

                break;
            case R.id.menuPassTrips:

                bottomNavigationView.hideBottomNavigation();
                setTitle(getResources().getString(R.string.C_c_chuy_n_i));
                if(fragmentManager.findFragmentByTag("chat")!=null)
                    fragmentManager.beginTransaction().hide(Objects.requireNonNull(fragmentManager.findFragmentByTag("chat"))).commit();
                if(fragmentManager.findFragmentByTag("sms")!=null)
                    fragmentManager.beginTransaction().hide(Objects.requireNonNull(fragmentManager.findFragmentByTag("sms"))).commit();
                if(fragmentManager.findFragmentByTag("addtrip")!=null)
                    fragmentManager.beginTransaction().hide(Objects.requireNonNull(fragmentManager.findFragmentByTag("addtrip"))).commit();
                if(fragmentManager.findFragmentByTag("info")!=null)
                    fragmentManager.beginTransaction().hide(Objects.requireNonNull(fragmentManager.findFragmentByTag("info"))).commit();
                if(fragmentManager.findFragmentByTag("event")!=null)
                    fragmentManager.beginTransaction().hide(Objects.requireNonNull(fragmentManager.findFragmentByTag("event"))).commit();
                if(fragmentManager.findFragmentByTag("comming_list")!=null)
                    fragmentManager.beginTransaction().hide(Objects.requireNonNull(fragmentManager.findFragmentByTag("comming_list"))).commit();


                if(fragmentManager.findFragmentByTag("pass_list")!=null){
                   // sendData2ShowTripFragment(passList,"pass_list");
                    fragmentManager.beginTransaction().show(Objects.requireNonNull(fragmentManager.findFragmentByTag("pass_list"))).commit();
                }else {
                    fragment = new ShowTripFragment();
                    bundle   = new Bundle();
                    bundle.putParcelable("user", user);
                    bundle.putParcelableArrayList("list",passList);
                    fragment.setArguments(bundle);
                    fragmentManager.beginTransaction().add(R.id.fragmentPa, fragment,"pass_list").commit(); }

                break;

            case R.id.menuBonus:
                bottomNavigationView.hideBottomNavigation();
                setTitle(getResources().getString(R.string.Khuy_n_m_i));
                if(fragmentManager.findFragmentByTag("event") !=null){
                    sendData2Event();
                    fragmentManager.beginTransaction().show(Objects.requireNonNull(fragmentManager.findFragmentByTag("event"))).commit();
                }else{
                    fragment = new CodeFragment();
                    bundle   = new Bundle();
                    bundle.putParcelable("user", user);
                    fragment.setArguments(bundle);
                    fragmentManager.beginTransaction().add(R.id.fragmentPa, fragment,"event").commit();
                }
                if(fragmentManager.findFragmentByTag("chat")!=null)
                    fragmentManager.beginTransaction().hide(Objects.requireNonNull(fragmentManager.findFragmentByTag("chat"))).commit();
                if(fragmentManager.findFragmentByTag("sms")!=null)
                    fragmentManager.beginTransaction().hide(Objects.requireNonNull(fragmentManager.findFragmentByTag("sms"))).commit();
                if(fragmentManager.findFragmentByTag("addtrip")!=null)
                    fragmentManager.beginTransaction().hide(Objects.requireNonNull(fragmentManager.findFragmentByTag("addtrip"))).commit();
                if(fragmentManager.findFragmentByTag("info")!=null)
                    fragmentManager.beginTransaction().hide(Objects.requireNonNull(fragmentManager.findFragmentByTag("info"))).commit();
                if(fragmentManager.findFragmentByTag("comming_list")!=null)
                    fragmentManager.beginTransaction().hide(Objects.requireNonNull(fragmentManager.findFragmentByTag("comming_list"))).commit();
                if(fragmentManager.findFragmentByTag("pass_list")!=null)
                    fragmentManager.beginTransaction().hide(Objects.requireNonNull(fragmentManager.findFragmentByTag("pass_list"))).commit();
                break;

            case R.id.menuHd:
                bottomNavigationView.hideBottomNavigation();
                setTitle(getResources().getString(R.string.Infor));
                if(fragmentManager.findFragmentByTag("info") !=null){
                    fragmentManager.beginTransaction().show(Objects.requireNonNull(fragmentManager.findFragmentByTag("info"))).commit();
                }else{
                    fragment = new AboutUsFragment();
                    fragmentManager.beginTransaction().add(R.id.fragmentPa, fragment,"info").commit();
                }
                if(fragmentManager.findFragmentByTag("chat")!=null)
                    fragmentManager.beginTransaction().hide(Objects.requireNonNull(fragmentManager.findFragmentByTag("chat"))).commit();
                if(fragmentManager.findFragmentByTag("sms")!=null)
                    fragmentManager.beginTransaction().hide(Objects.requireNonNull(fragmentManager.findFragmentByTag("sms"))).commit();
                if(fragmentManager.findFragmentByTag("addtrip")!=null)
                    fragmentManager.beginTransaction().hide(Objects.requireNonNull(fragmentManager.findFragmentByTag("addtrip"))).commit();
                if(fragmentManager.findFragmentByTag("event")!=null)
                    fragmentManager.beginTransaction().hide(Objects.requireNonNull(fragmentManager.findFragmentByTag("event"))).commit();
                if(fragmentManager.findFragmentByTag("comming_list")!=null)
                    fragmentManager.beginTransaction().hide(Objects.requireNonNull(fragmentManager.findFragmentByTag("comming_list"))).commit();
                if(fragmentManager.findFragmentByTag("pass_list")!=null)
                    fragmentManager.beginTransaction().hide(Objects.requireNonNull(fragmentManager.findFragmentByTag("pass_list"))).commit();

                break;

            case R.id.menuShare:
                Helper.shareText(this, user.getInvitecode());break;

            case R.id.menuReview:
                Helper.reviewApp(this, user);break;


        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
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
                Intent intent5 = new Intent(MainPassenger.this, MainActivity.class);
                startActivity(intent5);
            });
            dialog.show();
        }

    }

    @Override
    public void onGetReviewFailed(String err) {

        Helper.displayErrorMessage(MainPassenger.this,getString(R.string.errorconnect));
    }

    @Override
    public void onGetReportFailed(String err) {
        Helper.displayErrorMessage(MainPassenger.this,getString(R.string.errorconnect));
    }
}


