package com.example.thuantran.wego.View.Main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.thuantran.wego.Adapter.ReviewAdapter;
import com.example.thuantran.wego.Interface.Profile.Review;
import com.example.thuantran.wego.Object.ReviewContext;
import com.example.thuantran.wego.Object.User;
import com.example.thuantran.wego.Presenter.Profile.PresenterReview;
import com.example.thuantran.wego.R;
import com.example.thuantran.wego.Tools.Helper;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class ShowReviewActivity extends AppCompatActivity implements Review.View {
    private ProgressDialog dialog;
    private RadioGroup btre;
    private ImageView back,avatar;
    private TextView  ntriptotal, ratingBar, nrate,nreport, xem;
    private RecyclerView listView;
    private TextView namerw;
    private User user;

    private PresenterReview presenterReview;
    private ArrayList<ReviewContext> reviews, reports;
    private ReviewAdapter adapterReview, adapterReport;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main_show_review);

        btre      = findViewById(R.id.buttonre);
        back      = findViewById(R.id.backrw);
        namerw    = findViewById(R.id.namerw);
        avatar    = findViewById(R.id.image_rate);
        ntriptotal= findViewById(R.id.ntriptotal);
        ratingBar = findViewById(R.id.npdanhgia);
        nrate     = findViewById(R.id.ndanhgia);
        nreport   = findViewById(R.id.nreport);
        listView  = findViewById(R.id.lwrw);
        xem       = findViewById(R.id.xem);


        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        listView.setLayoutManager(layoutManager);


        Bundle b = getIntent().getBundleExtra("bundle");
        if (b != null) {

            user     = b.getParcelable("user");

            if(user !=null){

                namerw.setText(user.getName());
                Picasso.get().load(user.getAvatar())
                        .resize(250,250)
                        .centerCrop()
                        .into(avatar);

                ntriptotal.setText(user.getNtriptotal());

                dialog = new ProgressDialog(this);
                dialog.setMessage(getString(R.string.loading));
                dialog.setCancelable(false);
                dialog.show();

                // lấy review của người dùng hiện tại
                presenterReview = new PresenterReview(this);
                presenterReview.receivedHandleGetAllReview(user.getUserID());

            }

        }


        back.setOnClickListener(view -> finish());

        btre.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch (checkedId){

                    case R.id.review:

                        xem.setText(getString(R.string.xem_t_t_c_nh_gi) + reviews.size() + getString(R.string.review1));
                        adapterReview = new ReviewAdapter(reviews);
                        listView.setAdapter(adapterReview);
                        adapterReview.notifyDataSetChanged();
                        break;

                    case R.id.report:

                        xem.setText(getString(R.string.xem_t_t_c_nh_to) );
                        adapterReport = new ReviewAdapter(reports);
                        listView.setAdapter(adapterReport);
                        adapterReport.notifyDataSetChanged();
                        break;
                }
            }
        });

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onGetReviewSuccess(ArrayList<ReviewContext> reviews) {

        presenterReview.receivedHandleGetAllReport(user.getUserID());

        this.reviews = reviews;

        float rate;
        if (reviews.size() > 0){
            float sum = 0;
            for (int i= 0;i<reviews.size();i++){
                sum = sum + Float.valueOf(reviews.get(i).getRate());
            }
            rate = sum/reviews.size();
        }
        else{
            rate = 0;
        }

        ratingBar.setText(String.valueOf(rate));
        nrate.setText(String.valueOf(reviews.size()));

        xem.setText(getString(R.string.xem_t_t_c_nh_gi) + reviews.size() + getString(R.string.review1));
        adapterReview = new ReviewAdapter(reviews);
        listView.setAdapter(adapterReview);
        adapterReview.notifyDataSetChanged();
    }

    @Override
    public void onGetReportSuccess(ArrayList<ReviewContext> reports) {
        dialog.dismiss();

        this.reports = reports;
        nreport.setText(String.valueOf(reports.size()));
    }

    @Override
    public void onGetReviewFailed(String err) {
        dialog.dismiss();
    }

    @Override
    public void onGetReportFailed(String err) {
        dialog.dismiss();
    }
}
