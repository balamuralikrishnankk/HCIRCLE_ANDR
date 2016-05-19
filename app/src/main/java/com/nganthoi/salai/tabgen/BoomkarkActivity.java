package com.nganthoi.salai.tabgen;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Utils.ConstantValues;
import Utils.Methods;
import Utils.NetworkHelper;
import Utils.PreferenceHelper;
import adapter.BookmarkAdapter;
import models.BookMarkListModel;
import models.LikeModels;
import network.NetworkJob;
import network.NetworkRequest;
import network.NetworkResponse;
import threading.BackgroundJobClient;

/**
 * Created by atul on 18/5/16.
 */
public class BoomkarkActivity extends AppCompatActivity implements BackgroundJobClient {

    public RecyclerView bookmarkRecyclerview;
    PreferenceHelper preferenceHelper;
    String user_id,ip,url;
    List<BookMarkListModel> bookmarkList=new ArrayList<>();
    BookmarkAdapter bookmarkAdapter;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);
        toolbar = (Toolbar) findViewById(R.id.toolbarConversation);
        setSupportActionBar(toolbar);
        initComponent();
        if(NetworkHelper.isOnline(this)){
            callBookmarkWebservice();
        }

    }

    private void callBookmarkWebservice() {
        preferenceHelper=new PreferenceHelper(this);
        ip=preferenceHelper.getString("APPLICATION_IP");
        user_id=preferenceHelper.getString("USER_ID");
        url="http://"+ip+"/TabGenAdmin/bookmark.php";
        Methods.showProgressDialog(this, "Please wait..");
        NetworkRequest.Builder builder=new NetworkRequest.Builder(NetworkRequest.MethodType.POST,url, ConstantValues.BOOKMARK_LIST_RESPONSE);
        NetworkRequest networkRequest=builder.build();
        builder.addParameter("action","getBookmarks");
        builder.addParameter("user_id",user_id);
        builder.setContentType(NetworkRequest.ContentType.FORM_ENCODED);
        NetworkJob networkJob=new NetworkJob(this,networkRequest);
        networkJob.execute();
    }

    private void initComponent() {
        bookmarkRecyclerview=(RecyclerView)findViewById(R.id.bookmarkRecyclerview);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        bookmarkRecyclerview.setLayoutManager(layoutManager);

//        bookmarkAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackgroundJobComplete(int requestCode, Object result) {
            Methods.closeProgressDialog();
        if(requestCode==ConstantValues.BOOKMARK_LIST_RESPONSE){
//            try {
                    BookMarkListModel[] bookMarkListModel=new Gson().fromJson(((NetworkResponse) result).getResponseString(), BookMarkListModel[].class);
                    bookmarkList= Arrays.asList(bookMarkListModel);
            bookmarkAdapter=new BookmarkAdapter(this,bookmarkList);
            bookmarkRecyclerview.setAdapter(bookmarkAdapter);
            Log.v("SIZE","SIZE:::"+bookmarkList.size());
                    bookmarkAdapter.notifyDataSetChanged();
//            }catch (Exception e){
//                Log.v("ERROR","ERROR::"+e.toString());
//
//            }
        }
    }

    @Override
    public void onBackgroundJobAbort(int requestCode, Object reason) {
        Methods.closeProgressDialog();
        if(requestCode==ConstantValues.BOOKMARK_LIST_RESPONSE){
            Methods.toastShort(""+reason,this);
        }
    }

    @Override
    public void onBackgroundJobError(int requestCode, Object error) {
        Methods.closeProgressDialog();
        if(requestCode==ConstantValues.BOOKMARK_LIST_RESPONSE){
            Methods.toastShort(""+error,this);
        }
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
