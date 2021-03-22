package com.example.thuantran.wego.View.SignUp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.thuantran.wego.R;
import com.example.thuantran.wego.Tools.Helper;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class SignUpEmail extends AppCompatActivity {

    private ProgressDialog dialog;
    private EditText edEmail, edPassword,edPassword1;
    private TextView btCreateAcc,btBackLogin;
    private String email, pass;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_sign_up_email);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        edEmail     = findViewById(R.id.ipEmail);
        edPassword  = findViewById(R.id.ipPassword);
        edPassword1 = findViewById(R.id.ipPassword1);
        btCreateAcc = findViewById(R.id.btLogin);
        btBackLogin = findViewById(R.id.btBackLogin);





        btBackLogin.setOnClickListener(view -> {
            finish();
            overridePendingTransition(R.anim.enter1, R.anim.exit1);
        });


        btCreateAcc.setOnClickListener(v -> {

            email = edEmail.getText().toString();
            pass  = edPassword.getText().toString();
            String pass1  = edPassword1.getText().toString();

            if (!pass1.equals(pass)){
                Helper.displayErrorMessage(this,getString(R.string.invalide_pass));
                return; }

            if (!Helper.isValidEmail(email)){
                Helper.displayErrorMessage(this,getString(R.string.invalide_email));
                return; }

            if(pass.isEmpty()){
                Helper.displayErrorMessage(SignUpEmail.this,getString(R.string.missfield));
            }else{


                dialog = new ProgressDialog(this);
                dialog.setMessage(getString(R.string.loading));
                dialog.setCancelable(false);
                dialog.show();

                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                mAuth.createUserWithEmailAndPassword(email, pass)
                        .addOnCompleteListener(SignUpEmail.this, task -> {
                            if (task.isSuccessful()) {

                                String userID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

                                Intent intent = new Intent(SignUpEmail.this, CreateProfileActivity.class);
                                intent.putExtra("userID",userID);
                                intent.putExtra("phone","");
                                intent.putExtra("email",email);
                                startActivity(intent);

                                dialog.dismiss();

                            }else{

                                dialog.dismiss();
                                Helper.displayDiag(this,getString(R.string.err),getString(R.string.signupfail));

                            }

                        });
            }



        });

    }

}
