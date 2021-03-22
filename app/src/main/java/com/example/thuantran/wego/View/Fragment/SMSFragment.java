package com.example.thuantran.wego.View.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;

import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thuantran.wego.Adapter.ChatAdapter;
import com.example.thuantran.wego.Adapter.NotificationAdapter;
import com.example.thuantran.wego.Adapter.RecyclerItemClickListener;
import com.example.thuantran.wego.DataAccess.AccessFireBase;
import com.example.thuantran.wego.DataAccess.IAccessFireBase;
import com.example.thuantran.wego.Interface.Profile.SMS;
import com.example.thuantran.wego.Interface.Tools.ReceivedSMSListener;
import com.example.thuantran.wego.Object.PassengerTrip;
import com.example.thuantran.wego.Object.SMSContext;
import com.example.thuantran.wego.Object.User;
import com.example.thuantran.wego.Presenter.Profile.PresenterSMS;
import com.example.thuantran.wego.R;
import com.example.thuantran.wego.Tools.Helper;
import com.example.thuantran.wego.View.Passenger.PaConfirmActivity;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;



public class SMSFragment extends Fragment implements SMS.View {

    private ProgressDialog dialog;

    private RecyclerView smslw;
    private RadioGroup btSMS;
    private User user;
    private String userID;
    private PresenterSMS presenterSMS;
    private RelativeLayout relativeLayout;
    private TextView nodata;
    private ImageView imageView;

    private static String TAG = "SMSFragment";

    private ArrayList<SMSContext> smsContexts;
    private String keyfragment;
    private ArrayList<PassengerTrip> list;
    private ArrayList<PassengerTrip> removedList, receivedList;



    private boolean is_sms = false;
    private boolean isExistedSmsNotSeen = false;
    private boolean isUpdatedStatusSeen = false;


    public void sendData(User user, String keyfragment) {
        // Get data from activity without create new one
        this.user = user;
        this.keyfragment  = keyfragment;


        getListNotification();
    }

    public void sendData(User user, ArrayList<PassengerTrip> removedList, ArrayList<PassengerTrip> receivedList, String keyfragment ) {
        // Get data from activity without create new one
        this.user = user;
        this.removedList  = removedList;
        this.receivedList = receivedList;
        this.keyfragment  = keyfragment;


        getListNotification();
    }


