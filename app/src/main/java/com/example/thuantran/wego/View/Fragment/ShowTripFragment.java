package com.example.thuantran.wego.View.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.example.thuantran.wego.Adapter.RecyclerItemClickListener;
import com.example.thuantran.wego.Adapter.TripAdapter;
import com.example.thuantran.wego.DataAccess.AccessFireBase;
import com.example.thuantran.wego.DataAccess.IAccessFireBase;
import com.example.thuantran.wego.Interface.Tools.Fragment2Activity;
import com.example.thuantran.wego.Interface.Trip.Trip;
import com.example.thuantran.wego.Object.PassengerTrip;
import com.example.thuantran.wego.Presenter.Trip.PresenterTrip;
import com.example.thuantran.wego.R;
import com.example.thuantran.wego.Tools.CustomDialog;
import com.example.thuantran.wego.Tools.Helper;
import com.example.thuantran.wego.View.Passenger.PaConfirmActivity;
import com.example.thuantran.wego.View.Passenger.PaFinalActivity;
import com.example.thuantran.wego.Object.User;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;


public class ShowTripFragment extends Fragment implements Trip.View{


    private User user;
    private RecyclerView recyclerView;
    private RelativeLayout relativeLayout;
    private TripAdapter adapter;
    private PresenterTrip    presenterTrip;
    private ArrayList<PassengerTrip> passengerTripArrayList;
    private int position;
    private Fragment2Activity mcallback;


    private static int REQUEST_CODE_YES_DRIVER_SELECTED   =456;
    private static int REQUEST_CODE_FINAL_DRIVER_SELECTED =789;

