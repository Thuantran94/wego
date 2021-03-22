package com.example.thuantran.wego.Model.Profile;


import android.graphics.Bitmap;
import android.net.Uri;


import com.example.thuantran.wego.Interface.Profile.Image;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;


public class ModeImage {

    private Image.Presenter callback;



    public ModeImage( Image.Presenter callback){
        this.callback = callback;
    }


    public void handleUploadImage(Bitmap bitmap, String real_patch, String id){

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (real_patch !=null){


                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef = storage.getReference().child("images/"+id+".jpeg");


                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    //Compress the original bitmap down into a JPEG.
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                    byte[] data2 = baos.toByteArray();


                    UploadTask uploadTask = storageRef.putBytes(data2);

                    uploadTask.addOnFailureListener(exception -> {
                        // Handle unsuccessful uploads
                    }).addOnSuccessListener(taskSnapshot -> {

                        if (taskSnapshot.getMetadata() != null) {
                            if (taskSnapshot.getMetadata().getReference() != null) {
                                Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                                result.addOnSuccessListener(uri -> {
                                    String imageUrl = uri.toString();
                                    callback.onUpdateImageSuccess(imageUrl);
                                });
                            }
                        }

                    });

                }else{

                    callback.onUpdateImageSuccess(null);
                }

            }
        }).start();
    }

}
