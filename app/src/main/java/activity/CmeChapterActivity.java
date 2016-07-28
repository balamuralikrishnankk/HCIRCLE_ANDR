package activity;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;

import com.google.gson.Gson;
import com.nganthoi.salai.tabgen.R;

import java.util.ArrayList;
import java.util.List;

import Utils.ConstantValues;
import Utils.Methods;
import Utils.NetworkHelper;
import Utils.PreferenceHelper;
import adapter.CmeChapterAdapter;
import adapter.NewsFlipperAdapter;
import models.cme_chapter_model.CmeChapterModel;
import models.cme_chapter_model.Filename;
import models.news_model.NewsModel;
import network.NetworkJob;
import network.NetworkRequest;
import network.NetworkResponse;
import se.emilsjolander.flipview.OverFlipMode;
import threading.BackgroundJobClient;

/**
 * Created by atul on 28/7/16.
 */
public class CmeChapterActivity extends AppCompatActivity implements BackgroundJobClient {

    String detail_url,article_id;
    Toolbar toolbar;
    private ListView lvChapterList;
    PreferenceHelper preferenceHelper;
    private List<Filename> cmeFileList= new ArrayList<>();
    CmeChapterAdapter cmeChapterAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cme_chapter);
        detail_url=getIntent().getStringExtra("DETAIL_URL");
        article_id=getIntent().getStringExtra("ARTICLE_ID");
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= Build.VERSION_CODES.LOLLIPOP)
        {
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }else
        {
        }
        toolbar = (Toolbar) findViewById(R.id.toolbarConversation);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        lvChapterList=(ListView) findViewById(R.id.lvChapterList);
        if(detail_url!=null){
            if(NetworkHelper.isOnline(this)){
                getCmeArticle(detail_url);
            }else{
                Methods.showSnackbar("Sorry! You have lost the connection",this);
            }

        }


    }
    private void getCmeArticle(String detail_url) {
        preferenceHelper=new PreferenceHelper(this);
        Methods.showProgressDialog(this, "Please wait..");
//        user_id=preferenceHelper.getString("LOGIN_USER_ID");
        NetworkRequest.Builder builder=new NetworkRequest.Builder(NetworkRequest.MethodType.GET, detail_url, ConstantValues.CME_CHAPTER_RESPONSE);
        NetworkRequest networkRequest=builder.build();
        builder.setContentType(NetworkRequest.ContentType.FORM_ENCODED);
        NetworkJob networkJob=new NetworkJob(this,networkRequest);
        networkJob.execute();
    }

    @Override
    public void onBackgroundJobComplete(int requestCode, Object result) {
        Methods.closeProgressDialog();
//        try {
            if (result != null) {
                if (ConstantValues.CME_CHAPTER_RESPONSE == requestCode) {
                    CmeChapterModel newsModel = new Gson().fromJson(((NetworkResponse) result).getResponseString(), CmeChapterModel.class);
                    if (newsModel != null) {
                        cmeFileList = newsModel.getResponse().get(0).getFilenames();
                        if (cmeFileList.size() > 0) {
                            cmeChapterAdapter=new CmeChapterAdapter(this,0,cmeFileList);
                            lvChapterList.setAdapter(cmeChapterAdapter);
//                            mAdapter = new NewsFlipperAdapter(this, outputLiist);
////                        mAdapter.setCallback(this);
//                            mFlipView.setAdapter(mAdapter);
//                            mFlipView.setOnFlipListener(this);
//                            mFlipView.peakNext(false);
//                            mFlipView.setOverFlipMode(OverFlipMode.RUBBER_BAND);
//                            //        mFlipView.setEmptyView(findViewById(R.id.empty_view));
//                            mFlipView.setOnOverFlipListener(this);

                        }

                    }
                }
            }
//        }catch (Exception e){
//            Methods.showSnackbar("Sorry! You have lost the connection",this);
//        }

    }

    @Override
    public void onBackgroundJobAbort(int requestCode, Object reason) {
        Methods.closeProgressDialog();
    }

    @Override
    public void onBackgroundJobError(int requestCode, Object error) {
        Methods.closeProgressDialog();
    }

    @Override
    public boolean needAsyncResponse() {
        return true;
    }

    @Override
    public boolean needResponse() {
        return true;
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
