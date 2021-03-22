package com.example.thuantran.wego.View.Main;

import android.content.Intent;

import com.example.thuantran.wego.DataAccess.AccessFireBase;
import com.example.thuantran.wego.Interface.Profile.Profile;
import com.example.thuantran.wego.Interface.Tools.Fragment2Activity;
import com.example.thuantran.wego.Object.Car;
import com.example.thuantran.wego.Presenter.Profile.PresenterProfile;
import com.example.thuantran.wego.View.Fragment.WalletFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.thuantran.wego.View.Fragment.CarFragment;
import com.example.thuantran.wego.View.Fragment.ProfileFragment;
import com.example.thuantran.wego.Object.User;
import com.example.thuantran.wego.R;

import java.util.ArrayList;

public class AccountActivity extends AppCompatActivity implements Profile.View , Fragment2Activity {

    private ImageView  back;
    private BottomNavigationView navigation;
    private FragmentManager fragmentManager;
    private Fragment fragment;
    private Bundle bundle;
    private User user;
    private Car car;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_account);

        back = findViewById(R.id.backtk);
        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        Bundle b = getIntent().getBundleExtra("bundle");

        if (b != null) {

            user      = b.getParcelable("user");
            car       = b.getParcelable("car");
            int index = b.getInt("index");


            if(index ==0){

                if (user !=null){
                    fragmentManager = getSupportFragmentManager();
                    fragment = new ProfileFragment();
                    bundle   = new Bundle();
                    bundle.putParcelable("user",user);
                    fragment.setArguments(bundle);
                    fragmentManager.beginTransaction().add(R.id.content,fragment,"profile").commit();


                    PresenterProfile presenterProfile = new PresenterProfile(this);
                    presenterProfile.receivedHandleGetTBCar(this,user.getUserID());
                }
            }

            if(index ==1){


                if (car !=null){
                    fragmentManager = getSupportFragmentManager();
                    fragment = new CarFragment();
                    bundle   = new Bundle();
                    bundle.putParcelable("car",car);
                    fragment.setArguments(bundle);
                    fragmentManager.beginTransaction().add(R.id.content,fragment,"car").commit();
                }
            }

            if(index ==2){

                if (user !=null){

                    fragmentManager = getSupportFragmentManager();
                    fragment = new WalletFragment();
                    bundle   = new Bundle();
                    bundle.putParcelable("user",user);
                    fragment.setArguments(bundle);
                    fragmentManager.beginTransaction().add(R.id.content,fragment,"wallet").commit();

                    PresenterProfile presenterProfile = new PresenterProfile(this);
                    presenterProfile.receivedHandleGetTBCar(this,user.getUserID());
                }
            }



            back.setOnClickListener(view -> {


                final Intent data = new Intent();
                data.putExtra("user", user);
                setResult(RESULT_OK, data);
                finish();
            });


        }




    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {


        switch (item.getItemId()) {
            case R.id.navigation_profile:
                    fragment = new ProfileFragment();
                    bundle   = new Bundle();
                    bundle.putParcelable("user",user);
                    fragment.setArguments(bundle);
                    fragmentManager.beginTransaction().replace(R.id.content,fragment,"profile").commit();

                break;

            case R.id.navigation_car:
                    fragment = new CarFragment();
                    bundle   = new Bundle();
                    bundle.putParcelable("car",  car);
                    fragment.setArguments(bundle);
                    fragmentManager.beginTransaction().replace(R.id.content, fragment,"car").commit();

                break;

            case R.id.navigation_payment:
                    fragment = new WalletFragment();
                    bundle   = new Bundle();
                    bundle.putParcelable("user",user);
                    fragment.setArguments(bundle);
                    fragmentManager.beginTransaction().replace(R.id.content, fragment,"wallet").commit();

                break;




        }
        return true;
    };


    @Override
    public void onGetProfileSuccess(User user) { }
    @Override
    public void onGetProfileSuccess(ArrayList<User> user,ArrayList<Car> car) { }
    @Override
    public void onGetProfileFail(String err) { }
    @Override
    public void onGetTBCarSuccess(Car car) {
        this.car = car;
    }
    @Override
    public void onGetTBCarSuccess(ArrayList<Car> object) { }
    @Override
    public void onGetTBCarFail(String err) { }
    @Override
    public void getUser(User user) {
        this.user = user;
    }
    @Override
    public void getCar(Car car) {
        this.car =car;
    }
}
