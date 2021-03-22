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

import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thuantran.wego.DataAccess.AccessFireBase;
import com.example.thuantran.wego.DataAccess.IAccessFireBase;
import com.example.thuantran.wego.Interface.Profile.Image;
import com.example.thuantran.wego.Interface.Tools.Fragment2Activity;
import com.example.thuantran.wego.Presenter.Profile.PresenterImage;
import com.example.thuantran.wego.R;
import com.example.thuantran.wego.Object.Car;
import com.example.thuantran.wego.Tools.Helper;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class CarFragment extends Fragment implements View.OnClickListener, Image.View {

    private Button btUpdateAc;
    private ImageView avatar;
    private EditText edName, edPhone, edEmail;
    private ProgressDialog dialog;
    private Fragment2Activity mcallback;

    private int REQUEST_CODE = 123;
    private Car  car;
    private String userID;
    private String real_patch = null;
    private Bitmap bitmap;

    private String newavatar, newname, newcolor, newyear;


    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        mcallback = (Fragment2Activity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Objects.requireNonNull(getActivity()).getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        View view = inflater.inflate(R.layout.activity_main_change_profile, container, false);
        mapToLayout(view);

        TextView title = view.findViewById(R.id.titlename);
        edEmail.setEnabled(true);

        title.setText(getActivity().getResources().getString(R.string.InforCar));
        edName.setHint(getActivity().getResources().getString(R.string.NameCar));
        edPhone.setHint(getActivity().getResources().getString(R.string.YearCar));
        edEmail.setHint(getActivity().getResources().getString(R.string.ColorCar));


        Bundle bundle = getArguments();
        if(bundle != null){
            car    = bundle.getParcelable("car");

            if (car != null){

                userID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

                edName.setText(car.getNameCar());
                edPhone.setText(car.getYearCar());
                edEmail.setText(car.getColorCar());
                Picasso.get().load(car.getPhotoCar())
                        .resize(250,250)
                        .centerCrop()
                        .into(avatar);

                btUpdateAc.setOnClickListener(this);
                avatar.setOnClickListener(this);
                edEmail.setOnClickListener(this);
            }


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
                newcolor = edEmail.getText().toString().trim();
                newyear  = edPhone.getText().toString().trim();



                if(bitmap != null || !newname.equals(car.getNameCar())
                        || !newcolor.equals(car.getColorCar())|| !newyear.equals(car.getYearCar())){


                    dialog = new ProgressDialog(getActivity());
                    dialog.setMessage(this.getString(R.string.updating0));
                    dialog.setCancelable(false);
                    dialog.show();

                    PresenterImage presenterImage = new PresenterImage(this);
                    presenterImage.receivedHandleUploadImage(bitmap,real_patch, userID+"_photocar");

                }

                break;


        }
    }

    private void mapToLayout(View view){
        view.findViewById(R.id.logout).setVisibility(View.INVISIBLE);
        btUpdateAc  = view.findViewById(R.id.btUpdateAc);
        edName      = view.findViewById(R.id.edName);
        edPhone     = view.findViewById(R.id.edPhone);
        edEmail     = view.findViewById(R.id.edEmail);

        avatar      = view.findViewById(R.id.pavatar0);

        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(4);
        edPhone.setFilters(filterArray);
        edEmail.setEnabled(false);

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
            real_patch = Helper.getRealPathFromURI(Objects.requireNonNull(getActivity()),uri);


            try {
                InputStream inputStream = getActivity().getContentResolver().openInputStream(Objects.requireNonNull(uri));
                Bitmap oldbitmap = BitmapFactory.decodeStream(inputStream);
                Bitmap newbitmap = Bitmap.createScaledBitmap(oldbitmap, (int) (oldbitmap.getWidth()*0.5), (int) (oldbitmap.getHeight()*0.5), true);
                bitmap = newbitmap;
                avatar.setImageBitmap(newbitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }
    }





    @Override
    public void onUpdateImageSuccess(String response) {

        real_patch = null;
        if (response !=null)  {
            newavatar = response;
        }else{
            newavatar = car.getPhotoCar();
        }


        car.setNameCar(newname);
        car.setPhotoCar(newavatar);
        car.setColorCar(newcolor);
        car.setTypeCar("");
        car.setYearCar(newyear);
        mcallback.getCar(car);

        AccessFireBase.updateProfileCar(userID, car.getTypeCar(), car.getNameCar(), car.getColorCar(), car.getYearCar(), car.getPhotoCar(),
                new IAccessFireBase.iUpdateProfileCar() {
                    @Override
                    public void onSuccess() {
                        if (getActivity() != null){
                            getActivity().runOnUiThread(() -> Helper.displayDiagSuccess(getActivity(),getResources().getString(R.string.changedinfosuccess),""));
                        }
                        dialog.dismiss();

                    }

                    @Override
                    public void onFailed() {
                        Objects.requireNonNull(getActivity()).runOnUiThread(() -> Helper.displayErrorMessage(getActivity(),getResources().getString(R.string.errorconnect)));
                    }
                });
    }


    @Override
    public void onUpdateImageFail(String err) {
        dialog.dismiss();
        Toast.makeText(getActivity(), err, Toast.LENGTH_SHORT).show();
    }


}