    public void sendData(User user, ArrayList<PassengerTrip> passengerTripArrayList) {
        // Get data from activity without create new one
        this.passengerTripArrayList  = passengerTripArrayList;
        this.user = user;
        DisplayList();
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        mcallback = (Fragment2Activity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Objects.requireNonNull(getActivity()).getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        View rootview = inflater.inflate(R.layout.activity_main_passenger_trip, container, false);

        relativeLayout = rootview.findViewById(R.id.relativeLayoutnotrip);
        recyclerView   = rootview.findViewById(R.id.lwtrip);
        recyclerView.setAdapter(adapter);



        Bundle bundle = getArguments();
        if (bundle != null) {
            passengerTripArrayList = bundle.getParcelableArrayList("list");
            user                   = bundle.getParcelable("user");

            presenterTrip = new PresenterTrip(this);
            DisplayList();


        }
        initSwipe();

        return rootview;
    }

    private void DisplayList(){

        if (passengerTripArrayList !=null){

            if (passengerTripArrayList.size() == 0){
                relativeLayout.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.INVISIBLE);
            }else {
                relativeLayout.setVisibility(View.INVISIBLE);
                recyclerView.setVisibility(View.VISIBLE);

                for (int i =0;i<passengerTripArrayList.size();i++){


                    if (passengerTripArrayList.get(i).getIDdr().length() <5 ){
                        passengerTripArrayList.get(i).setAvatar(user.getAvatar());
                    }

                }

                adapter = new TripAdapter(getContext(),passengerTripArrayList);
                Helper.showTripAdapter(Objects.requireNonNull(getActivity()),recyclerView,adapter,passengerTripArrayList);


                recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(),recyclerView,new RecyclerItemClickListener.OnItemClickListener(){

                    @Override
                    public void onItemClick(View view, int i) {
                        position = i;
                        presenterTrip.receivedHandleSelectTrip(passengerTripArrayList,i);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }
                }));
            }

        }
    }



    @Override
    public void onGetMultiTripSuccess(ArrayList<PassengerTrip> list) { }

    @Override
    public void onGetOneTripSuccess(PassengerTrip passengerTrip) {

    }

    @Override
    public void onGetTBPaTripFail(String err) { }



    @Override
    public void haveDriverSelected(PassengerTrip trip) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("user",user);
        bundle.putParcelable("trip",trip);
        Intent intent = new Intent(getActivity(), PaConfirmActivity.class);
        intent.putExtra("bundle",bundle);
        startActivityForResult(intent,REQUEST_CODE_YES_DRIVER_SELECTED);
    }

    @Override
    public void finalDriverSelected(PassengerTrip trip) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("user",user);
        bundle.putParcelable("trip",trip);
        Intent intent = new Intent(getActivity(), PaFinalActivity.class);
        intent.putExtra("bundle",bundle);
        startActivityForResult(intent,REQUEST_CODE_FINAL_DRIVER_SELECTED);
    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode ==Activity.RESULT_OK && data != null){



            user = data.getParcelableExtra("user");
            mcallback.getUser(user);


            if(requestCode == REQUEST_CODE_YES_DRIVER_SELECTED) {

                PassengerTrip passengerTrip = data.getParcelableExtra("passengerTrip");

                if (passengerTrip !=null){
                    passengerTripArrayList.set(position, passengerTrip);

                }else{
                    passengerTripArrayList.remove(position);
                }

            }
            if(requestCode == REQUEST_CODE_FINAL_DRIVER_SELECTED) {

                PassengerTrip passengerTrip = data.getParcelableExtra("passengerTrip");

                if (passengerTripArrayList.size()>0){
                    passengerTripArrayList.set(position, passengerTrip);
                }




            }
        }


        adapter = new TripAdapter(getContext(),passengerTripArrayList);
        Helper.showTripAdapter(Objects.requireNonNull(getActivity()),recyclerView,adapter,passengerTripArrayList);



        // Co loi chuyen đi được chấp nhận sau khi đã hết hạn!!!!


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

                    PassengerTrip passengerTrip = passengerTripArrayList.get(position);


                    CustomDialog dialog = new CustomDialog(Objects.requireNonNull(getActivity()),R.drawable.ic_warning, 2);
                    dialog.setCancelable(false);
                    dialog.setTitle(getString(R.string.cancle_trip));


                    if (passengerTrip.getStt().equals("completed") && passengerTrip.getReviewPa2Dr().equals("")){
                        dialog.setTitle(R.string.ketthuccd);
                        dialog.setMessage(getString(R.string.kethuccd1)); }


                    dialog.setCancelText(getString(R.string.No));
                    dialog.setCancelClickListener(dialog1 -> {
                        dialog.dismiss();
                        adapter.notifyItemChanged(position); });

                    dialog.setConfirmText(getString(R.string.Yes));
                    dialog.setConfirmClickListener(dialog12 -> {
                        if (passengerTrip.getStt().equals("completed")){

                            if (passengerTrip.getReviewPa2Dr().equals("")){

                                Bundle bundle = new Bundle();
                                bundle.putParcelable("user",user);
                                bundle.putParcelable("trip", passengerTrip);
                                Intent intent = new Intent(getActivity(), PaFinalActivity.class);
                                intent.putExtra("bundle",bundle);
                                startActivityForResult(intent,REQUEST_CODE_FINAL_DRIVER_SELECTED);

                            }else{

                                AccessFireBase.removeTripWithOutRefund(passengerTrip.getID(), new IAccessFireBase.iRemoveTripWithOutRefund() {
                                    @Override
                                    public void onSuccess() {
                                        Log.d("sdsdsds","onSuccess removeTripWithOutRefund");
                                        Helper.displayErrorMessage(getActivity(),getString(R.string.deletetripsuccess));
                                        adapter = new TripAdapter(getContext(),passengerTripArrayList);
                                        Helper.showTripAdapter(Objects.requireNonNull(getActivity()),recyclerView,adapter,passengerTripArrayList);
                                    }

                                    @Override
                                    public void onFailed() {
                                        Helper.displayErrorMessage(getActivity(),getString(R.string.errorconnect)); }
                                });

                            }

                        }else{

                            AccessFireBase.removeTripWithRefund(passengerTrip.getID(), new IAccessFireBase.iRemoveTripWithRefund() {
                                @Override
                                public void onSuccess() {
                                    int ntrip = Integer.valueOf(user.getNtriptotal());
                                    user.setNtriptotal(String.valueOf(ntrip-1));
                                    mcallback.getUser(user);

                                    Helper.displayErrorMessage(getActivity(),getString(R.string.deletetripsuccess));
                                    adapter = new TripAdapter(getContext(),passengerTripArrayList);
                                    Helper.showTripAdapter(Objects.requireNonNull(getActivity()),recyclerView,adapter,passengerTripArrayList);

                                }

                                @Override
                                public void onFailed() {
                                    Helper.displayErrorMessage(getActivity(),getString(R.string.errorconnect));
                                }
                            });

                        }

                        dialog12.dismiss();
                    });
                    dialog.show();
                }
            }


        };


        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }


}
