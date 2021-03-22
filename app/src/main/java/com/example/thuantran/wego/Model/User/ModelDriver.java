package com.example.thuantran.wego.Model.User;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.thuantran.wego.DataAccess.Constant;
import com.example.thuantran.wego.Interface.User.Driver;
import com.example.thuantran.wego.Object.PassengerTrip;
import com.example.thuantran.wego.Object.User;
import com.example.thuantran.wego.R;
import com.example.thuantran.wego.Object.DriverTrip;
import com.example.thuantran.wego.Tools.Calcul;
import com.example.thuantran.wego.Tools.Helper;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.ArrayList;

public class ModelDriver {

    private Driver.Presenter callback;
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    private static final String TAG = "ModelDriver";



    public ModelDriver( Driver.Presenter callback){
        this.callback = callback;
    }


    public void handleGetMultiDriverTrip(Context context, String userID){

        Query mData = mDatabase.child(Constant.TRIP_DRIVER).orderByChild("userID").equalTo(userID);

        mData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                new TaskGetMultiDriverTrip().execute(dataSnapshot);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onGetMultiTripDriverFail(context.getString(R.string.notrip));
            }
        });
    }




    public void handleGetReceivedTrip(User user){

        Query mData = mDatabase.child(Constant.mRELATION).orderByChild("idDr").equalTo(user.getUserID());
        float svp = Calcul.svp(Float.valueOf(user.getReview()),Integer.valueOf(user.getNReview()));

        mData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                new TaskGetReceivedTrip(svp).execute(dataSnapshot);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }




    public void handleGetMultiPaTrip( String userID ,ArrayList<String[]> receivedList, LatLng currentPoint, float radius){



        ArrayList<String> trIDs     = new ArrayList<>();
        GeoFire geoFire   = new GeoFire(mDatabase.child(Constant.GEOFIRE_LOCATION_TRIP_PASSENGER));
        GeoQuery mGeoQuery = geoFire.queryAtLocation(new GeoLocation(currentPoint.getLatitude(),currentPoint.getLongitude()),radius);

        mGeoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {

                if (trIDs.size() < 50){
                    trIDs.add(key);
                    getDataTrip(userID,trIDs,receivedList);
                }
            }

            @Override
            public void onKeyExited(String key) {

                trIDs.remove(key);
                getDataTrip(userID,trIDs,receivedList);
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {

                if (trIDs.size()==0){
                    getDataTrip(userID,trIDs,receivedList);
                }

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });


    }



    private void getDataTrip( String userID , ArrayList<String> trIDs , ArrayList<String[]> receivedList){


        if (trIDs.size() == 0){
            callback.onGetMultiTripPaSuccess(null);
        }else{

            ArrayList<String> recycle = new ArrayList<>();
            ArrayList<PassengerTrip> Patrips = new ArrayList<>();

            for (String key:trIDs){

                Query mData = mDatabase.child(Constant.TRIP_PASSENGER).orderByKey().equalTo(key);
                mData.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot ds) {


                        new TaskGetDataTrip(key,userID,trIDs,receivedList,recycle, Patrips).execute(ds);


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

            }




        }


    }



    private class TaskGetDataTrip extends AsyncTask<DataSnapshot,Void,Void>{


        private String key;
        private String userID;
        private ArrayList<String>  trIDs;
        private ArrayList<String[]>  receivedList;
        private ArrayList<String> recycle;
        private ArrayList<PassengerTrip> Patrips;


        TaskGetDataTrip(String key, String userID, ArrayList<String> trIDs,ArrayList<String[]> receivedList,ArrayList<String> recycle,ArrayList<PassengerTrip> Patrips ){
            this.key = key;
            this.userID = userID;
            this.trIDs = trIDs;
            this.receivedList = receivedList;
            this.recycle = recycle;
            this.Patrips = Patrips;
        }


        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(Void aVoid) {

            if (trIDs.size()== Patrips.size() + recycle.size()){
                callback.onGetMultiTripPaSuccess(Patrips); }

        }

        @Override
        protected Void doInBackground(DataSnapshot... dataSnapshots) {

            boolean flag = true;
            PassengerTrip trip = dataSnapshots[0].child(key).getValue(PassengerTrip.class);
            if (trip != null) {
                trip.setID(key);

                // Không lấy các chuyến đi của người dùng hiện tại.
                if ( trip.getUserID().equals(userID)){
                    flag = false;
                    recycle.add(key);

                }else{
                    // Không lấy các chuyến đi đã được đánh dấu xóa (stt = -2)
                    // Không lấy các chuyến đi đã hết hạn (ngoại trừ các chuyến đi ngay bây giờ có iddr = -1)
                    if (Helper.isExpire(trip.getDate(),trip.getTime()) || trip.getStt().equals("removed") ) {
                        if (!(trip.getIDdr().equals("-1") || trip.getIDdr().equals(userID))){
                            flag = false;
                            recycle.add(key);
                        }
                    }
                }


                if (flag){

                    String stt = "pending";
                    if (!trip.getNMessenger().equals("0")) {

                        if (trip.getIDdr().equals(userID)) { // Chuyến đi được nhận bởi người dùng hiện tại.
                            stt = "accepted";
                        } else if (!trip.getIDdr().equals("0") && !trip.getIDdr().equals("-1")) {// Chuyến đi được nhận bởi người dùng khác
                            stt = "acceptedbyother";
                        } else {
                            if (receivedList != null){
                                for (int j = 0 ; j < receivedList.size(); j++){

                                    String[] received = receivedList.get(j);
                                    if(received[0].equals(trip.getID())){

                                        if (!received[1].equals("booknow")){
                                            stt = received[1];
                                        }else{
                                            stt = "received";
                                        }


                                        break;
                                    }
                                }
                            }
                        }
                        trip.setStt(stt);
                    }else {
                        trip.setStt(trip.getStt());
                    }

                    trip.setNMessenger("-1");
                    Patrips.add(trip);


                }


            }

            return null;
        }
    }


    private class TaskGetMultiDriverTrip extends AsyncTask<DataSnapshot,Void,Void>{

        private ArrayList<DriverTrip> Drtrips;

        @Override
        protected void onPreExecute() {
            Drtrips = new ArrayList<>();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            callback.onGetMultiTripDriverSuccess(Drtrips);
        }

        @Override
        protected Void doInBackground(DataSnapshot... dataSnapshots) {

            for (DataSnapshot ds : dataSnapshots[0].getChildren()){

                DriverTrip trip = ds.getValue(DriverTrip.class);
                if (trip != null) {
                    trip.setID(ds.getKey());
                    Drtrips.add(trip);
                }

            }

            return null;
        }
    }


    private class TaskGetReceivedTrip extends AsyncTask<DataSnapshot,Void,Void>{

        private float svp;
        private int refund;
        private ArrayList<String[]> receivedList;

        TaskGetReceivedTrip(float svp){
            this.svp = svp; }


        @Override
        protected void onPreExecute() {
            receivedList = new ArrayList<>();
            refund       = 0;
        }

        @Override
        protected void onPostExecute(Void aVoid) {


            if (refund>0){
                callback.onGetDetRefundSuccess(refund);}

            callback.onGetReceivedListSuccess(receivedList);

        }


        @Override
        protected Void doInBackground(DataSnapshot... dataSnapshots) {


            for (DataSnapshot ds : dataSnapshots[0].getChildren()){

                String[] received = new String[2];
                received[0] = ds.child("idPaTrip").getValue(String.class);
                received[1] = ds.child("stt").getValue(String.class);

                String date = ds.child("date").getValue(String.class);
                String time = ds.child("time").getValue(String.class);
                String cost = ds.child("cost").getValue(String.class);
                String stt  = ds.child("stt").getValue(String.class);

                if (date != null && time != null && cost != null && stt != null){

                    if(!Helper.isExpire(date,time)){

                        receivedList.add(received);
                    }else {

                        if (stt.equals("booknow") && !date.equals("01/01/2000")){
                            receivedList.add(received);
                        }else{

                            if (date.equals("01/01/2000")) {
                                cost   = cost.replace(",","");
                                refund = refund + Calcul.svf(Double.valueOf(cost),svp);
                            }
                            mDatabase.child(Constant.mRELATION).child(ds.getKey()).removeValue();
                        }


                    }
                }



            }

            return null;
        }
    }


}
