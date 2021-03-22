package com.example.thuantran.wego.Tools;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;

import com.example.thuantran.wego.Adapter.TripAdapter;
import com.example.thuantran.wego.DataAccess.AccessFireBase;
import com.example.thuantran.wego.DataAccess.Constant;
import com.example.thuantran.wego.DataAccess.IAccessFireBase;
import com.example.thuantran.wego.Model.Map.GPSTracker;
import com.example.thuantran.wego.Object.PassengerTrip;
import com.example.thuantran.wego.Object.User;
import com.example.thuantran.wego.View.Main.EventActivity;
import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.snackbar.Snackbar;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thuantran.wego.R;
import com.google.firebase.iid.FirebaseInstanceId;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;


public class Helper {

    public Helper(){

    }


    private static boolean isTurnOnLocation ;
    private DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    private static final String TAG = "Helper" ;
    private static final int ERROR_DIALOG_REQUEST = 9001;


    private static boolean isClick = false;

    private String[] Time;

    public void setTime(Date date) {
        String[] time = dateFormat.format(date).split("\\s");
        this.Time = time;
    }
    public String[] getTime(){
        return Time;
    }

    @SuppressLint("DefaultLocale")
    public static String getTimeStamp(){


        final Calendar mCalDate = Calendar.getInstance();
        int day      = mCalDate.get(Calendar.DAY_OF_MONTH);
        int month    = mCalDate.get(Calendar.MONTH)+1;
        int year     = mCalDate.get(Calendar.YEAR);
        int hour     = mCalDate.get(Calendar.HOUR_OF_DAY);
        int minute   = mCalDate.get(Calendar.MINUTE);

        String date  = String.format("%02d", day)+"/"+String.format("%02d", month)+"/"+year;
        String time  = String.format("%02d", hour)+":"+String.format("%02d", minute);

        return date +"-"+ time;

    }


    @SuppressLint("DefaultLocale")
    public void callSystemTime(){


        Date current_date = new Date();
        setTime(current_date);

    }




    public void handleShowDatePicker(Context context, iHelper.pickTime callback){

        new SingleDateAndTimePickerDialog.Builder(context)
                .curved()
                .minutesStep(5)
                .displayHours(true)
                .displayMinutes(true)
                .mainColor(context.getResources().getColor(R.color.colorOrange))
                .title("Thời gian khởi hành")

                .listener(date -> {

                    String[] mdate = dateFormat.format(date).split("\\s");
                    callback.onSuccess(mdate);

                }).display();


    }