    private ReceivedSMSListener mcallback;

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        mcallback = (ReceivedSMSListener) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.activity_main_sms, container, false);

        mapToLayout(v);


        Bundle bundle = getArguments();
        if (bundle != null) {

            user = bundle.getParcelable("user");

            if (user != null){
                keyfragment  = bundle.getString("keyfragment");
                removedList  = bundle.getParcelableArrayList("removedList");
                receivedList = bundle.getParcelableArrayList("receivedList");
                getListNotification();
                initSwipe();

                userID       = Objects.requireNonNull(user).getUserID();
                presenterSMS = new PresenterSMS(this);
                presenterSMS.receivedGetLastSMS(getActivity(),userID);


                btSMS.setOnCheckedChangeListener((group, checkedId) -> {

                    View radioButton = btSMS.findViewById(checkedId);
                    int index = btSMS.indexOfChild(radioButton);


                    switch (index){
                        case 0:

                            Objects.requireNonNull(getActivity()).setTitle(getResources().getString(R.string.SMS));
                            if (smsContexts == null || smsContexts.size() ==0 ){

                                relativeLayout.setVisibility(View.VISIBLE);
                                smslw.setVisibility(View.INVISIBLE);
                                imageView.setImageResource(R.drawable.nosms);
                                nodata.setText(getString(R.string.nosms));

                            }else {

                                relativeLayout.setVisibility(View.INVISIBLE);
                                smslw.setVisibility(View.VISIBLE);
                                ChatAdapter adapter = new ChatAdapter(getActivity(),smsContexts, userID,true);
                                smslw.setAdapter(adapter);
                                adapter.notifyDataSetChanged();

                            }

                            is_sms = true;
                            break;

                        case 1:
                            Objects.requireNonNull(getActivity()).setTitle(getResources().getString(R.string.noti));
                            if(list ==null || list.size() == 0 ){
                                smslw.setVisibility(View.INVISIBLE);
                                relativeLayout.setVisibility(View.VISIBLE);
                                imageView.setImageResource(R.drawable.nonotification);
                                nodata.setText(getString(R.string.b_n_kh_ng_c_th_ng));
                            }else {
                                relativeLayout.setVisibility(View.INVISIBLE);
                                smslw.setVisibility(View.VISIBLE);
                                NotificationAdapter adapter = new NotificationAdapter(getActivity(),list);
                                smslw.setAdapter(adapter);
                                adapter.notifyDataSetChanged();

                            }




                            is_sms = false;
                            break;
                    }


                });


                smslw.addOnItemTouchListener(new RecyclerItemClickListener(getContext(),smslw,new RecyclerItemClickListener.OnItemClickListener(){

                    @Override
                    public void onItemClick(View view, int position) {



                        if (is_sms){

                            dialog = new ProgressDialog(getActivity());
                            dialog.setMessage(getActivity().getString(R.string.loading));
                            dialog.setCancelable(false);
                            dialog.show();

                            presenterSMS.receivedGetTrip(smsContexts.get(position).getTrID());
                        }else {
                            PassengerTrip passengerTrip = list.get(position);
                            if(Integer.valueOf(passengerTrip.getNMessenger())>0){

                                Bundle bundle = new Bundle();
                                bundle.putParcelable("user",user);
                                bundle.putParcelable("trip", passengerTrip);
                                Intent intent = new Intent(getActivity(), PaConfirmActivity.class);
                                intent.putExtra("bundle",bundle);
                                startActivity(intent);

                                list.remove(position);
                                NotificationAdapter adapter = new NotificationAdapter(getActivity(),list);
                                adapter.notifyDataSetChanged();
                                smslw.setAdapter(adapter);
                            }
                        }
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }
                }));



            }


        }


        return v;

    }


    private void getListNotification(){


        list = new ArrayList<>();
        if(removedList !=null && removedList.size() > 0) {

            for (int i = 0; i < removedList.size(); i++) {
                PassengerTrip trip = removedList.get(i);
                trip.setStt("removed");
                trip.setNMessenger("-2");// refund
                list.add(trip);
            } }

        if(receivedList !=null && receivedList.size() > 0) {

            for (int i = 0; i < receivedList.size(); i++) {

                PassengerTrip trip = receivedList.get(i);
                trip.setStt("received");
                list.add(trip);
            } }

    }


    @Override
    public void onGetTripSuccess(PassengerTrip trip) {


        FragmentManager fragmentManager = getFragmentManager();

        if (fragmentManager != null){

            isUpdatedStatusSeen = true;
            AccessFireBase.updateStatusSeen(userID, trip.getID(), "seen", new IAccessFireBase.iUpdateStatusSeen() {
                @Override
                public void onSuccess() {
                    dialog.dismiss();

                    ChatFragment fragment = new ChatFragment();
                    Bundle bundle   = new Bundle();
                    bundle.putParcelable("user",user);
                    bundle.putParcelable("trip",trip);
                    fragment.setArguments(bundle);
                    isUpdatedStatusSeen = false;
                    if(keyfragment.equals("fragmentPa")){
                        fragmentManager.beginTransaction().add(R.id.fragmentPa,fragment,"chat").commit();
                    }else{
                        fragmentManager.beginTransaction().add(R.id.fragmentDr,fragment,"chat").commit();
                    }
                }

                @Override
                public void onFailed() {

                    dialog.dismiss();
                    Helper.displayErrorMessage(getActivity(),getString(R.string.errorconnect));

                }
            });


        }


    }

    @Override
    public void onGetTripFail(String err) {
        dialog.dismiss();
        Helper.displayErrorMessage(getActivity(),getString(R.string.tinnhandaxa));

    }


    @Override
    public void onGetSmsSuccess(ArrayList<SMSContext> smsContexts) {

    }

    @Override
    public void onGetSmsFail(String err) {

    }

    @Override
    public void onGetLastSmsSuccess(ArrayList<SMSContext> smsContexts) {
        Collections.reverse(smsContexts);

        this.smsContexts = smsContexts;


        if (smsContexts.size()==0){
            relativeLayout.setVisibility(View.VISIBLE);
            imageView.setImageResource(R.drawable.nosms);
            return;
        }else{
            relativeLayout.setVisibility(View.INVISIBLE);
            ChatAdapter adapter = new ChatAdapter(getActivity(),smsContexts, userID,true);
            smslw.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }

        is_sms = true;


        if (smsContexts.size() > 0){


            for (int i = 0; i<smsContexts.size();i++){

                if (smsContexts.get(i).getStatus().isEmpty()){
                    isExistedSmsNotSeen = true;
                    break;
                }

            }


            if (isExistedSmsNotSeen && !isUpdatedStatusSeen){
                mcallback.getSMS("isExistedSmsNotSeen");
                isExistedSmsNotSeen = false;
            }


        }
    }

    @Override
    public void onGetLastSmsFail(String err) {
        Helper.displayErrorMessage(getActivity(),getString(R.string.errorconnect));
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

                    if (is_sms){
                        AccessFireBase.removeLastSms(userID,smsContexts.get(position).getTrID());
                    }else {

                        PassengerTrip passengerTrip = list.get(position);
                        if (passengerTrip.getStt().equals("removed")){
                            AccessFireBase.removeTripWithOutRefund(list.get(position).getID(), new IAccessFireBase.iRemoveTripWithOutRefund() {
                                @Override
                                public void onSuccess() {
                                    list.remove(position);
                                    NotificationAdapter adapter = new NotificationAdapter(getActivity(),list);
                                    adapter.notifyDataSetChanged();
                                    smslw.setAdapter(adapter); }

                                @Override
                                public void onFailed() {
                                    Helper.displayErrorMessage(getActivity(),getString(R.string.errorconnect)); }
                            });

                        }


                    }



                }
            }


        };


        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(smslw);
    }


    private void mapToLayout(View v){

        smslw = v.findViewById(R.id.smslw);
        btSMS = v.findViewById(R.id.rbtsms);
        relativeLayout = v.findViewById(R.id.nodatasms);
        nodata         = v.findViewById(R.id.noti);
        imageView      = v.findViewById(R.id.img);


        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        smslw.setLayoutManager(layoutManager);
    }

}

