package com.nganthoi.salai.tabgen;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import java.io.File;

/**
 * Created by atul on 4/4/16.
 */
public class UploadActivity extends AppCompatActivity {

    public ImageView imgFile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        imgFile=(ImageView)findViewById(R.id.imgFile);

        showImage(new String());
    }
    public void showImage(String path){
        File imgFile1 = new  File(path);

        if(imgFile1.exists()){

            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile1.getAbsolutePath());
            imgFile.setImageBitmap(myBitmap);
//            ImageView myImage = (ImageView) findViewById(R.id.imgFile);



        }
    }
}
