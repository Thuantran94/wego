package com.example.thuantran.wego.View.Fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.thuantran.wego.R;
import com.example.thuantran.wego.Tools.Helper;
import com.example.thuantran.wego.View.Main.QuestionActivity;
import com.example.thuantran.wego.View.Main.TermAndPolicyActivity;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class AboutUsFragment extends Fragment {


    private ImageView imageView;
    private TextView businessName;
    private TextView businessAddress;
    private TextView name;
    private TextView description;
    private TextView email;
    private TextView phone;
    private TextView policy;
    private TextView question;

    private int REQUEST_CODE = 123;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.aboutus,container,false);


        imageView    = view.findViewById(R.id.imabus);
        businessName = view.findViewById(R.id.business_name);
        businessAddress = view.findViewById(R.id.business_address);
        name = view.findViewById(R.id.name);
        description = view.findViewById(R.id.description);
        email = view.findViewById(R.id.email);
        phone = view.findViewById(R.id.phone);
        policy = view.findViewById(R.id.termpolicy);
        question = view.findViewById(R.id.questions);

        Picasso.get().load(R.drawable.shop).into(imageView);


        Button contactUsButton = view.findViewById(R.id.contact_us);
        contactUsButton.setOnClickListener(v -> openDialog());

        Button callUsButton = view.findViewById(R.id.call_us);
        callUsButton.setOnClickListener(v -> initiatePhoneCall());


        policy.setOnClickListener(v -> {
            Intent i = new Intent(getContext(), TermAndPolicyActivity.class);
            startActivity(i);
        });

        question.setOnClickListener(v -> {
            Intent i = new Intent(getContext(), QuestionActivity.class);
            startActivity(i);
        });


        return view;
    }



    private void openDialog(){
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View subView = inflater.inflate(R.layout.layout_contactus, null);

        final EditText mSubject = subView.findViewById(R.id.subject);
        final EditText mMessage = subView.findViewById(R.id.message);


        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
        builder.setTitle(getString(R.string.contactus));
        builder.setView(subView);


        builder.setPositiveButton(getString(R.string.Gui), (dialog, which) -> {
            String subject = mSubject.getText().toString();
            String message = mMessage.getText().toString();

            if(TextUtils.isEmpty(subject) || TextUtils.isEmpty(message) ){
                Helper.displayErrorMessage(getContext(), getResources().getString(R.string.missfield));
            }else{

                if (message.length()<10){
                    Helper.displayErrorMessage(getContext(), getResources().getString(R.string.tooshor));
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

    private void initiatePhoneCall(){

        requestPermissions(new String[] {Manifest.permission.CALL_PHONE}, REQUEST_CODE);

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (grantResults.length > 0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
            Intent callUsIntent = new Intent(Intent.ACTION_CALL);
            callUsIntent.setData(Uri.parse("tel:" + getString(R.string.support_phone)));
            startActivity(callUsIntent);
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


}
