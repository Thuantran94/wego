package com.example.thuantran.wego.View.SignUp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thuantran.wego.DataAccess.AccessFireBase;
import com.example.thuantran.wego.DataAccess.IAccessFireBase;
import com.example.thuantran.wego.Interface.Profile.Image;
import com.example.thuantran.wego.Interface.Profile.SignUp;
import com.example.thuantran.wego.Object.User;
import com.example.thuantran.wego.Presenter.Profile.PresenterImage;
import com.example.thuantran.wego.Presenter.Profile.PresenterSignUp;
import com.example.thuantran.wego.R;
import com.example.thuantran.wego.Tools.Helper;
import com.example.thuantran.wego.View.Main.TermAndPolicyActivity;
import com.example.thuantran.wego.View.Main.WellComeActivity;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import java.io.FileNotFoundException;
import java.io.InputStream;


public class CreateProfileActivity extends AppCompatActivity implements SignUp.View, Image.View  {
    private ImageView avatar;
    private RadioGroup btGender;
    private ProgressDialog dialog;
    private Button btFinishCreateAcc;
    private TextView policy;
    private CheckBox checkBox;
    private EditText textName, textPhone, textEmail;
    private User user;
    private String name, id , phone, email;
    private String gender;
    private PresenterSignUp presenterSignUp;
    private static int REQUEST_CODE = 123;
    private String real_patch = null;
    private boolean isDetectedFace = false;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main_create_profit);
        mapToLayout();

        Intent intent = getIntent();

        id    = intent.getStringExtra("userID");
        email = intent.getStringExtra("email");
        phone = intent.getStringExtra("phone");

        textPhone.setText(phone);
        textEmail.setText(email);


        if(phone !=null && !phone.equals("")){
            textPhone.setEnabled(false);
        }

        if(email !=null && !email.equals("")){
            textEmail.setEnabled(false);
        }


        btGender.setOnCheckedChangeListener((group, checkedId) -> {

            switch (checkedId){
                case R.id.male:
                    gender = "nam";
                    if (bitmap == null){ avatar.setImageResource(R.drawable.ic_avatarboy); }
                    break;

                case R.id.female:
                    gender = "nu";
                    if (bitmap == null){ avatar.setImageResource(R.drawable.ic_avatargirl); }
                    break;
            }

        });



        // Các chính sách và điều khoản sử dụng
        policy.setOnClickListener(v -> {
            Intent intent4 = new Intent(this, TermAndPolicyActivity.class);
            startActivity(intent4);
        });

        avatar.setOnClickListener(v -> {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE);
            }else{
                Intent intent1 = new Intent(Intent.ACTION_PICK);
                intent1.setType("image/*");
                startActivityForResult(intent1,REQUEST_CODE);
            }

        });


        btFinishCreateAcc.setOnClickListener(v -> {



            name      = textName.getText().toString().trim();
            phone     = textPhone.getText().toString().trim();
            email     = textEmail.getText().toString().trim();

            if(!checkBox.isChecked()){
                Helper.displayErrorMessage(this,getResources().getString(R.string.acceptcodition));
                return;
            }

            if(gender == null){
                Helper.displayErrorMessage(this,getResources().getString(R.string.emptygender));
                return;
            }

            if(!name.isEmpty() && !phone.isEmpty() && !email.isEmpty()){

                if(!Helper.isValidName(name)){
                    Helper.displayErrorMessage(this,getResources().getString(R.string.invalide_name));
                    return;
                }


                if(!Helper.isValidEmail(email)){
                    Helper.displayErrorMessage(this,getResources().getString(R.string.invalide_email));
                    return;
                }


                if(!Helper.isValidPhone(phone)){
                    Helper.displayErrorMessage(this,getResources().getString(R.string.invalide_phone));
                    return;
                }

                if (bitmap !=null && !isDetectedFace){

                    Helper.displayDiagError(this,getString(R.string.anhkohople),getString(R.string.anhkohople1));


                }else{

                    dialog = new ProgressDialog(this);
                    dialog.setMessage(this.getString(R.string.creating));
                    dialog.setCancelable(false);
                    dialog.show();

                    name  = String.valueOf(name.charAt(0)).toUpperCase() + name.subSequence(1, name.length());
                    presenterSignUp = new PresenterSignUp(this);
                    presenterSignUp.receivedHandleCreateProfile(this,id, name, gender, phone, email);

                }




            }else{

                Helper.displayErrorMessage(this,getString(R.string.missfield));

            }



        });

    }

    @Override
    public void onCodeSendSuccess(String verificationId) { }
    @Override
    public void onCodeSendFail(String err) { }
    @Override
    public void onVerifyCodeSuccess(String str) { }
    @Override
    public void onVerifyCodeFail(String err) {

    }
    @Override
    public void onCreateProfileSuccess(User user) {

        this.user =user;
        PresenterImage presenterImage = new PresenterImage(this);
        presenterImage.receivedHandleUploadImage(bitmap,real_patch, user.getUserID()+"_avatar");

    }

    @Override
    public void onCreateProfileFail(String err) { Helper.displayErrorMessage(this,err); }



    @Override
    public void onUpdateImageSuccess(String response) {

        if (response !=null){
            user.setAvatar(response);
            AccessFireBase.updateAvatar(user.getUserID(), response, new IAccessFireBase.iUpdateAvatar() {
                @Override
                public void onSuccess() {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("user",user);
                    Intent intent4 = new Intent(CreateProfileActivity.this, WellComeActivity.class);
                    intent4.putExtra("bundle",bundle);
                    startActivity(intent4);

                    dialog.dismiss();
                }

                @Override
                public void onFailed() {

                }
            });
        }else{
            Bundle bundle = new Bundle();
            bundle.putParcelable("user",user);
            Intent intent4 = new Intent(CreateProfileActivity.this, WellComeActivity.class);
            intent4.putExtra("bundle",bundle);
            startActivity(intent4);

            dialog.dismiss();

        }



    }

    @Override
    public void onUpdateImageFail(String err) {
        dialog.dismiss();
        Toast.makeText(this, err, Toast.LENGTH_SHORT).show();
    }

    private void mapToLayout(){
        btGender    = findViewById(R.id.btGender);
        textName    = findViewById(R.id.edName);
        textPhone   = findViewById(R.id.edPhone);
        textEmail   = findViewById(R.id.edEmail);
        policy      = findViewById(R.id.iwww);
        btFinishCreateAcc = findViewById(R.id.btFinishCreateAcc);
        checkBox          = findViewById(R.id.checkDK);
        avatar      = findViewById(R.id.pavatar0);

    }


    @Override
    public void onBackPressed() {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (grantResults.length > 0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
            Intent intent1 = new Intent(Intent.ACTION_PICK);
            intent1.setType("image/*");
            startActivityForResult(intent1,REQUEST_CODE);
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null ){
            Uri uri = data.getData();
            real_patch = Helper.getRealPathFromURI(this,uri);


            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                Bitmap bitmapold    = BitmapFactory.decodeStream(inputStream);
                bitmap = Bitmap.createScaledBitmap(bitmapold, (int) (bitmapold.getWidth()*0.4), (int) (bitmapold.getHeight()*0.4), true);

                avatar.setImageBitmap(bitmap);


                FaceDetector faceDetector = new FaceDetector.Builder( this )
                        .setTrackingEnabled(false)
                        .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                        .setMode(FaceDetector.FAST_MODE)
                        .build();

                if (faceDetector.isOperational()){

                    Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                    SparseArray<Face> mface = faceDetector.detect(frame);
                    faceDetector.release();

                    isDetectedFace = mface.size() > 0;

                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }
    }

}