    public static void displayErrorMessage(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static void displayMessenger(Activity activity, String str){
        Snackbar snackbar = Snackbar.make(Objects.requireNonNull(activity).findViewById(android.R.id.content), str, Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(activity.getResources().getColor(R.color.colorPrimary));
        snackbar.show();

    }

    public static void displayDiag(Activity activity, String title, String sms){
        CustomDialog dialog = new CustomDialog(activity,R.drawable.ic_question, 1);
        dialog.setCancelable(false);
        dialog.setTitle(title);
        dialog.setMessage(sms);
        dialog.setCancelText(activity.getString(R.string.Close));
        dialog.setCancelClickListener(Dialog::dismiss);
        dialog.show();
    }

    public static void displayDiagSuccess(Activity activity, String title, String sms){
        CustomDialog dialog = new CustomDialog(activity,R.drawable.ic_success, 1);
        dialog.setCancelable(false);
        dialog.setTitle(title);
        dialog.setMessage(sms);
        dialog.setCancelText(activity.getString(R.string.Close));
        dialog.setCancelClickListener(Dialog::dismiss);
        dialog.show();
    }


    public static void displayDiagWarning(Activity activity, String title, String sms){

        CustomDialog dialog = new CustomDialog(activity,R.drawable.ic_warning, 1);
        dialog.setCancelable(false);
        dialog.setTitle(title);
        dialog.setMessage(sms);
        dialog.setCancelText(activity.getString(R.string.Close));
        dialog.setCancelClickListener(Dialog::dismiss);
        dialog.show();

    }


    public static void displayDiagError(Activity activity, String title, String sms){
        CustomDialog dialog = new CustomDialog(activity,R.drawable.ic_error, 1);
        dialog.setCancelable(false);
        dialog.setTitle(title);
        dialog.setMessage(sms);
        dialog.setCancelText(activity.getString(R.string.Close));
        dialog.setCancelClickListener(Dialog::dismiss);
        dialog.show();
    }




    public static void isgetLocation(Activity activity, String userID, IAccessFireBase.iGetLocationSuccess callback){

        GPSTracker gps   = new GPSTracker(activity);
        double latitude  = gps.getLatitude();
        double longitude = gps.getLongitude();


        if(latitude == 0.0 || longitude == 0.0){

            callback.onSuccess();
            //callback.onFailed("nogps");

        }else{
            if (latitude> Constant.BOUND_CORNER_NW.getLatitude()
                    ||latitude< Constant.BOUND_CORNER_SE.getLatitude()
                    || longitude< Constant.BOUND_CORNER_NW.getLongitude()
                    || longitude> Constant.BOUND_CORNER_SE.getLongitude()){

                callback.onFailed("limited");

            }else{
                if (userID != null){

                    AccessFireBase.updateLocation(userID,latitude,longitude, new IAccessFireBase.iUpdateLocation() {
                        @Override
                        public void onSuccess() {

                            callback.onSuccess();

                            FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {

                                    String token = Objects.requireNonNull(task.getResult()).getToken();

                                    AccessFireBase.updateToken(userID,token);

                                }

                            });
                        }

                        @Override
                        public void onFailed() { callback.onFailed("nointernet"); }
                    });

                }

            }
        }

    }




    public static LatLng fromStringToLatLng(String strLatLgn){


        String[] newStrLatLgn = strLatLgn.substring(1,strLatLgn.length()-1).split(",");

        double lgn = Double.valueOf(newStrLatLgn[0]);
        double lat = Double.valueOf(newStrLatLgn[1]);

        return  new LatLng(lat,lgn);


    }

    public static String fromStringLatLngToFullAddress(Context context,String strLatLgn) {

        String[] newStrLatLgn = null;

        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = null;
        try {
            newStrLatLgn = strLatLgn.substring(1, strLatLgn.length() - 1).split(",");
            addresses = geocoder.getFromLocation(Double.valueOf(newStrLatLgn[1]), Double.valueOf(newStrLatLgn[0]), 1);

        } catch (Exception e) {
            e.printStackTrace();

        }

        if (addresses !=null){
            return addresses.get(0).getAddressLine(0);
        }else{
            return null;
        }
    }



    public static boolean isServicesOK(Activity activity) {

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(activity);

        if (available == ConnectionResult.SUCCESS) {
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(activity, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }
        return false;
    }


    public static  boolean isNetwordAvailiable(Activity activity){
        ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;
    }


    public static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }

