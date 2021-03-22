package com.example.thuantran.wego.View.Fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thuantran.wego.Object.User;
import com.example.thuantran.wego.R;
import com.example.thuantran.wego.Tools.Helper;
import com.vtc.sdkpay2.function.ICallback;
import com.vtc.sdkpay2.function.VTCPaySDK;
import com.vtc.sdkpay2.model.VTCAppInfoModel;

import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class WalletFragment extends Fragment {


    private Button atm;
    private ImageView visa, mastercard ;
    private EditText points;
    private TextView mwallet, r50, r100, r200;
    private User user;

    private VTCAppInfoModel vtcAppInfoModel;


    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Objects.requireNonNull(getActivity()).getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        View view = inflater.inflate(R.layout.activity_main_wallet, container, false);


        r50           = view.findViewById(R.id.r50);
        r100          = view.findViewById(R.id.r100);
        r200          = view.findViewById(R.id.r200);



        mwallet       = view.findViewById(R.id.mwallet);
        points        = view.findViewById(R.id.edPoints);
        visa          = view.findViewById(R.id.visa);
        mastercard    = view.findViewById(R.id.mastercard);
        atm           = view.findViewById(R.id.atm);




        Bundle bundle = getArguments();
        if(bundle != null){
            user   = bundle.getParcelable("user");

            if (user != null) {
                mwallet.setText(user.getPoints() + getString(R.string.points));

                vtcAppInfoModel = new VTCAppInfoModel(getString(R.string.vtcpay_appid), getString(R.string.vtcpay_account), getString(R.string.vtcpay_mk));
                vtcAppInfoModel.setEnvironment(VTCPaySDK.ALPHA_URL);
                vtcAppInfoModel.setOrderCode("sdssdcssddz");
                vtcAppInfoModel.setReceiverAccount("0988345395");
                vtcAppInfoModel.setCurrency(VTCPaySDK.VND);

            }

        }


        r50.setOnClickListener(v -> points.setText("50000"));
        r100.setOnClickListener(v -> points.setText("100000"));
        r200.setOnClickListener(v -> points.setText("200000"));


        visa.setOnClickListener(v -> {
            vtcAppInfoModel.setPaymentType(VTCPaySDK.BANK_QUOCTE);
            paymentVTC();
        });

        mastercard.setOnClickListener(v -> {
            vtcAppInfoModel.setPaymentType(VTCPaySDK.BANK_QUOCTE);
            paymentVTC();
        });


        atm.setOnClickListener(v -> {
            vtcAppInfoModel.setPaymentType(VTCPaySDK.BANK_NOIDIA);
            paymentVTC();
        });



        return view;
    }


    private void paymentVTC(){



        String DT = points.getText().toString().trim();

        if (!DT.isEmpty()){

            double amount = Double.valueOf(DT);

            if (amount<20000){
                Helper.displayDiagWarning(getActivity(),getString(R.string.err),"Mệnh giá tối thiểu cho mỗi giao dịch là 20000DT");
                return;
            }

            vtcAppInfoModel.setAmount(amount);
            VTCPaySDK.getInstance().nextFunctionVTCPay(getActivity(), VTCPaySDK.PAYMENT, vtcAppInfoModel, new ICallback() {
                @Override
                public void callback(int responseCode, String desc) {
                    Toast.makeText(getActivity(), desc, Toast.LENGTH_LONG).show();

                }
            });
        }else{
            Helper.displayDiagWarning(getActivity(),getString(R.string.err),"Mệnh giá tối thiểu cho mỗi giao dịch là 20000DT");
        }



    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == VTCPaySDK.ACTIVITY_REQUEST_CODE_LOGIN) {

            if (resultCode == RESULT_OK) {


            }

        }


    }
}

