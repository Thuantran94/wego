package com.example.thuantran.wego.View.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thuantran.wego.Adapter.ChatAdapter;
import com.example.thuantran.wego.DataAccess.AccessFireBase;
import com.example.thuantran.wego.DataAccess.IAccessFireBase;
import com.example.thuantran.wego.Interface.Profile.SMS;
import com.example.thuantran.wego.Object.PassengerTrip;
import com.example.thuantran.wego.Object.SMSContext;
import com.example.thuantran.wego.Object.User;
import com.example.thuantran.wego.Presenter.Profile.PresenterSMS;
import com.example.thuantran.wego.R;
import com.example.thuantran.wego.Tools.Helper;

import java.util.ArrayList;



public class ChatFragment extends Fragment implements SMS.View {


    private User user;
    private PassengerTrip trip;
    private EditText edSMS;
    private RecyclerView listSMS;
    private Helper helper;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_main_chat,container,false);

        Button btSend = view.findViewById(R.id.btSend);
        edSMS     = view.findViewById(R.id.edSMS);
        listSMS   = view.findViewById(R.id.lwSMS);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setReverseLayout(false);
        layoutManager.setStackFromEnd(true);
        listSMS.setLayoutManager(layoutManager);

        Bundle bundle = getArguments();
        if (bundle != null) {
            user = bundle.getParcelable("user");
            trip = bundle.getParcelable("trip");



            if (user !=null && trip !=null){

                btSend.setOnClickListener(v -> {

                    helper = new Helper();
                    helper.callSystemTime();
                    String[] rs2      = helper.getTime();
                    String datesms    = rs2[0];
                    String timesms    = rs2[1];
                    String contextsms = edSMS.getText().toString().trim();
                    if(!contextsms.equals("")){
                        AccessFireBase.addSms(user, contextsms, datesms,timesms, trip, new IAccessFireBase.iAddSms() {
                            @Override
                            public void onSuccess() {
                                edSMS.setText("");
                                AccessFireBase.updateLastSms(user.getUserID(), user.getAvatar(), user.getName(), contextsms ,datesms,timesms, trip,
                                        new IAccessFireBase.iUpdateLastSms() {
                                            @Override
                                            public void onSuccess() {

                                            }

                                            @Override
                                            public void onFailed() {
                                                Helper.displayErrorMessage(getActivity(),getString(R.string.errorconnect));
                                            }
                                        });

                            }

                            @Override
                            public void onFailed() {
                                Helper.displayErrorMessage(getActivity(),getString(R.string.errorconnect));
                            }
                        });

                    }
                });


                PresenterSMS presenterSMS = new PresenterSMS(this);
                presenterSMS.receivedGetSMS(getActivity(),trip.getID());


            }
        }




        return view;
    }




    @Override
    public void onGetSmsSuccess(ArrayList<SMSContext> smsContexts) {

        ChatAdapter adapter = new ChatAdapter( getActivity(),smsContexts, user.getUserID(),false);
        listSMS.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    @Override
    public void onGetSmsFail(String err) {

    }

    @Override
    public void onGetLastSmsSuccess(ArrayList<SMSContext> arraySMS) {

    }

    @Override
    public void onGetLastSmsFail(String err) {

    }



    @Override
    public void onGetTripSuccess(PassengerTrip passengerTrip) {

    }

    @Override
    public void onGetTripFail(String err) {

    }
}
