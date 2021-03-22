package com.example.thuantran.wego.Model.Profile;

import android.app.Activity;
import android.content.Context;


import androidx.annotation.NonNull;


import com.example.thuantran.wego.DataAccess.Constant;
import com.example.thuantran.wego.Interface.Profile.SignUp;
import com.example.thuantran.wego.Model.Map.GPSTracker;
import com.example.thuantran.wego.Object.User;
import com.example.thuantran.wego.R;
import com.example.thuantran.wego.Tools.Calcul;
import com.example.thuantran.wego.Tools.Helper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ModelSignUp {

    private SignUp.Presenter callback;
    private static final String TAG ="ModelSignUp";


    public ModelSignUp(SignUp.Presenter callback) {
        this.callback = callback;

    }


    public void sendCode(Activity activity,String phone){

        if (phone.isEmpty()){ return; }

        if(!Helper.isValidPhone(phone)){
            callback.onCodeSendFail(activity.getString(R.string.invalide_phone));
            return;
        }



        new Thread(() -> {

            String newphone;
            if(phone.charAt(0) == '0'){
                String[] str =  phone.split("0",2);
                newphone = activity.getString(R.string.phone_region_id)+str[1];
            }else{
                newphone = phone;
            }

            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    newphone,
                    60,
                    TimeUnit.SECONDS,
                    activity,
                    new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                        @Override
                        public void onVerificationCompleted(@NotNull PhoneAuthCredential credential) {

                            signInWithPhoneAuthCredential(activity,credential);
                        }

                        @Override
                        public void onVerificationFailed(FirebaseException e) {

                            callback.onCodeSendFail(activity.getString(R.string.invalidephonenumber));


                        }

                        @Override
                        public void onCodeSent(@NotNull String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                            // The SMSContext verification activity_main_code has been sent

                            callback.onCodeSendSuccess(verificationId);


                            // Save verification ID and resending token so we can use them later
                            //phoneVireficationId = verificationId;
                            //resendTokes         = token;
                        }
                    });

        }).start();

    }


    public void VerifyCode(Activity activity,String code, String phoneVireficationId){

        if (code.length()!=6){
            callback.onVerifyCodeFail(activity.getResources().getString(R.string.verifyfail));
        }else{
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(phoneVireficationId,code);
            signInWithPhoneAuthCredential(activity,credential);
        }
    }

    private void signInWithPhoneAuthCredential(Activity activity,PhoneAuthCredential credential) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            String userID = mAuth.getCurrentUser().getUid();

                            callback.onVerifyCodeSuccess(userID);

                        } else {

                            callback.onVerifyCodeFail(activity.getResources().getString(R.string.loginfial1));

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification activity_main_code entered was invalid
                            }
                        }
                    }
                });
    }





    public void handleCreateProfile(Context context,String id, String name, String gender, String phone, String email) {

        GPSTracker gps   = new GPSTracker(context);
        double latitude  = gps.getLatitude();
        double longitude = gps.getLongitude();


        new Thread(() -> {
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

            // INITIAL DATA TABLE USER
            String onCreated = Helper.getTimeStamp();
            String review    = "0";
            String nreview   = "0";
            String ntriptotal= "0";
            String invitedby = "";
            String points    = "2000";
            String lng       = String.valueOf(longitude);
            String lat       = String.valueOf(latitude);
            String status    = "online";

            String photocar  = "https://firebasestorage.googleapis.com/v0/b/wego1-db35f.appspot.com/o/images%2Favatarcar.png?alt=media&token=f524fb12-d88e-4cd0-a0bd-2f21b88e7302";
            String avatar;
            if (gender.equals("nu")){
                 avatar = "https://firebasestorage.googleapis.com/v0/b/wego1-db35f.appspot.com/o/images%2Favatargirl.png?alt=media&token=7cd8bd7b-9f5b-4713-9b6e-1d8382803496";
            }else{
                 avatar = "https://firebasestorage.googleapis.com/v0/b/wego1-db35f.appspot.com/o/images%2Favatarboy.png?alt=media&token=80268309-8180-410e-9c4a-5d69538290bc";
            }


            String invitecode = Calcul.create_invite_code(id,onCreated);


            User user = new User(id,avatar,name,gender,phone,email,points,review,nreview,lat,lng,status,ntriptotal,invitecode,invitedby,onCreated);
            mDatabase.child(Constant.USER).child(id).child(Constant.PROFILE).setValue(user, (databaseError, databaseReference) -> {
                if(databaseError == null){

                    callback.onCreateProfileSuccess(user);

                    Map<String, Object> map = new HashMap<>();
                    map.put("typeCar","");
                    map.put("nameCar","");
                    map.put("colorCar","");
                    map.put("photoCar",photocar);
                    map.put("yearCar","");
                    mDatabase.child(Constant.USER).child(id).child(Constant.MCAR).setValue(map);

                }else{
                    callback.onCreateProfileFail("create database error:"+ databaseError.toString()); }
            });


            //}


        }).start();




    }

}
