package com.example.thuantran.wego.View.Main;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.thuantran.wego.R;
import com.example.thuantran.wego.Tools.Helper;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.util.Objects;


public class QuestionActivity extends AppCompatActivity {

    private ExpandableTextView qs1,qs2,qs3,qs4,qs5,qs6,qs7,qs8,qs9,qs10;
    private ImageView Back;
    private TextView  twemail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main_question);


        twemail = findViewById(R.id.twemail);
        Back = findViewById(R.id.back);
        qs1  = findViewById(R.id.qs1);
        qs2  = findViewById(R.id.qs2);
        qs3  = findViewById(R.id.qs3);
        qs4  = findViewById(R.id.qs4);
        qs5  = findViewById(R.id.qs5);
        qs6  = findViewById(R.id.qs6);
        qs7  = findViewById(R.id.qs7);
        qs8  = findViewById(R.id.qs8);
        qs9  = findViewById(R.id.qs9);
        qs10 = findViewById(R.id.qs10);

        qs1.setText(getString(R.string.qs1));
        qs2.setText(getString(R.string.qs2));
        qs3.setText(getString(R.string.qs3));
        qs4.setText(getString(R.string.qs4));
        qs5.setText(getString(R.string.qs5));
        qs6.setText(getString(R.string.qs6));
        qs7.setText(getString(R.string.qs7));
        qs8.setText(getString(R.string.qs8));
        qs9.setText(getString(R.string.qs9));
        qs10.setText(getString(R.string.qs10));


        twemail.setOnClickListener(v -> openDialog());


        Back.setOnClickListener(v -> finish());


    }

    private void openDialog(){
        LayoutInflater inflater = LayoutInflater.from(this);
        View subView = inflater.inflate(R.layout.layout_contactus, null);

        final EditText mSubject = subView.findViewById(R.id.subject);
        final EditText mMessage = subView.findViewById(R.id.message);


        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(this));
        builder.setTitle(getString(R.string.contactus));
        builder.setView(subView);


        builder.setPositiveButton(getString(R.string.Gui), (dialog, which) -> {
            String subject = mSubject.getText().toString();
            String message = mMessage.getText().toString();

            if(TextUtils.isEmpty(subject) || TextUtils.isEmpty(message) ){
                Helper.displayDiagWarning(this, getResources().getString(R.string.missfield),"");
            }else{

                if (message.length()<10){
                    Helper.displayDiagWarning(this, getResources().getString(R.string.tooshor),"");
                    return;
                }


                // send the information to remote server.
                Intent mailIntent = new Intent(Intent.ACTION_VIEW);
                Uri data = Uri.parse("mailto:?subject=" + subject + "&body=" + message + "&to=" + getString(R.string.support_email));
                mailIntent.setData(data);
                startActivity(Intent.createChooser(mailIntent,  getResources().getString(R.string.cud)));

            }

        });

        builder.setNegativeButton(getString(R.string.Cancel2), (dialog, which) -> {

        });

        builder.show();
    }

}
