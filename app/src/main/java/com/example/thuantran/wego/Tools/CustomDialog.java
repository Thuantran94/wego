package com.example.thuantran.wego.Tools;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;


import com.example.thuantran.wego.R;


public class CustomDialog extends Dialog {

    private OnCustomDialogListener mCancelClickListener;
    private OnCustomDialogListener mConfirmClickListener;


    public interface OnCustomDialogListener {
        void onClick(CustomDialog dialog);
    }


    private Context context;
    private int rsid;
    private String title, message;
    private String strNo, strYes;
    private int nbutton;


    public CustomDialog(@NonNull Context context, int rsid, int nbutton) {
        super(context);
        this.context = context;
        this.rsid = rsid;
        this.nbutton = nbutton;


    }



    public void setTitle(String text) {
        title = text;
    }

    public void setMessage(String text) {
        message = text;
    }

    public void setCancelText(String text) {

        strNo = text;
    }

    public void setConfirmText(String text) {
        strYes = text;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (nbutton == 1){
            setContentView(R.layout.custom_dialog_one_button);
        }else{
            setContentView(R.layout.custom_dialog);
            Button btn_yes = findViewById(R.id.btn_yes);

            btn_yes.setOnClickListener(v -> {

                if (mConfirmClickListener != null) { mConfirmClickListener.onClick(CustomDialog.this);}
                else{ dismiss(); }
            });


        }

        ImageView imdiag = findViewById(R.id.imdiag);
        TextView twtitle = findViewById(R.id.title);
        TextView twsms = findViewById(R.id.context);
        Button btn_yes = findViewById(R.id.btn_yes);
        Button btn_no = findViewById(R.id.btn_no);

        imdiag.setImageDrawable(context.getResources().getDrawable(rsid));

        if (title != null){ twtitle.setText(title);}

        if (message != null){ twsms.setText(message);}

        if (strNo !=null){ btn_no.setText(strNo);}

        if (strYes !=null){ btn_yes.setText(strYes);}



        btn_no.setOnClickListener(v -> {

            if (mCancelClickListener != null) { mCancelClickListener.onClick(CustomDialog.this);}
            else{ dismiss();}

        });

    }

    public CustomDialog setConfirmClickListener( OnCustomDialogListener listener) {

        mConfirmClickListener = listener;
        return this;
    }


    public CustomDialog setCancelClickListener( OnCustomDialogListener listener) {

        mCancelClickListener = listener;
        return this;
    }


}
