package com.example.thuantran.wego.View.Driver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.thuantran.wego.DataAccess.AccessFireBase;
import com.example.thuantran.wego.DataAccess.IAccessFireBase;
import com.example.thuantran.wego.Interface.User.Driver;
import com.example.thuantran.wego.Object.DriverTrip;
import com.example.thuantran.wego.Object.PassengerTrip;
import com.example.thuantran.wego.Object.User;
import com.example.thuantran.wego.Presenter.User.PresenterDriver;
import com.example.thuantran.wego.R;
import com.example.thuantran.wego.Adapter.DriverTripAdapter;
import com.example.thuantran.wego.Tools.CustomDialog;
import com.example.thuantran.wego.Tools.Helper;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public class DrTripActivity extends AppCompatActivity implements Driver.View {

    private ProgressDialog dialog;
    private ImageView back, add;
    private RecyclerView recyclerView;
    private RelativeLayout relativeLayout;



    private User user;
    private ArrayList<DriverTrip> driverTripArrayList;
    private DriverTripAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main_driver_trip);


        relativeLayout= findViewById(R.id.relativeLayoutnotrip);
        back = findViewById(R.id.backhomedr);
        add = findViewById(R.id.add);
        recyclerView  = findViewById(R.id.lwproposer);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        Bundle b = getIntent().getBundleExtra("bundle");
        if (b != null) {
            user     = b.getParcelable("user");
            if (user != null){
                String userID = user.getUserID();
                initSwipe();


                dialog = new ProgressDialog(this);
                dialog.setMessage(this.getString(R.string.loading));
                dialog.setCancelable(false);
                dialog.show();


                PresenterDriver presenterDriver = new PresenterDriver(DrTripActivity.this);
                presenterDriver.receivedHandleMultiDriverTrip(DrTripActivity.this, userID);



                back.setOnClickListener(v -> finish());


            }
        }

    }


    @Override
    public void onGetReceivedListSuccess(ArrayList<String[]> arrayList) {

    }

    @Override
    public void onGetDetRefundSuccess(int refund) {

    }

    @Override
    public void onGetMultiTripPaSuccess(ArrayList<PassengerTrip> arrayList) {

    }


    @Override
    public void onGetMultiTripDriverSuccess(ArrayList<DriverTrip> arrayList) {

        dialog.dismiss();

        driverTripArrayList = arrayList;

        if ((driverTripArrayList != null)){

            if( driverTripArrayList.size() == 0){
                relativeLayout.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.INVISIBLE);

            }else{
                relativeLayout.setVisibility(View.INVISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
                adapter = new DriverTripAdapter(DrTripActivity.this, driverTripArrayList);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            }

        }

        add.setOnClickListener(v -> {


            Bundle bundle1 = new Bundle();
            bundle1.putParcelable("user",user);
            bundle1.putInt("limit",driverTripArrayList.size());
            Intent i = new Intent(DrTripActivity.this, DrAddTripActivity.class);
            i.putExtra("bundle",bundle1);
            startActivity(i);
        });


    }

    @Override
    public void onGetMultiTripDriverFail(String err) {
        dialog.dismiss();
    }



    private void initSwipe(){
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT ) {

            @Override
            public boolean onMove(@NotNull RecyclerView recyclerView, @NotNull RecyclerView.ViewHolder viewHolder, @NotNull RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onChildDraw(@NotNull Canvas c, @NotNull RecyclerView recyclerView, @NotNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                if (direction == ItemTouchHelper.LEFT){

                    DriverTrip driverTrip = driverTripArrayList.get(position);


                    CustomDialog dialog = new CustomDialog(DrTripActivity.this,R.drawable.ic_warning, 2);
                    dialog.setCancelable(false);
                    dialog.setTitle(R.string.cancle_trip);
                    dialog.setCancelText(getString(R.string.No));
                    dialog.setCancelClickListener(dialog1 -> {
                        dialog1.cancel();
                        adapter.notifyItemChanged(position); });
                    dialog.setConfirmText(getString(R.string.Yes));
                    dialog.setConfirmClickListener(dialog12 -> {

                        AccessFireBase.removeTripDriver(driverTrip.getID());
                        Helper.displayErrorMessage(DrTripActivity.this,getString(R.string.deletetripsuccess));
                        adapter = new DriverTripAdapter(DrTripActivity.this, driverTripArrayList);
                        recyclerView.setAdapter(adapter);
                    });
                    dialog.show();


                }
            }


        };


        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }
}