    public static boolean isValidName(String name) {
        String nameRegex = "^([a-zA-ZàáảãạăằắẳẵặâầấẩẫậđĐèéẻẽẹêềếễệìíỉĩịòóỏõọôồốổỗộơờớởợùúủũụưừứữựỳýỷỹỵÀÁẢÃẠĂẰẮẴẶÂẦẤẨẪẬÈÉẺẼẸÊỀẾỄỆÌÍỈĨỊÒÓỎÕỌÔỒỐỔỖỘÙÚỦŨỤƯỪỨỮỬỰỲÝỶỸỴ]{2,}" +
                "\\s" +
                "?([a-zA-ZàáảãạăằắẳẵặâầấẩẫậđĐèéẻẽẹêềếễệìíỉĩịòóỏõọôồốổỗộơờớởợùúủũụưừứữựỳýỷỹỵÀÁẢÃẠĂẰẮẴẶÂẦẤẨẪẬÈÉẺẼẸÊỀẾỄỆÌÍỈĨỊÒÓỎÕỌÔỒỐỔỖỘÙÚỦŨỤƯỪỨỮỬỰỲÝỶỸỴ]{2,})?" +
                "\\s" +
                "?([a-zA-ZàáảãạăằắẳẵặâầấẩẫậđĐèéẻẽẹêềếễệìíỉĩịòóỏõọôồốổỗộơờớởợùúủũụưừứữựỳýỷỹỵÀÁẢÃẠĂẰẮẴẶÂẦẤẨẪẬÈÉẺẼẸÊỀẾỄỆÌÍỈĨỊÒÓỎÕỌÔỒỐỔỖỘÙÚỦŨỤƯỪỨỮỬỰỲÝỶỸỴ]{2,})?" +
                "\\s" +
                "?([a-zA-ZàáảãạăằắẳẵặâầấẩẫậđĐèéẻẽẹêềếễệìíỉĩịòóỏõọôồốổỗộơờớởợùúủũụưừứữựỳýỷỹỵÀÁẢÃẠĂẰẮẴẶÂẦẤẨẪẬÈÉẺẼẸÊỀẾỄỆÌÍỈĨỊÒÓỎÕỌÔỒỐỔỖỘÙÚỦŨỤƯỪỨỮỬỰỲÝỶỸỴ]{2,})?" +
                "\\s" +
                "?([a-zA-ZàáảãạăằắẳẵặâầấẩẫậđĐèéẻẽẹêềếễệìíỉĩịòóỏõọôồốổỗộơờớởợùúủũụưừứữựỳýỷỹỵÀÁẢÃẠĂẰẮẴẶÂẦẤẨẪẬÈÉẺẼẸÊỀẾỄỆÌÍỈĨỊÒÓỎÕỌÔỒỐỔỖỘÙÚỦŨỤƯỪỨỮỬỰỲÝỶỸỴ]{2,})?" +
                "\\s" +
                "?([a-zA-ZàáảãạăằắẳẵặâầấẩẫậđĐèéẻẽẹêềếễệìíỉĩịòóỏõọôồốổỗộùúủũụưừứữựỳýỷỹỵÀÁẢÃẠĂẰẮẴẶÂẦẤẨẪẬÈÉẺẼẸÊỀẾỄỆÌÍỈĨỊÒÓỎÕỌÔỒỐỔỖỘÙÚỦŨỤƯỪỨỮỬỰỲÝỶỸỴ]{2,})?)";

        //àáảãạăằắẳẵặâầấẩẫậèéẻẽẹêềếễệìíỉĩịòóỏõọôồốổỗộùúủũụưừứữựỳýỷỹỵÀÁẢÃẠĂẰẮẴẶÂẦẤẨẪẬÈÉẺẼẸÊỀẾỄỆÌÍỈĨỊÒÓỎÕỌÔỒỐỔỖỘÙÚỦŨỤƯỪỨỮỬỰỲÝỶỸỴ

        Pattern pat = Pattern.compile(nameRegex);
        if (name == null)
            return false;
        return pat.matcher(name).matches();
    }

    public static boolean isValidPhone(String phone) {

        String[] prefix_phones = {"032","033","034","035","036","037","038","039","070","076","077","078","079","081","082","083","084","085","056","058","076"};


        if (phone.length() !=10){
            return false;
        }else {
            boolean flag = false;
            String prefix = phone.substring(0,3);
            for (String prefix_phone : prefix_phones) {
                if (prefix.equals(prefix_phone)) {
                    flag = true;
                    break;
                }
            }

            return flag;
        }
    }


