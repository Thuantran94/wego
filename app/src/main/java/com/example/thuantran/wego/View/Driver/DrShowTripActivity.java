package com.example.thuantran.wego.View.Driver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.example.thuantran.wego.Adapter.RecyclerItemClickListener;
import com.example.thuantran.wego.Adapter.TripAdapter;
import com.example.thuantran.wego.Object.PassengerTrip;
import com.example.thuantran.wego.Object.User;
import com.example.thuantran.wego.R;
import com.example.thuantran.wego.Tools.Helper;
import java.util.ArrayList;


public class DrShowTripActivity extends AppCompatActivity {


    private RadioGroup btTrips;
    private RecyclerView recyclerView;
    private TripAdapter adapter;
    private ImageView back;
    private RelativeLayout relativeLayout;



    private User user;

    private boolean is_coming = true;

    private ArrayList<PassengerTrip> arrayListTrip;
    private ArrayList<PassengerTrip> comingList, passList;
    private int position;
    private static int REQUEST_CODE_FINAL_PASENGER_SELECTED =1234;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main_show_trip);

        relativeLayout = findViewById(R.id.relativeLayoutnotrip);
        btTrips        = findViewById(R.id.rbtrip);
        back           = findViewById(R.id.backtk);
        recyclerView   = findViewById(R.id.smslw);

        Bundle b = getIntent().getBundleExtra("bundle");
        if (b != null) {
            user     = b.getParcelable("user");
            arrayListTrip   = b.getParcelableArrayList("arrayListTrip");

            if (user !=null && arrayListTrip !=null){

                getComingEtPass();

            }

        }


        btTrips.setOnCheckedChangeListener((group, checkedId) -> {

            View radioButton = btTrips.findViewById(checkedId);
            int index = btTrips.indexOfChild(radioButton);

            switch (index){

                case 0:
                    if (comingList==null || comingList.size() == 0){
                        relativeLayout.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.INVISIBLE);
                    }else {
                        relativeLayout.setVisibility(View.INVISIBLE);
                        recyclerView.setVisibility(View.VISIBLE);
                        adapter = new TripAdapter(DrShowTripActivity.this,comingList);
                        Helper.showTripAdapter(DrShowTripActivity.this,recyclerView,adapter,comingList);
                        showList();
                    }
                    is_coming = true;

                    break;
                case 1:
                    if (passList==null || passList.size() == 0){
                        relativeLayout.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.INVISIBLE);
                    }else{
                        relativeLayout.setVisibility(View.INVISIBLE);
                        recyclerView.setVisibility(View.VISIBLE);
                        adapter = new TripAdapter(DrShowTripActivity.this,passList);
                        Helper.showTripAdapter(DrShowTripActivity.this,recyclerView,adapter,passList);
                        showList();
                    }


                    is_coming = false;
                    break;
            }

        });


        back.setOnClickListener(v -> {
            final Intent data = new Intent();
            data.putExtra("user", user);
            setResult(RESULT_OK, data);
            finish();
        });


    }

    private void getComingEtPass(){

        comingList     = new ArrayList<>();
        passList       = new ArrayList<>();
        for(int i=0; i<arrayListTrip.size();i++){

            PassengerTrip passengerTrip = arrayListTrip.get(i);

            if(passengerTrip.getIDdr().equals(user.getUserID())) {
                if (!Helper.isExpire(passengerTrip.getDate(), passengerTrip.getTime())) {
                    comingList.add(passengerTrip);
                } else {
                    if ( passengerTrip.getIDdr().length()>5){
                        passengerTrip.setStt("completed");
                        passList.add(passengerTrip);
                    }
                }
            }

        }


        if (comingList==null || comingList.size() == 0){
            relativeLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
        }else {
            relativeLayout.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter = new TripAdapter(this,comingList);
            Helper.showTripAdapter(this,recyclerView,adapter,comingList);
            showList();
        }


    }




    private void showList(){

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this,recyclerView,new RecyclerItemClickListener.OnItemClickListener(){

            @Override
            public void onItemClick(View view, int i) {
                position = i;
                PassengerTrip trip;

                if (is_coming){
                     trip = comingList.get(i);
                }else {
                     trip = passList.get(i);
                }

                Bundle bundle = new Bundle();
                bundle.putParcelable("user",user);
                bundle.putParcelable("trip",trip);
                Intent intent12 = new Intent(DrShowTripActivity.this, DrFinalActivity.class);
                intent12.putExtra("bundle",bundle);
                startActivityForResult(intent12, REQUEST_CODE_FINAL_PASENGER_SELECTED);

            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_FINAL_PASENGER_SELECTED && resultCode == Activity.RESULT_OK && data != null) {

            if(!is_coming){
                user = data.getParcelableExtra("user");
                PassengerTrip passengerTrip = data.getParcelableExtra("trip");
                passList.set(position, passengerTrip);
            }


        }
    }



}

