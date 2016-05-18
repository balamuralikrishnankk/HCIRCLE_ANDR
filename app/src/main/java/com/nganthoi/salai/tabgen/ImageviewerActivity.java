package com.nganthoi.salai.tabgen;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.ImageView;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import java.io.File;

/**
 * Created by atul on 18/4/16.
 */
public class ImageviewerActivity extends AppCompatActivity {

    SubsamplingScaleImageView wvImageViewer;
    String filePath;
    File file;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);
        filePath=getIntent().getStringExtra("FILENAME");
        toolbar = (Toolbar) findViewById(R.id.toolbarConversation);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        wvImageViewer=(SubsamplingScaleImageView)findViewById(R.id.wvImageViewer);
        if(filePath!=null){
            try {
                file = new File(filePath);
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(file));
                wvImageViewer.setImage(ImageSource.bitmap(bitmap));
                wvImageViewer.setZoomEnabled(true);

//                wvImageViewer.setP(new PointF(1718f, 581f));
//                wvImageViewer.setPanLimit(SubsamplingScaleImageView.PAN_LIMIT_CENTER);
//                wvImageViewer.setZoomEnabled(true);
            }catch (Exception e){

            }
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
