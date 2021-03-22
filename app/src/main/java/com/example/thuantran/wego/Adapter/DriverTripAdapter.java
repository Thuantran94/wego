package com.example.thuantran.wego.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thuantran.wego.Tools.Helper;
import com.example.thuantran.wego.R;
import com.example.thuantran.wego.Object.DriverTrip;

import java.util.List;


public class DriverTripAdapter extends RecyclerView.Adapter<DriverTripAdapter.RecyclerViewHolder> {
    private Context context;
    private List <DriverTrip> driverTripList;

    public DriverTripAdapter(Context context, List <DriverTrip> driverTripList){
        this.driverTripList = driverTripList;
        this.context = context;
    }


    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.layout_driver_trip, parent, false);
        return new RecyclerViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        DriverTrip driverTrip = driverTripList.get(position);

        holder.depart.setText(Helper.fromStringLatLngToFullAddress(context,driverTrip.getDepart()));
        holder.destination.setText(Helper.fromStringLatLngToFullAddress(context,driverTrip.getDestination()));

        holder.date.setText(driverTrip.getDate()+ context.getString(R.string.attime) +driverTrip.getTime());


        String typeCar;
        if (driverTrip.getRequestCar().equals("0")){
            typeCar = context.getString(R.string.Bike);
            holder.typecar.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_bike_white_16dp,0,0,0);
        }else {
            typeCar = context.getString(R.string.Taxi);
            holder.typecar.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_directions_car_white_16dp,0,0,0);

        }

        holder.seat.setText(driverTrip.getSeat());
        holder.typecar.setText(typeCar);


    }

    @Override
    public int getItemCount() {
        return driverTripList == null ? 0:driverTripList.size();
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder {

        TextView date, time, depart, destination, seat, typecar;


        RecyclerViewHolder(View itemView) {
            super(itemView);

            date         = itemView.findViewById(R.id.ddate);
            seat         = itemView.findViewById(R.id.seat);
            typecar      = itemView.findViewById(R.id.dstypecar);
            depart       = itemView.findViewById(R.id.ddepart);
            destination  = itemView.findViewById(R.id.ddestination);


        }
    }

}
