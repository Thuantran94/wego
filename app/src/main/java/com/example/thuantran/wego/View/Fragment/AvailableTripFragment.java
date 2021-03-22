package com.example.thuantran.wego.View.Fragment;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.thuantran.wego.Adapter.RecyclerItemClickListener;
import com.example.thuantran.wego.Adapter.TripAdapter;
import com.example.thuantran.wego.Interface.Tools.Fragment2Activity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.example.thuantran.wego.R;
import com.example.thuantran.wego.Tools.Helper;
import com.example.thuantran.wego.View.Driver.DrConfirmActivity;
import com.example.thuantran.wego.View.Driver.DrFinalActivity;
import com.example.thuantran.wego.Object.PassengerTrip;
import com.example.thuantran.wego.Object.User;

import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.Objects;

public class AvailableTripFragment extends Fragment {


    private RecyclerView recyclerView;
    private RelativeLayout relativeLayout;
    private final int REQUEST_CODE_CONFIRM = 123;
    private Fragment2Activity mcallback;
    private TripAdapter adapter;
    private ArrayList<PassengerTrip> arrayListTrip = new ArrayList<>();
    private ArrayList<PassengerTrip> avaliabeTrip, ListTripShowed;
    private User user;


    public void sendData(User user,ArrayList<PassengerTrip> arrayListTrip) {
        // Get data from activity without create new one
        this.user = user;
        this.arrayListTrip = arrayListTrip;

        showTrip();
    }
    public void sendData(User user) {
        this.user = user;

        showTrip();
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        mcallback = (Fragment2Activity) getActivity();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Objects.requireNonNull(getActivity()).setTitle(getResources().getString(R.string.Nearme));
        View view = inflater.inflate(R.layout.activity_main_avaliable_trip,container,false);
        recyclerView   =  view.findViewById(R.id.lwtrip1);
        relativeLayout = view.findViewById(R.id.notripfound);


        initSwipe();

        return view;
    }







    private void showTrip(){

        if (user !=null){


            if (arrayListTrip == null || arrayListTrip.size() ==0){
                relativeLayout.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.INVISIBLE);
            }else {
                relativeLayout.setVisibility(View.INVISIBLE);
                recyclerView.setVisibility(View.VISIBLE);

                // Lọc ra các chuyến đi đã nhận
                avaliabeTrip   = new ArrayList<>();
                for (int i = 0; i< arrayListTrip.size();i++){
                    PassengerTrip trip = arrayListTrip.get(i);
                    if (!Helper.isExpire(trip.getDate(),trip.getTime()) || trip.getIDdr().equals("-1") ){
                        avaliabeTrip.add(trip);
                    }
                }


                if (avaliabeTrip.size() ==0){
                    relativeLayout.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.INVISIBLE);
                }else{
                    adapter = new TripAdapter(getContext(),avaliabeTrip);
                    adapter.notifyDataSetChanged();
                    Helper.showTripAdapter(Objects.requireNonNull(getActivity()),recyclerView,adapter,avaliabeTrip);
                    showList(avaliabeTrip);
                }

            }

        }

    }


    private void showList(ArrayList<PassengerTrip> arrayList){

        ListTripShowed = arrayList;

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(),recyclerView,new RecyclerItemClickListener.OnItemClickListener(){

            @Override
            public void onItemClick(View view, int i) {

                PassengerTrip trip = ListTripShowed.get(i);
                String stt     = trip.getStt();

                switch (stt){
                    case "acceptedbyother":
                        Toast.makeText(getActivity(), getResources().getString(R.string.receivedTrip), Toast.LENGTH_SHORT).show();
                        break;
                    case "accepted":
                        Bundle bundle12 = new Bundle();
                        bundle12.putParcelable("user",user);
                        bundle12.putParcelable("trip",trip);
                        Intent intent12 = new Intent(getActivity(), DrFinalActivity.class);
                        intent12.putExtra("bundle", bundle12);
                        startActivity(intent12);
                        break;
                    default:
                        Bundle bundle1 = new Bundle();
                        bundle1.putParcelable("user",user);
                        bundle1.putParcelable("trip",trip);
                        Intent intent1 = new Intent(getActivity(), DrConfirmActivity.class);
                        intent1.putExtra("bundle", bundle1);
                        startActivityForResult(intent1,REQUEST_CODE_CONFIRM);
                }

            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CONFIRM && resultCode == Activity.RESULT_OK) {

            this.user = data.getParcelableExtra("user");
            mcallback.getUser(user);

        }
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

                    ListTripShowed.remove(position);
                    adapter = new TripAdapter(getActivity(),ListTripShowed);
                    adapter.notifyDataSetChanged();
                    Helper.showTripAdapter(Objects.requireNonNull(getActivity()),recyclerView,adapter,ListTripShowed);

                }

            }

        };


        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }



}





