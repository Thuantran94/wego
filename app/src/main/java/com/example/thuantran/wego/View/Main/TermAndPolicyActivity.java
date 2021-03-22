package com.example.thuantran.wego.View.Main;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.thuantran.wego.R;
import com.example.thuantran.wego.Tools.Helper;

public class TermAndPolicyActivity extends AppCompatActivity {


    private TextView seemore;
    private ImageView Back;
    private Button termsButton,policyButton,intro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main_term_and_policy);

        Back             = findViewById(R.id.back);
        intro      = findViewById(R.id.intro);
        termsButton      = findViewById(R.id.terms);
        policyButton     = findViewById(R.id.policy);


        Back.setOnClickListener(v -> finish());


        intro.setOnClickListener(v -> {


            LayoutInflater inflater = LayoutInflater.from(this);
            View subView = inflater.inflate(R.layout.layout_intro, null);
            seemore      = subView.findViewById(R.id.seemore);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(subView);
            builder.setCancelable(false);
            builder.setPositiveButton(getString(R.string.Close), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {


                }
            });
            builder.show();


            seemore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Helper.displayDiagWarning(TermAndPolicyActivity.this,getString(R.string.err),"Website đang được cập nhật, vui lòng quay lại sau.");
                }
            });

        });



        policyButton.setOnClickListener(v -> {


            LayoutInflater inflater = LayoutInflater.from(this);
            View subView = inflater.inflate(R.layout.layout_policies, null);
            seemore      = subView.findViewById(R.id.seemore);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(subView);
            builder.setCancelable(false);
            builder.setPositiveButton(getString(R.string.Close), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {


                }
            });
            builder.show();


            seemore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Helper.displayDiagWarning(TermAndPolicyActivity.this,getString(R.string.err),"Website đang được cập nhật, vui lòng quay lại sau.");
                }
            });

        });


        termsButton.setOnClickListener(v -> {

            LayoutInflater inflater = LayoutInflater.from(this);
            View subView = inflater.inflate(R.layout.layout_terms, null);
            seemore      = subView.findViewById(R.id.seemore);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(subView);
            builder.setCancelable(false);
            builder.setPositiveButton(getString(R.string.Close), (dialog, which) -> {


            });
            builder.show();


            seemore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Helper.displayDiagWarning(TermAndPolicyActivity.this,getString(R.string.err),"Website đang được cập nhật, vui lòng quay lại sau.");
                }
            });

        });



    }




}
