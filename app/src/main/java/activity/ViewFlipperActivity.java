package activity;

import Utils.ConstantValues;
import Utils.Methods;
import Utils.NetworkHelper;
import Utils.PreferenceHelper;
import adapter.FlipAdapter;
import adapter.FlipAdapter.Callback;
import models.cmearticlemodel.CmeArticleModel;
import models.cmearticlemodel.Output;
import models.cmetabmodel.CmeTabModel;
import network.NetworkJob;
import network.NetworkRequest;
import network.NetworkResponse;
import se.emilsjolander.flipview.FlipView.OnFlipListener;
import se.emilsjolander.flipview.FlipView.OnOverFlipListener;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.google.gson.Gson;
import com.nganthoi.salai.tabgen.BuildConfig;
import com.nganthoi.salai.tabgen.ChatFragment;
import com.nganthoi.salai.tabgen.ConversationActivity;
import com.nganthoi.salai.tabgen.R;

import java.util.ArrayList;
import java.util.List;

import se.emilsjolander.flipview.FlipView;
import se.emilsjolander.flipview.OverFlipMode;
import threading.BackgroundJobClient;

/**
 * Created by atul on 4/7/16.
 */
public class ViewFlipperActivity extends AppCompatActivity implements BackgroundJobClient, Callback, OnFlipListener, OnOverFlipListener{

    private FlipView mFlipView;
    private FlipAdapter mAdapter;
    String tab_id;
    List<Output> outputLiist=new ArrayList<>();
    PreferenceHelper preferenceHelper;
    Toolbar toolbar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceHelper=new PreferenceHelper(this);
        setContentView(R.layout.activity_view_flipper);

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
        tab_id=getIntent().getStringExtra("TAB_ID");
        if(tab_id!=null){
            if(NetworkHelper.isOnline(this)){
                getCmeArticle();
            }else{
                Methods.toastShort("Please check your internet connection..",this);
            }

        }
        mFlipView = (FlipView) findViewById(R.id.flip_view);

    }

    private void getCmeArticle() {
        preferenceHelper=new PreferenceHelper(this);
        Methods.showProgressDialog(this, "Please wait..");
        NetworkRequest.Builder builder=new NetworkRequest.Builder(NetworkRequest.MethodType.GET, ConstantValues.CME_ARTICLE_URL+""+tab_id, ConstantValues.CME_ARTICLE_RESPONSE);
        NetworkRequest networkRequest=builder.build();
        builder.setContentType(NetworkRequest.ContentType.FORM_ENCODED);
        NetworkJob networkJob=new NetworkJob(this,networkRequest);
        networkJob.execute();
    }

    @Override
    public void onPageRequested(int page) {
        mFlipView.smoothFlipTo(page);
    }

    @Override
    public void onFlippedToPage(FlipView v, int position, long id) {

//        mAdapter.
//        if(position > mFlipView.getPageCount()-3 && mFlipView.getPageCount()<30){
//            mAdapter.addItems(5);
//        }
        
    }

    @Override
    public void onOverFlip(FlipView v, OverFlipMode mode, boolean overFlippingPrevious, float overFlipDistance, float flipDistancePerPage) {
        Log.i("overflip", "overFlipDistance = "+overFlipDistance);
    }

    @Override
    public void onBackgroundJobComplete(int requestCode, Object result) {
        Methods.closeProgressDialog();
        if(result!=null){
            if(ConstantValues.CME_ARTICLE_RESPONSE==requestCode){
                CmeArticleModel cmeArticleModel = new Gson().fromJson(((NetworkResponse) result).getResponseString(), CmeArticleModel.class);
                if(cmeArticleModel!=null){
                    outputLiist=cmeArticleModel.getOutput();
                    if(outputLiist.size()>0){
                        mAdapter = new FlipAdapter(this,outputLiist);
                        mAdapter.setCallback(this);
                        mFlipView.setAdapter(mAdapter);
                        mFlipView.setOnFlipListener(this);
                        mFlipView.peakNext(false);
                        mFlipView.setOverFlipMode(OverFlipMode.RUBBER_BAND);
                        //        mFlipView.setEmptyView(findViewById(R.id.empty_view));
                        mFlipView.setOnOverFlipListener(this);
                        Log.v("ARTICLE","ARTICLE:::"+cmeArticleModel.getOutput().get(0).getTextualContent());
                    }

                }
            }
        }

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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean needAsyncResponse() {
        return true;
    }

    @Override
    public boolean needResponse() {
        return true;
    }
}
