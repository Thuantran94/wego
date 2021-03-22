package com.example.thuantran.wego.Presenter.Profile;

import android.graphics.Bitmap;

import com.example.thuantran.wego.Interface.Profile.Image;
import com.example.thuantran.wego.Model.Profile.ModeImage;

public class PresenterImage implements Image.Presenter {


    private ModeImage modeImage;
    private Image.View callback;

    public PresenterImage(Image.View callback){
        this.callback = callback;
    }



    public void receivedHandleUploadImage(Bitmap bitmap, String real_patch , String id){
        modeImage = new ModeImage(this);
        modeImage.handleUploadImage(bitmap,real_patch, id);
    }



    @Override
    public void onUpdateImageSuccess(String reponse) {
        callback.onUpdateImageSuccess(reponse);

    }

    @Override
    public void onUpdateImageFail(String err) {
        callback.onUpdateImageFail(err);

    }


}
