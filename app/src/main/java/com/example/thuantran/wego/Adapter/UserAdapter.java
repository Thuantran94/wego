package com.example.thuantran.wego.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thuantran.wego.R;
import com.example.thuantran.wego.Object.UserSelected;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.RecyclerViewHolder> {

    private Context context;
    private List<UserSelected> userList;
    private CountDownTimer cTimer = null;

    public UserAdapter(Context context, List<UserSelected> userList){
        this.context = context;
        this.userList = userList;
    }

    public void cancelTimer() {
        if (cTimer !=null)
            cTimer.cancel();
    }


    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.layout_driver_info, parent, false);
        return new RecyclerViewHolder(view);
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {

        UserSelected user = userList.get(position);

        holder.date.setText(user.getDate()+context.getString(R.string.attime)+user.getTime());
        holder.name.setText(user.getName());
        holder.cost.setText(user.getCost()+context.getString(R.string.VND));
        holder.infoCar.setText(user.getInfoCar());

        holder.review.setText(String.valueOf(user.getReview()));
        holder.nreview.setText(String.valueOf(user.getNreview()));

        if (user.getGender().equals("nam")){
            holder.gender.setImageResource(R.drawable.ic_masculine);}
        else {
            holder.gender.setImageResource(R.drawable.ic_female); }


        Picasso.get().load(user.getAvatar())
                .resize(250,250)
                .centerCrop()
                .into(holder.avatar);
        Picasso.get().load(user.getPhotoCar())
                .resize(250,250)
                .centerCrop()
                .into(holder.photoCar);



        String EVENT_DATE_TIME =user.getDate()+" "+user.getTime()+":00";
        String DATE_FORMAT = "dd/MM/yyyy HH:mm:ss";


        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        try {
            final Date event_date   = dateFormat.parse(EVENT_DATE_TIME);
            final Date current_date = new Date();

            long ftime = 1800000;
            long  diff = event_date.getTime() - (current_date.getTime() + ftime);

            /*
            cTimer = new CountDownTimer(diff, 1000) {
                @SuppressLint("DefaultLocale")
                public void onTick(long diff) {
                    long Days = diff / (24 * 60 * 60 * 1000);
                    long Hours = diff / (60 * 60 * 1000) % 24;
                    long Minutes = diff / (60 * 1000) % 60;
                    long Seconds = diff / 1000 % 60;
                    holder.timeremain.setText(String.format("%02d", Days)+context.getString(R.string.day)+String.format("%02d", Hours)+"h "+String.format("%02d", Minutes) +"m ");

                }
                public void onFinish() {
                    holder.timeremain.setText(context.getString(R.string.cddkt));
                }
            }.start();

            */

            if (diff>0){
                long Days = diff / (24 * 60 * 60 * 1000);
                long Hours = diff / (60 * 60 * 1000) % 24;
                long Minutes = diff / (60 * 1000) % 60;
                long Seconds = diff / 1000 % 60;
                holder.timeremain.setText(String.format("%01d", Days)+context.getString(R.string.day)+String.format("%02d", Hours)+"h "+String.format("%02d", Minutes) +"m ");

            }else{
                holder.timeremain.setText(context.getString(R.string.cddkt));
            }



        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return userList == null ? 0: userList.size();
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder {
        private TextView name, date, time, cost, infoCar,timeremain, nreview;
        private TextView review;
        private ImageView avatar, photoCar, gender;

        RecyclerViewHolder(View itemView) {
            super(itemView);
            name         = itemView.findViewById(R.id.dten);
            date         = itemView.findViewById(R.id.dinfor);
            cost         = itemView.findViewById(R.id.dprix);
            infoCar      = itemView.findViewById(R.id.dxe);
            review       = itemView.findViewById(R.id.npdanhgia);
            nreview      = itemView.findViewById(R.id.ndanhgia);
            timeremain   = itemView.findViewById(R.id.timeremain);
            avatar       = itemView.findViewById(R.id.davatar);
            gender       = itemView.findViewById(R.id.gender);
            photoCar     = itemView.findViewById(R.id.photocar);
        }
    }

}
