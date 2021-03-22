package com.example.thuantran.wego.View.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.thuantran.wego.Adapter.ViewPagerAdapter;
import com.example.thuantran.wego.DataAccess.AccessFireBase;
import com.example.thuantran.wego.DataAccess.IAccessFireBase;
import com.example.thuantran.wego.Interface.Tools.Fragment2Activity;
import com.example.thuantran.wego.Object.User;
import com.example.thuantran.wego.R;
import com.example.thuantran.wego.Tools.Helper;
import com.example.thuantran.wego.View.Main.EventActivity;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class CodeFragment extends Fragment  {

    private ProgressDialog dialog;
    private Fragment2Activity mcallback;
    private Button button;
    private TextView xemthem;
    private EditText editText;
    private ViewPager viewPager;
    private User user;
    private String code;


    private int currentPage = 0;
    private int[] banners   = {R.drawable.banner0,R.drawable.banner1, R.drawable.banner2, R.drawable.banner3, R.drawable.banner4,R.drawable.banner };



    public void sendData(User user ) {
        this.user   = user;
        validate_your_code();
    }



    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        mcallback = (Fragment2Activity) getActivity();
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         View view = inflater.inflate(R.layout.activity_main_code,container,false);
         button    = view.findViewById(R.id.promcode);
         editText  = view.findViewById(R.id.codeprom);
         viewPager = view.findViewById(R.id.viewpager);
         xemthem   = view.findViewById(R.id.xemthem);





        Bundle bundle = getArguments();
        if(bundle != null) {
            user = bundle.getParcelable("user");
            if (user != null) {
                validate_your_code();
            }
        }


        ViewPagerAdapter adapter = new ViewPagerAdapter(getActivity(),banners);
        viewPager.setAdapter(adapter);



        final Handler handler = new Handler();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(() -> {

                    viewPager.setCurrentItem(currentPage, true);
                    currentPage++;

                    if (currentPage == banners.length) {
                        currentPage = 0;
                    }
                });
            }
        },1000,5000);



        xemthem.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EventActivity.class);
            Objects.requireNonNull(getActivity()).startActivity(intent);
        });




        return view;
    }


    private void validate_your_code(){

        button.setOnClickListener(v -> {

            code = editText.getText().toString().trim();

            if (code.length()<6 ){
                Helper.displayErrorMessage(getActivity(),getString(R.string.wrongcode));
                return; }


            dialog = new ProgressDialog(getActivity());
            dialog.setMessage(getString(R.string.sendrequest));
            dialog.setCancelable(false);
            dialog.show();




            if (code.equals("HITEST")){

                AccessFireBase.checkPromoCode(user.getUserID(), code, new IAccessFireBase.iCheckPromoCode() {
                    @Override
                    public void onSuccess() {
                        update(100000);
                    }
                    @Override
                    public void onFailed() {
                        dialog.dismiss();
                        Helper.displayErrorMessage(getActivity(),getString(R.string.wrongcode1));
                    }
                });
            }

            else if (code.equals("WCWEGO")){

                AccessFireBase.checkPromoCode(user.getUserID(), code, new IAccessFireBase.iCheckPromoCode() {
                    @Override
                    public void onSuccess() {
                        update(2000);
                    }
                    @Override
                    public void onFailed() {
                        dialog.dismiss();
                        Helper.displayErrorMessage(getActivity(),getString(R.string.wrongcode1));
                    }
                });
            }


            else if (code.equals("WEGO10") && user.getNtriptotal().equals("10")){

                AccessFireBase.checkPromoCode(user.getUserID(), code, new IAccessFireBase.iCheckPromoCode() {
                    @Override
                    public void onSuccess() {
                        update(5000);
                    }
                    @Override
                    public void onFailed() {
                        dialog.dismiss();
                        Helper.displayErrorMessage(getActivity(),getString(R.string.wrongcode1));
                    }
                });
            }


            else if (code.equals("WEGO30") && user.getNtriptotal().equals("30")){

                AccessFireBase.checkPromoCode(user.getUserID(), code, new IAccessFireBase.iCheckPromoCode() {
                    @Override
                    public void onSuccess() {
                        update(10000);
                    }
                    @Override
                    public void onFailed() {
                        dialog.dismiss();
                        Helper.displayErrorMessage(getActivity(),getString(R.string.wrongcode1));
                    }
                });
            }

            else if (code.equals("WEGO90") && user.getNtriptotal().equals("90")){

                AccessFireBase.checkPromoCode(user.getUserID(), code, new IAccessFireBase.iCheckPromoCode() {
                    @Override
                    public void onSuccess() {
                        update(20000);
                    }
                    @Override
                    public void onFailed() {
                        dialog.dismiss();
                        Helper.displayErrorMessage(getActivity(),getString(R.string.wrongcode1));
                    }
                });
            }
            else{
                dialog.dismiss();
                Helper.displayErrorMessage(getActivity(),getString(R.string.wrongcode1));
            }


        });

    }


    private void update(int amount){

        int pts   = Integer.valueOf(user.getPoints())+amount;
        user.setPoints(String.valueOf(pts));
        mcallback.getUser(user);
        AccessFireBase.updatePoints(user.getUserID(), user.getPoints(), new IAccessFireBase.iUpdatePoint() {
            @Override
            public void onSuccess() {
                Helper.displayDiagSuccess(getActivity(),getString(R.string.codesuccess),getString(R.string.bncdt)+amount+getString(R.string.points));
                dialog.dismiss();
            }

            @Override
            public void onFailed() {
                Helper.displayErrorMessage(getActivity(),getString(R.string.errorconnect));
                dialog.dismiss();
            }
        });

    }




}
