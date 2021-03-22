package com.example.thuantran.wego.View.Main;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.example.thuantran.wego.DataAccess.IAccessFireBase;
import com.example.thuantran.wego.DataAccess.ReadDataFromFireBase;
import com.example.thuantran.wego.DataAccess.ReadDataFromFireBaseListener;
import com.example.thuantran.wego.DataAccess.UpdateService;
import com.example.thuantran.wego.Object.User;
import com.example.thuantran.wego.R;
import com.example.thuantran.wego.Tools.Helper;
import com.example.thuantran.wego.View.SignUp.CreateProfileActivity;
import com.example.thuantran.wego.View.Driver.MainDriver;
import com.example.thuantran.wego.View.Passenger.MainPassenger;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreen extends AppCompatActivity implements ReadDataFromFireBaseListener {
    private final int SPLASH_DISPLAY_LENGTH = 2000;


    private FirebaseUser currentUser;
    private String userID, phone, email;


    // Bắt sự kiện người dùng thoát ứng dụng
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (userID != null){
            Intent intent = new Intent(Intent.ACTION_SYNC, null, this, UpdateService.class);
            intent.putExtra("userID",userID);
            intent.putExtra("status","offline");
            startService(intent); }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main_splash_green);

        final Handler handler = new Handler();

        handler.postDelayed(() -> {

            if (!Helper.isNetwordAvailiable(this)){
                Helper.displayDiagError(this,getString(R.string.err),getString(R.string.Checkinternet));
                return;
            }

            if(!Helper.isServicesOK(this)){
                Helper.displayDiag(this,getString(R.string.noti),getString(R.string.googlePlaysv));
                return;
            }


            // Auto turn on GPS.
            //ModelLocationService modelLocationService = new ModelLocationService();
            //modelLocationService.EnableGPSAutoMatically(this);
            //if (!modelLocationService.isEnable()){ return; }



            /*
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

            mDatabase.child(Constant.GEOFIRE_LOCATION_TRIP_PASSENGER).removeValue();
            mDatabase.child(Constant.TRIP_PASSENGER).removeValue();
            mDatabase.child(Constant.GEOFIRE_LOCATION_TRIP_DRIVER).removeValue();
            mDatabase.child(Constant.TRIP_DRIVER).removeValue();


            mDatabase.child(Constant.TRIP_PASSENGER).orderByChild("userID").equalTo("c4dY6Zr6yIWDoPFLxzaBjxoWMBP2").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot ds:dataSnapshot.getChildren()){

                        String key = ds.getKey();


                        mDatabase.child(Constant.TRIP_PASSENGER).child(key).removeValue();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            */





            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            currentUser = mAuth.getCurrentUser();

            if(currentUser == null){

                Intent mainIntent = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(mainIntent);
                overridePendingTransition(R.anim.enter, R.anim.exit);

            }else{

                userID = currentUser.getUid();
                email  = currentUser.getEmail();
                phone  = currentUser.getPhoneNumber();


                Helper.isgetLocation(this, userID, new IAccessFireBase.iGetLocationSuccess() {
                    @Override
                    public void onSuccess() {

                        ReadDataFromFireBase readDataFromFireBase = new ReadDataFromFireBase(SplashScreen.this);
                        readDataFromFireBase.getUserFromFireBase(userID);
                    }

                    @Override
                    public void onFailed(String err) {

                        if (err.equals("nointernet")){
                            Helper.displayDiagError(SplashScreen.this,getString(R.string.err),getString(R.string.Checkinternet));
                        }

                        if (err.equals("nogps")){
                            Helper.displayDiagError(SplashScreen.this,getString(R.string.connectGPSfail),getString(R.string.defaultPoint)); }

                        if (err.equals("limited")){
                            Helper.displayDiagError(SplashScreen.this,getString(R.string.sorry),getString(R.string.diachihanche)); }

                    }
                });

            }

        }, SPLASH_DISPLAY_LENGTH);


    }

    @Override
    public void onGetUserSuccess(User user) {

        if (user !=null && user.getName() != null){

            if(user.getModel() == null){

                Bundle bundle = new Bundle();
                bundle.putParcelable("user",user);
                Intent intent = new Intent(SplashScreen.this, WellComeActivity.class);
                intent.putExtra("bundle",bundle);
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);

            }else {

                switch (user.getModel()){

                    case "passenger":
                        Bundle bundle1 = new Bundle();
                        bundle1.putParcelable("user",user);
                        Intent intent1 = new Intent(SplashScreen.this, MainPassenger.class);
                        intent1.putExtra("bundle",bundle1);
                        startActivity(intent1);
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                        break;

                    case "driver":
                        Bundle bundle2 = new Bundle();
                        bundle2.putParcelable("user",user);
                        Intent intent2 = new Intent(SplashScreen.this, MainDriver.class);
                        intent2.putExtra("bundle",bundle2);
                        startActivity(intent2);
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                        break;
                }
            }

        }else{

            Intent intent = new Intent(SplashScreen.this, CreateProfileActivity.class);
            intent.putExtra("userID",userID);
            intent.putExtra("phone",phone);
            intent.putExtra("email",email);
            startActivity(intent);
            overridePendingTransition(R.anim.enter, R.anim.exit);
        }
    }

    @Override
    public void onGetUserFailed(String err) {
        Helper.displayErrorMessage(this,getString(R.string.errorconnect));
    }

}
