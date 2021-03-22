package com.example.thuantran.wego.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.thuantran.wego.Object.SMSContext;
import com.example.thuantran.wego.R;
import com.example.thuantran.wego.Tools.Helper;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.RecyclerViewHolder> {

    private Context context;
    private ArrayList<SMSContext> smsContexts;
    private String userID;
    private boolean isnotisms;

    public ChatAdapter(Context context, ArrayList<SMSContext> smsContexts, String userID, boolean isnotisms){
        this.context = context;
        this.smsContexts = smsContexts;
        this.userID = userID;
        this.isnotisms = isnotisms;
    }


    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;

        if (isnotisms){
            view = inflater.inflate(R.layout.layout_sms_noti, parent, false);
        }else{
            if (viewType==0){
                view = inflater.inflate(R.layout.layout_sms_sent, parent, false);
            }else {
                view = inflater.inflate(R.layout.layout_sms_received, parent, false);
            }
        }

        return new RecyclerViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {

        SMSContext sms = smsContexts.get(position);
        String userIDsms  = sms.getUserID();
        String avatarsms  = sms.getAvatar();
        String namesms    = sms.getName();
        String contextsms = sms.getContext();
        String timesms    = sms.getTime();
        String datesms    = sms.getDate();


        if (isnotisms){

            if (contextsms.length()> 20){
                contextsms = contextsms.substring(0,20) + "..."; }

            if (sms.getStatus().equals("seen")){
                holder.sms.setTypeface(holder.sms.getTypeface(), Typeface.NORMAL);
                holder.sms.setTextColor(context.getResources().getColor(R.color.colorSecondaryText)); }
            else{
                holder.sms.setTypeface(holder.sms.getTypeface(), Typeface.BOLD);
                holder.sms.setTextColor(context.getResources().getColor(R.color.colorBlack));}
        }

        holder.sms.setText(contextsms);

        long oneday   = 24 * 60 * 60 * 1000;
        long onehour  =      60 * 60 * 1000;
        long onemin   =           60 * 1000;

        long passedtime = Helper.TimeExpired(datesms,timesms);

        if (passedtime < onehour){
            int mins  = Math.round(passedtime/onemin) ;

            if (mins==0){
                holder.time.setText( context.getString(R.string.minute0));
            }else{
                holder.time.setText(mins + context.getString(R.string.minute1));
            }
        }else if (passedtime < oneday){
            int hours  = Math.round(passedtime/onehour) ;
            holder.time.setText(hours + context.getString(R.string.hour1));
        }else{
            holder.time.setText(datesms);
        }


        holder.name.setText(namesms);
        Picasso.get().load(avatarsms)
                .resize(250,250)
                .centerCrop()
                .into(holder.avatar);




    }

    @Override
    public int getItemCount() {
        return smsContexts == null ? 0: smsContexts.size();
    }

    @Override
    public int getItemViewType(int position) {

        SMSContext sms = smsContexts.get(position);

        if (sms.getUserID().equals(userID)){

            // If the current user is the sender of the message
            return 0;
        } else {
            // If some other user sent the message
            return 1;
        }
    }


    class RecyclerViewHolder extends RecyclerView.ViewHolder {

        private TextView name, sms, time;
        private ImageView avatar;

        RecyclerViewHolder(View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.avatarsms);
            name   = itemView.findViewById(R.id.namesms);
            time   = itemView.findViewById(R.id.timesms);
            sms    = itemView.findViewById(R.id.contextsms);
        }
    }




}
