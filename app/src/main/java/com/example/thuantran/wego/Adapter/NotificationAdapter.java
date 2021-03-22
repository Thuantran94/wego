package com.example.thuantran.wego.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thuantran.wego.Object.PassengerTrip;
import com.example.thuantran.wego.R;
import com.example.thuantran.wego.Tools.Helper;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.RecyclerViewHolder> {


    private List<PassengerTrip> TripList;
    private Context context;

    public NotificationAdapter(Context context,List<PassengerTrip> TripList){
        this.TripList = TripList;
        this.context = context;

    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.layout_notification, parent, false);
        return new RecyclerViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {

        PassengerTrip trip = TripList.get(position);

        holder.depart.setText(Helper.fromStringLatLngToFullAddress(context,trip.getDepart()));
        holder.destination.setText(Helper.fromStringLatLngToFullAddress(context,trip.getDestination()));

        holder.date.setText(context.getString(R.string.departday)+ trip.getDate()+ context.getString(R.string.attime)+ trip.getTime());






        switch (trip.getStt()){
            case "removed":
                holder.stt.setText(context.getString(R.string.het_han));
                holder.stt.setTextColor(context.getResources().getColor(R.color.red));
                break;
            case "completed":
                holder.stt.setText(context.getString(R.string.finished));
                holder.stt.setTextColor(context.getResources().getColor(R.color.green));
                trip.setNMessenger("-1"); // an dong tin nhan
                break;
            case "pending":
                holder.stt.setText(context.getString(R.string.dang_tim));
                holder.stt.setTextColor(context.getResources().getColor(R.color.colorBlack));
                holder.stt.setTextColor(context.getResources().getColor(R.color.colorBlack));
                holder.message.setTextColor(context.getResources().getColor(R.color.colorBlack));
                break;
            case "received":
                holder.stt.setText(context.getString(R.string.dang_cho));
                holder.stt.setTextColor(context.getResources().getColor(R.color.cyan));

                break;
            case "accepted":
                holder.stt.setText(context.getString(R.string.danhan));
                holder.stt.setTextColor(context.getResources().getColor(R.color.green));
                //holder.stt.setTextColor(Color.rgb(255,0,255));
                trip.setNMessenger("-1"); // an dong tin nhan
                break;
            case "acceptedbyother":
                holder.stt.setText(context.getString(R.string.da_co_nguoi_nhan));
                holder.stt.setTextColor(context.getResources().getColor(R.color.red));
                trip.setNMessenger("-1"); // an dong tin nhan
                break;

        }

        switch (trip.getNMessenger()){
            case "-2":
                holder.message.setText("");
                holder.message.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                break;
            case "-1":
                holder.message.setText("");
                break;
            case "0":
                holder.message.setText(context.getString(R.string.tin_nhan));
                holder.message.setTextColor(context.getResources().getColor(R.color.colorBlack));
                break;
            default:

                holder.message.setText(trip.getNMessenger()+context.getString(R.string.tin_nhan_moi));
                holder.message.setTextColor(context.getResources().getColor(R.color.red));
                break; }


    }

    @Override
    public int getItemCount() {
        return TripList == null? 0:TripList.size();
    }



    class RecyclerViewHolder extends RecyclerView.ViewHolder {
      private TextView date, depart, destination, message, stt;

        RecyclerViewHolder(View itemView) {
            super(itemView);
            date         = itemView.findViewById(R.id.date);
            depart       = itemView.findViewById(R.id.depart);
            destination  = itemView.findViewById(R.id.destination);
            message      = itemView.findViewById(R.id.message);
            stt          = itemView.findViewById(R.id.stt);
        }
    }

}
