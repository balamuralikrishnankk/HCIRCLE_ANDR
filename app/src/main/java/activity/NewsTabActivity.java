package activity;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ExpandableListView;

import com.google.gson.Gson;
import com.nganthoi.salai.tabgen.R;

import java.util.ArrayList;

import Utils.ConstantValues;
import Utils.Methods;
import Utils.NetworkHelper;
import Utils.PreferenceHelper;
import adapter.CmeAdapter;
import adapter.NewsTabAdapter;
import models.cmetabmodel.CmeTabModel;
import models.cmetabmodel.OrgUnit;
import models.news_tab_model.NewsTabModel;
import network.NetworkJob;
import network.NetworkRequest;
import network.NetworkResponse;
import threading.BackgroundJobClient;

/**
 * Created by atul on 26/7/16.
 */
public class NewsTabActivity extends AppCompatActivity implements BackgroundJobClient{

    ExpandableListView expandableListView;
    ArrayList<models.news_tab_model.OrgUnit> orgUnitList=new ArrayList<>();
    NewsTabAdapter newsTabAdapter;
    View cmeView;
    PreferenceHelper preferenceHelper;
    Toolbar toolbar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_tab);
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
        expandableListView = (ExpandableListView)findViewById(R.id.expandableListViewCME);
        if(NetworkHelper.isOnline(this)){
            getNewsTabList();
        }else{
            Methods.toastShort("Please check your internet connection..",this);
        }
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;
        expandableListView.setIndicatorBounds(width - 120, width);
    }

    private void getNewsTabList() {
        preferenceHelper=new PreferenceHelper(this);
        Methods.showProgressDialog(this, "Please wait..");
        NetworkRequest.Builder builder=new NetworkRequest.Builder(NetworkRequest.MethodType.GET, ConstantValues.NEWS_TAB_DATA+""+preferenceHelper.getString("LOGIN_USER_ID"), ConstantValues.NEWS_TAB_RESPONSE);
        NetworkRequest networkRequest=builder.build();
        builder.setContentType(NetworkRequest.ContentType.FORM_ENCODED);
        NetworkJob networkJob=new NetworkJob(this,networkRequest);
        networkJob.execute();
    }

    @Override
    public void onBackgroundJobComplete(int requestCode, Object result) {
        Methods.closeProgressDialog();
        if(result!=null){
            if(ConstantValues.NEWS_TAB_RESPONSE==requestCode) {
                NewsTabModel newsTabModel = new Gson().fromJson(((NetworkResponse) result).getResponseString(), NewsTabModel.class);
                if (newsTabModel != null) {
                    orgUnitList = (ArrayList<models.news_tab_model.OrgUnit>) newsTabModel.getResponse().getOrgUnits();
                    if(orgUnitList.size()>0) {
                        newsTabAdapter = new NewsTabAdapter(this, orgUnitList);
                        expandableListView.setAdapter(newsTabAdapter);
                    }
                    Log.v("CmeFragment", "CmeFragment:::" + newsTabModel.getResponse().getOrgUnits().get(0).getName());
                }
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
}