    public static String getRealPathFromURI(Activity activity, Uri contentUri) {
        String path = null;
        String[] proj = { MediaStore.MediaColumns.DATA };
        Cursor cursor = activity.getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            path = cursor.getString(column_index);
        }
        cursor.close();
        return path;
    }


    @SuppressLint("SimpleDateFormat")
    public static boolean isExpire(String date, String time){
        String DATE_FORMAT = "dd/MM/yyyy HH:mm:ss";
        String END_DATE =date+" "+time+":00";
        long  diff = 0;

        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        try {
            final Date end_date   = dateFormat.parse(END_DATE);
            final Date current_date = new Date();

            diff = end_date.getTime() - current_date.getTime() ;



        } catch (ParseException e) {
            e.printStackTrace();
        }

        return diff < 0;
    }

    @SuppressLint("SimpleDateFormat")
    public static long TimeExpired(String date, String time){
        String DATE_FORMAT = "dd/MM/yyyy HH:mm:ss";
        String END_DATE =date+" "+time+":00";
        long  diff = 0;

        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        try {
            final Date end_date   = dateFormat.parse(END_DATE);
            final Date current_date = new Date();

            diff = current_date.getTime() - end_date.getTime() ;



        } catch (ParseException e) {
            e.printStackTrace();
        }

        return diff ;
    }




    @SuppressLint("SimpleDateFormat")
    public static boolean isIncorrectBookingTime(String date, String time){
        String DATE_FORMAT = "dd/MM/yyyy HH:mm:ss";
        String END_DATE =date+" "+time+":00";
        long  diff = 0;

        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        try {
            final Date end_date   = dateFormat.parse(END_DATE);
            final Date current_date = new Date();

            diff = end_date.getTime() - current_date.getTime() ;



        } catch (ParseException e) {
            e.printStackTrace();
        }

        return diff < 60 * 60 * 1000 || diff > 5 * 24 * 60 * 60 * 1000;

    }

    @SuppressLint("SimpleDateFormat")
    private static boolean isCurrentTrip(String date, String time, String duration){
        String DATE_FORMAT = "dd/MM/yyyy HH:mm:ss";
        String END_DATE =date+" "+time+":00";
        long  diff = 0;


        long dura = Integer.valueOf(duration)*60*1000;


        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        try {
            final Date end_date   = dateFormat.parse(END_DATE);
            final Date current_date = new Date();

            diff = current_date.getTime() - end_date.getTime()  ;



        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (diff>0){
            return diff < dura + 30*60*1000;
        }else {
            return false;
        }

    }


    public static PassengerTrip getCurrentTrip(ArrayList<PassengerTrip> passTrips){

        boolean flag = false;
        PassengerTrip trip  = null;
        if (passTrips != null && passTrips.size() > 0){

            for (int i = 0; i<passTrips.size();i++){
                trip = passTrips.get(i);

                if (Helper.isCurrentTrip(trip.getDate(),trip.getTime(),trip.getDuration())){
                    flag = true;
                    break;
                }

            }
        }
        if (flag){
            return trip;
        }else
            return null;

    }


    public static void drawNavigationRoute(Activity activity, PassengerTrip currentTrip){

        String[] destinationlatlng = currentTrip.getDestination().substring(1, currentTrip.getDestination().length() - 1).split(",");
        String url = "google.navigation:q="+destinationlatlng[1]+","+destinationlatlng[0];
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.setPackage("com.google.android.apps.maps");
        activity.startActivity(intent);


    }


    public static void showTripAdapter(Activity activity, RecyclerView recyclerView,TripAdapter adapter ,ArrayList<PassengerTrip> listtrip){


        activity.runOnUiThread(() -> {
            if (listtrip !=null){

                LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
                layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(layoutManager);

                adapter.notifyDataSetChanged();
                recyclerView.setAdapter(adapter);
            }
        });


    }


    public static void shareText(Activity activity, String code) {

        LayoutInflater inflater = LayoutInflater.from(activity);
        View subView = inflater.inflate(R.layout.layout_share, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(subView);
        builder.setCancelable(false);

        final Button   share     = subView.findViewById(R.id.sharenow);
        final TextView sharecode = subView.findViewById(R.id.sharecode);
        final TextView info = subView.findViewById(R.id.infoshare);


        sharecode.setText(code);


        share.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            String shareBodyText = "Xin chào,\nNhận ngay 2000DT cho lần đăng nhập đầu tiên!\nNhận thêm 5000DT với mã giới thiệu của tôi: " + code +"\nChú ý, để áp dụng mã này bạn phải có tổng cộng tối thiểu 2 chuyến đi sử dụng Wego.";
            intent.putExtra(Intent.EXTRA_SUBJECT, "WEGO - Kết nối và đi cùng nhau.");
            intent.putExtra(Intent.EXTRA_TEXT, shareBodyText);
            activity.startActivity(Intent.createChooser(intent, activity.getString(R.string.cud)));
        });

        info.setOnClickListener(v -> {
            Intent intent = new Intent(activity, EventActivity.class);
            activity.startActivity(intent);
        });


        builder.setNegativeButton(activity.getString(R.string.Close), (dialog, which) -> { });
        builder.show();





    }




    public static void reviewApp(Activity activity, User user) {

        LayoutInflater inflater = LayoutInflater.from(activity);
        View subView = inflater.inflate(R.layout.layout_review_app, null);

        final EditText mMessage = subView.findViewById(R.id.message);
        final RatingBar giaodien = subView.findViewById(R.id.giaodien);
        final RatingBar trainghiem = subView.findViewById(R.id.trainghiem);
        final RatingBar tinhnang = subView.findViewById(R.id.tinhnang);
        final RatingBar nhucau = subView.findViewById(R.id.nhucau);


        final EditText ntrip      = subView.findViewById(R.id.n);
        final Button createPaTrip = subView.findViewById(R.id.createPaTrip);
        final Button createDrTrip = subView.findViewById(R.id.createDrTrip);
        final Button deletePaTrip = subView.findViewById(R.id.deletePaTrip);
        final Button deleteDrTrip = subView.findViewById(R.id.deleteDrTrip);


        deletePaTrip.setVisibility(View.INVISIBLE);
        deleteDrTrip.setVisibility(View.INVISIBLE);


        createPaTrip.setOnClickListener(v -> {

            if (!isClick){
                isClick = true;
                String str = ntrip.getText().toString().trim();

                if (str.isEmpty()){
                    str = "1";
                }

                int n = Integer.valueOf(str);


                TestData.createPaTrip(activity,user, n, new IAccessFireBase.iAddTripPassenger() {
                    @Override
                    public void onSuccess(String trID) {

                        isClick =false;
                    }

                    @Override
                    public void onFailed() {

                    }
                });
            }
        });

        createDrTrip.setOnClickListener(v -> {

            if (!isClick){
                isClick = true;
                String str = ntrip.getText().toString().trim();

                if (str.isEmpty()){
                    str = "1";
                }

                int n = Integer.valueOf(str);

                TestData.createDrTrip(activity, user, n, new IAccessFireBase.iAddTripDriver() {
                    @Override
                    public void onSuccess() {
                        isClick = false;
                    }

                    @Override
                    public void onFailed() {

                    }
                });
            }


        });


        deletePaTrip.setOnClickListener(v -> TestData.deletePaTrip(activity));
        deleteDrTrip.setOnClickListener(v -> TestData.deleteDrTrip(activity));


        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("TEST ONLY");
        builder.setView(subView);
        builder.setCancelable(false);

        builder.setPositiveButton(activity.getString(R.string.Gui), (dialog, which) -> {

            String message     = mMessage.getText().toString();

            float fgiaodien    = giaodien.getRating();
            float ftrainghiem  = trainghiem.getRating();
            float ftinhnang    = tinhnang.getRating();
            float fnhucau      = nhucau.getRating();

            AccessFireBase.addReviewApp(user.getUserID(), message, fgiaodien, ftrainghiem, ftinhnang, fnhucau, new IAccessFireBase.iAddFeedBack() {
                @Override
                public void onSuccess() {
                    Helper.displayDiagSuccess(activity,activity.getString(R.string.camonnhanxet),"");
                }

                @Override
                public void onFailed() {

                }
            });



        });

        builder.setNegativeButton(activity.getString(R.string.Close), (dialog, which) -> {

        });

        builder.show();

    }



}
