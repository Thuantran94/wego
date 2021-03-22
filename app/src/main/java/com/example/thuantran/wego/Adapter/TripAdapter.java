package com.example.thuantran.wego.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thuantran.wego.Object.PassengerTrip;
import com.example.thuantran.wego.Tools.Helper;
import com.example.thuantran.wego.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.RecyclerViewHolder>  {



    private Context context;
    private List <PassengerTrip> TripList;


    public TripAdapter(Context context,List <PassengerTrip> TripList){
            this.context = context;
            this.TripList = TripList;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.layout_passenger_trip, parent, false);
        return new RecyclerViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {



        PassengerTrip trip = TripList.get(position);

        String departStr = Helper.fromStringLatLngToFullAddress(context,trip.getDepart());
        String destiStr  = Helper.fromStringLatLngToFullAddress(context,trip.getDestination());

        if (departStr !=null && destiStr !=null){
            holder.depart.setText(departStr);
            holder.destination.setText(destiStr);

        }



        holder.date.setText(trip.getDate()+ context.getString(R.string.attime)+ trip.getTime());
        holder.persons.setText(trip.getNPerson());
        holder.prix.setText(trip.getCost() +context.getString(R.string.VND));



        Picasso.get().load(trip.getAvatar())
                .resize(250,250)
                .centerCrop()
                .into(holder.photo);



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
                if(trip.getIDdr().equals("-1")){
                    holder.stt.setText(context.getString(R.string.urgent));
                    holder.stt.setTextColor(context.getResources().getColor(R.color.red));
                    //holder.relativeLayout.setBackground(context.getResources().getDrawable(R.drawable.background_red_textview));

                }else {
                    holder.stt.setText(context.getString(R.string.dang_tim));
                    holder.stt.setTextColor(context.getResources().getColor(R.color.colorBlack));
                    holder.relativeLayout.setBackground(context.getResources().getDrawable(R.drawable.background_textview_orange));

                }


                break;
            case "received":
                if (trip.getNMessenger().equals("-1")){
                    holder.stt.setText(context.getString(R.string.dang_cho));
                    holder.stt.setTextColor(context.getResources().getColor(R.color.cyan));
                    //holder.relativeLayout.setBackground(context.getResources().getDrawable(R.drawable.background_cyan_textview));
                } else{
                    holder.stt.setText(context.getString(R.string.dang_tim));
                    holder.stt.setTextColor(context.getResources().getColor(R.color.colorBlack));
                    holder.relativeLayout.setBackground(context.getResources().getDrawable(R.drawable.background_textview_orange));

                }
                break;
            case "accepted":
                holder.stt.setText(context.getString(R.string.danhan));
                holder.stt.setTextColor(context.getResources().getColor(R.color.green));
                //holder.relativeLayout.setBackground(context.getResources().getDrawable(R.drawable.background_green_textview));
                //holder.stt.setTextColor(Color.rgb(255,0,255));
                trip.setNMessenger("-1"); // an dong tin nhan
                break;
            case "acceptedbyother":
                holder.stt.setText(context.getString(R.string.da_co_nguoi_nhan));
                holder.stt.setTextColor(context.getResources().getColor(R.color.gray));
                //holder.relativeLayout.setBackground(context.getResources().getDrawable(R.drawable.background_gray_textview));
                trip.setNMessenger("-1"); // an dong tin nhan
                break;

        }

        switch (trip.getNMessenger()){
            case "-2":
                holder.message.setText("");
                holder.message.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                break;
            case "-1":
                holder.message.setTextSize(16);
                holder.message.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
                holder.message.setText(trip.getName());
                holder.imagesms.setVisibility(View.INVISIBLE);
                break;
            case "0":
                holder.message.setText("0");
                holder.message.setTextColor(context.getResources().getColor(R.color.colorSecondaryText));
                holder.imagesms.setImageResource(R.drawable.ic_chat_grey);
                break;
            default:

                holder.message.setText(trip.getNMessenger());
                holder.message.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                holder.imagesms.setImageResource(R.drawable.ic_chat_orange);
                break; }
    }

    @Override
    public int getItemCount() {
        return TripList == null ? 0 : TripList.size();
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder {

        private TextView date, persons, prix, depart, destination, message, stt;
        private ImageView photo, imagesms;
        private RelativeLayout relativeLayout;


        RecyclerViewHolder(View itemView) {
            super(itemView);
            date         = itemView.findViewById(R.id.date);
            persons      = itemView.findViewById(R.id.persons);
            prix         = itemView.findViewById(R.id.prix);
            depart       = itemView.findViewById(R.id.depart);
            destination  = itemView.findViewById(R.id.destination);
            message      = itemView.findViewById(R.id.message);
            stt          = itemView.findViewById(R.id.stt);
            photo        = itemView.findViewById(R.id.avatar);
            imagesms     = itemView.findViewById(R.id.imagesms);
            relativeLayout = itemView.findViewById(R.id.relative);


        }
    }


}
