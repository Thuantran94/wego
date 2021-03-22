package com.example.thuantran.wego.DataAccess;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.thuantran.wego.Object.DriverTrip;
import com.example.thuantran.wego.Object.Favorite;
import com.example.thuantran.wego.Object.PassengerTrip;
import com.example.thuantran.wego.Object.User;
import com.example.thuantran.wego.Tools.Helper;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.HashMap;
import java.util.Map;

public class AccessFireBase {

    private static DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private static GeoFire GEOFIRE_TRIP_PASSENGER = new GeoFire(mDatabase.child(Constant.GEOFIRE_LOCATION_TRIP_PASSENGER));
    private static GeoFire GEOFIRE_TRIP_DRIVER    = new GeoFire(mDatabase.child(Constant.GEOFIRE_LOCATION_TRIP_DRIVER));



    public static void updateToken(String userID, String token){

        new Thread(() -> {
            Map<String, Object> map = new HashMap<>();
            map.put("token",token);
            mDatabase.child(Constant.USER).child(userID).child(Constant.PROFILE).updateChildren(map);
        }).start();

    }

    public static void updateLocation(String userID, double lat, double lng, IAccessFireBase.iUpdateLocation callback){

        new Thread(() -> {
            Map<String, Object> map = new HashMap<>();
            map.put("lat",String.valueOf(lat));
            map.put("lng",String.valueOf(lng));
            mDatabase.child(Constant.USER).child(userID).child(Constant.PROFILE).updateChildren(map, (databaseError, databaseReference) -> {
                if (databaseError == null){ callback.onSuccess(); }
                else{ callback.onFailed(); }
            });
        }).start();

    }

