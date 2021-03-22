package com.example.thuantran.wego.Tools;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;

import com.example.thuantran.wego.DataAccess.AccessFireBase;
import com.example.thuantran.wego.DataAccess.Constant;
import com.example.thuantran.wego.DataAccess.IAccessFireBase;
import com.example.thuantran.wego.Object.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class TestData {




    //TestData.createUser(12000);
    //TestData.createPaTrip(10000);
    //TestData.createDrTrip(10000);

    //DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    //mDatabase.child("TRIP_PASSENGER").child("-Lo6RGABJkyVkp04KZWH").removeValue();
    //mDatabase.child("TRIP_PASSENGER").child("-Lo6Vd9-d8ldklXiGU_H").removeValue();
    //mDatabase.child(Constant.GEOFIRE_LOCATION_TRIP_PASSENGER).child("-Lo6RGABJkyVkp04KZWH").removeValue();
    //mDatabase.child(Constant.GEOFIRE_LOCATION_TRIP_PASSENGER).child("-Lo6Vd9-d8ldklXiGU_H").removeValue();
    //mDatabase.child("USER").removeValue();



    @SuppressLint("LogNotTimber")
    public static void  createUser(int n){

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        String onCreated = Helper.getTimeStamp();
        String review    = "0";
        String nreview   = "0";
        String ntriptotal= "0";
        String invitedby = "";
        String points    = "2000";
        String photocar  = "https://firebasestorage.googleapis.com/v0/b/wegodatabase.appspot.com/o/images%2Fdefault_photocar.png?alt=media&token=f5f5474a-b5d5-4313-aa7f-2199cf9a3b17";
        String gender    = "0";

        String[] listAvatar = new String[5];
        listAvatar[0] =  "https://firebasestorage.googleapis.com/v0/b/wegodatabase.appspot.com/o/images%2FYIuo4o7gl6UYHphjwo0xVDnvUUk2_avatar.jpeg?alt=media&token=00a39f64-e3f8-45fa-b023-386ea8a9efd1";
        listAvatar[1] =  "https://firebasestorage.googleapis.com/v0/b/wegodatabase.appspot.com/o/images%2FUGKBghdv5rYdPSorI4sWdH5ZT1w2_avatar.jpeg?alt=media&token=e329cb17-d1a7-4658-88f1-10301cd5279f";
        listAvatar[2] =  "https://firebasestorage.googleapis.com/v0/b/wegodatabase.appspot.com/o/images%2Foi0x30JyAie4dUJiwRA43ujJJch2_avatar.jpeg?alt=media&token=5cd55bb3-40a1-4637-a4e6-d11c3cd89716";
        listAvatar[3] =  "https://firebasestorage.googleapis.com/v0/b/wegodatabase.appspot.com/o/images%2FxyIycGKGVET1jCIMmCVBps3SEgw2_avatar.jpeg?alt=media&token=8fa7fa38-af4a-42f0-8127-6c586abe2f44";
        listAvatar[4] =  "https://firebasestorage.googleapis.com/v0/b/wegodatabase.appspot.com/o/images%2FfiYa2sjm6ofCEa8b4g5kNnCPGe33_avatar.jpeg?alt=media&token=1906b230-1ce2-440d-80e4-592fbd6a13f7";



        String[] listnames = new String[5];
        listnames[0] =  "Mai Thi Tran";
        listnames[1] =  "Daisy Nguyen";
        listnames[2] =  "Davik Thanh";
        listnames[3] =  "Kenny Tran";
        listnames[4] =  "Vo Mai Thao";
        listnames[5] =  "Ngoc Trinh";
        listnames[6] =  "Trang Diamond";
        listnames[7] =  "Vo Ngoc Tram";
        listnames[8] =  "Phuc NNguyen";
        listnames[9] =  "Hong Anh";
        listnames[10]=  "Thuy Linh";


        for (int i = 0; i<n;i++){


            DatabaseReference keyRef    = mDatabase.child(Constant.USER).push();
            String id = keyRef.getKey();


            String lng    = "106."+randomInRange(454250,842264);
            String lat    = "10."+randomInRange(680868,900258);
            String name   = getRandomArray(listnames);
            String phone  = "0" +randomNumber(9);
            String email  = randomString(10)+"@"+randomString(3)+".com";
            String avatar = getRandomArray(listAvatar);
            String status = "online";

            String invitecode = Calcul.create_invite_code(id,onCreated);

            User user = new User(id,avatar,name,gender,phone,email,points,review,nreview,lat,lng,status,ntriptotal,invitecode,invitedby,onCreated);
            keyRef.child(Constant.PROFILE).setValue(user, (databaseError, databaseReference) -> {


            Map<String, Object> map = new HashMap<>();
            map.put("typeCar","");
            map.put("nameCar","");
            map.put("colorCar","");
            map.put("photoCar",photocar);
            map.put("yearCar","");
            mDatabase.child(Constant.USER).child(id).child(Constant.MCAR).setValue(map);

        });
    }

        Log.d("testdata","created " + n + " users");

    }



    public static void deletePaTrip(Activity activity){

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();


        mDatabase.child(Constant.TRIP_PASSENGER).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                mDatabase.child(Constant.GEOFIRE_LOCATION_TRIP_PASSENGER).removeValue().addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()){
                        Helper.displayDiagSuccess(activity,"Đã xoá thành công tất cả chuyến đi của hành khách","");
                    }else{
                        Helper.displayDiagSuccess(activity,"Lỗi","GEOFIRE_LOCATION_TRIP_PASSENGER "+ task1.getException());


                    }
                });
            }else {

                Helper.displayDiagSuccess(activity,"Lỗi","TRIP_PASSENGER "+ task.getException());
            }
        });

  /*
        mDatabase.child(Constant.GEOFIRE_LOCATION_TRIP_PASSENGER).removeValue();
        mDatabase.child(Constant.TRIP_PASSENGER).orderByChild("userID").equalTo("rLrNdi7fUegmfL7WMsNLYb6FuCI3").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds:dataSnapshot.getChildren()){

                    String key = ds.getKey();


                    mDatabase.child(Constant.TRIP_PASSENGER).child(key).removeValue();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
 */



    }


    public static void deleteDrTrip(Activity activity){
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child(Constant.TRIP_DRIVER).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                mDatabase.child(Constant.GEOFIRE_LOCATION_TRIP_DRIVER).removeValue().addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()){
                        Helper.displayDiagSuccess(activity,"Đã xoá thành công tất cả chuyến đi của tài xế","");
                    }else{
                        Helper.displayDiagSuccess(activity,"Lỗi","GEOFIRE_LOCATION_TRIP_DRIVER "+task1.getException());
                    }
                });
            }else {
                Helper.displayDiagSuccess(activity,"Lỗi","TRIP_DRIVER "+task.getException());
            }
        });
    }




    @SuppressLint("LogNotTimber")
    public static void  createPaTrip(Activity activity,User user, int n, IAccessFireBase.iAddTripPassenger callback){

        String name   = user.getName();
        String avatar = user.getAvatar();
        String userID = user.getUserID();

        boolean isSuccess = false;

        for (int i = 0; i<n;i++){

            if ( i== n-1){ isSuccess = true; }

            String date   = randomInRange(1,30)+"/10/2019";
            String time   = randomInRange(0,24)+":"+randomInRange(0,59);
            String depart      = "[" + "106."+randomInRange(454250,842264) +","+"10."+randomInRange(680868,900258)+"]";
            String destination =  "[" + "106."+randomInRange(454250,842264) +","+"10."+randomInRange(680868,900258)+"]";
            int nPerson = Integer.valueOf(randomInRange(1,4));
            String duration = randomInRange(50,100);
            String distance = randomInRange(10,20);
            String typeRequest = "bike";
            String drID = "0";
            String cost = randomInRange(10,100)+",000";


            String onCreated = "01/10/2019";


            boolean finalIsSuccess = isSuccess;
            AccessFireBase.addTrip(name, avatar, userID, date, time, depart, destination, nPerson,
                    duration, distance, cost, typeRequest, drID, onCreated, new IAccessFireBase.iAddTripPassenger() {
                        @Override
                        public void onSuccess(String trID) {

                            if (finalIsSuccess){
                                Helper.displayDiagSuccess(activity,"Đã tạo ngẫu nhiên "+n+" chuyến đi của hành khách! ","");
                                callback.onSuccess(trID);
                            }


                        }

                        @Override
                        public void onFailed() {

                            if (finalIsSuccess){
                                Helper.displayDiagSuccess(activity,"Lỗi! ","");
                            }

                        }
                    });

        }


    }






    @SuppressLint("LogNotTimber")
    public static void  createDrTrip(Activity activity, User user, int n, IAccessFireBase.iAddTripDriver callback){

        String avatar = user.getAvatar();
        String userID = user.getUserID();


        String onCreated = Helper.getTimeStamp();

        boolean isSuccess = false;

        for (int i = 0; i<n;i++){


            if ( i== n-1){ isSuccess = true; }


            String date   = randomInRange(1,30)+"/10/2019";
            String time   = randomInRange(0,24)+":"+randomInRange(0,59);
            String depart      = "[" + "106."+randomInRange(454250,842264) +","+"10."+randomInRange(680868,900258)+"]";
            String destination =  "[" + "106."+randomInRange(454250,842264) +","+"10."+randomInRange(680868,900258)+"]";


            int nSeat = Integer.valueOf(randomInRange(1,4));
            String requestCar = "bike";
            int repeat = 0;


            boolean finalIsSuccess = isSuccess;
            AccessFireBase.addTrip(  userID, avatar,  date,  time,  depart,  destination,  nSeat,requestCar,
                    repeat,  onCreated, new IAccessFireBase.iAddTripDriver() {
                        @Override
                        public void onSuccess() {

                            if (finalIsSuccess){
                                Helper.displayDiagSuccess(activity,"Đã tạo ngẫu nhiên "+n+" chuyến đi của tài xế! ","");
                            callback.onSuccess();
                            }

                        }

                        @Override
                        public void onFailed() {

                            if (finalIsSuccess){
                                Helper.displayDiagSuccess(activity,"Lỗi! ","");
                            }

                        }
                    });

        }

    }














    public static String getRandomArray(String[] array) {
        int rnd = new Random().nextInt(array.length);
        return array[rnd];
    }




    private static String randomString(int n){
        char[] chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKMNOQPRSTUVXYZ0123456789".toCharArray();
        StringBuilder sb = new StringBuilder(20);
        Random random = new Random();
        for (int i = 0; i < n; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }

        return sb.toString();
    }

    private static String randomInRange(int a , int b){

        int randomDouble = a + (int)(Math.random() * ((b - a) + 1));

        return String.valueOf(randomDouble);

    }

    private static String randomNumber(int n){
        char[] chars = "123456789".toCharArray();
        StringBuilder sb = new StringBuilder(20);
        Random random = new Random();
        for (int i = 0; i < n; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }

        return sb.toString();
    }

    private static String randomNumberS(int n){
        char[] chars = "12345".toCharArray();
        StringBuilder sb = new StringBuilder(20);
        Random random = new Random();
        for (int i = 0; i < n; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }

        return sb.toString();
    }

}
