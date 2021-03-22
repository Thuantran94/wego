package com.example.thuantran.wego.View.Main;


import android.annotation.SuppressLint;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.thuantran.wego.DataAccess.AccessFireBase;
import com.example.thuantran.wego.DataAccess.IAccessFireBase;
import com.example.thuantran.wego.Object.User;
import com.example.thuantran.wego.R;
import com.example.thuantran.wego.Tools.Helper;
import com.example.thuantran.wego.View.Driver.MainDriver;
import com.example.thuantran.wego.View.Passenger.MainPassenger;
import com.q42.android.scrollingimageview.ScrollingImageView;


public class WellComeActivity extends AppCompatActivity  {

    private View view;
    private Button btDriver, btPassenger;
    private TextView twHello, twhello00;
    private CheckBox cbcheck;
    private User user;
    private boolean isClicked;



    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_wellcome);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mapToLayout();

        isClicked =  false;

        Bundle b = getIntent().getBundleExtra("bundle");
        if (b != null) {

            user     = b.getParcelable("user");

            if (user != null) {

                twHello.setText(user.getName());

                Helper.isgetLocation(this, user.getUserID(), new IAccessFireBase.iGetLocationSuccess() {
                    @Override
                    public void onSuccess() {

                        AccessFireBase.updateStatus(user.getUserID(),"online");


                        Helper helper = new Helper();
                        helper.callSystemTime();
                        String[] rs2 = helper.getTime();
                        String[] time = rs2[1].split(":",2);
                        int hour = Integer.valueOf(time[0]);

                        if (hour<6){
                            twhello00.setText(getResources().getString(R.string.xin_ch_o));
                            view.setBackground(getResources().getDrawable(R.drawable.background_gradient_blue_dark));
                        }else if(hour < 15){
                            twhello00.setText(getString(R.string.morning));
                            view.setBackground(getResources().getDrawable(R.drawable.background_gradient_blue_light));
                        }else if(hour < 20){
                            twhello00.setText(getString(R.string.afternoon));
                            view.setBackground(getResources().getDrawable(R.drawable.background_gradient_blue_orange));
                        }else {
                            twhello00.setText(getString(R.string.evening));
                            view.setBackground(getResources().getDrawable(R.drawable.background_gradient_blue_night));
                        }


                        // ACCESS TO PASSENGER MODE
                        btPassenger.setOnClickListener(v -> {

                            if (cbcheck.isChecked()){ AccessFireBase.updateModel(user.getUserID(),"passenger"); }

                            if(!isClicked){
                                Bundle bundle = new Bundle();
                                bundle.putParcelable("user",user);
                                Intent intent12 = new Intent(WellComeActivity.this, MainPassenger.class);
                                intent12.putExtra("bundle", bundle);
                                startActivity(intent12);
                                overridePendingTransition(R.anim.enter, R.anim.exit);
                            }

                            isClicked = true;
                        });


                        // ACCESS TO DRIVER MODE
                        btDriver.setOnClickListener(v -> {

                            if (cbcheck.isChecked()){ AccessFireBase.updateModel(user.getUserID(),"driver"); }

                            if(!isClicked) {
                                Bundle bundle = new Bundle();
                                bundle.putParcelable("user",user);
                                Intent intent1 = new Intent(WellComeActivity.this, MainDriver.class);
                                intent1.putExtra("bundle", bundle);
                                startActivity(intent1);
                                overridePendingTransition(R.anim.enter, R.anim.exit);

                            }
                            isClicked = true;
                        });


                    }

                    @Override
                    public void onFailed(String err) {

                        if (err.equals("nointernet")){
                            Helper.displayDiagError(WellComeActivity.this,getString(R.string.err),getString(R.string.errorconnect)); }

                        if (err.equals("nogps")){
                            Helper.displayDiagError(WellComeActivity.this,getString(R.string.connectGPSfail),getString(R.string.defaultPoint)); }

                        if (err.equals("limited")){
                            Helper.displayDiagError(WellComeActivity.this,getString(R.string.sorry),getString(R.string.diachihanche)); }



                    }
                });

            }
        }






    }
    @Override
    public void onBackPressed() {

    }

    private void mapToLayout(){

        view        = findViewById(R.id.view);
        cbcheck     = findViewById(R.id.cbcheck);
        btDriver    = findViewById(R.id.btDriver);
        btPassenger = findViewById(R.id.btPassenger);
        twhello00   = findViewById(R.id.twhello00);
        twHello     = findViewById(R.id.twhello);
        ScrollingImageView scrollingBackground = findViewById(R.id.scrolling_background);
        scrollingBackground.stop();
        scrollingBackground.start();

    }



}