    public static void updatePoints(String userID, String point, IAccessFireBase.iUpdatePoint callback){

        new Thread(() -> {
            Map<String, Object> map = new HashMap<>();
            map.put("points",point);
            mDatabase.child(Constant.USER).child(userID).child(Constant.PROFILE).updateChildren(map, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    if (databaseError == null){ callback.onSuccess(); }
                    else{ callback.onFailed(); }
                }
            });
        }).start();
    }

    public static void updateStatus(String userID, String status){

        new Thread(() -> {
            Map<String, Object> map = new HashMap<>();
            map.put("status",status);
            mDatabase.child(Constant.USER).child(userID).child(Constant.PROFILE).updateChildren(map);
        }).start();

    }

    public static void updateAvatar(String userID, String avatar, IAccessFireBase.iUpdateAvatar callback){

        new Thread(() -> {
            Map<String, Object> map = new HashMap<>();
            map.put("avatar",avatar);
            mDatabase.child(Constant.USER).child(userID).child(Constant.PROFILE).updateChildren(map, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    if (databaseError ==null){ callback.onSuccess(); }
                    else { callback.onFailed(); }
                }
            });
        }).start();
    }

    public static void updateReview(String userID, String rate, int nreview){

        new Thread(() -> {
            Map<String, Object> map = new HashMap<>();
            map.put("review",rate);
            map.put("nreview",String.valueOf(nreview));
            mDatabase.child(Constant.USER).child(userID).child(Constant.PROFILE).updateChildren(map);
        }).start();
    }

    public static void updateModel(String userID, String str){
        new Thread(() -> {
            Map<String, Object> map = new HashMap<>();
            map.put("model",str);
            mDatabase.child(Constant.USER).child(userID).child(Constant.PROFILE).updateChildren(map);
        }).start();
    }

    public static void updateProfile(String userID, String newname, String image, IAccessFireBase.iUpdateProfile callback){

        new Thread(() -> {

            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            Map<String, Object> map = new HashMap<>();
            map.put("name",newname);
            map.put("avatar",image);

            mDatabase.child(Constant.USER).child(userID).child(Constant.PROFILE).updateChildren(map, (databaseError, databaseReference) -> {
                if (databaseError ==null){ callback.onSuccess(); }
                else { callback.onFailed(); }
            });

        }).start();
    }

    public static void updateProfileCar(String userID, String TypeCar, String NameCar, String ColorCar, String YearCar, String photo, IAccessFireBase.iUpdateProfileCar callback){


        new Thread(() -> {
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            Map<String, Object> map = new HashMap<>();
            map.put("typeCar",TypeCar);
            map.put("nameCar",NameCar);
            map.put("colorCar",ColorCar);
            map.put("yearCar",YearCar);
            map.put("photoCar",photo);

            mDatabase.child(Constant.USER).child(userID).child(Constant.MCAR).updateChildren(map, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    if (databaseError ==null){ callback.onSuccess(); }
                    else { callback.onFailed(); }
                }
            });

        }).start();

    }


    public static void updateMessenger(String trID, IAccessFireBase.iUpdateMessenger callback){

        new Thread(() -> {
            Query mData     = mDatabase.child(Constant.TRIP_PASSENGER).child(trID).orderByChild("nmessenger");
            mData.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    PassengerTrip passengerTrip = dataSnapshot.getValue(PassengerTrip.class);
                    Map<String, Object> map = new HashMap<>();
                    if (passengerTrip != null) {
                        map.put("nmessenger",String.valueOf(Integer.valueOf(passengerTrip.getNMessenger())+1));
                    }
                    mDatabase.child(Constant.TRIP_PASSENGER).child(trID).updateChildren(map, (databaseError, databaseReference) -> {
                        if (databaseError ==null){ callback.onSuccess(); }
                        else { callback.onFailed(); }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    callback.onFailed();

                }
            });

        }).start();
    }

    public static void updateTotalTrip(String userID, String ntrip){

        new Thread(() -> {

            Map<String, Object> map = new HashMap<>();
            map.put("ntriptotal",ntrip);
            mDatabase.child(Constant.USER).child(userID).child(Constant.PROFILE).updateChildren(map);
        }).start();
    }

    public static void updateReviewTrip(String trID, String typeUser, IAccessFireBase.iUpdateReview callback ){

        new Thread(() -> {
            Map<String, Object> map = new HashMap<>();
            if (typeUser.equals("Pa")){ map.put("reviewPa2Dr","completed"); }
            else{ map.put("reviewDr2Pa","completed"); }
            mDatabase.child(Constant.TRIP_PASSENGER).child(trID).updateChildren(map, (databaseError, databaseReference) -> {
                if (databaseError ==null){ callback.onSuccess(); }
                else { callback.onFailed(); }
            });

        }).start();
    }

    public static void updateRelationStt(String trID, String myDrID, IAccessFireBase.iUpdateRelationStt callback){

        new Thread(() -> mDatabase.child(Constant.mRELATION).orderByChild("idPaTrip").equalTo(trID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    String key = ds.getKey();
                    String idDr = ds.child("idDr").getValue(String.class);

                    if (idDr != null && idDr.equals(myDrID)) {

                        if (key != null) {
                            mDatabase.child(Constant.mRELATION).child(key).child("stt").setValue("accepted", (databaseError, databaseReference) -> {
                                if (databaseError ==null){ callback.onSuccess(); }
                                else { callback.onFailed(); }
                            });
                        }
                        updateRelationDate(trID);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        })).start();
    }


    public static void updateReceivedTrip(String trID, String avatar, String name, String time, String cost, String iddr, IAccessFireBase.iUpdateReceivedTrip callback){

        new Thread(() -> {

            Map<String, Object> mapTRIP = new HashMap<>();
            mapTRIP.put("avatar",avatar);
            mapTRIP.put("name",name);
            mapTRIP.put("time",time);
            mapTRIP.put("cost",cost);
            mapTRIP.put("stt","accepted");
            mapTRIP.put("iddr",iddr);
            mDatabase.child(Constant.TRIP_PASSENGER).child(trID).updateChildren(mapTRIP, (databaseError, databaseReference) -> {
                if (databaseError ==null){ callback.onSuccess(); }
                else { callback.onFailed(); }
            });
        }).start();
    }


    public static void updateLastSms(String Sender, String avatar, String name, String sms, String datesms, String timesms, PassengerTrip passengerTrip, IAccessFireBase.iUpdateLastSms callback){

        String  Receiver;

        if(passengerTrip.getUserID().equals(Sender)){

            Receiver = passengerTrip.getIDdr();
        }else{
            Receiver = passengerTrip.getUserID();
        }

        String trID = passengerTrip.getID();

        new Thread(() -> {

            Map<String, Object> map = new HashMap<>();
            map.put("userID",Sender);
            map.put("avatar",avatar);
            map.put("name",name);
            map.put("context",sms);
            map.put("status","");
            map.put("date",datesms);
            map.put("time",timesms);
            map.put("trID",trID);
            mDatabase.child(Constant.USER).child(Receiver).child(Constant.LAST_SMS).child(trID).updateChildren(map, (databaseError, databaseReference) -> {

                if (databaseError ==null){
                    Map<String, Object> map1 = new HashMap<>();
                    map1.put("userID",Sender);
                    map1.put("avatar",avatar);
                    map1.put("name",name);
                    map1.put("context",sms);
                    map1.put("status","seen");
                    map1.put("date",datesms);
                    map1.put("time",timesms);
                    map1.put("trID",trID);
                    mDatabase.child(Constant.USER).child(Sender).child(Constant.LAST_SMS).child(trID).updateChildren(map1, (databaseError1, databaseReference1) -> {
                        if (databaseError1 ==null){ callback.onSuccess(); }
                        else { callback.onFailed(); }
                    });

                }else {
                    callback.onFailed();
                }
            });
        }).start();
    }

    public static void updateStatusSeen(String userID, String trID, String status, IAccessFireBase.iUpdateStatusSeen callback){

        new Thread(() -> {

            Map<String, Object> map = new HashMap<>();
            map.put("status",status);
            mDatabase.child(Constant.USER).child(userID).child(Constant.LAST_SMS).child(trID).updateChildren(map, (databaseError, databaseReference) -> {
                if (databaseError ==null){ callback.onSuccess(); }
                else { callback.onFailed(); }
            });
        }).start();
    }


    public static void update_invitedby(String userID, String str){

        new Thread(() -> {

            Map<String, Object> map = new HashMap<>();
            map.put("invitedby",str);
            mDatabase.child(Constant.USER).child(userID).child(Constant.PROFILE).updateChildren(map);
        }).start();
    }

    public static void updateStatusTrip(String trID, String stt){

        new Thread(() -> {

            Map<String, Object> map = new HashMap<>();
            map.put("stt",stt);
            mDatabase.child(Constant.TRIP_PASSENGER).child(trID).updateChildren(map);

        }).start();
    }

    public static void updateHomeAddress(String userID, double lat, double lng){

        new Thread(() -> mDatabase.child(Constant.USER).child(userID).child(Constant.ADDRESS).child("Home").setValue(new Favorite(lat,lng))).start();

    }

    public static void updateWorkAddress(String userID, double lat, double lng){

        new Thread(() -> mDatabase.child(Constant.USER).child(userID).child(Constant.ADDRESS).child("Work").setValue(new Favorite(lat,lng))).start();

    }

    public static void updateFavoriteAddress(String userID, double lat, double lng){

        new Thread(() -> mDatabase.child(Constant.USER).child(userID).child(Constant.ADDRESS).child("Favorite1").setValue(new Favorite(lat,lng))).start();

    }

    private static void updateRelationDate(String trID){

        new Thread(() -> {

            // set thoi gian cho chuyen di ve 01/01/2000  => chuyen di het han
            mDatabase.child(Constant.mRELATION).orderByChild("idPaTrip").equalTo(trID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds: dataSnapshot.getChildren()) {
                        String key = ds.getKey();
                        String stt = ds.child("stt").getValue(String.class);

                        if (key != null && stt != null && !stt.equals("accepted")) {
                            mDatabase.child(Constant.mRELATION).child(key).child("date").setValue("01/01/2000");
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }).start();
    }



    public static void addTrip(String name, String avatar, String userID, String date, String time, String depart, String destination, int nPerson,
                               String duration, String distance , String cost, String typeRequest , String drID, String onCreated,
                               IAccessFireBase.iAddTripPassenger callback){

        if(date !=null && time !=null && depart !=null && destination !=null &&
                duration !=null && distance !=null && nPerson>0 && cost !=null){

            new Thread(() -> {
                String nMessenger      = "0";
                String stt             = "pending";
                String reviewPa2Dr     = "";
                String reviewDr2Pa     = "";

                DatabaseReference keyRef = mDatabase.child(Constant.TRIP_PASSENGER).push();
                String trID = keyRef.getKey();



                PassengerTrip trip = new PassengerTrip(name, avatar, userID, date, time, depart, destination,
                        typeRequest, String.valueOf(nPerson), distance, duration, cost,
                        nMessenger, stt, drID, reviewPa2Dr, reviewDr2Pa, onCreated);

                keyRef.setValue(trip, (databaseError, databaseReference) -> {
                    if (databaseError == null){


                        // add depart point to geofire
                        LatLng DepartPoint = Helper.fromStringToLatLng(depart);
                        GEOFIRE_TRIP_PASSENGER.setLocation(trID, new GeoLocation(DepartPoint.getLatitude(), DepartPoint.getLongitude()), (key, error) -> {
                            if (error == null){

                                if (drID.equals("-1")){

                                    mDatabase.child(Constant.TRIP_PASSENGER_BOOKNOW).child(trID).setValue(trip, (databaseError1, databaseReference1) -> {
                                        if (databaseError1 == null){ callback.onSuccess(trID);
                                        } else { callback.onFailed(); } });

                                }else{
                                    callback.onSuccess(trID);
                                }



                            } else { callback.onFailed(); }
                        });
                    }else {
                        callback.onFailed();
                    }
                });



            }).start(); }
        else{
            callback.onFailed(); }
    }

    public static void addTrip(String userID, String avatar, String date, String time, String depart, String destination,
                               int nSeat, String typeRequest, int repeat, String onCreated, IAccessFireBase.iAddTripDriver callback){

        if(date !=null && time !=null && depart !=null && destination !=null){

            new Thread(() -> {
                DatabaseReference keyRef = mDatabase.child(Constant.TRIP_DRIVER).push();
                String trID = keyRef.getKey();
                keyRef.setValue(new DriverTrip(userID, avatar, date, time, depart, destination,
                        String.valueOf(nSeat), typeRequest, String.valueOf(repeat), onCreated), (databaseError, databaseReference) -> {
                    if (databaseError == null){
                        LatLng DepartPoint = Helper.fromStringToLatLng(depart);
                        GEOFIRE_TRIP_DRIVER.setLocation(trID, new GeoLocation(DepartPoint.getLatitude(), DepartPoint.getLongitude()), new GeoFire.CompletionListener() {
                            @Override
                            public void onComplete(String key, DatabaseError error) {
                                if (error == null){ callback.onSuccess();
                                } else { callback.onFailed(); }
                            }
                        });
                    }else {
                        callback.onFailed();
                    }
                });



            }).start(); }
    }

    public static void addRelation(String trID, String userID, String pdate, String ftime, String fcost, String iddr, IAccessFireBase.iAddRelation callback){

        new Thread(() -> {
            Map<String, Object> map = new HashMap<>();
            map.put("idPaTrip",trID);
            map.put("idDr",userID);
            map.put("date",pdate);
            map.put("time",ftime);
            map.put("cost",fcost);

            if (iddr.equals("-1")){ map.put("stt","booknow"); }
            else { map.put("stt","received"); }

            mDatabase.child(Constant.mRELATION).push().setValue(map, (databaseError, databaseReference) -> {
                if (databaseError == null){ callback.onSuccess(); }
                else{ callback.onFailed(); }
            });
        }).start();
    }

    public static void addSms(User user, String sms, String datesms , String timesms, PassengerTrip passengerTrip, IAccessFireBase.iAddSms callback){

        String trID = passengerTrip.getID();

        new Thread(() -> {
            Map<String, Object> map = new HashMap<>();

            map.put("userID",user.getUserID());
            map.put("avatar",user.getAvatar());
            map.put("name",user.getName());
            map.put("context",sms);
            map.put("date",datesms);
            map.put("time",timesms);
            map.put("trID",trID);

            if (!sms.equals("")){
                mDatabase.child(Constant.SMS).child(trID).push().setValue(map, (databaseError, databaseReference) -> {
                    if (databaseError == null){ callback.onSuccess(); }
                    else{ callback.onFailed(); }
                });
            }

        }).start();


    }

    public static void addReview(String userID, String trID , String name, String avatar, String sms, float rate, String timestamp, IAccessFireBase.iAddReview callback){

        new Thread(() -> {
            Map<String, Object> map = new HashMap<>();
            map.put("name",name);
            map.put("avatar",avatar);
            map.put("context",sms);
            map.put("rate",String.valueOf(rate));
            map.put("timestamp",timestamp);
            mDatabase.child(Constant.USER).child(userID).child(Constant.REVIEW).child(trID).setValue(map, (databaseError, databaseReference) -> {
                if (databaseError == null){ callback.onSuccess(); }
                else{ callback.onFailed(); }
            });
        }).start();
    }

    public static void addReport(String userID, String trID , String name, String avatar, String sms, String timestamp, IAccessFireBase.iAddReport callback){

        new Thread(() -> {
            Map<String, Object> map = new HashMap<>();
            map.put("name",name);
            map.put("avatar",avatar);
            map.put("context",sms);
            map.put("rate","0");
            map.put("timestamp",timestamp);
            mDatabase.child(Constant.USER).child(userID).child(Constant.REPORT).child(trID).setValue(map, (databaseError, databaseReference) -> {
                if (databaseError == null){ callback.onSuccess(); }
                else{ callback.onFailed(); }
            });
        }).start();
    }

    public static void add_points(String userID, int point_refund){

        new Thread(() -> {
            Query mData     = mDatabase.child(Constant.USER).child(userID).child(Constant.PROFILE).orderByChild("points");
            mData.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);

                    if (user != null) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("points",String.valueOf(Integer.valueOf(user.getPoints())+point_refund));
                        mDatabase.child(Constant.USER).child(userID).child(Constant.PROFILE).updateChildren(map);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }).start();
    }


    public static void removeTripWithOutRefund(String trID, IAccessFireBase.iRemoveTripWithOutRefund callback){

        mDatabase.child(Constant.GEOFIRE_LOCATION_TRIP_PASSENGER).child(trID).removeValue((databaseError, databaseReference) -> {
            if (databaseError == null){
                mDatabase.child(Constant.TRIP_PASSENGER).child(trID).removeValue((databaseError1, databaseReference1) -> {
                    if (databaseError1 == null){
                        mDatabase.child(Constant.SMS).child(trID).removeValue((databaseError11, databaseReference11) -> {
                            callback.onSuccess();
                            remove_relation(trID,"01/01/1000"); // remove without a refund
                        });
                    }
                    else{ callback.onFailed(); }
                });
                callback.onSuccess();
            }
            else{ callback.onFailed(); }
        });

    }


    public static void removeTripWithRefund(String trID, IAccessFireBase.iRemoveTripWithRefund callback){

        mDatabase.child(Constant.GEOFIRE_LOCATION_TRIP_PASSENGER).child(trID).removeValue((databaseError, databaseReference) -> {
            if (databaseError == null){
                mDatabase.child(Constant.TRIP_PASSENGER).child(trID).removeValue((databaseError1, databaseReference1) -> {
                    if (databaseError1 == null){
                        mDatabase.child(Constant.SMS).child(trID).removeValue((databaseError11, databaseReference11) -> {
                            callback.onSuccess();
                            remove_relation(trID,"01/01/2000"); // remove with a refund
                        });
                    }
                    else{ callback.onFailed(); }
                });
                callback.onSuccess();
            }
            else{ callback.onFailed(); }
        });

    }



    public static void removeBookNow(String trID, IAccessFireBase.iRemoveTripBookNow callback){
        mDatabase.child(Constant.TRIP_PASSENGER_BOOKNOW).child(trID).removeValue((databaseError, databaseReference) -> {
            if (databaseError == null){ callback.onSuccess();
            }else{ callback.onFailed(); }
        });
    }



    public static void checkPromoCode(String userID, String code, IAccessFireBase.iCheckPromoCode callback ){

        Query mdata = mDatabase.child(Constant.USER).child(userID).child(Constant.PROMOCODE);
        mdata.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String status = dataSnapshot.child(code).getValue(String.class);

                if (status == null){
                    Map<String, Object> map = new HashMap<>();
                    map.put(code,"actived");
                    mDatabase.child(Constant.USER).child(userID).child(Constant.PROMOCODE).setValue(map, (databaseError, databaseReference) -> {
                        if (databaseError == null){
                            callback.onSuccess();
                        }else{
                            callback.onFailed();
                        }
                    });

                }else{ callback.onFailed();}

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    public static void removeTripDriver(String trID){
        new Thread(() -> {
            mDatabase.child(Constant.GEOFIRE_LOCATION_TRIP_DRIVER).child(trID).removeValue();
            mDatabase.child(Constant.TRIP_DRIVER).child(trID).removeValue();

        });

    }

    public static void removeLastSms(String userID, String trID){

        new Thread(() -> mDatabase.child(Constant.USER).child(userID).child(Constant.LAST_SMS).child(trID).removeValue()).start();
    }

    private static void remove_relation(String trID, String flag_date){

        new Thread(() -> {

            // set thoi gian cho chuyen di ve 01/01/2000  => chuyen di het han
            mDatabase.child(Constant.mRELATION).orderByChild("idPaTrip").equalTo(trID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds: dataSnapshot.getChildren()) {
                        String key = ds.getKey();

                        if (key != null) {
                            Map<String, Object> map = new HashMap<>();
                            map.put("date",flag_date);
                            mDatabase.child(Constant.mRELATION).child(key).updateChildren(map);
                        }

                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }).start();
    }



    public static void addReviewApp(String userID, String sms, float fgiaodien, float ftrainghiem, float ftinhnang, float fnhucau, IAccessFireBase.iAddFeedBack callback){

        new Thread(() -> {
            Map<String, Object> map = new HashMap<>();
            map.put("nhanxet",sms);
            map.put("ui",String.valueOf(fgiaodien));
            map.put("ux",String.valueOf(ftrainghiem));
            map.put("function",String.valueOf(ftinhnang));
            map.put("useful",String.valueOf(fnhucau));


            mDatabase.child("FEEDBACK").child(userID).updateChildren(map, (databaseError, databaseReference) -> {
                if (databaseError == null){
                    callback.onSuccess();
                }else{
                    callback.onFailed();
                }
            });

        }).start();
    }


}
