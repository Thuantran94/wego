package com.example.thuantran.wego.View.Fragment;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.thuantran.wego.DataAccess.AccessFireBase;
import com.example.thuantran.wego.DataAccess.IAccessFireBase;
import com.example.thuantran.wego.Interface.Profile.Image;
import com.example.thuantran.wego.Interface.Tools.Fragment2Activity;
import com.example.thuantran.wego.Presenter.Profile.PresenterImage;
import com.example.thuantran.wego.R;
import com.example.thuantran.wego.Object.User;
import com.example.thuantran.wego.Tools.Helper;
import com.example.thuantran.wego.View.Main.MainActivity;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Objects;



public class ProfileFragment extends Fragment implements View.OnClickListener, Image.View {

    private Button btUpdateAc, Logout;
    private ImageView avatar;
    private EditText edName, edPhone, edEmail;
    private ProgressDialog dialog;

    private Bitmap bitmap;
    private User user;
    private String userID;
    private static int REQUEST_CODE = 123;
    private String real_patch = null;
    private boolean isDetectedFace = false;
    private Fragment2Activity mcallback;

    private String newname, newemail, newavatar;


    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        mcallback = (Fragment2Activity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        View view = inflater.inflate(R.layout.activity_main_change_profile, container, false);
        mapToLayout(view);



        Bundle bundle = getArguments();
        if(bundle != null){
            user   = bundle.getParcelable("user");
            userID = Objects.requireNonNull(user).getUserID();

            edName.setText(user.getName());
            edPhone.setText(user.getPhone());
            edEmail.setText(user.getEmail());
            if (user.getAvatar() != null){
                Picasso.get().load(user.getAvatar())
                        .resize(250,250)
                        .centerCrop()
                        .into(avatar);
            }


            btUpdateAc.setOnClickListener(this);
            Logout.setOnClickListener(this);
            avatar.setOnClickListener(this);

        }



        return view;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.pavatar0:

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE);
                }else{
                    Intent intent1 = new Intent(Intent.ACTION_PICK);
                    intent1.setType("image/*");
                    startActivityForResult(intent1,REQUEST_CODE);
                }
                break;

            case R.id.btUpdateAc:

                 newname  = edName.getText().toString().trim();
                 newemail = edEmail.getText().toString().trim();


                if(bitmap !=null || !newname.equals(user.getName()) || !newemail.equals(user.getEmail())){

                    if (bitmap !=null && !isDetectedFace){
                        Helper.displayDiagError(getActivity(),getString(R.string.anhkohople),getString(R.string.anhkohople1));
                        return; }

                    if(!Helper.isValidName(newname)){
                        Helper.displayErrorMessage(getActivity(),getResources().getString(R.string.invalide_name));
                        return; }



                    dialog = new ProgressDialog(getActivity());
                    dialog.setMessage(this.getString(R.string.updating0));
                    dialog.setCancelable(false);
                    dialog.show();


                    PresenterImage presenterImage = new PresenterImage(this);
                    presenterImage.receivedHandleUploadImage(bitmap,real_patch, userID+"_avatar");



                }





                break;

            case R.id.logout:
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                mAuth.signOut();
                AccessFireBase.updateStatus(userID,"offline");
                Intent intent5 = new Intent(getActivity(), MainActivity.class);
                startActivity(intent5);
                break;

        }
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

        if (requestCode == REQUEST_CODE && resultCode == getActivity().RESULT_OK && data != null ){
            Uri uri = data.getData();
            real_patch = Helper.getRealPathFromURI(Objects.requireNonNull(getActivity()),uri);


            try {
                InputStream inputStream = getActivity().getContentResolver().openInputStream(uri);
                 Bitmap bitmapold    = BitmapFactory.decodeStream(inputStream);
                 bitmap = Bitmap.createScaledBitmap(bitmapold, (int) (bitmapold.getWidth()*0.4), (int) (bitmapold.getHeight()*0.4), true);
                 avatar.setImageBitmap(bitmap);


                FaceDetector faceDetector = new FaceDetector.Builder( getContext() )
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





    @Override
    public void onUpdateImageSuccess(String response) {

        real_patch = null;


        if (response !=null){
            newavatar = response; }
        else{
            newavatar = user.getAvatar(); }


            user.setName(newname);
            user.setAvatar(newavatar);
            mcallback.getUser(user);

            AccessFireBase.updateProfile(user.getUserID(), user.getName(), user.getAvatar(), new IAccessFireBase.iUpdateProfile() {
                @Override
                public void onSuccess() {
                    if (getActivity() != null){
                        getActivity().runOnUiThread(() -> Helper.displayDiagSuccess(getActivity(),getResources().getString(R.string.changedinfosuccess),"")); }

                     dialog.dismiss();
                }

                @Override
                public void onFailed() {
                    if (getActivity() != null){
                        getActivity().runOnUiThread(() -> Helper.displayErrorMessage(getActivity(),getResources().getString(R.string.errorconnect))); }
                    dialog.dismiss();
                }
            });

    }


    @Override
    public void onUpdateImageFail(String err) {
        dialog.dismiss();
        Toast.makeText(getActivity(), err, Toast.LENGTH_SHORT).show();
    }


    private void mapToLayout(View view){
        btUpdateAc  = view.findViewById(R.id.btUpdateAc);
        Logout      = view.findViewById(R.id.logout);
        edName      = view.findViewById(R.id.edName);
        edPhone     = view.findViewById(R.id.edPhone);
        edEmail     = view.findViewById(R.id.edEmail);
        avatar      = view.findViewById(R.id.pavatar0);
        edPhone.setEnabled(false);

    }


}