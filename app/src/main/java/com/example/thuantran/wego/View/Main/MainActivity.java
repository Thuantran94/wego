package com.example.thuantran.wego.View.Main;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.thuantran.wego.DataAccess.ReadDataFromFireBase;
import com.example.thuantran.wego.DataAccess.ReadDataFromFireBaseListener;
import com.example.thuantran.wego.Interface.Profile.SignUp;
import com.example.thuantran.wego.Object.User;
import com.example.thuantran.wego.Presenter.Profile.PresenterSignUp;
import com.example.thuantran.wego.R;
import com.example.thuantran.wego.Tools.Helper;
import com.example.thuantran.wego.View.SignUp.CreateProfileActivity;
import com.example.thuantran.wego.View.Driver.MainDriver;
import com.example.thuantran.wego.View.Passenger.MainPassenger;
import com.example.thuantran.wego.View.SignUp.SignUpEmail;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;


public class MainActivity extends AppCompatActivity implements ReadDataFromFireBaseListener, SignUp.View{

    private TextView btCreateAcc;
    private ProgressDialog dialog;
    private EditText edEmail, edPassword;
    private Button  btLogin;
    private EditText textPhone;
    private String phone, email, pass;
    private String userID;
    private boolean stopTimer;
    private PresenterSignUp presenterSignUp;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_login);
        mapToLayout();


        // Tạo mới một tài khoản, di chuyến đến SignUpEmail.
        btCreateAcc.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SignUpEmail.class);
            startActivity(intent);
            overridePendingTransition(R.anim.enter, R.anim.exit);
        });




        btLogin.setOnClickListener(view -> {
            stopTimer = false;
            phone     = textPhone.getText().toString().trim();

            if (!phone.isEmpty()){
                presenterSignUp = new PresenterSignUp(MainActivity.this);
                presenterSignUp.receivedHandleSendCode(MainActivity.this,phone);
            }else{

                email = edEmail.getText().toString();
                pass  = edPassword.getText().toString();

                // Kiểm tra input người dùng, nếu hợp lệ thông báo đăng nhập thành công.
                if(!Helper.isValidEmail(email)){
                    Helper.displayErrorMessage(MainActivity.this,getString(R.string.invalide_email));
                    return; }
                if( pass.isEmpty()){
                    Helper.displayErrorMessage(MainActivity.this,getString(R.string.missfield));
                }else{


                    dialog = new ProgressDialog(this);
                    dialog.setMessage(getString(R.string.loading));
                    dialog.setCancelable(false);
                    dialog.show();

                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    mAuth.signInWithEmailAndPassword(email, pass)
                            .addOnCompleteListener(MainActivity.this, task -> {
                                if (task.isSuccessful()) {

                                    userID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();


                                    ReadDataFromFireBase readDataFromFireBase = new ReadDataFromFireBase(this);
                                    readDataFromFireBase.getUserFromFireBase(userID);

                                } else {

                                    dialog.dismiss();
                                    Helper.displayErrorMessage(this,getString(R.string.loginfial1));
                                }

                                // ...
                            });

                }

            }
        });



    }



    @Override
    public void onCodeSendSuccess(String verificationId) {

        btLogin.setEnabled(false);
        final Handler handler = new Handler();
        handler.post(new Runnable() {

            int seconds = 30;

            @SuppressLint("SetTextI18n")
            public void run() {
                seconds--;
                btLogin.setText(getString(R.string.thao_tac_sau)+seconds+ "s");
                if (seconds == 0) {
                    stopTimer = true;
                    btLogin.setEnabled(true);
                    btLogin.setText(getString(R.string.Continue));
                }
                if(!stopTimer) { handler.postDelayed(this, 1000); }
            }
        });


        LayoutInflater inflater = LayoutInflater.from(this);
        View subView = inflater.inflate(R.layout.layout_verify_code, null);
        final EditText mMessage = subView.findViewById(R.id.code);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.mxn));
        builder.setMessage(getString(R.string.send_code)+phone +".");
        builder.setView(subView);
        builder.setCancelable(false);


        builder.setPositiveButton(getResources().getString(R.string.ok), (dialog, which) -> {
            String code = mMessage.getText().toString();
            presenterSignUp.receivedHandleVerifyCode(MainActivity.this,code, verificationId);
        });


        builder.setNegativeButton(getResources().getString(R.string.Cancel1), (dialog, which) -> {  });
        builder.show();
    }

    @Override
    public void onCodeSendFail(String err) {

        runOnUiThread(() -> Helper.displayErrorMessage(MainActivity.this,err));

    }


    @Override
    public void onVerifyCodeSuccess(String userID) {
        this.userID = userID;
        Helper.displayErrorMessage(this,getResources().getString(R.string.veryfisuccess));

        ReadDataFromFireBase readDataFromFireBase = new ReadDataFromFireBase(this);
        readDataFromFireBase.getUserFromFireBase(userID);
    }

    @Override
    public void onGetUserSuccess(User user) {

        dialog.dismiss();

        if (user !=null){

            if(user.getModel() == null){

                Bundle bundle = new Bundle();
                bundle.putParcelable("user",user);
                Intent intent = new Intent(MainActivity.this, WellComeActivity.class);
                intent.putExtra("bundle",bundle);
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);

            }else {

                switch (user.getModel()){

                    case "passenger":
                        Bundle bundle1 = new Bundle();
                        bundle1.putParcelable("user",user);
                        Intent intent1 = new Intent(MainActivity.this, MainPassenger.class);
                        intent1.putExtra("bundle",bundle1);
                        startActivity(intent1);
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                        break;

                    case "driver":
                        Bundle bundle2 = new Bundle();
                        bundle2.putParcelable("user",user);
                        Intent intent2 = new Intent(MainActivity.this, MainDriver.class);
                        intent2.putExtra("bundle",bundle2);
                        startActivity(intent2);
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                        break;
                }
            }

        }else{

            Intent intent = new Intent(MainActivity.this, CreateProfileActivity.class);
            intent.putExtra("userID",userID);
            intent.putExtra("phone",phone);
            intent.putExtra("email","");
            startActivity(intent);
            overridePendingTransition(R.anim.enter, R.anim.exit);
        }
    }

    @Override
    public void onGetUserFailed(String err) {
        dialog.dismiss();
        Helper.displayErrorMessage(this,getString(R.string.errorconnect));
    }


    @Override
    public void onVerifyCodeFail(String err) {
        Helper.displayErrorMessage(this,err);
    }
    @Override
    public void onCreateProfileSuccess(User user) { }
    @Override
    public void onCreateProfileFail(String err) { }

    @Override
    public void onBackPressed() {

    }
    private void mapToLayout() {
        btLogin     = findViewById(R.id.btLogin);
        textPhone   = findViewById(R.id.textPhone);
        edEmail     = findViewById(R.id.ipEmail);
        edPassword  = findViewById(R.id.ipPassword);
        btCreateAcc = findViewById(R.id.btDk);

    }


}

